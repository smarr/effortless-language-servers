package som.langserv.som;

import static trufflesom.compiler.Symbol.Primitive;

import java.util.ArrayDeque;
import java.util.Deque;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bdt.basic.ProgramDefinitionError;
import bdt.source.SourceCoordinate;
import som.langserv.SemanticTokenModifier;
import som.langserv.SemanticTokenType;
import trufflesom.compiler.ClassGenerationContext;
import trufflesom.compiler.Lexer;
//import som.langserv.SemanticTokenType;
import trufflesom.compiler.MethodGenerationContext;
import trufflesom.compiler.ParserAst;
import trufflesom.compiler.Symbol;
import trufflesom.interpreter.nodes.ExpressionNode;
import trufflesom.vmobjects.SSymbol;


/**
 * Extension of the normal SOM parser to record more structural information
 * that is useful for tooling.
 */
public class SomParser extends ParserAst {

  private SomStructures              structuralProbe;
  private final Deque<SourceSection> sourceSections;

  private boolean parsingLiteralSymbol;

  public SomParser(final String content, final Source source,
      final SomStructures structuralProbe) {
    super(content, source, structuralProbe);
    assert structuralProbe != null : "Needed for this extended parser.";
    this.structuralProbe = structuralProbe;
    sourceSections = new ArrayDeque<>();
  }

  @Override
  protected Lexer createLexer(final String content) {
    return new SomLexer(content);
  }

  @Override
  protected void className(final ClassGenerationContext cgenc, final int coord)
      throws ParseError {
    super.className(cgenc, coord);
    storePosition(coord, cgenc.getName().getString(), SemanticTokenType.CLASS);
  }

  @Override
  public ExpressionNode method(final MethodGenerationContext mgenc)
      throws ProgramDefinitionError {
    int coord = getStartIndex();
    var result = super.method(mgenc);
    storePosition(coord, mgenc.getSignature().getString(), SemanticTokenType.METHOD);
    return result;
  }

  @Override
  protected void primitiveBlock() throws ParseError {
    int coord = getStartIndex();
    storePosition(coord, Primitive.toString(), SemanticTokenType.KEYWORD);
    super.primitiveBlock();
  }

  @Override
  protected SSymbol selector() throws ParseError {
    var prevSym = sym;
    SSymbol sel = super.selector();
    if (prevSym != Symbol.Keyword && prevSym != Symbol.KeywordSequence) {
      // we don't override the keywordSelector to capture the source section
      // so, we only need to ignore the source section of unary and binary literal symbols
      // which is what selector() parses.
      sourceSections.removeLast();
    }

    return sel;
  }

  @Override
  protected ExpressionNode variableRead(final MethodGenerationContext mgenc,
      final SSymbol variableName, final long coord) {
    ExpressionNode result = super.variableRead(mgenc, variableName, coord);
    structuralProbe.reportCall(result, SourceCoordinate.createSourceSection(source, coord));
    return result;
  }

  @Override
  protected ExpressionNode unaryMessage(final MethodGenerationContext mgenc,
      final ExpressionNode receiver) throws ParseError {
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.unaryMessage(mgenc, receiver);
    SourceSection selector = sourceSections.getLast();
    // Can't access the ss from the result, because parent pointers are not set on nodes:
    // assert result.getSourceSection().getCharIndex() == selector.getCharIndex();
    structuralProbe.reportCall(result, sourceSections.removeLast());
    assert stackHeight == sourceSections.size();
    return result;
  }

  @Override
  protected ExpressionNode binaryMessage(final MethodGenerationContext mgenc,
      final ExpressionNode receiver) throws ProgramDefinitionError {
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.binaryMessage(mgenc, receiver);
    SourceSection selector = sourceSections.getLast();
    // Can't access the ss from the result, because parent pointers are not set on nodes:
    // assert result.getSourceSection().getCharIndex() == selector.getCharIndex();
    structuralProbe.reportCall(result, sourceSections.removeLast());
    assert stackHeight == sourceSections.size();
    return result;
  }

