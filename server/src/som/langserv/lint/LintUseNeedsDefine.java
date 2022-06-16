package som.langserv.lint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;

import som.langserv.structure.DocumentStructures;
import som.langserv.structure.LanguageElementId;


public class LintUseNeedsDefine implements WorkspaceLinter {

  @Override
  public void lint(final Collection<DocumentStructures> structures) {
    // collect everything that's defined
    Set<LanguageElementId> allDefinedElements = new HashSet<>();
    for (var s : structures) {
      var defs = s.getAllDefinitions();
      if (defs == null) {
        continue;
      }
      allDefinedElements.addAll(defs.keySet());
    }

    // now check everything that's referenced,
    // and report anything for which we do not have a definition
    for (var s : structures) {
      var allRefs = s.getAllReferences();
      if (allRefs == null) {
        continue;
      }

      for (var ref : allRefs.entrySet()) {
        LanguageElementId id = ref.getKey();
        if (!allDefinedElements.contains(id)) {
          for (var r : ref.getValue()) {
            Diagnostic diag = new Diagnostic();
            diag.setRange(r.getRange());
            diag.setMessage("`" + id.getName()
                + "` does not seem to be defined, which might cause run-time errors.");
            diag.setSeverity(DiagnosticSeverity.Warning);
            s.addDiagnostic(diag);
          }
        }
      }
    }
  }
}
