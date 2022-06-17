package som.langserv.lint;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;

import som.langserv.som.PositionConversion;
import som.langserv.structure.DocumentStructures;


public class LintFileHasNSEnding implements FileLinter {

  @Override
  public void lint(final String filePath, final String text,
      final DocumentStructures structures) {
    if (!filePath.endsWith(".ns")) {
      structures.addDiagnostic(new Diagnostic(
          new Range(PositionConversion.pos(1, 1), PositionConversion.pos(1, 1)),
          "File name does not use the .ns extension.", DiagnosticSeverity.Hint,
          "Lint File Name"));
    }
  }
}
