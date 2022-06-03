package som.langserv.lint;

import java.util.Collection;

import som.langserv.structure.DocumentStructures;


public class LintUseNeedsDefine implements WorkspaceLinter {

  @Override
  public void lint(final Collection<DocumentStructures> structures) {
    // iterate over all references, and make sure there's a matching define
    boolean defined = false;
    if (!defined) {
    }
  }
}
