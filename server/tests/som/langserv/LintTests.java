package som.langserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static som.langserv.Helpers.assertRange;

import java.util.List;

import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.Test;

import som.langserv.lint.LintEndsWithNewline;
import som.langserv.som.SomAdapter;
import som.langserv.structure.DocumentStructures;


public class LintTests {

  private static DocumentStructures lintEndsWithNewline(final String text) {
    var structures = new DocumentStructures("file:/Hello.som", "/Hello.som");
    new LintEndsWithNewline().lint("/Hello.som", text, structures);
    return structures;
  }

  @Test
  public void testMissingNewlineIsReportedOnLastCharacter() {
    var structures = lintEndsWithNewline("Hello = (\n  run = ()\n)");

    var diags = structures.getDiagnostics();
    assertEquals(1, diags.size());
    assertRange(2, 0, 2, 1, diags.get(0).getRange());
  }

  @Test
  public void testFileEndingWithNewline() {
    assertNull(lintEndsWithNewline("Hello = ()\n").getDiagnostics());
  }

  @Test
  public void testEmptyFile() {
    assertNull(lintEndsWithNewline("").getDiagnostics());
  }

  @Test
  public void testQuickFixAddsNewline() {
    var diag = lintEndsWithNewline("Hello = (\n  run = ()\n)").getDiagnostics().get(0);

    var fixes = new LintEndsWithNewline().getQuickFixes("file:/Hello.som", diag);
    assertEquals(1, fixes.size());

    var fix = fixes.get(0);
    assertEquals(CodeActionKind.QuickFix, fix.getKind());
    assertTrue(fix.getIsPreferred());
    assertEquals(List.of(diag), fix.getDiagnostics());

    var edits = fix.getEdit().getChanges().get("file:/Hello.som");
    assertEquals(1, edits.size());
    assertEquals("\n", edits.get(0).getNewText());
    assertRange(2, 1, 2, 1, edits.get(0).getRange());
  }

  @Test
  public void testNoQuickFixForOtherDiagnostics() {
    var other = new Diagnostic(new Range(new Position(0, 0), new Position(0, 1)), "other");
    other.setSource("Parser");

    assertTrue(new LintEndsWithNewline().getQuickFixes("file:/Hello.som", other).isEmpty());
  }

  @Test
  public void testAdapterProvidesQuickFixesOfItsLinters() {
    var diag = lintEndsWithNewline("Hello = (\n  run = ()\n)").getDiagnostics().get(0);

    var actions = new SomAdapter().getCodeActions("file:/Hello.som", List.of(diag));
    assertEquals(1, actions.size());
    assertEquals("Add a newline at the end of the file", actions.get(0).getTitle());
  }
}
