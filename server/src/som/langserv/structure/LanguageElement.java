package som.langserv.structure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentHighlightKind;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureInformation;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;


public final class LanguageElement extends DocumentSymbol implements WithRange {

  private LanguageElementId id;

  private List<Reference> containedReferences;

  protected List<LanguageElement> allChildren;

  private SignatureInformation signature;

  /**
   * If true, this element will be listed as a symbol on document and workspace symbol
   * requests.
   */
  private final boolean listAsSymbol;

  public LanguageElement(final String name, final SymbolKind kind,
      final LanguageElementId id, final Range identifierRange, final boolean listAsSymbol) {
    super();
    setName(name);
    setKind(kind);
    setSelectionRange(identifierRange);
    this.id = id;
    this.listAsSymbol = listAsSymbol;
  }

  public boolean matches(final String query) {
    return id.matches(query);
  }

  public LanguageElement(final SymbolKind kind, final boolean listAsSymbol) {
    super();
    setKind(kind);
    this.listAsSymbol = listAsSymbol;
  }

  @Override
  public LanguageElementId getId() {
    return id;
  }

  public void setId(final LanguageElementId id) {
    this.id = id;
  }

  public void addContained(final Reference ref) {
    if (containedReferences == null) {
      containedReferences = new ArrayList<>();
    }
    containedReferences.add(ref);
  }

  public List<Reference> getReferences() {
    return containedReferences;
  }

  public void addChild(final LanguageElement symbol) {
    if (symbol.listAsSymbol) {
      var children = getChildren();

      if (children == null) {
        children = new ArrayList<>();
        setChildren(children);
      }
      children.add(symbol);
    }

    if (allChildren == null) {
      allChildren = new ArrayList<>();
    }
    allChildren.add(symbol);
  }

  public List<LanguageElement> getAllChildren() {
    return allChildren;
  }

  public boolean hasId() {
    return id != null;
  }

  public SignatureInformation getSignature() {
    return signature;
  }

  public void setSignature(final SignatureInformation sig) {
    signature = sig;
  }

  public SymbolInformation createSymbolInfo(final String containerUri) {
    SymbolInformation info = new SymbolInformation();
    info.setKind(getKind());
    info.setName(getName());

    Location l = new Location();
    l.setRange(getRange());
    l.setUri(containerUri);
    info.setLocation(l);

    return info;
  }

  public LocationLink createLocationLink(final String containerUri, final Range origin) {
    LocationLink link = new LocationLink();

    link.setOriginSelectionRange(origin);
    link.setTargetRange(getRange());
    link.setTargetSelectionRange(getSelectionRange());
    link.setTargetUri(containerUri);

    return link;
  }

  public Location createLocation(final String containerUri, final Range origin) {
    Location loc = new Location();
    loc.setRange(getSelectionRange());
    loc.setUri(containerUri);
    return loc;
  }

  public DocumentHighlight createHighlight() {
    DocumentHighlight highlight = new DocumentHighlight();
    highlight.setRange(getSelectionRange());
    highlight.setKind(getHighlightkind());
    return highlight;
  }

  /** This is a definition, so, always the Text type. */
  public DocumentHighlightKind getHighlightkind() {
    return DocumentHighlightKind.Text;
  }

  public CompletionItem createCompletionItem(final String partialName) {
    CompletionItem item = new CompletionItem();
    item.setLabel(getName());
    item.setKind(getCompletionKind(getKind()));
    item.setDetail(getDetail());
    return item;
  }

  private static CompletionItemKind getCompletionKind(final SymbolKind kind) {
    switch (kind) {
      case File:
        return CompletionItemKind.File;
      case Module:
        return CompletionItemKind.Module;
      case Namespace:
        return CompletionItemKind.Module;
      case Package:
        return CompletionItemKind.Module;
      case Class:
        return CompletionItemKind.Class;
      case Method:
        return CompletionItemKind.Method;
      case Property:
        return CompletionItemKind.Property;
      case Field:
        return CompletionItemKind.Field;
      case Constructor:
        return CompletionItemKind.Constructor;
      case Enum:
        return CompletionItemKind.Enum;
      case Interface:
        return CompletionItemKind.Interface;
      case Function:
        return CompletionItemKind.Function;
      case Variable:
        return CompletionItemKind.Variable;
      case Constant:
        return CompletionItemKind.Constant;
      case String:
        return CompletionItemKind.Value;
      case Number:
        return CompletionItemKind.Value;
      case Boolean:
        return CompletionItemKind.Value;
      case Array:
        return CompletionItemKind.Value;
      case Object:
        return CompletionItemKind.Value;
      case Key:
        return CompletionItemKind.Keyword;
      case Null:
        return CompletionItemKind.Value;
      case EnumMember:
        return CompletionItemKind.EnumMember;
      case Struct:
        return CompletionItemKind.Struct;
      case Event:
        return CompletionItemKind.Event;
      case Operator:
        return CompletionItemKind.Operator;
      case TypeParameter:
        return CompletionItemKind.TypeParameter;
      default:
        throw new RuntimeException(
            "Missing case? kind: " + kind.getValue() + ", " + kind.toString());
    }
  }

}
