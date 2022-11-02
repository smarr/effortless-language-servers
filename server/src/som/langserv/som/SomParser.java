package som.langserv.som;

import static trufflesom.compiler.Symbol.Identifier;
import static trufflesom.vm.SymbolTable.symbolFor;
import static util.PositionConversion.getEnd;
import static util.PositionConversion.getStart;
import static util.PositionConversion.toRange;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.ParameterInformation;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureInformation;
import org.eclipse.lsp4j.SymbolKind;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bdt.basic.ProgramDefinitionError;
import bdt.source.SourceCoordinate;
import som.langserv.structure.DocumentStructures;
import som.langserv.structure.LanguageElement;
import som.langserv.structure.LanguageElementId;
import som.langserv.structure.SemanticTokenModifier;
import som.langserv.structure.SemanticTokenType;
import trufflesom.compiler.ClassGenerationContext;
import trufflesom.compiler.Field;
import trufflesom.compiler.Lexer;
import trufflesom.compiler.MethodGenerationContext;
import trufflesom.compiler.ParserAst;
import trufflesom.compiler.Symbol;
import trufflesom.compiler.Variable.Argument;
import trufflesom.compiler.Variable.Local;
import trufflesom.interpreter.nodes.ArgumentReadNode.LocalArgumentReadNode;
import trufflesom.interpreter.nodes.ArgumentReadNode.LocalArgumentWriteNode;
import trufflesom.interpreter.nodes.ArgumentReadNode.NonLocalArgumentReadNode;
import trufflesom.interpreter.nodes.ArgumentReadNode.NonLocalArgumentWriteNode;
import trufflesom.interpreter.nodes.ExpressionNode;
import trufflesom.interpreter.nodes.FieldNode.FieldReadNode;
import trufflesom.interpreter.nodes.FieldNode.FieldWriteNode;
import trufflesom.interpreter.nodes.GlobalNode;
import trufflesom.interpreter.nodes.GlobalNode.FalseGlobalNode;
import trufflesom.interpreter.nodes.GlobalNode.NilGlobalNode;
import trufflesom.interpreter.nodes.GlobalNode.TrueGlobalNode;
import trufflesom.interpreter.nodes.LocalVariableNode.LocalVariableReadNode;
import trufflesom.interpreter.nodes.LocalVariableNode.LocalVariableWriteNode;
import trufflesom.interpreter.nodes.NonLocalVariableNode.NonLocalVariableReadNode;
import trufflesom.interpreter.nodes.NonLocalVariableNode.NonLocalVariableWriteNode;
import trufflesom.vm.NotYetImplementedException;
import trufflesom.vm.SymbolTable;
import trufflesom.vmobjects.SSymbol;


/**
 * Extension of the normal SOM parser to record more structural information
 * that is useful for tooling.
 */
public class SomParser extends ParserAst {

  private final DocumentStructures symbols;
  private final SomAdapter         somAdapter;

  private LanguageElement currentClass;
  private LanguageElement currentMethod;

  private final ArrayList<Integer> keywordStart;
  private final ArrayList<String>  keywordParts;

  public SomParser(final String content, final Source source,
      final SomStructures structuralProbe, final SomAdapter somAdapter) {
    super(content, source, structuralProbe);
    assert structuralProbe != null : "Needed for this extended parser.";
    assert structuralProbe.source == source;
    this.keywordParts = new ArrayList<>();
    this.keywordStart = new ArrayList<>();
    this.symbols = structuralProbe.getSymbols();
    this.somAdapter = somAdapter;
  }

  @Override
  protected Lexer createLexer(final String content) {
    return new SomLexer(content,
        ((SomStructures) super.structuralProbe).getSymbols().getSemanticTokens());
  }

  @Override
  protected void className(final ClassGenerationContext cgenc, final int coord)
      throws ParseError {
    super.className(cgenc, coord);
    String name = cgenc.getName().getString();
    recordTokenSemantics(coord, name, SemanticTokenType.CLASS);
    currentClass = startSymbol(name, SymbolKind.Class, coord, new GlobalId(cgenc.getName()));
    currentClass.setDetail(name);
  }

  @Override
  protected void superclass(final ClassGenerationContext cgenc) throws ParseError {
    if (sym == Identifier) {
      int coord = getStartIndex();
      recordTokenSemantics(coord, text, SemanticTokenType.CLASS);
    }

    super.superclass(cgenc);
  }

