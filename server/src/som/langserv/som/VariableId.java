package som.langserv.som;

import som.langserv.structure.LanguageElementId;
import trufflesom.compiler.Variable;


public class VariableId extends LanguageElementId {

  private transient final Variable var;

  public VariableId(final Variable var) {
    this.var = var;
  }

  @Override
  public String getName() {
    return var.getName();
  }

  @Override
  public int hashCode() {
    return var.hashCode();
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
    VariableId other = (VariableId) obj;
    // Variable.equals() asserts that names are identical when the coords are,
    // but names are not interned anymore, and we compare variables across files
    return var.coord == other.var.coord && var.getName().equals(other.var.getName());
  }
}
