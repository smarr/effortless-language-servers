package som.langserv;

import static org.junit.Assert.assertEquals;
import static som.langserv.Helpers.assertToken;
import static som.langserv.Helpers.printAllToken;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.junit.Test;

import som.langserv.som.SomAdapter;


public class SomTests {

  @Test
  public void testLoadFile() throws IOException, URISyntaxException {
    var adapter = new SomAdapter();
    var diagnostics =
        adapter.loadFile(new File(SomAdapter.CORE_LIB_PATH + "/Examples/Hello.som"));

    assertEquals(0, diagnostics.size());
  }

  @Test
  public void testLoadingSomWorkspace() throws URISyntaxException {
    var adapter = new SomAdapter();
    var client = new TestLanguageClient();

    adapter.connect(client);
    ForkJoinTask<?> task = adapter.loadWorkspace("file:" + SomAdapter.CORE_LIB_PATH);
    task.join();

    // there are currently two known parse errors in the core lib:
    // - Self.som, where super is assigned to
    // - Examples/Benchmarks/DeltaBlue/SortedCollection.som where we trigger "Currently #dnu
    // with super sent is not yet implemented. "
    assertEquals(2, client.diagnostics.size());
  }

  @Test
  public void testSemanticHighlightingInSmallExample() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    adapter.parse("Hello = (\n"
        + "    \"The 'run' method is called when initializing the system\"\n"
        + "    run = ('Hello, World from SOM' println )\n"
        + ")\n", path);

    List<int[]> tokens = adapter.getSemanticTokens(path);
    printAllToken(tokens);

    assertToken(1, 0, "Hello", SemanticTokenType.CLASS, tokens.get(0));
    assertToken(2, 4, "\"The 'run' method is called when initializing the system\"",
        SemanticTokenType.COMMENT, tokens.get(1));

    assertToken(3, 4, "run", SemanticTokenType.METHOD, tokens.get(2));
    assertToken(3, 11, "'Hello, World from SOM'", SemanticTokenType.STRING,
        tokens.get(3));
    assertToken(3, 35, "println", SemanticTokenType.METHOD, tokens.get(4));

    assertEquals(5, tokens.size());
  }
}
