package som.langserv.structure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureInformation;
import org.eclipse.lsp4j.SymbolKind;


public class LanguageElement extends DocumentSymbol implements WithRange {

  private LanguageElementId id;

  private List<Reference> containedReferences;

  private SignatureInformation signature;

  public LanguageElement(final String name, final SymbolKind kind,
      final LanguageElementId id, final Range identifierRange) {
    super();
    setName(name);
    setKind(kind);
    setSelectionRange(identifierRange);
    this.id = id;
  }

  public LanguageElement(final SymbolKind kind) {
    super();
    setKind(kind);
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
    var children = getChildren();

    if (children == null) {
      children = new ArrayList<>();
      setChildren(children);
    }
    children.add(symbol);
  }

  public boolean hasId() {
    return id != null;
  }

  public LanguageElementId getId() {
    return id;
  }

  public SignatureInformation getSignature() {
    return signature;
  }

  public void setSignature(final SignatureInformation sig) {
    signature = sig;
  }
}
