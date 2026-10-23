package som.langserv.lint;

import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;

import som.langserv.structure.DocumentStructures;


public class LintEndsWithNewline implements FileLinter {

  private static final String SOURCE = "Lint Ends with Newline";

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
        DiagnosticSeverity.Information, SOURCE));
  }

  @Override
  public List<CodeAction> getQuickFixes(final String documentUri,
      final Diagnostic diagnostic) {
    if (!SOURCE.equals(diagnostic.getSource())) {
      return List.of();
    }

    Position end = diagnostic.getRange().getEnd();
    TextEdit edit = new TextEdit(new Range(end, end), "\n");

    CodeAction fix = new CodeAction("Add a newline at the end of the file");
    fix.setKind(CodeActionKind.QuickFix);
    fix.setDiagnostics(List.of(diagnostic));
    fix.setIsPreferred(true);
    fix.setEdit(new WorkspaceEdit(Map.of(documentUri, List.of(edit))));
    return List.of(fix);
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
