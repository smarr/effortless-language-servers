package som.langserv.newspeak;

import som.langserv.structure.LanguageElementId;


public class SlotId extends LanguageElementId {

  private final String name;

  public SlotId(final String name) {
    this.name = name;
  }

  @Override
  protected String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }

    if (obj.getClass() != getClass()) {
      return false;
    }
    return ((SlotId) obj).name.equals(name);
  }
}
