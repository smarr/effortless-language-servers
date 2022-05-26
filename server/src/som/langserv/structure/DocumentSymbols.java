package som.langserv.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;


public class DocumentSymbols {

  private final ArrayList<LanguageElement> symbolsScope;

  private final ArrayList<LanguageElement> rootSymbols;

  private ArrayList<Reference> rootReference;

  private Map<LanguageElementId, Set<LanguageElement>> symbols;

  public DocumentSymbols() {
    this.symbolsScope = new ArrayList<>();
    this.rootSymbols = new ArrayList<>();
  }

  /**
   * Start a new symbol, which is implicitly nested in the last one that was
   * started.
   */
  public LanguageElement startSymbol(final String name, final SymbolKind kind,
      final LanguageElementId id, final Range identifierRange) {
    LanguageElement symbol = new LanguageElement(name, kind, id, identifierRange);
    addToScopes(symbol);

    symbolsScope.add(symbol);

    return symbol;
  }

  public LanguageElement startSymbol(final SymbolKind kind) {
    LanguageElement symbol = new LanguageElement(kind);
    addToScopes(symbol);

    symbolsScope.add(symbol);

    return symbol;
  }

  private void addToScopes(final LanguageElement symbol) {
    if (symbolsScope.isEmpty()) {
      rootSymbols.add(symbol);
    } else {
      LanguageElement current = symbolsScope.get(symbolsScope.size() - 1);
      current.addChild(symbol);
    }
  }

  public void completeSymbol(final LanguageElement symbol, final Range fullRange) {
    symbol.setRange(fullRange);
    int lastSymbolIdx = symbolsScope.size() - 1;

    assert symbolsScope.get(lastSymbolIdx) == symbol;
    symbolsScope.remove(lastSymbolIdx);

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

  public void recordDefinition(final String name, final LanguageElementId id,
      final SymbolKind kind, final Range range) {
    LanguageElement symbol = new LanguageElement(name, kind, id, range);
    addToScopes(symbol);

    assert symbol.hasId();
    recordForLookup(symbol);
  }

  /**
   * Reference a symbol, i.e., a language element, based on it's identity
   * criterion.
   *
   * @param id something that a {@code LanguageElementId} would match on
   * @param range the code range were the reference is in the file
   */
  public void referenceSymbol(final LanguageElementId id, final Range range) {
    Reference ref = new Reference(id, range);
    if (!symbolsScope.isEmpty()) {
      LanguageElement current = symbolsScope.get(symbolsScope.size() - 1);
      current.addContained(ref);
    } else {
      if (rootReference == null) {
        rootReference = new ArrayList<>();
        rootReference.add(ref);
      }
    }
  }

  /** Not sure this is really needed. */
  public void referenceSymbol(final LanguageElement name, final Range range) {
    throw new RuntimeException("Not yet implemented");
  }

  public List<? extends DocumentSymbol> getRootSymbols() {
    return rootSymbols;
  }

  public Hover getHover(final Position position) {
    WithRange symbol = getMostPrecise(position, rootSymbols);
    if (symbol == null) {
      return null;
    }

    if (symbol instanceof LanguageElement) {
      LanguageElement e = (LanguageElement) symbol;
      Hover hover = new Hover();
      hover.setRange(e.getSelectionRange());
      MarkupContent content = new MarkupContent("plaintext", e.getDetail());
      hover.setContents(content);

      return hover;
    } else if (symbol instanceof Reference) {
      Reference ref = (Reference) symbol;
      var similar = lookup(ref);
      if (similar == null) {
        return null;
      }

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
    } else {
      throw new RuntimeException("Not yet implemented for " + symbol.getClass());
    }
  }

  private Set<LanguageElement> lookup(final Reference ref) {
    if (symbols == null) {
      return null;
    }
    return symbols.get(ref.id);
  }

  @SuppressWarnings("unchecked")
  private WithRange getMostPrecise(final Position pos,
      final List<? extends WithRange> es) {
    for (var e : es) {
      if (isIn(pos, e)) {
        if (e instanceof LanguageElement) {
          var le = (LanguageElement) e;
          @SuppressWarnings("rawtypes")
          List children = le.getChildren();
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

  private boolean isIn(final Position pos, final WithRange e) {
    Range r = e.getRange();

    if (r.getStart().getLine() < pos.getLine() && pos.getLine() < r.getEnd().getLine()) {
      // strictly within a multiline range
      return true;
    }

    if (pos.getLine() < r.getStart().getLine() || r.getEnd().getLine() < pos.getLine()) {
      // strictly outside
      return false;
    }

    // now we know it's on the relevant lines, but not yet whether within the characters
    if (pos.getLine() == r.getStart().getLine()
        && pos.getCharacter() < r.getStart().getCharacter()) {
      return false;
    }

    if (pos.getLine() == r.getEnd().getLine()
        && r.getEnd().getCharacter() < pos.getCharacter()) {
      return false;
    }

    // so, we are not before the range, and not after it
    return true;
  }
}
