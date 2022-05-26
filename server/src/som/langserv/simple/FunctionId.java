package som.langserv.simple;

import java.util.Objects;

import som.langserv.structure.LanguageElementId;


public class FunctionId extends LanguageElementId {

  private final String name;

  public FunctionId(final String name) {
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
    FunctionId other = (FunctionId) obj;
    return Objects.equals(name, other.name);
  }
}
