package som.langserv.simple;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;

import com.oracle.truffle.sl.nodes.SLBlock;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLStatementNode;

import simple.SLNodeFactory;
import simple.nodes.SimpleString;
import som.langserv.structure.DocumentSymbols;
import som.langserv.structure.LanguageElement;
import som.langserv.structure.LanguageElementId;
import som.langserv.structure.SemanticTokenType;


public class SimpleNodeFactory extends SLNodeFactory {

  private final SimpleStructures probe;
  private final DocumentSymbols  symbols;

  private List<String>    paramNames;
  private LanguageElement currentFunction;

  public SimpleNodeFactory(final SimpleStructures probe) {
    super(null, null);
    this.probe = probe;
    this.symbols = probe.getSymbols();
  }

  @Override
  public void startFunction(final Token identifier, final Token s) {
    probe.addSemanticToken(identifier, SemanticTokenType.FUNCTION);

    Range r = new Range();
    r.setStart(new Position(s.getLine(), s.getCharPositionInLine()));
    r.setEnd(
        new Position(s.getLine(), s.getCharPositionInLine() + identifier.getText().length()));

    currentFunction = symbols.startSymbol(identifier.getText(), SymbolKind.Function,
        new FunctionId(identifier.getText()), r);

    paramNames = new ArrayList<>(3);
  }

  @Override
  public void finishFunction(final SLStatementNode result) {}

  public void finishFunction(final Token endBrace) {
    Range selectionRange = currentFunction.getSelectionRange();

    Position end = new Position(endBrace.getLine(), endBrace.getCharPositionInLine() + 1);

    setFunctionSignature();
    paramNames = null;

    symbols.completeSymbol(currentFunction, new Range(selectionRange.getStart(), end));
  }

  private void setFunctionSignature() {
    String details = currentFunction.getName() + "(";
    int i = 0;

    for (String param : paramNames) {
      if (i > 0) {
        details += ", ";
      }
      details += param;
      i += 1;
    }

    details += ")";
    currentFunction.setDetail(details);
  }

  @Override
  public SLStatementNode finishBlock(final List<SLStatementNode> body, final int startIndex,
      final int length) {
    return new SLBlock(body, startIndex, length);
  }

  @Override
  public void addFormalParameter(final Token identifier) {
    probe.addSemanticToken(identifier, SemanticTokenType.PARAMETER);
    paramNames.add(identifier.getText());
  }

  @Override
  public SLStatementNode createBreak(final Token b) {
    probe.addSemanticToken(b, SemanticTokenType.KEYWORD);
    return super.createBreak(b);
  }

  @Override
  public SLStatementNode createContinue(final Token c) {
    probe.addSemanticToken(c, SemanticTokenType.KEYWORD);
    return super.createContinue(c);
  }

  @Override
  public SLStatementNode createDebugger(final Token d) {
    probe.addSemanticToken(d, SemanticTokenType.KEYWORD);
    return super.createDebugger(d);
  }

  @Override
  public SLExpressionNode createBinary(final Token op, final SLExpressionNode result,
      final SLExpressionNode result2) {
    probe.addSemanticToken(op, SemanticTokenType.OPERATOR);
    return super.createBinary(op, result, result2);
  }

  @Override
  public SLExpressionNode createStringLiteral(final Token identifier,
      final boolean isLiteral) {
    if (isLiteral) {
      probe.addSemanticToken(identifier, SemanticTokenType.STRING);
    }
    return super.createStringLiteral(identifier, isLiteral);
  }

  @Override
  public SLExpressionNode createNumericLiteral(final Token numeric_LITERAL) {
    probe.addSemanticToken(numeric_LITERAL, SemanticTokenType.NUMBER);
    return super.createNumericLiteral(numeric_LITERAL);
  }

  @Override
  public SLExpressionNode createRead(final SLExpressionNode assignmentName) {
    if (assignmentName instanceof SimpleString) {
      probe.addSemanticToken(
          ((SimpleString) assignmentName).identifier,
          SemanticTokenType.VARIABLE);
    }
    return super.createRead(assignmentName);
  }

  @Override
  public SLExpressionNode createCall(final SLExpressionNode receiver,
      final List<SLExpressionNode> parameters, final Token e) {
    if (receiver instanceof SimpleString) {
      SimpleString s = (SimpleString) receiver;
      probe.addSemanticToken(s.identifier, SemanticTokenType.FUNCTION);
      referenceSymbol(new FunctionId(s.identifier.getText()), s.identifier);
    }
    return super.createCall(receiver, parameters, e);
  }

  private void referenceSymbol(final LanguageElementId id, final Token token) {
    symbols.referenceSymbol(id, new Range(
        new Position(token.getLine(), token.getCharPositionInLine()),
        new Position(token.getLine(),
            token.getCharPositionInLine() + token.getText().length())));
  }
}
