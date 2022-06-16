package som.langserv.newspeak;

import static som.langserv.som.PositionConversion.getEnd;
import static som.langserv.som.PositionConversion.getStart;
import static som.langserv.som.PositionConversion.toRange;
import static som.vm.Symbols.symbolFor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.ParameterInformation;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureInformation;
import org.eclipse.lsp4j.SymbolKind;

import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.basic.ProgramDefinitionError;
import som.compiler.AccessModifier;
import som.compiler.MethodBuilder;
import som.compiler.MixinBuilder;
import som.compiler.Parser;
import som.compiler.Variable.Argument;
import som.compiler.Variable.Local;
import som.interpreter.SomLanguage;
import som.interpreter.nodes.ArgumentReadNode.LocalArgumentReadNode;
import som.interpreter.nodes.ArgumentReadNode.NonLocalArgumentReadNode;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.LocalVariableNode.LocalVariableReadNode;
import som.interpreter.nodes.NonLocalVariableNode.NonLocalVariableReadNode;
import som.interpreter.nodes.literals.LiteralNode;
import som.langserv.som.BlockId;
import som.langserv.structure.DocumentStructures;
import som.langserv.structure.LanguageElement;
import som.langserv.structure.LanguageElementId;
import som.langserv.structure.SemanticTokenModifier;
import som.langserv.structure.SemanticTokenType;
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

  private final DocumentStructures symbols;

  private final ArrayDeque<LanguageElement> currentClass;

  private LanguageElement currentMethod;

  private final ArrayList<Integer> keywordStart;
  private final ArrayList<String>  keywordParts;

  public NewspeakParser(final String content, final Source source,
      final NewspeakStructures structuralProbe, final SomLanguage lang) throws ParseError {
    super(content, source, structuralProbe, lang);
    this.symbols = structuralProbe.getSymbols();

    currentClass = new ArrayDeque<>();

    keywordStart = new ArrayList<>();
    keywordParts = new ArrayList<>();
  }

  @Override
  protected String className() throws ParseError {
    int coord = getStartIndex();
    var name = super.className();
    recordTokenSemantics(coord, name, SemanticTokenType.CLASS);

    LanguageElement clazz =
        startSymbol(name, SymbolKind.Class, coord, new SymbolId(symbolFor(name)));
    currentClass.push(clazz);
    clazz.setDetail(name);

    return name;
  }

  @Override
  protected void mixinApplication(final MixinBuilder mxnBuilder, final int mixinId)
      throws ProgramDefinitionError {
    // in this case, classBody() isn't called,
    // so, we need to complete the possible primary factory method
    completePrimaryFactoryMethod(mxnBuilder);

    super.mixinApplication(mxnBuilder, mixinId);
  }

  @Override
  protected void classBody(final MixinBuilder mxnBuilder) throws ProgramDefinitionError {
    int coord = getStartIndex();
    try {
      super.classBody(mxnBuilder);
    } finally {
      LanguageElement clazz = currentClass.pop();

      Range range = toRange(getSource(coord));

      symbols.completeSymbol(clazz,
          new Range(clazz.getSelectionRange().getStart(), range.getEnd()));
    }
  }

  @Override
  protected void classHeader(final MixinBuilder mxnBuilder) throws ProgramDefinitionError {
    try {
      super.classHeader(mxnBuilder);
    } finally {
      completePrimaryFactoryMethod(mxnBuilder);
    }
  }

  private void completePrimaryFactoryMethod(final MixinBuilder mxnBuilder) {
    if (currentMethod != null) {
      SourceSection ss = mxnBuilder.getInitializerSource();
      if (ss == null) {
        ss = getSource(getStartIndex());
      }
      Range selection = currentMethod.getSelectionRange();
      Range init = toRange(ss);

      symbols.completeSymbol(currentMethod, new Range(selection.getStart(), init.getEnd()));
      currentMethod = null;
    }
  }

  @Override
  protected void classSideDecl(final MixinBuilder mxnBuilder)
      throws ProgramDefinitionError {
    int coord = getStartIndex();

    LanguageElement clazz = symbols.startSymbol(SymbolKind.Class, true);
    clazz.setName("class");
    currentClass.push(clazz);

    try {
      super.classSideDecl(mxnBuilder);
    } finally {
      currentClass.pop();

      clazz.setId(new SymbolId(symbolFor("class")));
      clazz.setDetail(mxnBuilder.getName() + " class");

      symbols.completeSymbol(clazz, toRange(getSource(coord)));
      clazz.setSelectionRange(clazz.getRange());
    }
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
  protected ExpressionNode keywordMessage(final MethodBuilder builder,
      final ExpressionNode receiver, final boolean explicitRcvr,
      final boolean eventualSend, final SourceSection sendOperator)
      throws ProgramDefinitionError {
    int stackHeight = keywordParts.size();
    ExpressionNode result = super.keywordMessage(
        builder, receiver, explicitRcvr, eventualSend, sendOperator);
    int numParts = keywordParts.size() - stackHeight;

    assert numParts >= 1;
    int[] starts = new int[numParts];

    StringBuilder kw = new StringBuilder();

    for (int i = numParts - 1; i >= 0; i--) {
      kw.append(keywordParts.remove(keywordParts.size() - 1));
      starts[i] = keywordStart.remove(keywordStart.size() - 1);
    }

    SSymbol msg = symbolFor(kw.toString());

    SymbolId call = new SymbolId(msg);

    for (int i = 0; i < numParts; i += 1) {
      referenceSymbol(call, starts[i], msg.getString().length());
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
  protected ExpressionNode implicitUnaryMessage(final MethodBuilder meth,
      final SSymbol selector, final SourceSection section) {
    ExpressionNode result = super.implicitUnaryMessage(meth, selector, section);

    if (result instanceof LocalArgumentReadNode
        || result instanceof NonLocalArgumentReadNode) {
      recordTokenSemantics(section, SemanticTokenType.PARAMETER);

      Argument arg;
      if (result instanceof LocalArgumentReadNode l) {
        arg = l.getArg();
      } else {
        arg = ((NonLocalArgumentReadNode) result).getArg();
      }
      referenceSymbol(new VariableId(arg), section);
    } else if (result instanceof LocalVariableReadNode
        || result instanceof NonLocalVariableReadNode) {
      recordTokenSemantics(section, SemanticTokenType.VARIABLE);
      Local local;
      if (result instanceof LocalVariableReadNode l) {
        local = l.getLocal();
      } else {
        local = ((NonLocalVariableReadNode) result).getLocal();
      }
      referenceSymbol(new VariableId(local), section);
    } else {
      recordTokenSemantics(section, SemanticTokenType.METHOD);
      referenceSymbol(new SymbolId(selector), section);
    }
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
  protected String keyword() throws ParseError {
    int coord = getStartIndex();
    String result = super.keyword();
    recordTokenSemantics(coord, result, SemanticTokenType.METHOD);
    keywordParts.add(result);
    keywordStart.add(coord);
    return result;
  }

  @Override
  protected Argument argument(final MethodBuilder builder) throws ParseError {
    Argument arg = super.argument(builder);
    LanguageElement a = recordSymbolDefinition(
        arg.name.getString(), new VariableId(arg), SymbolKind.Variable, arg.source, false, 0);
    a.setDetail(arg.name.getString());
    return arg;
  }

  @Override
  protected void unaryPattern(final MethodBuilder builder) throws ParseError {
    assert currentMethod == null;

    int coord = getStartIndex();
    currentMethod = symbols.startSymbol(SymbolKind.Method, true);

    super.unaryPattern(builder);

    currentMethod.setName(builder.getSignature().getString());
    currentMethod.setId(new SymbolId(builder.getSignature()));
    currentMethod.setSelectionRange(getRange(coord, builder.getSignature().getString()));

    currentMethod.setDetail(builder.getSignature().getString());
  }

  @Override
  protected void binaryPattern(final MethodBuilder builder) throws ParseError {
    assert currentMethod == null;

    int coord = getStartIndex();
    currentMethod = symbols.startSymbol(SymbolKind.Method, true);

    super.binaryPattern(builder);

    String name = builder.getSignature().getString();
    currentMethod.setName(name);
    currentMethod.setId(new SymbolId(builder.getSignature()));
    currentMethod.setSelectionRange(getRange(coord, name));

    currentMethod.setDetail(name + " " + builder.getArgument(1).name.getString());
  }

  @Override
  protected void keywordPattern(final MethodBuilder builder) throws ParseError {
    assert currentMethod == null;
    assert keywordParts.size() == 0 : "We are not in any method, so, this is expected to be zero";
    assert keywordStart.size() == 0 : "We are not in any method, so, this is expected to be zero";
    assert keywordStart.size() == keywordParts.size();

    currentMethod = symbols.startSymbol(SymbolKind.Method, true);

    super.keywordPattern(builder);

    String name = builder.getSignature().getString();
    currentMethod.setName(name);
    currentMethod.setId(new SymbolId(builder.getSignature()));

    Position start = getStart(source, keywordStart.get(0));
    Position end = getEnd(source, keywordStart.get(keywordStart.size() - 1),
        keywordParts.get(keywordParts.size() - 1).length());
    currentMethod.setSelectionRange(new Range(start, end));

    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < keywordParts.size(); i += 1) {
      sb.append(keywordParts.get(i));
      sb.append(' ');
      sb.append(builder.getArgument(i + 1).name.getString());
      if (i < keywordParts.size() - 1) {
        sb.append(' ');
      }
    }

    currentMethod.setDetail(sb.toString());

    keywordParts.clear();
    keywordStart.clear();
  }

  @Override
  protected MethodBuilder methodDeclaration(final AccessModifier accessModifier,
      final int coord, final MixinBuilder mxnBuilder)
      throws ProgramDefinitionError {

    MethodBuilder builder = super.methodDeclaration(accessModifier, coord, mxnBuilder);

    symbols.completeSymbol(currentMethod, toRange(getSource(coord)));
    currentMethod.setSignature(createSignature(builder));
    currentMethod = null;

    return builder;
  }

  private SignatureInformation createSignature(final MethodBuilder mbuilder) {
    SignatureInformation info = new SignatureInformation();
    info.setLabel(mbuilder.getFullName());

    List<ParameterInformation> params = new ArrayList<>(mbuilder.getNumberOfArguments() - 1);

    for (int i = 1; i < mbuilder.getNumberOfArguments(); i += 1) {
      ParameterInformation p = new ParameterInformation();
      p.setLabel(mbuilder.getArgument(i).name.getString());
      params.add(p);
    }

    info.setParameters(params);

    return info;
  }

  @Override
  protected String slotDecl() throws ParseError {
    int coord = getStartIndex();
    var slotName = super.slotDecl();

    recordTokenSemantics(coord, slotName, SemanticTokenType.PROPERTY);
    int inOuterScope = currentMethod == null ? 0 : 1;
    recordSymbolDefinition(slotName,
        new SymbolId(symbolFor(slotName)), SymbolKind.Property, getSource(coord), true,
        inOuterScope);

    return slotName;
  }

  @Override
  protected Local localDefinition(final MethodBuilder builder,
      final List<ExpressionNode> expressions) throws ProgramDefinitionError {
    Local local = super.localDefinition(builder, expressions);
    if (local != null) {
      recordSymbolDefinition(local.name.getString(), new VariableId(local),
          SymbolKind.Variable, local.source, false, 0);
    }
    return local;
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
  protected ExpressionNode literalObject(final MethodBuilder builder)
      throws ProgramDefinitionError {
    LanguageElement currentM = currentMethod;
    currentMethod = null;

    int coord = getStartIndex();
    LanguageElement clazz =
        startSymbol("objL", SymbolKind.Class, coord, new LiteralId("objL"));
    currentClass.push(clazz);
    clazz.setDetail("objL");

    ExpressionNode node = super.literalObject(builder);

    currentMethod = currentM;

    return node;
  }

  @Override
  public ExpressionNode nestedBlock(final MethodBuilder mbuilder)
      throws ProgramDefinitionError {
    int coord = getStartIndex();
    LanguageElement block = symbols.startSymbol(SymbolKind.Function, true);
    ExpressionNode result = super.nestedBlock(mbuilder);

    block.setName(mbuilder.getSignature().getString());

    StringBuilder sb = new StringBuilder();
    sb.append('[');

    for (int i = 1; i < mbuilder.getNumberOfArguments(); i += 1) {
      if (i > 1) {
        sb.append(' ');
      }

      var a = mbuilder.getArgument(i);
      sb.append(':');
      sb.append(a.name.getString());
    }

    sb.append(']');

    block.setDetail(sb.toString());
    block.setId(new BlockId(block.getName()));
    symbols.completeSymbol(block, toRange(getSource(coord)));
    block.setSelectionRange(block.getRange());

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

  protected void recordTokenSemantics(final int coords, final String length,
      final SemanticTokenType tokenType) {
    recordTokenSemantics(coords, length, tokenType, (SemanticTokenModifier[]) null);
  }

  protected void recordTokenSemantics(final int coords, final String length,
      final SemanticTokenType tokenType, final SemanticTokenModifier... modifiers) {
    symbols.getSemanticTokens().addSemanticToken(source.getLineNumber(coords) - 1,
        source.getColumnNumber(coords) - 1, length.length(), tokenType, modifiers);
  }

  protected void recordTokenSemantics(final SourceSection source,
      final SemanticTokenType tokenType) {
    symbols.getSemanticTokens().addSemanticToken(source.getStartLine() - 1,
        source.getStartColumn() - 1, source.getCharLength(), tokenType);
  }

  private LanguageElement startSymbol(final String name, final SymbolKind kind,
      final int startCoord, final LanguageElementId id) {
    return symbols.startSymbol(
        name, kind, id, toRange(source, startCoord, name.length()), true);
  }

  private void referenceSymbol(final LanguageElementId id, final int startCoord,
      final int length) {
    symbols.referenceSymbol(id, toRange(source, startCoord, length));
  }

  private void referenceSymbol(final LanguageElementId id, final SourceSection ss) {
    symbols.referenceSymbol(id, toRange(ss));
  }

  private Range getRange(final int startCoord, final String name) {
    return toRange(source, startCoord, name.length());
  }

  private LanguageElement recordSymbolDefinition(final String string,
      final LanguageElementId id,
      final SymbolKind kind, final SourceSection ss, final boolean listAsSymbol,
      final int inOuterScope) {
    return symbols.recordDefinition(string, id, kind, toRange(ss), false,
        listAsSymbol, inOuterScope);
  }
}
