package som.langserv.simple;

import static som.langserv.simple.PositionConversion.getEnd;
import static som.langserv.simple.PositionConversion.getRange;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.ParameterInformation;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureInformation;
import org.eclipse.lsp4j.SymbolKind;

import com.oracle.truffle.sl.nodes.SLBlock;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLRead;
import com.oracle.truffle.sl.nodes.SLStatementNode;

import simple.SLNodeFactory;
import simple.nodes.SimpleString;
import som.langserv.structure.DocumentStructures;
import som.langserv.structure.LanguageElement;
import som.langserv.structure.LanguageElementId;
import som.langserv.structure.Reference;
import som.langserv.structure.SemanticTokenType;
import som.langserv.structure.SemanticTokens;


public class SimpleNodeFactory extends SLNodeFactory {

  private final DocumentStructures structures;
  private final SemanticTokens     semanticTokens;

  private List<String>    paramNames;
  private LanguageElement currentFunction;

  public SimpleNodeFactory(final DocumentStructures structures) {
    super(null, null);
    this.structures = structures;
    this.semanticTokens = structures.getSemanticTokens();
  }

  protected void addSemanticToken(final Token token, final SemanticTokenType type) {
    semanticTokens.addSemanticToken(
        // line is 1-based, but needs to be 0-based
        token.getLine() - 1,
        // char position is already 0-based
        token.getCharPositionInLine(),
        token.getText().length(),
        type);
  }

  @Override
  public void startFunction(final Token identifier, final Token s) {
    addSemanticToken(identifier, SemanticTokenType.FUNCTION);

    currentFunction = structures.startSymbol(identifier.getText(), SymbolKind.Function,
        new VarId(identifier.getText(), null), getRange(identifier), true);

    paramNames = new ArrayList<>(3);
  }

  @Override
  public void finishFunction(final SLStatementNode result) {}

  public void finishFunction(final Token endBrace) {
    if (currentFunction == null) {
      return;
    }

    Range selectionRange = currentFunction.getSelectionRange();

    setFunctionSignature();
    paramNames = null;

    structures.completeSymbol(currentFunction,
        new Range(selectionRange.getStart(), getEnd(endBrace)));
  }

  private void setFunctionSignature() {
    String details = currentFunction.getName() + "(";
    int i = 0;

    List<ParameterInformation> params = new ArrayList<>(paramNames.size());

    for (String param : paramNames) {
      if (i > 0) {
        details += ", ";
      }
      details += param;
      i += 1;

      params.add(new ParameterInformation(param));
    }

    details += ")";
    currentFunction.setDetail(details);

    SignatureInformation info = new SignatureInformation(details);
    info.setParameters(params);

    currentFunction.setSignature(info);
  }

  @Override
  public SLStatementNode finishBlock(final List<SLStatementNode> body, final int startIndex,
      final int length) {
    return new SLBlock(body, startIndex, length);
  }

  @Override
  public void addFormalParameter(final Token identifier) {
    addSemanticToken(identifier, SemanticTokenType.PARAMETER);
    recordDefinition(identifier, new VarId(identifier.getText(), currentFunction),
        SymbolKind.Variable);
    paramNames.add(identifier.getText());
  }

  @Override
  public SLStatementNode createBreak(final Token b) {
    addSemanticToken(b, SemanticTokenType.KEYWORD);
    return super.createBreak(b);
  }

  @Override
  public SLStatementNode createContinue(final Token c) {
    addSemanticToken(c, SemanticTokenType.KEYWORD);
    return super.createContinue(c);
  }

  @Override
  public SLStatementNode createDebugger(final Token d) {
    addSemanticToken(d, SemanticTokenType.KEYWORD);
    return super.createDebugger(d);
  }

  @Override
  public SLExpressionNode createBinary(final Token op, final SLExpressionNode result,
      final SLExpressionNode result2) {
    addSemanticToken(op, SemanticTokenType.OPERATOR);
    return super.createBinary(op, result, result2);
  }

