package som.langserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static som.langserv.Helpers.assertToken;
import static som.langserv.Helpers.printAllToken;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.junit.Test;

import som.langserv.newspeak.NewspeakAdapter;
import som.langserv.structure.SemanticTokenType;


public class NewspeakTests {
  @Test
  public void testLoadFile() throws IOException, URISyntaxException {
    var adapter = new NewspeakAdapter();
    var symbols =
        adapter.loadFile(new File(NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns"));

    assertNull(symbols.getDiagnostics());
  }

  @Test
  public void testLoadingNewspeakWorkspace() throws URISyntaxException, InterruptedException {
    var adapter = new NewspeakAdapter();
    var client = new TestLanguageClient();

    adapter.connect(client);
    Thread thread = (Thread) adapter.loadWorkspace("file:" + NewspeakAdapter.CORE_LIB_PATH);
    thread.join();

    int warnings = 0;
    int errors = 0;
    int others = 0;

    for (PublishDiagnosticsParams dp : client.diagnostics) {
      for (Diagnostic d : dp.getDiagnostics()) {
        if (d.getSeverity() == DiagnosticSeverity.Error) {
          errors += 1;
        } else if (d.getSeverity() == DiagnosticSeverity.Warning) {
          warnings += 1;
        } else {
          others += 1;
        }
      }
    }

    // There are currently various known warnings, errors, and other things
    // Some of the errors are old SOM files not adapted to Newspeak
    // Carefully fix these numbers when needed, they may be brittle
    assertEquals(48, warnings);
    assertEquals(11, errors);
    assertEquals(4, others);
  }

  @Test
  public void testSmallHelloFile() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    adapter.parse("class Debug usingPlatform: platform = Value ()(\n"
        + "  public main: args = ()\n"
        + ") )\n", path);

    List<int[]> tokenTuples =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokenTuples);

    assertToken(1, 1, "class", SemanticTokenType.KEYWORD, tokenTuples.get(0));
    assertToken(1, 7, "Debug", SemanticTokenType.CLASS, tokenTuples.get(1));
    assertToken(1, 13, "usingPlatform:", SemanticTokenType.METHOD, tokenTuples.get(2));
    assertToken(1, 28, "platform", SemanticTokenType.PARAMETER, tokenTuples.get(3));
    assertToken(1, 39, "Value", SemanticTokenType.METHOD, tokenTuples.get(4));

    assertToken(2, 3, "public", SemanticTokenType.MODIFIER, tokenTuples.get(5));
    assertToken(2, 10, "main:", SemanticTokenType.METHOD, tokenTuples.get(6));
    assertToken(2, 16, "args", SemanticTokenType.PARAMETER, tokenTuples.get(7));

    assertEquals(8, tokenTuples.size());
  }

  @Test
  public void testSymbols() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    var structures = adapter.parse("class Hello usingPlatform: platform = Value ()(\n"
        + "  public main: args = (\n"
        + "    ^ 0\n"
        + "  )\n"
        + ") : (\n"
        + "  public factoryMethod = ()\n"
        + ")\n", path);

    assertNull(structures.getDiagnostics());

    var symbols = adapter.documentSymbol(path);
    assertEquals(1, symbols.size());
    var classSymbol = symbols.get(0);
    assertEquals("Hello", classSymbol.getName());

    var children = classSymbol.getAllChildren();
    assertEquals(3, children.size());
    assertEquals("usingPlatform:", children.get(0).getName());
    assertEquals("main:", children.get(1).getName());
    assertEquals("class", children.get(2).getName());

    var usingPlatform = children.get(0).getAllChildren();
    assertEquals(1, usingPlatform.size());
    assertEquals("platform", usingPlatform.get(0).getName());

    var main = children.get(1).getAllChildren();
    assertEquals(1, main.size());
    assertEquals("args", main.get(0).getName());

    var clazz = children.get(2).getAllChildren();
    assertEquals(1, clazz.size());
    assertEquals("factoryMethod", clazz.get(0).getName());
  }
}
