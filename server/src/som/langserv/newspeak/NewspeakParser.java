package som.langserv.newspeak;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.basic.ProgramDefinitionError;
import bd.source.SourceCoordinate;
import som.compiler.MethodBuilder;
import som.compiler.Parser;
import som.interpreter.SomLanguage;
import som.interpreter.nodes.ExpressionNode;
import som.vmobjects.SSymbol;


/**
 * Extension of the SOMns parser to record additional structural information
 * that is useful for tooling.
 */
public class NewspeakParser extends Parser {

  private NewspeakStructures         struturalProbe;
  private final Deque<SourceSection> sourceSections;
  private List<Integer>              listOfVarsStartsLines;
  private List<Integer>              listOfVarsStartsCol;

  public NewspeakParser(final String content, final long fileSize, final Source source,
      final NewspeakStructures structuralProbe, final SomLanguage lang) throws ParseError {
    super(content, fileSize, source, structuralProbe, lang);
    // assert structuralProbe != null : "Needed for this extended parser.";
    this.struturalProbe = structuralProbe;
    sourceSections = new ArrayDeque<>();
    listOfVarsStartsLines = new ArrayList();
    listOfVarsStartsCol = new ArrayList();
  }

  @Override
  protected ExpressionNode implicitUnaryMessage(final MethodBuilder meth,
      final SSymbol selector, final SourceSection section) {
    SourceCoordinate coord = getCoordinate();
    ExpressionNode result = super.implicitUnaryMessage(meth, selector, section);

    SourceSection s = sourceSections.getLast();
    assert result.getSourceSection().getCharIndex() == s.getCharIndex();
    struturalProbe.reportCall(result, sourceSections.removeLast());
    return result;
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
  protected ExpressionNode unaryMessage(final ExpressionNode receiver,
      final boolean eventualSend, final SourceSection sendOperator) throws ParseError {
    @SuppressWarnings("unused")
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.unaryMessage(receiver, eventualSend, sendOperator);
    SourceSection selector = sourceSections.getLast();
    assert result.getSourceSection().getCharIndex() == selector.getCharIndex();
    struturalProbe.reportCall(result, sourceSections.removeLast());
    // assert stackHeight == sourceSections.size();
    return result;
  }

  @Override
  protected ExpressionNode binaryMessage(final MethodBuilder builder,
      final ExpressionNode receiver, final boolean eventualSend,
      final SourceSection sendOperator) throws ProgramDefinitionError {
    @SuppressWarnings("unused")
    int stackHeight = sourceSections.size();
    ExpressionNode result = super.binaryMessage(
        builder, receiver, eventualSend, sendOperator);
    SourceSection selector = sourceSections.getLast();
    assert result.getSourceSection().getCharIndex() == selector.getCharIndex();
    struturalProbe.reportCall(result, sourceSections.removeLast());
    // assert stackHeight == sourceSections.size();
    return result;
  }

  @Override
  protected ExpressionNode keywordMessage(final MethodBuilder builder,
      final ExpressionNode receiver, final boolean explicitRcvr,
      final boolean eventualSend, final SourceSection sendOperator)
      throws ProgramDefinitionError {
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
    // assert stackHeight == sourceSections.size();
    return result;
  }

  @Override
  protected void storePosition(final SourceCoordinate coords, final String length,
      final int tokenTypevalue) {
    struturalProbe.addTokenPosition(coords.startLine,
        coords.startColumn, length.length(), tokenTypevalue, 0);
  }

  @Override
  protected void storeCommentPosition(final SourceCoordinate startCoords,
      final SourceCoordinate endCoords,
      final String commentLength) {
    int amountLines = endCoords.startLine - startCoords.startLine;
    if (amountLines != 0) {
      int count = 0;
      while (count + startCoords.startLine < endCoords.startLine) {
        if (startCoords.startColumn == 0) {
          struturalProbe.addTokenPosition(startCoords.startLine + count,
              startCoords.startColumn + 1,
              200, 5,
              0);
        } else {
          struturalProbe.addTokenPosition(startCoords.startLine + count,
              startCoords.startColumn,
              200, 5,
              0);
        }
        count++;
      }
    } else {
      struturalProbe.addTokenPosition(startCoords.startLine, startCoords.startColumn,
          commentLength.length(), 5,
          0);
    }
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
  protected ExpressionNode setterSends(final MethodBuilder builder)
      throws ProgramDefinitionError {
    ExpressionNode result = super.setterSends(builder);
    struturalProbe.reportAssignment(result, sourceSections.removeLast());
    return result;
  }

  @Override
  protected String setterKeyword() throws ParseError {
    SourceCoordinate coord = getCoordinate();
    String result = super.setterKeyword();
    sourceSections.addLast(getSource(coord));
    return result;
  }

  @Override
  protected void unaryPattern(final MethodBuilder builder) throws ParseError {
    super.unaryPattern(builder);
    sourceSections.removeLast();
  }

  @Override
  protected void binaryPattern(final MethodBuilder builder) throws ParseError {
    super.binaryPattern(builder);
    sourceSections.removeLast();
  }

  @Override
  protected void keywordPattern(final MethodBuilder builder) throws ParseError {
    super.keywordPattern(builder);

    // remove one less than number of arguments
    for (int i = 1; i < builder.getSignature().getNumberOfSignatureArguments(); i += 1) {
      sourceSections.removeLast();
    }
  }
}
