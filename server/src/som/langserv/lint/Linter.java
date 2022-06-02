package som.langserv.lint;

import som.langserv.structure.DocumentStructures;


public interface Linter {
  void lint(String filePath, String text, DocumentStructures structures);
}
