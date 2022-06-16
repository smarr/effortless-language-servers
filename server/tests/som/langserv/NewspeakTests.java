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
  public void testSemanticHighlightingInSmallExample() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    adapter.parse("class Debug usingPlatform: platform = Value ()(\n"
        + "  public main: args = ()\n"
        + ") )\n", path);

    List<int[]> tokenTuples =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokenTuples);

    assertToken(0, 0, "class", SemanticTokenType.KEYWORD, tokenTuples.get(0));
    assertToken(0, 6, "Debug", SemanticTokenType.CLASS, tokenTuples.get(1));
    assertToken(0, 12, "usingPlatform:", SemanticTokenType.METHOD, tokenTuples.get(2));
    assertToken(0, 27, "platform", SemanticTokenType.PARAMETER, tokenTuples.get(3));
    assertToken(0, 38, "Value", SemanticTokenType.METHOD, tokenTuples.get(4));

    assertToken(1, 2, "public", SemanticTokenType.MODIFIER, tokenTuples.get(5));
    assertToken(1, 9, "main:", SemanticTokenType.METHOD, tokenTuples.get(6));
    assertToken(1, 15, "args", SemanticTokenType.PARAMETER, tokenTuples.get(7));

    assertEquals(8, tokenTuples.size());
  }

  @Test
  public void testSemanticHighlightingHelloWorld() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    adapter.parse("class Hello usingPlatform: platform = Value ()(\n"
        + "  public main: args = (\n"
        + "    'Hello World!' println.\n"
        + "    args from: 2 to: args size do: [ :arg | arg print. ' ' print ].\n"
        + "    '' println.\n"
        + "    ^ 0\n"
        + "  )\n"
        + ")\n"
        + "\n", path);

    List<int[]> tokenTuples =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokenTuples);

    assertToken(0, 0, "class", SemanticTokenType.KEYWORD, tokenTuples.get(0));
    assertToken(0, 6, "Hello", SemanticTokenType.CLASS, tokenTuples.get(1));
    assertToken(0, 12, "usingPlatform:", SemanticTokenType.METHOD, tokenTuples.get(2));
    assertToken(0, 27, "platform", SemanticTokenType.PARAMETER, tokenTuples.get(3));
    assertToken(0, 38, "Value", SemanticTokenType.METHOD, tokenTuples.get(4));

    assertToken(1, 2, "public", SemanticTokenType.MODIFIER, tokenTuples.get(5));
    assertToken(1, 9, "main:", SemanticTokenType.METHOD, tokenTuples.get(6));
    assertToken(1, 15, "args", SemanticTokenType.PARAMETER, tokenTuples.get(7));

    assertToken(2, 4, "'Hello World!'", SemanticTokenType.STRING, tokenTuples.get(8));
    assertToken(2, 19, "println", SemanticTokenType.METHOD, tokenTuples.get(9));

    assertToken(3, 4, "args", SemanticTokenType.PARAMETER, tokenTuples.get(10));
    assertToken(3, 9, "from:", SemanticTokenType.METHOD, tokenTuples.get(11));
    assertToken(3, 15, "2", SemanticTokenType.NUMBER, tokenTuples.get(12));
    assertToken(3, 17, "to:", SemanticTokenType.METHOD, tokenTuples.get(13));
    assertToken(3, 21, "args", SemanticTokenType.PARAMETER, tokenTuples.get(14));
    assertToken(3, 26, "size", SemanticTokenType.METHOD, tokenTuples.get(15));
    assertToken(3, 31, "do:", SemanticTokenType.METHOD, tokenTuples.get(16));
    assertToken(3, 38, "arg", SemanticTokenType.PARAMETER, tokenTuples.get(17));
    assertToken(3, 44, "arg", SemanticTokenType.PARAMETER, tokenTuples.get(18));
    assertToken(3, 48, "print", SemanticTokenType.METHOD, tokenTuples.get(19));
    assertToken(3, 55, "' '", SemanticTokenType.STRING, tokenTuples.get(20));
    assertToken(3, 59, "print", SemanticTokenType.METHOD, tokenTuples.get(21));

    assertToken(4, 4, "''", SemanticTokenType.STRING, tokenTuples.get(22));
    assertToken(4, 7, "println", SemanticTokenType.METHOD, tokenTuples.get(23));

    assertToken(5, 6, "0", SemanticTokenType.NUMBER, tokenTuples.get(24));

    assertEquals(25, tokenTuples.size());
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

  @Test
  public void testHover() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    var structures = adapter.parse("class Hello usingPlatform: platform = Value ()(\n"
        + "run = (\n"
        + "  self run: 1234\n"
        + ")\n"
        + "run: arg = ()\n"
        + ")\n", path);
    assertNull(structures.getDiagnostics());

    Hover hover = adapter.hover(path, new Position(2, 8));
    assertNotNull(hover);

    assertRange(2, 7, 2, 7 + "run:".length(), hover.getRange());

    assertEquals("plaintext", hover.getContents().getRight().getKind());
    assertEquals("run: arg\n", hover.getContents().getRight().getValue());
  }
}
