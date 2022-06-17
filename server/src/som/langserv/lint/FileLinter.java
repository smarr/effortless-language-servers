package som.langserv.lint;

import som.langserv.structure.DocumentStructures;


public interface FileLinter {
  void lint(String filePath, String text, DocumentStructures structures);
}
