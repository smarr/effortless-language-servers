package som.langserv;

import static org.junit.Assert.assertEquals;
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


public class NewspeakTests {
  @Test
  public void testLoadFile() throws IOException, URISyntaxException {
    var adapter = new NewspeakAdapter();
    var diagnostics =
        adapter.loadFile(new File(NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns"));

    assertEquals(0, diagnostics.size());
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

    List<int[]> tokenTuples = adapter.getSemanticTokens(path);
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
}
