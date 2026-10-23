package som.langserv.lint;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import som.langserv.structure.DocumentStructures;


public class LintEndsWithNewline implements FileLinter {

  @Override
  public void lint(final String filePath, final String text,
      final DocumentStructures structures) {
    if (text.isEmpty() || text.charAt(text.length() - 1) == '\n') {
      return;
    }

    Position end = endPosition(text);
    structures.addDiagnostic(new Diagnostic(
        new Range(new Position(end.getLine(), end.getCharacter() - 1), end),
        "File is expected to end on a new line.",
        DiagnosticSeverity.Information, "Lint Ends with Newline"));
  }

  /** The position after the last character. */
  private static Position endPosition(final String text) {
    int line = 0;
    int lineStart = 0;
    for (int i = 0; i < text.length(); i += 1) {
      if (text.charAt(i) == '\n') {
        line += 1;
        lineStart = i + 1;
      }
    }
    return new Position(line, text.length() - lineStart);
  }
}
