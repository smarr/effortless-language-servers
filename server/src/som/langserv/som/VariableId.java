package som.langserv.som;

import java.util.Objects;

import som.langserv.structure.LanguageElementId;
import trufflesom.compiler.Variable;


public class VariableId extends LanguageElementId {

  private final Variable var;

  public VariableId(final Variable var) {
    this.var = var;
  }

  @Override
  protected String getName() {
    return var.getName().getString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(var);
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
    return Objects.equals(var, other.var);
  }
}
