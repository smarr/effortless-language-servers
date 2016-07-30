package som.langserv;

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import som.compiler.Lexer.SourceCoordinate;
import som.compiler.MethodBuilder;
import som.compiler.MixinBuilder.MixinDefinitionError;
import som.compiler.Parser;
import som.interpreter.nodes.ExpressionNode;
import som.vmobjects.SSymbol;


/**
 * Extension of the normal SOM parser to record more structural information
 * that is useful for tooling.
 */
public class SomParser extends Parser {

  private SomStructures struturalProbe;
  private final Deque<SourceSection> sourceSections;

  public SomParser(final Reader reader, final long fileSize, final Source source,
      final SomStructures structuralProbe) {
    super(reader, fileSize, source, structuralProbe);
//    assert structuralProbe != null : "Needed for this extended parser.";
    this.struturalProbe = structuralProbe;
    sourceSections = new ArrayDeque<>();
  }

  @Override
  protected ExpressionNode unaryMessage(final ExpressionNode receiver,
      final boolean eventualSend, final SourceSection sendOperator) throws ParseError {
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.unaryMessage(receiver, eventualSend, sendOperator);
    struturalProbe.reportCall(result, sourceSections.removeLast());
//    assert stackHeight == sourceSections.size();
    return result;
  }

  @Override
  protected ExpressionNode binaryMessage(final MethodBuilder builder,
      final ExpressionNode receiver, final boolean eventualSend,
      final SourceSection sendOperator) throws ParseError, MixinDefinitionError {
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.binaryMessage(
        builder, receiver, eventualSend, sendOperator);
    struturalProbe.reportCall(result, sourceSections.removeLast());
//    assert stackHeight == sourceSections.size();
    return result;
  }

  @Override
  protected ExpressionNode keywordMessage(final MethodBuilder builder,
      final ExpressionNode receiver, final boolean explicitRcvr,
      final boolean eventualSend, final SourceSection sendOperator) throws ParseError, MixinDefinitionError {
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.keywordMessage(
        builder, receiver, explicitRcvr, eventualSend, sendOperator);
    int numParts = sourceSections.size() - stackHeight;

    assert numParts >= 1;
    SourceSection[] sections = new SourceSection[numParts];
    for (int i = numParts - 1; i >= 0; i--) {
      sections[i] = sourceSections.removeLast();
    }

    struturalProbe.reportCall(result, sections);
//    assert stackHeight == sourceSections.size();
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
  protected ExpressionNode assignments(final MethodBuilder builder)
      throws ParseError, MixinDefinitionError {
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.assignments(builder);
    struturalProbe.reportAssignment(result, sourceSections.removeLast());
//    assert stackHeight == sourceSections.size();
    return result;
  }

  @Override
  protected SSymbol assignment() throws ParseError {
    SourceCoordinate coord = getCoordinate();
    SSymbol result = super.assignment();
    sourceSections.addLast(getSource(coord));
    return result;
  }
}
