package som.langserv.som;

import java.util.ArrayDeque;
import java.util.Deque;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.basic.ProgramDefinitionError;
import bd.source.SourceCoordinate;
//import som.langserv.SemanticTokenType;
import trufflesom.compiler.MethodGenerationContext;
import trufflesom.compiler.ParserAst;
import trufflesom.interpreter.nodes.ExpressionNode;
import trufflesom.vm.Universe;
import trufflesom.vmobjects.SSymbol;


/**
 * Extension of the normal SOM parser to record more structural information
 * that is useful for tooling.
 */
public class SomParser extends ParserAst {

  private SomStructures              structuralProbe;
  private final Deque<SourceSection> sourceSections;

  public SomParser(final String content, final Source source,
      final SomStructures structuralProbe, final Universe universe) {
    super(content, source, structuralProbe, universe);
    assert structuralProbe != null : "Needed for this extended parser.";
    this.structuralProbe = structuralProbe;
    sourceSections = new ArrayDeque<>();

  }

  @Override
  protected SSymbol selector() throws ParseError {
    SSymbol sel = super.selector();
    if (sel.getNumberOfSignatureArguments() <= 2) {
      // in this case, we called binarySelector() or unarySelector() and put a
      // source section on the stack that we need to remove
      sourceSections.removeLast();
    }
    return sel;
  }

  @Override
  protected ExpressionNode variableRead(final MethodGenerationContext mgenc,
      final SSymbol variableName, final SourceSection source) {
    ExpressionNode result = super.variableRead(mgenc, variableName, source);
    structuralProbe.reportCall(result, source);
    return result;
  }

  @Override
  protected ExpressionNode unaryMessage(final MethodGenerationContext mgenc,
      final ExpressionNode receiver) throws ParseError {
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.unaryMessage(mgenc, receiver);
    SourceSection selector = sourceSections.getLast();
    assert result.getSourceSection().getCharIndex() == selector.getCharIndex();
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
    assert result.getSourceSection().getCharIndex() == selector.getCharIndex();
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
  protected SSymbol unarySelector() throws ParseError {
    SourceCoordinate coord = getCoordinate();
    SSymbol result = super.unarySelector();
    sourceSections.addLast(getSource(coord));
    return result;
  }

  @Override
  protected SSymbol binarySelector() throws ParseError {
    SourceCoordinate coord = getCoordinate();
    SSymbol result = super.binarySelector();
    sourceSections.addLast(getSource(coord));

    return result;
  }

  @Override
  protected String keyword() throws ParseError {
    SourceCoordinate coord = getCoordinate();
    String result = super.keyword();
    sourceSections.addLast(getSource(coord));

    return result;
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
    SourceCoordinate coord = getCoordinate();
    SSymbol result = super.assignment();
    sourceSections.addLast(getSource(coord));
    return result;
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

  @Override
  protected void storePosition(final SourceCoordinate coords, final String className,
      final int tokenTypevalue) {
    structuralProbe.addTokenPosition(coords.startLine, coords.startColumn,
        className.length(), tokenTypevalue, 0);
  }

  protected void storeAllComments() {
    // why dose this work out of order?
    // i check all tokens are in the right order later on and makre sure they are
    structuralProbe.addTokenPosition(this.lexer.getCommentsPositions());

  }

}
