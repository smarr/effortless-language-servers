package som.langserv.lint;

import java.util.Collection;

import som.langserv.structure.DocumentStructures;


public interface WorkspaceLinter {
  void lint(Collection<DocumentStructures> structures);
}
