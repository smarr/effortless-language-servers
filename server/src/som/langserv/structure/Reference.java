package som.langserv.structure;

import org.eclipse.lsp4j.Range;


public class Reference implements WithRange {

  public final LanguageElementId id;

  private final Range range;

  public Reference(final LanguageElementId id, final Range range) {
    this.id = id;
    this.range = range;
  }

  @Override
  public Range getRange() {
    return range;
  }

}
