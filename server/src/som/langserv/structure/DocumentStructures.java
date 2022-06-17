package som.langserv.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpContext;
import org.eclipse.lsp4j.SignatureInformation;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;

import util.ArrayListIgnoreIfLastIdentical;


public class DocumentStructures {

  private List<Diagnostic> diagnostics;

  private final ArrayList<LanguageElement> symbolsScope;

  private final ArrayList<LanguageElement> rootSymbols;

  private ArrayList<Reference> rootReference;

  private Map<LanguageElementId, Set<LanguageElement>> symbols;
  private Map<LanguageElementId, List<Reference>>      allReferences;

  private final String remoteUri;
  private final String normalizedUri;

  private ArrayList<LanguageElement> afterNavigationSymbols;

  private final SemanticTokens semanticTokens;

  public DocumentStructures(final String remoteUri, final String normalizedUri) {
    this.symbolsScope = new ArrayList<>();
    this.rootSymbols = new ArrayList<>();

    this.remoteUri = remoteUri;
    this.normalizedUri = normalizedUri;
    this.semanticTokens = new SemanticTokens();
  }

  public Map<LanguageElementId, Set<LanguageElement>> getAllDefinitions() {
    return symbols;
  }

  public Map<LanguageElementId, List<Reference>> getAllReferences() {
    return allReferences;
  }

  public SemanticTokens getSemanticTokens() {
    return semanticTokens;
  }

  public List<Diagnostic> getDiagnostics() {
    return diagnostics;
  }

  public Diagnostic getFirstErrorOrNull() {
    if (diagnostics == null) {
      return null;
    }

    for (Diagnostic d : diagnostics) {
      if (d.getSeverity() == DiagnosticSeverity.Error) {
        return d;
      }
    }
    return null;
  }

  public void addDiagnostic(final Diagnostic diag) {
    if (diagnostics == null) {
      diagnostics = new ArrayList<>(1);
    }

    diagnostics.add(diag);
  }

  /**
   * Start a new symbol, which is implicitly nested in the last one that was
   * started.
   */
  public LanguageElement startSymbol(final String name, final SymbolKind kind,
      final LanguageElementId id, final Range identifierRange, final boolean listAsSymbol) {
    LanguageElement symbol =
        new LanguageElement(name, kind, id, identifierRange, listAsSymbol);
    addToScopes(symbol, 0);

    symbolsScope.add(symbol);

    return symbol;
  }

  public LanguageElement startSymbol(final SymbolKind kind, final boolean listAsSymbol) {
    LanguageElement symbol = new LanguageElement(kind, listAsSymbol);
    addToScopes(symbol, 0);

    symbolsScope.add(symbol);

    return symbol;
  }

  private void addToScopes(final LanguageElement symbol, final int inOuterScope) {
    if (symbolsScope.isEmpty()) {
      assert inOuterScope == 0;
      rootSymbols.add(symbol);
    } else {
      LanguageElement current = symbolsScope.get(symbolsScope.size() - 1 - inOuterScope);
      current.addChild(symbol);
    }
  }

  public void completeSymbol(final LanguageElement symbol, final Range fullRange) {
    symbol.setRange(fullRange);

    // we may complete a symbol, as part of a finally handler,
    // i.e., when unwinding the stack with an error
    // in this case, we remove everything up to the closed element
    // TODO: do we need to try to complete the other bits? we probably need a range on them...
    assert symbolsScope.contains(symbol);
    boolean removed = false;
    while (!removed) {
      int lastSymbolIdx = symbolsScope.size() - 1;
      var last = symbolsScope.get(lastSymbolIdx);
      symbolsScope.remove(lastSymbolIdx);
      if (last == symbol) {
        removed = true;
      }
    }

    assert symbol.hasId();
    recordForLookup(symbol);
  }

