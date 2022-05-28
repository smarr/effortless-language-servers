package som.langserv.structure;

import org.eclipse.lsp4j.Range;


public interface WithRange {
  LanguageElementId getId();

  Range getRange();
}
