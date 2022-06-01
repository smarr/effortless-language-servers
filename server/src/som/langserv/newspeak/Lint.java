package som.langserv.newspeak;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;

import som.compiler.MixinDefinition;
import som.langserv.newspeak.NewspeakStructures.Call;
import som.langserv.som.PositionConversion;
import som.langserv.structure.DocumentStructures;


public class Lint {
  private static final String LINT_NAME = "SOMns Lint";

  public static void checkModuleName(final String filepath, final MixinDefinition def,
      final DocumentStructures structures) {
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
          PositionConversion.toRange(def.getNameSourceSection()),
          "Module name '" + def.getName().getString() + "' does not match file name '"
              + fileName
              + "'.",
          DiagnosticSeverity.Information, LINT_NAME));
    }
  }

  private static void checkFileEnding(final String name, final List<Diagnostic> diagnostics) {
    if (!name.endsWith(".ns")) { // TODO: generalise this
      diagnostics.add(new Diagnostic(
          new Range(PositionConversion.pos(1, 1), PositionConversion.pos(1, 1)),
          "File name does not use the .ns extension.", DiagnosticSeverity.Hint, LINT_NAME));
    }
  }

  public static void checkLastChar(final String text, final DocumentStructures structures) {
    int finalCharPos = text.length() - 1;
    if (text.charAt(finalCharPos) != '\n') {

      diagnostics.add(new Diagnostic(
          new Range(PositionConversion.pos(finalCharPos - 1, finalCharPos - 1),
              PositionConversion.pos(finalCharPos, finalCharPos)),
          "You must end on a new line.",
          DiagnosticSeverity.Information, LINT_NAME));
    }

  }

  public static void checkSends(final Map<String, NewspeakStructures> structuralProbes,
      final NewspeakStructures newProbe, final List<Diagnostic> diagnostics) {
    Collection<NewspeakStructures> probes;
    synchronized (structuralProbes) {
      probes = new ArrayList<>(structuralProbes.values());
    }

    List<Call> calls = newProbe.getCalls();
    for (Call c : calls) {
      if (newProbe.defines(c.selector)) {
        continue;
      }

      boolean defined = false;
      for (NewspeakStructures p : probes) {
        if (p.defines(c.selector)) {
          defined = true;
          break;
        }
      }

      if (!defined) {
        Range r = new Range(
            PositionConversion.pos(c.sections[0].getStartLine(),
                c.sections[0].getStartColumn()),
            PositionConversion.pos(c.sections[c.sections.length - 1].getEndLine(),
                c.sections[c.sections.length - 1].getEndColumn() + 1));
        diagnostics.add(new Diagnostic(r,
            "No " + c.selector.getString() + " defined. Might cause run time error.",
            DiagnosticSeverity.Warning, LINT_NAME));
      }
    }
  }
}