  @Override
  protected ExpressionNode keywordMessage(final MethodGenerationContext mgenc,
      final ExpressionNode receiver) throws ProgramDefinitionError {
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.keywordMessage(mgenc, receiver);
    int numParts = sourceSections.size() - stackHeight;

    assert numParts >= 1;
    SourceSection[] sections = new SourceSection[numParts];
    for (int i = numParts - 1; i >= 0; i--) {
      sections[i] = sourceSections.removeLast();
    }

    structuralProbe.reportCall(result, sections);
    assert stackHeight == sourceSections.size();
    return result;
  }

  @Override
  protected SSymbol literalSymbol() throws ParseError {
    int coord = getStartIndex();
    parsingLiteralSymbol = true;

    SSymbol result = super.literalSymbol();
    storePosition(coord, result.getString() + 1, SemanticTokenType.STRING);

    parsingLiteralSymbol = false;
    return result;
  }

  @Override
  protected SSymbol unarySelector() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.unarySelector();
    recordSourceSection(coord);
    if (!parsingLiteralSymbol) {
      storePosition(coord, result.getString(), SemanticTokenType.METHOD);
    }
    return result;
  }

  @Override
  protected SSymbol binarySelector() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.binarySelector();
    recordSourceSection(coord);
    return result;
  }

  @Override
  protected String keyword() throws ParseError {
    int coord = getStartIndex();
    String result = super.keyword();
    recordSourceSection(coord);
    storePosition(coord, result, SemanticTokenType.METHOD);
    return result;
  }

  @Override
  protected SSymbol field() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.field();
    storePosition(coord, result.getString(), SemanticTokenType.PROPERTY);
    return result;
  }

  @Override
  protected SSymbol argument() throws ProgramDefinitionError {
    int coord = getStartIndex();
    SSymbol s = super.argument();
    storePosition(coord, s.getString(), SemanticTokenType.PARAMETER);
    return s;
  }

  @Override
  protected SSymbol local() throws ProgramDefinitionError {
    int coord = getStartIndex();
    SSymbol s = variable();
    storePosition(coord, s.getString(), SemanticTokenType.VARIABLE);
    return s;
  }

  protected void recordSourceSection(final int coord) {
    sourceSections.addLast(
        SourceCoordinate.createSourceSection(source, getCoordWithLength(coord)));
  }

  @Override
  protected ExpressionNode assignation(final MethodGenerationContext mgenc)
      throws ProgramDefinitionError {
    ExpressionNode result = super.assignation(mgenc);
    structuralProbe.reportAssignment(result, sourceSections.removeLast());
    return result;
  }

  @Override
  protected SSymbol assignment() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.assignment();
    recordSourceSection(coord);
    return result;
  }

  @Override
  protected String string() throws ParseError {
    int coord = getStartIndex();
    String s = super.string();
    storePosition(coord, "'" + s + "'", SemanticTokenType.STRING);
    return s;
  }

  @Override
  protected void unaryPattern(final MethodGenerationContext mgenc) throws ParseError {
    super.unaryPattern(mgenc);
    sourceSections.removeLast();
  }

  @Override
  protected void binaryPattern(final MethodGenerationContext mgenc)
      throws ProgramDefinitionError {
    super.binaryPattern(mgenc);
    sourceSections.removeLast();
  }

  @Override
  protected void keywordPattern(final MethodGenerationContext mgenc)
      throws ProgramDefinitionError {
    super.keywordPattern(mgenc);

    // remove one less than number of arguments
    for (int i = 1; i < mgenc.getSignature().getNumberOfSignatureArguments(); i += 1) {
      sourceSections.removeLast();
    }
  }

  protected void storePosition(final int startCoord, final String token,
      final SemanticTokenType type) {
    storePosition(startCoord, token, type, (SemanticTokenModifier[]) null);
  }

  protected void storePosition(final int startCoord, final String token,
      final SemanticTokenType type, final SemanticTokenModifier... modifiers) {
    int line = SourceCoordinate.getLine(source, startCoord);
    int column = SourceCoordinate.getColumn(source, startCoord);
    structuralProbe.addTokenPosition(line, column, token.length(), type, modifiers);
  }

  protected void storeAllComments() {
    structuralProbe.addTokenPosition(((SomLexer) lexer).getCommentsPositions());
  }
}
