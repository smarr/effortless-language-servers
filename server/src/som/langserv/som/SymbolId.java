package som.langserv.som;

import som.langserv.structure.LanguageElementId;
import trufflesom.vmobjects.SSymbol;


public class SymbolId extends LanguageElementId {
  private final SSymbol sym;

  public SymbolId(final SSymbol sym) {
    this.sym = sym;
  }

  @Override
  protected String getName() {
    return sym.getString();
  }

  @Override
  public int hashCode() {
    return sym.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SymbolId other = (SymbolId) obj;
    return sym == other.sym;
  }
}
