package som.langserv.newspeak;

import som.langserv.structure.LanguageElementId;


public class LiteralId extends LanguageElementId {

  private final String name;

  public LiteralId(final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj;
  }
}
