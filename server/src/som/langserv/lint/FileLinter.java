package som.langserv.lint;

import java.util.List;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;

import som.langserv.structure.DocumentStructures;


public interface FileLinter {
  void lint(String filePath, String text, DocumentStructures structures);

  /** Quick fixes for a diagnostic, if it was reported by this linter. */
  default List<CodeAction> getQuickFixes(final String documentUri,
      final Diagnostic diagnostic) {
    return List.of();
  }
}
