package som.langserv.som;

import java.util.Objects;

import som.langserv.structure.LanguageElementId;
import trufflesom.compiler.Field;


public class FieldId extends LanguageElementId {

  private final Field field;
  private final int   fieldIndex;

  public FieldId(final Field field) {
    this.field = field;
    this.fieldIndex = field.getIndex();
  }

  public FieldId(final int fieldIndex) {
    this.field = null;
    this.fieldIndex = fieldIndex;
  }

  @Override
  protected String getName() {
    return field.getName().getString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, fieldIndex);
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
    FieldId other = (FieldId) obj;
    return field == other.field && fieldIndex == other.fieldIndex;
  }
}
