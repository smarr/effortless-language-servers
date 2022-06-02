package som.langserv.lint;

import java.io.File;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DocumentSymbol;

import som.langserv.structure.DocumentStructures;


public class LintRootSymbolMatchesFileName implements FileLinter {

  @Override
  public void lint(final String filePath, final String text,
      final DocumentStructures structures) {
    File f = new File(filePath);
    String fileName = f.getName();
    String moduleName = fileName;
    if (moduleName.contains(".")) {
      moduleName = moduleName.substring(0, moduleName.indexOf("."));
    }

    var roots = structures.getRootSymbols();
    if (roots == null || roots.isEmpty()) {
      return;
    }

    DocumentSymbol root = roots.get(0);
    if (!root.getName().equals(moduleName)) {
      structures.addDiagnostic(new Diagnostic(
          root.getSelectionRange(),
          "Module name '" + root.getName() + "' does not match file name '"
              + fileName
              + "'.",
          DiagnosticSeverity.Information, "Lint Module matches file name"));
    }
  }
}
