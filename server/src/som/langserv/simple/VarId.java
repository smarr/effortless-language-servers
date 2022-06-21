package som.langserv.simple;

import java.util.Objects;

import som.langserv.structure.LanguageElement;
import som.langserv.structure.LanguageElementId;


public class VarId extends LanguageElementId {

  private final String          name;
  private final LanguageElement containingFn;

  public VarId(final String name, final LanguageElement fn) {
    this.name = name;
    this.containingFn = fn;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, containingFn);
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
    VarId other = (VarId) obj;
    if (!name.equals(other.name)) {
      return false;
    }
    return containingFn == other.containingFn;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "VarId(" + name + ")";
  }
}
