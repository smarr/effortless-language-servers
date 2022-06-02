package som.langserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static som.langserv.Helpers.assertRange;
import static som.langserv.Helpers.assertToken;
import static som.langserv.Helpers.printAllToken;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.Test;

import som.langserv.som.SomAdapter;
import som.langserv.structure.SemanticTokenType;


public class SomTests {

  @Test
  public void testLoadFile() throws IOException, URISyntaxException {
    var adapter = new SomAdapter();
    var structures =
        adapter.loadFile(new File(SomAdapter.CORE_LIB_PATH + "/Examples/Hello.som"));

    assertNull(structures.getDiagnostics());
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
    var structures = adapter.parse("Hello = (\n"
        + "    \"The 'run' method is called when initializing the system\"\n"
        + "    run = ('Hello, World from SOM' println )\n"
        + ")\n", path);

    assertNull(structures.getDiagnostics());

    List<int[]> tokens =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokens);

    assertToken(1, 1, "Hello", SemanticTokenType.CLASS, tokens.get(0));
    assertToken(2, 5, "\"The 'run' method is called when initializing the system\"",
        SemanticTokenType.COMMENT, tokens.get(1));

    assertToken(3, 5, "run", SemanticTokenType.METHOD, tokens.get(2));
    assertToken(3, 12, "'Hello, World from SOM'", SemanticTokenType.STRING,
        tokens.get(3));
    assertToken(3, 36, "println", SemanticTokenType.METHOD, tokens.get(4));

    assertEquals(5, tokens.size());
  }

  @Test
  public void testSymbols() {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    var structures = adapter.parse("Hello = (\n"
        + "    \"The 'run' method is called when initializing the system\"\n"
        + "    run = ('Hello, World from SOM' println )\n"
        + ")\n", path);

    assertNull(structures.getDiagnostics());

    var symbols = adapter.documentSymbol(path);
    assertEquals(1, symbols.size());
    var classSymbol = symbols.get(0);
    assertEquals("Hello", classSymbol.getName());

    var children = classSymbol.getChildren();
    assertEquals(1, children.size());
    assertEquals("run", children.get(0).getName());
  }

  @Test
  public void testSymbolDetails() {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    var structures = adapter.parse("Hello = (\n"
        + "run = ()\n"
        + "run:   arg = ()\n"
        + "+   arg = ()\n"
        + "run: arg   with:  arg2   = ()\n"
        + ")\n", path);

    assertNull(structures.getDiagnostics());

    var symbols = adapter.documentSymbol(path);
    assertEquals(1, symbols.size());
    var classSymbol = symbols.get(0);
    assertEquals("Hello", classSymbol.getName());

    var children = classSymbol.getChildren();
    assertEquals(4, children.size());
    assertEquals("run", children.get(0).getName());
    assertEquals(null, children.get(0).getDetail());

    assertEquals("run:", children.get(1).getName());
    assertEquals("run: arg", children.get(1).getDetail());

    assertEquals("+", children.get(2).getName());
    assertEquals("+ arg", children.get(2).getDetail());

    assertEquals("run:with:", children.get(3).getName());
    assertEquals("run: arg with: arg2", children.get(3).getDetail());
  }

  @Test
  public void testSymbolLineAndPositionInfo() {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    var structures = adapter.parse("Hello = (\n"
        + "run = ()\n"
        + "run:   arg = ()\n"
        + "+   arg = ()\n"
        + "run: arg   with:  arg2   = ()\n"
        + ")\n", path);

    assertNull(structures.getDiagnostics());

    var symbols = adapter.documentSymbol(path);
    assertEquals(1, symbols.size());
    var classSymbol = symbols.get(0);
    assertEquals("Hello", classSymbol.getName());
    assertRange(1, 1, 1, 6, classSymbol.getSelectionRange());

    var children = classSymbol.getChildren();
    assertEquals(4, children.size());
    assertEquals("run", children.get(0).getName());
    assertRange(2, 1, 2, 4, children.get(0).getSelectionRange());

    assertEquals("run:", children.get(1).getName());
    assertRange(3, 1, 3, 5, children.get(1).getSelectionRange());

    assertEquals("+", children.get(2).getName());
    assertRange(4, 1, 4, 2, children.get(2).getSelectionRange());

    assertEquals("run:with:", children.get(3).getName());
    assertRange(5, 1, 5, 17, children.get(3).getSelectionRange());
  }

  @Test
  public void testHover() {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    var structures = adapter.parse("Hello = (\n"
        + "run = (\n"
        + "  self run: 1234\n"
        + ")\n"
        + "run: arg = ()\n"
        + ")\n", path);

    assertNull(structures.getDiagnostics());

    Hover hover = adapter.hover(path, new Position(3, 9));
    assertNotNull(hover);

    Range r = hover.getRange();

    assertEquals(3, r.getStart().getLine());
    assertEquals(8, r.getStart().getCharacter());

    assertEquals(3, r.getEnd().getLine());
    assertEquals(8 + "run:".length(), r.getEnd().getCharacter());

    assertEquals("plaintext", hover.getContents().getRight().getKind());
    assertEquals("run: arg\n", hover.getContents().getRight().getValue());
  }
}