  @Override
  protected void classSide(final ClassGenerationContext cgenc)
      throws ProgramDefinitionError, ParseError {
    if (sym == Symbol.Separator) {
      int coord = getStartIndex();

      LanguageElement clazz = currentClass;
      currentClass = startSymbol("class", SymbolKind.Class, coord,
          new GlobalId(SymbolTable.symbolFor(cgenc.getName().getString() + " class")));

      super.classSide(cgenc);

      completeSymbol(currentClass, getCoordWithLength(coord));
      currentClass = clazz;
    }
  }

  @Override
  public void classdef(final ClassGenerationContext cgenc) throws ProgramDefinitionError {
    try {
      super.classdef(cgenc);
      // this happens only in the success case
      somAdapter.putStructures(symbols);
    } finally {
      if (currentClass != null) {
        completeSymbol(currentClass, cgenc.getSourceCoord());
      }
    }
  }

  @Override
  protected void primitiveBlock() throws ParseError {
    int coord = getStartIndex();
    recordTokenSemantics(coord, "primitive", SemanticTokenType.KEYWORD);
    super.primitiveBlock();
  }

  @Override
  public ExpressionNode nestedBlock(final MethodGenerationContext mgenc)
      throws ProgramDefinitionError {
    int coord = getStartIndex();
    LanguageElement block = startSymbol(SymbolKind.Function);
    ExpressionNode result = super.nestedBlock(mgenc);

    block.setName(mgenc.getSignature().getString());

    StringBuilder sb = new StringBuilder();
    sb.append('[');

    for (int i = 1; i < mgenc.getNumberOfArguments(); i += 1) {
      if (i > 1) {
        sb.append(' ');
      }

      var a = mgenc.getArgument(i);
      sb.append(':');
      sb.append(a.getName().getString());
    }

    sb.append(']');

    block.setDetail(sb.toString());
    block.setId(new BlockId(block.getName()));
    completeSymbol(block, getCoordWithLength(coord));
    block.setSelectionRange(block.getRange());

    return result;
  }

  @Override
  protected ExpressionNode variableRead(final MethodGenerationContext mgenc,
      final SSymbol variableName, final long coord) {
    ExpressionNode result = super.variableRead(mgenc, variableName, coord);
    SourceSection sourceSection = SourceCoordinate.createSourceSection(source, coord);

    if (result instanceof LocalArgumentReadNode
        || result instanceof NonLocalArgumentReadNode) {
      if (variableName == SymbolTable.symSelf && sourceSection.getCharLength() == 0) {
        // skip, this is a synthetic self read, at the end of a block
      } else {
        recordTokenSemantics(sourceSection, SemanticTokenType.PARAMETER);
      }
      Argument arg;
      if (result instanceof LocalArgumentReadNode l) {
        arg = l.arg;
      } else {
        arg = ((NonLocalArgumentReadNode) result).arg;
      }
      // let's ignore self, like in Eclipse, where this is also ignored
      if (!arg.isSelf()) {
        referenceSymbol(new VariableId(arg), sourceSection);
      }
    } else if (result instanceof LocalVariableReadNode
        || result instanceof NonLocalVariableReadNode) {
      recordTokenSemantics(sourceSection, SemanticTokenType.VARIABLE);
      Local l;
      if (result instanceof LocalVariableReadNode) {
        l = ((LocalVariableReadNode) result).getLocal();
      } else {
        l = ((NonLocalVariableReadNode) result).getLocal();
      }
      referenceSymbol(new VariableId(l), sourceSection);
    } else if (result instanceof FieldReadNode r) {
      recordTokenSemantics(sourceSection, SemanticTokenType.PROPERTY);
      referenceSymbol(new FieldId((r).getFieldIndex()), sourceSection);
    } else if (result instanceof GlobalNode) {
      if (result instanceof NilGlobalNode || result instanceof TrueGlobalNode
          || result instanceof FalseGlobalNode) {
        recordTokenSemantics(sourceSection, SemanticTokenType.KEYWORD);
      } else if (variableName.getString().equals("system")) {
        recordTokenSemantics(sourceSection, SemanticTokenType.VARIABLE);
      } else {
        recordTokenSemantics(sourceSection, SemanticTokenType.CLASS);
        referenceSymbol(new GlobalId(variableName), sourceSection);
      }
    }
    return result;
  }

