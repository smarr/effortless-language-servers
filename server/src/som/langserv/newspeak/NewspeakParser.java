package som.langserv.newspeak;

import java.util.ArrayDeque;
import java.util.Deque;

import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.basic.ProgramDefinitionError;
import som.compiler.MethodBuilder;
import som.compiler.Parser;
import som.interpreter.SomLanguage;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.literals.LiteralNode;
import som.langserv.SemanticTokenModifier;
import som.langserv.SemanticTokenType;
import som.vmobjects.SSymbol;
import tools.debugger.Tags.ArgumentTag;
import tools.debugger.Tags.CommentTag;
import tools.debugger.Tags.KeywordTag;
import tools.debugger.Tags.LiteralTag;
import tools.debugger.Tags.LocalVariableTag;


/**
 * Extension of the SOMns parser to record additional structural information
 * that is useful for tooling.
 */
public class NewspeakParser extends Parser {

  private NewspeakStructures         struturalProbe;
  private final Deque<SourceSection> sourceSections;

  public NewspeakParser(final String content, final Source source,
      final NewspeakStructures structuralProbe, final SomLanguage lang) throws ParseError {
    super(content, source, structuralProbe, lang);
    // assert structuralProbe != null : "Needed for this extended parser.";
    this.struturalProbe = structuralProbe;
    sourceSections = new ArrayDeque<>();
  }

  @Override
  protected String className() throws ParseError {
    int coord = getStartIndex();
    var name = super.className();
    recordTokenSemantics(coord, name, SemanticTokenType.CLASS);
    return name;
  }

  @Override
  protected boolean acceptIdentifier(final String identifier, final Class<? extends Tag> tag) {
    int coord = getStartIndex();
    boolean result = super.acceptIdentifier(identifier, tag);
    if (result) {
      if (tag == KeywordTag.class) {
        switch (identifier) {
          case "private":
          case "public":
          case "protected":
            recordTokenSemantics(coord, identifier, SemanticTokenType.MODIFIER);
            break;
          default:
            recordTokenSemantics(coord, identifier, SemanticTokenType.KEYWORD);
            break;
        }
      } else if (tag == LiteralTag.class) {
        switch (identifier) {
          case "true":
          case "false":
          case "nil":
          case "objL":
            recordTokenSemantics(coord, identifier, SemanticTokenType.KEYWORD);
        }
      }
    }
    return result;
  }

  @Override
  protected ExpressionNode implicitUnaryMessage(final MethodBuilder meth,
      final SSymbol selector, final SourceSection section) {
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

  protected void recordTokenSemantics(final int coords, final String length,
      final SemanticTokenType tokenType) {
    recordTokenSemantics(coords, length, tokenType, (SemanticTokenModifier[]) null);
  }

  protected void recordTokenSemantics(final int coords, final String length,
      final SemanticTokenType tokenType, final SemanticTokenModifier... modifiers) {
    struturalProbe.addSemanticToken(source.getLineNumber(coords),
        source.getColumnNumber(coords), length.length(), tokenType, modifiers);
  }

  protected void recordTokenSemantics(final SourceSection source,
      final SemanticTokenType tokenType) {
    struturalProbe.addSemanticToken(source.getStartLine(),
        source.getStartColumn(), source.getCharLength(), tokenType);
  }

  @Override
  protected SSymbol unarySelector() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.unarySelector();
    sourceSections.addLast(getSource(coord));
    recordTokenSemantics(coord, result.getString(), SemanticTokenType.METHOD);
    return result;
  }

  @Override
  protected SSymbol binarySelector() throws ParseError {
    int coord = getStartIndex();
    SSymbol result = super.binarySelector();
    recordTokenSemantics(coord, result.getString(), SemanticTokenType.METHOD);
    sourceSections.addLast(getSource(coord));
    return result;
  }

  @Override
  protected String keyword() throws ParseError {
    int coord = getStartIndex();
    String result = super.keyword();
    recordTokenSemantics(coord, result, SemanticTokenType.METHOD);
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
    int coord = getStartIndex();
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

  @Override
  protected String slotDecl() throws ParseError {
    int coord = getStartIndex();
    var slotName = super.slotDecl();

    recordTokenSemantics(coord, slotName, SemanticTokenType.PROPERTY);

    return slotName;
  }

  @Override
  protected String localDecl() throws ParseError {
    int coord = getStartIndex();

    var localName = super.localDecl();

    recordTokenSemantics(coord, localName, SemanticTokenType.VARIABLE);

    return localName;
  }

  @Override
  protected LiteralNode literalNumber() throws ParseError {
    int coord = getStartIndex();
    var result = super.literalNumber();

    SourceSection source = getSource(coord);
    recordTokenSemantics(source, SemanticTokenType.NUMBER);

    return result;
  }

  @Override
  protected LiteralNode literalSymbol() throws ParseError {
    var result = super.literalSymbol();

    recordTokenSemantics(result.getSourceSection(), SemanticTokenType.STRING);
    return result;
  }

  @Override
  protected LiteralNode literalString() throws ParseError {
    var result = super.literalString();

    recordTokenSemantics(result.getSourceSection(), SemanticTokenType.STRING);
    return result;
  }

  @Override
  protected LiteralNode literalChar() throws ParseError {
    var result = super.literalChar();

    recordTokenSemantics(result.getSourceSection(), SemanticTokenType.STRING);
    return result;
  }

  @Override
  protected void reportSyntaxElement(final Class<? extends Tag> type,
      final SourceSection source) {
    if (type == CommentTag.class) {
      recordTokenSemantics(source, SemanticTokenType.COMMENT);
    } else if (type == LocalVariableTag.class) {
      recordTokenSemantics(source, SemanticTokenType.VARIABLE);
    } else if (type == ArgumentTag.class) {
      recordTokenSemantics(source, SemanticTokenType.PARAMETER);
    }
  }
}
