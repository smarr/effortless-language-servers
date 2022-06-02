package som.langserv.lint;

import java.util.Map;

import som.langserv.structure.DocumentStructures;


public interface WorkspaceLinter {
  void lint(Map<String, DocumentStructures> structures);
}