  @Override
  public SLExpressionNode createStringLiteral(final Token identifier,
      final boolean isLiteral) {
    if (isLiteral) {
      addSemanticToken(identifier, SemanticTokenType.STRING);
    }
    return super.createStringLiteral(identifier, isLiteral);
  }

  @Override
  public SLExpressionNode createNumericLiteral(final Token numeric_LITERAL) {
    addSemanticToken(numeric_LITERAL, SemanticTokenType.NUMBER);
    return super.createNumericLiteral(numeric_LITERAL);
  }

  private LanguageElement getCurrentFunIfItHasDefinition(final String varName) {
    if (currentFunction.getAllChildren() != null) {
      for (var c : currentFunction.getAllChildren()) {
        if (c.getName().equals(varName)) {
          return currentFunction;
        }
      }
    }
    return null;
  }

  @Override
  public SLExpressionNode createRead(final SLExpressionNode assignmentName) {
    if (assignmentName instanceof SimpleString t) {
      addSemanticToken(
          t.identifier,
          SemanticTokenType.VARIABLE);
      referenceSymbol(new VarId(t.identifier.getText(),
          getCurrentFunIfItHasDefinition(t.identifier.getText())), t.identifier);
    } else {
      throw new RuntimeException("Not yet implemented " + assignmentName.getClass());
    }
    return super.createRead(assignmentName);
  }

  @Override
  public SLExpressionNode createAssignment(final SLExpressionNode assignmentName,
      final SLExpressionNode result) {
    if (assignmentName instanceof SimpleString s) {
      recordDefinition(s.identifier, new VarId(s.identifier.getText(), currentFunction),
          SymbolKind.Variable);
    } else {
      throw new RuntimeException("Not yet implemented " + assignmentName.getClass());
    }
    return super.createAssignment(assignmentName, result);
  }

  @Override
  public SLExpressionNode createCall(final SLExpressionNode receiver,
      final List<SLExpressionNode> parameters, final Token e) {
    Token name = receiver.getLastName();
    addSemanticToken(name, SemanticTokenType.FUNCTION);
    referenceSymbol(new VarId(name.getText(), currentFunction), name);

    return super.createCall(receiver, parameters, e);
  }

  @Override
  public SLExpressionNode createReadProperty(final SLExpressionNode receiver,
      final SLExpressionNode nestedAssignmentName) {
    if (nestedAssignmentName instanceof SimpleString s) {
      referenceSymbol(new PropertyId(s.identifier.getText()), s.identifier).markAsRead();
    } else if (nestedAssignmentName instanceof SLRead r) {
      SLExpressionNode name = r.assignmentName;
      if (name instanceof SimpleString s) {
        referenceSymbol(
            new VarId(s.identifier.getText(),
                getCurrentFunIfItHasDefinition(s.identifier.getText())),
            s.identifier).markAsRead();
      } else {
        throw new RuntimeException("Not yet implemented for " + name.getClass());
      }
    } else {
      throw new RuntimeException("Not yet implemented " + nestedAssignmentName.getClass());
    }
    return super.createReadProperty(receiver, nestedAssignmentName);
  }

  @Override
  public SLExpressionNode createWriteProperty(
      final SLExpressionNode assignmentReceiver, final SLExpressionNode assignmentName,
      final SLExpressionNode result) {
    if (!assignmentName.isRead()) {
      Token name = assignmentName.getLastName();
      LanguageElement e = recordDefinition(name, new PropertyId(name.getText()),
          SymbolKind.Property, true, false);
      e.setDetail(name.getText());
    }
    return super.createWriteProperty(assignmentReceiver, assignmentName, result);
  }

  private LanguageElement recordDefinition(final Token t, final LanguageElementId id,
      final SymbolKind kind) {
    return recordDefinition(t, id, kind, false, false);
  }

  private LanguageElement recordDefinition(final Token t, final LanguageElementId id,
      final SymbolKind kind, final boolean afterNavigation, final boolean listAsSymbol) {
    return structures.recordDefinition(
        t.getText(), id, kind, getRange(t), afterNavigation, listAsSymbol);
  }

  private Reference referenceSymbol(final LanguageElementId id, final Token token) {
    return structures.referenceSymbol(id, getRange(token));
  }
}
