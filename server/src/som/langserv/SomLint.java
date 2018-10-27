package som.langserv;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;

import som.compiler.MixinDefinition;
import som.langserv.SomStructures.Call;


public class SomLint {
  private static final String LINT_NAME = "SOMns Lint";

  public static void checkModuleName(final String filepath, final MixinDefinition def,
      final List<Diagnostic> diagnostics) {
    File f = new File(filepath);
    String name = f.getName();
    checkFileEnding(name, diagnostics);

    checkFileNameMatchesModule(def, name, diagnostics);
  }

  private static void checkFileNameMatchesModule(final MixinDefinition def,
      final String fileName,
      final List<Diagnostic> diagnostics) {
    String moduleName = fileName;
    if (moduleName.contains(".")) {
      moduleName = moduleName.substring(0, moduleName.indexOf("."));
    }

    if (!moduleName.equals(def.getName().getString())) {

      diagnostics.add(new Diagnostic(
          Adapter.toRange(def.getNameSourceSection()),
          "Module name '" + def.getName().getString() + "' does not match file name '"
              + fileName
              + "'.",
          DiagnosticSeverity.Information, LINT_NAME));
    }
  }

  private static void checkFileEnding(final String name, final List<Diagnostic> diagnostics) {
    if (!name.endsWith(".ns")) { // TODO: generalise this
      diagnostics.add(new Diagnostic(new Range(Adapter.pos(1, 1), Adapter.pos(1, 1)),
          "File name does not use the .ns extension.", DiagnosticSeverity.Hint, LINT_NAME));
    }
  }

  public static void checkSends(final Map<String, SomStructures> structuralProbes,
      final SomStructures newProbe, final List<Diagnostic> diagnostics) {
    Collection<SomStructures> probes;
    synchronized (structuralProbes) {
      probes = new ArrayList<>(structuralProbes.values());
    }

    List<Call> calls = newProbe.getCalls();
    for (Call c : calls) {
      if (newProbe.defines(c.selector)) {
        continue;
      }

      boolean defined = false;
      for (SomStructures p : probes) {
        if (p.defines(c.selector)) {
          defined = true;
          break;
        }
      }

      if (!defined) {
        Range r = new Range(Adapter.pos(c.sections[0].getStartLine(), c.sections[0].getStartColumn()),
            Adapter.pos(c.sections[c.sections.length - 1].getEndLine(),
                c.sections[c.sections.length - 1].getEndColumn() + 1));
        diagnostics.add(new Diagnostic(r,
            "No " + c.selector.getString() + " defined. Might cause run time error.",
            DiagnosticSeverity.Warning, LINT_NAME));
      }
    }
  }
}
