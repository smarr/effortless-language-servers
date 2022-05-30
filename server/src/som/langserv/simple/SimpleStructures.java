package som.langserv.simple;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpContext;
import org.eclipse.lsp4j.SymbolInformation;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.tools.structure.StructuralProbe;
import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Variable;
import som.interpreter.nodes.ExpressionNode;
import som.langserv.structure.DocumentData;
import som.langserv.structure.DocumentSymbols;
import som.langserv.structure.LanguageElementId;
import som.langserv.structure.Pair;
import som.langserv.structure.ParseContextKind;
import som.langserv.structure.SemanticTokenType;
import som.langserv.structure.SemanticTokens;
import som.vmobjects.SSymbol;


public class SimpleStructures
    extends StructuralProbe<String, MixinDefinition, DocumentSymbol, SlotDefinition, Variable>
    implements SemanticTokens, DocumentData {

  private final Source           source;
  private final ExpressionNode[] map;

  private final List<Diagnostic> diagnostics;

  private final List<int[]> semanticTokens;

  private final SimpleNodeFactory nodeFactory;

  private final DocumentSymbols symbols;

  public static class Call {
    final SSymbol         selector;
    final SourceSection[] sections;

    Call(final SSymbol selector, final SourceSection[] sections) {
      this.selector = selector;
      this.sections = sections;
    }
  }

  public SimpleStructures(final int length, final String remoteUri,
      final String normalizedUri) {
    this.source = null;

    this.map = new ExpressionNode[length];
    this.diagnostics = new ArrayList<>(0);
    this.semanticTokens = new ArrayList<>();
    this.symbols = new DocumentSymbols(remoteUri, normalizedUri);

    this.nodeFactory = new SimpleNodeFactory(this);
  }

  @Override
  public List<? extends DocumentSymbol> getRootSymbols() {
    return symbols.getRootSymbols();
  }

  @Override
  public void symbols(final List<SymbolInformation> results, final String query) {
    symbols.find(results, query);
  }

  public SimpleNodeFactory getFactory() {
    return nodeFactory;
  }

  public String getDocumentUri() {
    return source.getURI().toString();
  }

  public List<Diagnostic> getDiagnostics() {
    return diagnostics;
  }

  public synchronized ExpressionNode getElementAt(final int line, final int character) {
    int idx = source.getLineStartOffset(line) + character;
    return map[idx];
  }

  @Override
  public List<int[]> getSemanticTokens() {
    return semanticTokens;
  }

  public void addSemanticToken(final Token token, final SemanticTokenType type) {
    addSemanticToken(
        token.getLine(),
        // char position is 0-based, so, +1 to make it 1-based
        token.getCharPositionInLine() + 1,
        token.getText().length(),
        type);
  }

  public DocumentSymbols getSymbols() {
    return symbols;
  }

  @Override
  public Hover getHover(final Position position) {
    return symbols.getHover(position);
  }

  @Override
  public SignatureHelp getSignatureHelp(final Position position,
      final SignatureHelpContext context) {
    return symbols.getSignatureHelp(position, context);
  }

  @Override
  public Pair<LanguageElementId, Range> getElement(final Position pos) {
    return symbols.getElement(pos);
  }

  @Override
  public void lookupDefinitions(final Pair<LanguageElementId, Range> element,
      final List<LocationLink> definitions) {
    symbols.lookupDefinitions(element, definitions);
  }

  @Override
  public void lookupDefinitionsLocation(final Pair<LanguageElementId, Range> element,
      final List<Location> definitions) {
    symbols.lookupDefinitionsLocation(element, definitions);
  }

  @Override
  public void lookupReferences(final Pair<LanguageElementId, Range> element,
      final List<Location> references) {
    symbols.lookupReferences(element, references);
  }

  @Override
  public List<DocumentHighlight> getHighlight(final Position position) {
    return symbols.getHighlight(position);
  }

  @Override
  public Pair<ParseContextKind, String> getPossiblyIncompleteElement(final Position position) {
    return symbols.getPossiblyIncompleteElement(position);
  }

  @Override
  public void find(final String partialName, final Position position,
      final List<CompletionItem> results) {
    symbols.find(partialName, position, results);
  }
}
