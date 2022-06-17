package som.langserv.structure;

public abstract class LanguageElementId {

  public boolean matches(final String query) {
    String name = getName();
    return fuzzyMatch(name, query);
  }

  private static boolean fuzzyMatch(final String name, final String query) {
    if (query == null) {
      return true;
    }

    String nameLow = name.toLowerCase();
    String queryLow = query.toLowerCase();

    // trivial case
    if (queryLow.equals(nameLow)) {
      return true;
    }

    // simple prefix
    if (nameLow.startsWith(queryLow)) {
      return true;
    }

    // TODO: camel case matching etc...
    return false;
  }

  /**
   * Used to match against queries for symbols.
   */
  public abstract String getName();

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + getName() + ")";
  }
}
