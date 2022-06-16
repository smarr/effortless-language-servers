package som.langserv.som;

import java.util.Objects;

import som.langserv.structure.LanguageElementId;
import trufflesom.vmobjects.SSymbol;


public class GlobalId extends LanguageElementId {
  private final SSymbol name;

  public GlobalId(final SSymbol name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name.getString();
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
    GlobalId other = (GlobalId) obj;
    return name == other.name;
  }
}