  private void recordForLookup(final LanguageElement symbol) {
    if (symbols == null) {
      symbols = new HashMap<>();
    }

    var similar = symbols.get(symbol.getId());
    if (similar == null) {
      similar = new HashSet<>();
      symbols.put(symbol.getId(), similar);
    }

    similar.add(symbol);
  }

  private void recordForAfterNavigation(final LanguageElement symbol) {
    if (afterNavigationSymbols == null) {
      afterNavigationSymbols = new ArrayList<>();
    }

    afterNavigationSymbols.add(symbol);
  }

  public void recordDefinition(final String name, final LanguageElementId id,
      final SymbolKind kind, final Range range) {
    recordDefinition(name, id, kind, range, false, false);
  }

  public LanguageElement recordDefinition(final String name, final LanguageElementId id,
      final SymbolKind kind, final Range range, final boolean afterNavigation,
      final boolean listAsSymbol) {
    return recordDefinition(name, id, kind, range, afterNavigation, listAsSymbol, 0);
  }

  public LanguageElement recordDefinition(final String name, final LanguageElementId id,
      final SymbolKind kind, final Range range, final boolean afterNavigation,
      final boolean listAsSymbol, final int inOuterScope) {
    assert range != null;
    LanguageElement symbol = new LanguageElement(name, kind, id, range, listAsSymbol);
    symbol.setRange(range);
    addToScopes(symbol, inOuterScope);

    assert symbol.hasId();
    recordForLookup(symbol);

    if (afterNavigation) {
      recordForAfterNavigation(symbol);
    }

    return symbol;
  }

  /**
   * Reference a symbol, i.e., a language element, based on it's identity
   * criterion.
   *
   * @param id something that a {@code LanguageElementId} would match on
   * @param range the code range were the reference is in the file
   */
  public Reference referenceSymbol(final LanguageElementId id, final Range range) {
    Reference ref = new Reference(id, range);
    if (!symbolsScope.isEmpty()) {
      LanguageElement current = symbolsScope.get(symbolsScope.size() - 1);
      current.addContained(ref);
    } else {
      if (rootReference == null) {
        rootReference = new ArrayList<>();
      }
      rootReference.add(ref);
    }

    if (allReferences == null) {
      allReferences = new HashMap<>();
    }

    List<Reference> list = allReferences.get(ref.id);
    if (list == null) {
      list = new ArrayList<>(3);
      allReferences.put(ref.id, list);
    }
    list.add(ref);
    return ref;
  }

  /** Not sure this is really needed. */
  public void referenceSymbol(final LanguageElement name, final Range range) {
    throw new RuntimeException("Not yet implemented");
  }

  public List<LanguageElement> getRootSymbols() {
    return rootSymbols;
  }

  public Hover getHover(final Position position) {
    WithRange symbol = getMostPrecise(position, rootSymbols);
    if (symbol == null) {
      return null;
    }

    if (symbol instanceof LanguageElement e) {
      return createHover(e);
    } else if (symbol instanceof Reference ref) {
      var similar = lookup(ref);
      if (similar == null) {
        return null;
      }
      return createHover(ref, similar);
    } else {
      throw new RuntimeException("Not yet implemented for " + symbol.getClass());
    }
  }

  private static Hover createHover(final LanguageElement e) {
    assert e.getDetail() != null;
    Hover hover = new Hover();
    hover.setRange(e.getSelectionRange());
    MarkupContent content = new MarkupContent("plaintext", e.getDetail());
    hover.setContents(content);

    return hover;
  }

  private static Hover createHover(final Reference ref, final Set<LanguageElement> similar) {
    Hover hover = new Hover();
    hover.setRange(ref.getRange());

    StringBuilder sb = new StringBuilder();

    for (LanguageElement i : similar) {
      sb.append(i.getDetail());
      sb.append('\n');
    }

    MarkupContent content = new MarkupContent("plaintext", sb.toString());
    hover.setContents(content);

    return hover;
  }

