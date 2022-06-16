package som.langserv.newspeak;

import som.compiler.Variable;
import som.langserv.structure.LanguageElementId;


public class VariableId extends LanguageElementId {

  private transient final Variable var;

  public VariableId(final Variable var) {
    this.var = var;
  }

  @Override
  protected String getName() {
    return var.name.getString();
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
    return var.equals(other.var);
  }
}