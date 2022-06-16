package som.langserv.som;

import som.langserv.structure.LanguageElementId;


public class BlockId extends LanguageElementId {

  private final String name;

  public BlockId(final String name) {
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
