package som.langserv.simple;

import java.util.Objects;

import som.langserv.structure.LanguageElementId;


public class PropertyId extends LanguageElementId {

  private final String name;

  public PropertyId(final String name) {
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
    PropertyId other = (PropertyId) obj;
    return Objects.equals(name, other.name);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "PropertyId(" + name + ")";
  }
}
