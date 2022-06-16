package som.langserv.simple;

import java.util.Objects;

import som.langserv.structure.LanguageElementId;


public class VarId extends LanguageElementId {

  private final String name;

  public VarId(final String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
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
    return Objects.equals(name, other.name);
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
