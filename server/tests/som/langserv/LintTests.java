package som.langserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static som.langserv.Helpers.assertRange;

import org.junit.Test;

import som.langserv.lint.LintEndsWithNewline;
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
}