  public SignatureHelp getSignatureHelp(final Position position,
      final SignatureHelpContext context) {
    WithRange symbol = getMostPrecise(position, rootSymbols);
    if (symbol == null) {
      return null;
    }

    if (symbol instanceof LanguageElement e) {
      if (e.getSignature() == null) {
        return null;
      }

      SignatureHelp help = new SignatureHelp();
      var sigs = new ArrayList<SignatureInformation>(1);
      sigs.add(e.getSignature());
      help.setSignatures(sigs);
      return help;
    } else if (symbol instanceof Reference ref) {
      var similar = lookup(ref);
      if (similar == null) {
        return null;
      }

      SignatureHelp help = new SignatureHelp();
      var sigs = new ArrayList<SignatureInformation>(similar.size());

      for (LanguageElement e : similar) {
        var sig = e.getSignature();
        if (sig != null) {
          sigs.add(sig);
        }
      }

      if (sigs.isEmpty()) {
        return null;
      }

      help.setSignatures(sigs);
      return help;
    } else {
      throw new RuntimeException("Not yet implemented for " + symbol.getClass());
    }
  }

  public List<DocumentHighlight> getHighlight(final Position position) {
    WithRange symbol = getMostPrecise(position, rootSymbols);
    if (symbol == null) {
      return null;
    }

    if (symbol instanceof LanguageElement e) {
      List<DocumentHighlight> result = new ArrayListIgnoreIfLastIdentical<>();
      result.add(e.createHighlight());
      addAllReferences(e.getId(), result);
      return result;
    } else if (symbol instanceof Reference ref) {
      var similar = lookup(ref);
      if (similar == null) {
        return null;
      }

      var result = new ArrayListIgnoreIfLastIdentical<DocumentHighlight>(similar.size());

      for (LanguageElement e : similar) {
        result.add(e.createHighlight());
      }

      addAllReferences(symbol.getId(), result);

      if (result.isEmpty()) {
        return null;
      }

      return result;
    } else {
      throw new RuntimeException("Not yet implemented for " + symbol.getClass());
    }
  }

  private void addAllReferences(final LanguageElementId id,
      final List<DocumentHighlight> result) {
    if (allReferences == null) {
      return;
    }
    List<Reference> list = allReferences.get(id);
    if (list != null) {
      for (var r : list) {
        result.add(r.createHighlight());
      }
    }
  }

  private Set<LanguageElement> lookup(final Reference ref) {
    if (symbols == null) {
      return null;
    }
    return symbols.get(ref.id);
  }

  private WithRange getMostPrecise(final Position pos,
      final List<? extends WithRange> es) {
    if (es == null) {
      return null;
    }

    for (var e : es) {
      if (isIn(pos, e)) {
        if (e instanceof LanguageElement le) {
          List<LanguageElement> children = le.getAllChildren();
          if (children != null) {
            var child = getMostPrecise(pos, children);
            if (child != null) {
              return child;
            }
          }

          var refs = le.getReferences();
          if (refs != null) {
            for (var ref : refs) {
              if (isIn(pos, ref)) {
                return ref;
              }
            }
          }
        }
        return e;
      }
    }

    return null;
  }

  private static boolean isIn(final Position pos, final WithRange e) {
    Range r = e.getRange();
    assert r != null;

    return isIn(pos, r);
  }

  private static boolean isIn(final Position pos, final Range range) {
    if (range.getStart().getLine() < pos.getLine()
        && pos.getLine() < range.getEnd().getLine()) {
      // strictly within a multiline range
      return true;
    }

    if (pos.getLine() < range.getStart().getLine()
        || range.getEnd().getLine() < pos.getLine()) {
      // strictly outside
      return false;
    }

    // now we know it's on the relevant lines, but not yet whether within the characters
    if (pos.getLine() == range.getStart().getLine()
        && pos.getCharacter() < range.getStart().getCharacter()) {
      return false;
    }

    if (pos.getLine() == range.getEnd().getLine()
        && range.getEnd().getCharacter() < pos.getCharacter()) {
      return false;
    }

    // so, we are not before the range, and not after it
    return true;
  }

