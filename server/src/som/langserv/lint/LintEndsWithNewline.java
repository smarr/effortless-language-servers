package som.langserv.lint;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;

import som.langserv.som.PositionConversion;
import som.langserv.structure.DocumentStructures;


public class LintEndsWithNewline implements FileLinter {

  @Override
  public void lint(final String filePath, final String text,
      final DocumentStructures structures) {
    int finalCharPos = text.length() - 1;

    if (text.charAt(finalCharPos) != '\n') {
      structures.addDiagnostic(new Diagnostic(
          new Range(PositionConversion.pos(finalCharPos - 1, finalCharPos - 1),
              PositionConversion.pos(finalCharPos, finalCharPos)),
          "File is expected to end on a new line.",
          DiagnosticSeverity.Information, "Lint Ends with Newline"));
    }
  }
}