  @Override
  protected ExpressionNode variableWrite(final MethodGenerationContext mgenc,
      final SSymbol variableName, final ExpressionNode exp, final long coord)
      throws ParseError {
    ExpressionNode result = super.variableWrite(mgenc, variableName, exp, coord);
    SourceSection sourceSection = SourceCoordinate.createSourceSection(source, coord);

    if (result instanceof LocalArgumentWriteNode
        || result instanceof NonLocalArgumentWriteNode) {
      recordTokenSemantics(sourceSection, SemanticTokenType.PARAMETER);

      Argument arg;
      if (result instanceof LocalArgumentWriteNode w) {
        arg = w.arg;
      } else {
        arg = ((NonLocalArgumentWriteNode) result).arg;
      }

      assert !arg.isSelf();
      referenceSymbol(new VariableId(arg), sourceSection);
    } else if (result instanceof LocalVariableWriteNode
        || result instanceof NonLocalVariableWriteNode) {
      recordTokenSemantics(sourceSection, SemanticTokenType.VARIABLE);
      Local l;
      if (result instanceof LocalVariableWriteNode w) {
        l = w.getLocal();
      } else {
        l = ((NonLocalVariableWriteNode) result).getLocal();
      }
      referenceSymbol(new VariableId(l), sourceSection);
    } else if (result instanceof FieldWriteNode w) {
      recordTokenSemantics(sourceSection, SemanticTokenType.PROPERTY);
      referenceSymbol(new FieldId(w.getFieldIndex()), sourceSection);
    } else {
      throw new NotYetImplementedException(
          "odd, what am I missing here?: " + result.getClass().getName());
    }
    return result;
  }