  public void find(final List<SymbolInformation> results, final String query) {
    if (symbols == null) {
      return;
    }

    for (var e : symbols.entrySet()) {
      if (e.getKey().matches(query)) {
        for (var s : e.getValue()) {
          String uri = getUri();
          results.add(s.createSymbolInfo(uri));
        }
      }
    }
  }

  private void findIn(final String partialName, final Position pos,
      final List<LanguageElement> es, final List<CompletionItem> results) {
    if (es == null) {
      return;
    }

    for (var e : es) {
      if (isIn(pos, e)) {
        List<LanguageElement> children = e.getAllChildren();
        findIn(partialName, pos, children, results);
      }

      if (e.matches(partialName)) {
        results.add(e.createCompletionItem(partialName));
      }
    }
  }

  public void find(final String partialName, final ParseContextKind context,
      final Position position, final List<CompletionItem> results) {
    if (context == ParseContextKind.Primary) {
      findIn(partialName, position, rootSymbols, results);
    } else if (context == ParseContextKind.Navigation && afterNavigationSymbols != null) {
      for (var e : afterNavigationSymbols) {
        if (e.matches(partialName)) {
          results.add(e.createCompletionItem(partialName));
        }
      }
    }
  }

  public String getUri() {
    return (remoteUri != null) ? remoteUri : normalizedUri;
  }

  public Pair<LanguageElementId, Range> getElement(final Position pos) {
    WithRange symbol = getMostPrecise(pos, rootSymbols);
    if (symbol == null) {
      return null;
    }

    if (symbol instanceof LanguageElement e) {
      return new Pair<>(e.getId(), e.getSelectionRange());
    } else if (symbol instanceof Reference ref) {
      return new Pair<>(ref.id, ref.getRange());
    } else {
      throw new RuntimeException("Not yet implemented for " + symbol.getClass());
    }
  }

  public void lookupDefinitions(final Pair<LanguageElementId, Range> element,
      final List<LocationLink> definitions) {
    if (symbols == null) {
      return;
    }

    var defs = symbols.get(element.v1);

    if (defs == null) {
      return;
    }

    for (var d : defs) {
      definitions.add(d.createLocationLink(getUri(), element.v2));
    }
  }

  public void lookupDefinitionsLocation(final Pair<LanguageElementId, Range> element,
      final List<Location> definitions) {
    if (symbols == null) {
      return;
    }

    var defs = symbols.get(element.v1);

    if (defs == null) {
      return;
    }

    for (var d : defs) {
      definitions.add(d.createLocation(getUri(), element.v2));
    }
  }

  public void lookupReferences(final Pair<LanguageElementId, Range> element,
      final List<Location> references) {
    if (allReferences == null) {
      return;
    }

    var refs = allReferences.get(element.v1);
    if (refs == null) {
      return;
    }

    for (var r : refs) {
      references.add(r.createLocation(getUri(), element.v2));
    }
  }

  public Pair<ParseContextKind, String> getPossiblyIncompleteElement(final Position position) {
    var e = getMostPrecise(position, rootSymbols);
    if (e == null) {
      return null;
    }

    if (e instanceof LanguageElement le && !isIn(position, le.getSelectionRange())) {
      boolean isNavigation = false;
      Range navRange = null;
      if (diagnostics != null) {
        for (Diagnostic d : diagnostics) {
          if (d.getData() == Boolean.TRUE) {
            isNavigation = true;
            navRange = d.getRange();
          }
        }
      }

      if (isNavigation && isIn(position, navRange)) {
        return new Pair<>(ParseContextKind.Navigation, "");
      }
    }

    return new Pair<>(ParseContextKind.Primary, e.getName());
  }

  @Override
  public String toString() {
    return "DocumentSymbols(" + getUri() + ")";
  }
}