  @Override
  protected SSymbol unarySelector() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.unarySelector();
    recordTokenSemantics(coord, result.getString(), SemanticTokenType.METHOD);
    return result;
  }

  @Override
  protected SSymbol unarySendSelector() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.unarySendSelector();

    recordTokenSemantics(coord, result.getString(), SemanticTokenType.METHOD);
    referenceSymbol(new SymbolId(result), coord, result.getString().length());
    return result;
  }

  @Override
  protected SSymbol binarySelector() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.binarySelector();
    recordTokenSemantics(coord, result.getString(), SemanticTokenType.METHOD);
    return result;
  }

  @Override
  protected SSymbol binarySendSelector() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.binarySendSelector();

    recordTokenSemantics(coord, result.getString(), SemanticTokenType.METHOD);
    referenceSymbol(new SymbolId(result), coord, result.getString().length());
    return result;
  }

  @Override
  protected ExpressionNode keywordMessage(final MethodGenerationContext mgenc,
      final ExpressionNode receiver) throws ProgramDefinitionError {
    int stackHeight = keywordParts.size();
    ExpressionNode result = super.keywordMessage(mgenc, receiver);
    int numParts = keywordParts.size() - stackHeight;

    assert numParts >= 1;
    int[] starts = new int[numParts];
    String[] parts = new String[numParts];

    StringBuilder kw = new StringBuilder();

    for (int i = numParts - 1; i >= 0; i--) {
      parts[i] = keywordParts.remove(keywordParts.size() - 1);
      starts[i] = keywordStart.remove(keywordStart.size() - 1);
    }

    for (String p : parts) {
      kw.append(p);
    }

    SSymbol msg = symbolFor(kw.toString());

    SymbolId call = new SymbolId(msg);

    for (int i = 0; i < numParts; i += 1) {
      referenceSymbol(call, starts[i], msg.getString().length());
    }
    return result;
  }

  @Override
  protected String keyword() throws ParseError {
    int coord = getStartIndex();
    String result = super.keyword();
    recordTokenSemantics(coord, result, SemanticTokenType.METHOD);
    keywordParts.add(result);
    keywordStart.add(coord);
    return result;
  }

  @Override
  protected Argument argument(final MethodGenerationContext mgenc)
      throws ProgramDefinitionError {
    Argument arg = super.argument(mgenc);
    recordTokenSemantics(arg.coord, SemanticTokenType.PARAMETER);
    recordSymbolDefinition(
        arg.getName().getString(), new VariableId(arg), SymbolKind.Variable, arg.coord, false);
    return arg;
  }

  @Override
  protected Local local(final MethodGenerationContext mgenc) throws ProgramDefinitionError {
    Local l = super.local(mgenc);

    recordTokenSemantics(l.coord, SemanticTokenType.VARIABLE);
    recordSymbolDefinition(
        l.getName().getString(), new VariableId(l), SymbolKind.Variable, l.coord, false);
    return l;
  }

  @Override
  protected Object literalInteger(final boolean isNegative) throws ParseError {
    int coord = getStartIndex();
    String t = text;
    if (isNegative) {
      t = "-" + t;
      coord -= 1; // TODO: this is kind a unsafe, but probably good enough
    }

    Object result = super.literalInteger(isNegative);

    recordTokenSemantics(coord, t, SemanticTokenType.NUMBER);

    return result;
  }

  @Override
  protected double literalDouble(final boolean isNegative) throws ParseError {
    int coord = getStartIndex();
    String t = text;
    if (isNegative) {
      t = "-" + t;
      coord -= 1; // TODO: this is kind a unsafe, but probably good enough
    }

    double result = super.literalDouble(isNegative);

    recordTokenSemantics(coord, t, SemanticTokenType.NUMBER);

    return result;
  }

  @Override
  protected SSymbol literalSymbol() throws ParseError {
    int coord = getStartIndex();

    SSymbol result = super.literalSymbol();
    recordTokenSemantics(coord, result.getString() + 1, SemanticTokenType.STRING);
    recordSymbolDefinition(result.getString(), new SymbolId(result), SymbolKind.Constant,
        coord);

    return result;
  }

  @Override
  protected String string() throws ParseError {
    int coord = getStartIndex();
    String s = super.string();
    recordTokenSemantics(coord, "'" + s + "'", SemanticTokenType.STRING);
    return s;
  }

  @Override
  protected Field instanceField(final ClassGenerationContext cgenc) throws ParseError {
    Field f = super.instanceField(cgenc);

    recordSymbolDefinition(f.getName().getString(), new FieldId(f),
        SymbolKind.Field, f.getSourceCoordinate(), true);
    recordTokenSemantics(f.getSourceCoordinate(), SemanticTokenType.PROPERTY);

    return f;
  }

  @Override
  protected Field classField(final ClassGenerationContext cgenc) throws ParseError {
    Field f = super.classField(cgenc);

    recordSymbolDefinition(f.getName().getString(), new FieldId(f),
        SymbolKind.Field, f.getSourceCoordinate(), true);
    recordTokenSemantics(f.getSourceCoordinate(), SemanticTokenType.PROPERTY);

    return f;
  }

  @Override
  public ExpressionNode method(final MethodGenerationContext mgenc)
      throws ProgramDefinitionError {
    int coord = getStartIndex();
    ExpressionNode result = super.method(mgenc);
    completeSymbol(currentMethod, getCoordWithLength(coord));

    currentMethod.setSignature(createSignature(mgenc));

    return result;
  }

  private SignatureInformation createSignature(final MethodGenerationContext mgenc) {
    SignatureInformation info = new SignatureInformation();
    info.setLabel(mgenc.getName());

    List<ParameterInformation> params = new ArrayList<>(mgenc.getNumberOfArguments() - 1);

    for (int i = 1; i < mgenc.getNumberOfArguments(); i += 1) {
      ParameterInformation p = new ParameterInformation();
      p.setLabel(mgenc.getArgument(i).getName().getString());
      params.add(p);
    }

    info.setParameters(params);

    return info;
  }

  @Override
  protected void unaryPattern(final MethodGenerationContext mgenc) throws ParseError {
    int coord = getStartIndex();
    currentMethod = startSymbol(SymbolKind.Method);
    super.unaryPattern(mgenc);

    currentMethod.setName(mgenc.getSignature().getString());
    currentMethod.setId(new SymbolId(mgenc.getSignature()));
    currentMethod.setSelectionRange(getRange(coord, mgenc.getSignature().getString()));

    currentMethod.setDetail(mgenc.getSignature().getString());
  }

  @Override
  protected void binaryPattern(final MethodGenerationContext mgenc)
      throws ProgramDefinitionError {
    int coord = getStartIndex();
    currentMethod = startSymbol(SymbolKind.Method);

    super.binaryPattern(mgenc);

    String name = mgenc.getSignature().getString();
    currentMethod.setName(name);
    currentMethod.setId(new SymbolId(mgenc.getSignature()));
    currentMethod.setSelectionRange(getRange(coord, name));

    currentMethod.setDetail(name + " " + mgenc.getArgument(1).getName().getString());
  }

  @Override
  protected void keywordPattern(final MethodGenerationContext mgenc)
      throws ProgramDefinitionError {
    assert keywordParts.size() == 0 : "We are not in any method, so, this is expected to be zero";
    assert keywordStart.size() == 0 : "We are not in any method, so, this is expected to be zero";
    assert keywordStart.size() == keywordParts.size();

    currentMethod = startSymbol(SymbolKind.Method);

    super.keywordPattern(mgenc);

    String name = mgenc.getSignature().getString();
    currentMethod.setName(name);
    currentMethod.setId(new SymbolId(mgenc.getSignature()));

    Position start = getStart(source, keywordStart.get(0));
    Position end = getEnd(source, keywordStart.get(keywordStart.size() - 1),
        keywordParts.get(keywordParts.size() - 1).length());
    currentMethod.setSelectionRange(new Range(start, end));

    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < keywordParts.size(); i += 1) {
      builder.append(keywordParts.get(i));
      builder.append(' ');
      builder.append(mgenc.getArgument(i + 1).getName().getString());
      if (i < keywordParts.size() - 1) {
        builder.append(' ');
      }
    }

    currentMethod.setDetail(builder.toString());

    keywordParts.clear();
    keywordStart.clear();
  }

  private void recordTokenSemantics(final int startCoord, final String token,
      final SemanticTokenType type) {
    int line = SourceCoordinate.getLine(source, startCoord);
    int column = SourceCoordinate.getColumn(source, startCoord);
    symbols.getSemanticTokens().addSemanticToken(line - 1, column - 1, token.length(), type,
        (SemanticTokenModifier[]) null);
  }

  private void recordTokenSemantics(final long coord,
      final SemanticTokenType type) {
    int line = SourceCoordinate.getLine(source, coord);
    int column = SourceCoordinate.getColumn(source, coord);
    int length = SourceCoordinate.getLength(coord);

    symbols.getSemanticTokens().addSemanticToken(line - 1, column - 1, length, type,
        (SemanticTokenModifier[]) null);
  }

  private void recordTokenSemantics(final SourceSection section,
      final SemanticTokenType type) {
    symbols.getSemanticTokens().addSemanticToken(section.getStartLine() - 1,
        section.getStartColumn() - 1,
        section.getCharLength(), type, (SemanticTokenModifier[]) null);
  }

  private LanguageElement startSymbol(final String name, final SymbolKind kind,
      final int startCoord, final LanguageElementId id) {
    return symbols.startSymbol(
        name, kind, id, toRange(source, startCoord, name.length()), true);
  }

  private Range getRange(final int startCoord, final String name) {
    return toRange(source, startCoord, name.length());
  }

  private LanguageElement startSymbol(final SymbolKind kind) {
    return symbols.startSymbol(kind, true);
  }

  private void completeSymbol(final LanguageElement symbol, final long coord) {
    SourceSection ss = SourceCoordinate.createSourceSection(source, coord);
    symbols.completeSymbol(symbol, toRange(ss));
  }

  private void recordSymbolDefinition(final String string, final LanguageElementId id,
      final SymbolKind kind, final int startCoord) {
    symbols.recordDefinition(string, id, kind, toRange(source, startCoord, string.length()));
  }

  private void recordSymbolDefinition(final String string, final LanguageElementId id,
      final SymbolKind kind, final long coordWithLength, final boolean listAsSymbol) {
    symbols.recordDefinition(string, id, kind, toRange(source, coordWithLength), false,
        listAsSymbol);
  }

  private void referenceSymbol(final LanguageElementId id, final SourceSection sourceSection) {
    symbols.referenceSymbol(id, toRange(sourceSection));
  }

  private void referenceSymbol(final LanguageElementId id, final int startCoord,
      final int length) {
    symbols.referenceSymbol(id, toRange(source, startCoord, length));
  }
}
