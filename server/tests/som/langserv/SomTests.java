package som.langserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static som.langserv.Helpers.assertRange;
import static som.langserv.Helpers.assertStart;
import static som.langserv.Helpers.assertToken;
import static som.langserv.Helpers.printAllToken;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SymbolInformation;
import org.junit.Test;

import som.langserv.som.SomAdapter;
import som.langserv.structure.SemanticTokenType;
import util.ArrayListIgnoreIfLastIdentical;


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

    assertToken(0, 0, "Hello", SemanticTokenType.CLASS, tokens.get(0));
    assertToken(1, 4, "\"The 'run' method is called when initializing the system\"",
        SemanticTokenType.COMMENT, tokens.get(1));

    assertToken(2, 4, "run", SemanticTokenType.METHOD, tokens.get(2));
    assertToken(2, 11, "'Hello, World from SOM'", SemanticTokenType.STRING,
        tokens.get(3));
    assertToken(2, 35, "println", SemanticTokenType.METHOD, tokens.get(4));

    assertEquals(5, tokens.size());
  }

  @Test
  public void testSemanticHighlightingFields() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    adapter.parse("Hello = (\n"
        + "| f1 field2\n"
        + "  anotherField |\n"
        + "----\n"
        + "| abcd \n"
        + ")\n", path);

    List<int[]> tokens =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokens);

    assertToken(0, 0, "Hello", SemanticTokenType.CLASS, tokens.get(0));
    assertToken(1, 2, "f1", SemanticTokenType.PROPERTY, tokens.get(1));
    assertToken(1, 5, "field2", SemanticTokenType.PROPERTY, tokens.get(2));
    assertToken(2, 2, "anotherField", SemanticTokenType.PROPERTY, tokens.get(3));

    assertToken(4, 2, "abcd", SemanticTokenType.PROPERTY, tokens.get(4));

    assertEquals(5, tokens.size());
  }

  @Test
  public void testSemanticHighlightingArgAndLocal() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    adapter.parse("Hello = (\n"
        + "run: arg = (\n"
        + "  | myLocal |\n"
        + ") )\n", path);

    List<int[]> tokens =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokens);

    assertToken(0, 0, "Hello", SemanticTokenType.CLASS, tokens.get(0));
    assertToken(1, 0, "run:", SemanticTokenType.METHOD, tokens.get(1));
    assertToken(1, 5, "arg", SemanticTokenType.PARAMETER, tokens.get(2));
    assertToken(2, 4, "myLocal", SemanticTokenType.VARIABLE, tokens.get(3));

    assertEquals(4, tokens.size());
  }

  @Test
  public void testSemanticHighlightingTwoMethods() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    adapter.parse("Hello = (\n"
        + "first = ( ^ self at: 1 )\n"
        + "last  = ( ^ self at: self length )\n"
        + ") )\n", path);

    List<int[]> tokens =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokens);

    assertToken(0, 0, "Hello", SemanticTokenType.CLASS, tokens.get(0));
    assertToken(1, 0, "first", SemanticTokenType.METHOD, tokens.get(1));

    assertToken(1, 12, "self", SemanticTokenType.PARAMETER, tokens.get(2));
    assertToken(1, 17, "at:", SemanticTokenType.METHOD, tokens.get(3));
    assertToken(1, 21, "1", SemanticTokenType.NUMBER, tokens.get(4));

    assertToken(2, 0, "last", SemanticTokenType.METHOD, tokens.get(5));

    assertToken(2, 12, "self", SemanticTokenType.PARAMETER, tokens.get(6));
    assertToken(2, 17, "at:", SemanticTokenType.METHOD, tokens.get(7));
    assertToken(2, 21, "self", SemanticTokenType.PARAMETER, tokens.get(8));
    assertToken(2, 26, "length", SemanticTokenType.METHOD, tokens.get(9));

    assertEquals(10, tokens.size());
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

    var children = classSymbol.getAllChildren();
    assertEquals(1, children.size());
    assertEquals("run", children.get(0).getName());
  }

  @Test
  public void testSymbolsInClasses() {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    var structures = adapter.parse("Hello = (\n"
        + "  | aField field2 |\n"
        + "  run = ('Hello, World from SOM' println )\n"
        + "  ----\n"
        + "  | classField1 field2 |\n"
        + ")\n", path);

    assertNull(structures.getDiagnostics());

    var symbols = adapter.documentSymbol(path);
    assertEquals(1, symbols.size());
    var classSymbol = symbols.get(0);
    assertEquals("Hello", classSymbol.getName());

    var children = classSymbol.getAllChildren();
    assertEquals(4, children.size());
    assertEquals(4, classSymbol.getChildren().size());

    assertEquals("aField", children.get(0).getName());
    assertEquals("field2", children.get(1).getName());
    assertEquals("run", children.get(2).getName());
    assertEquals("class", children.get(3).getName());

    var classChildren = children.get(3).getChildren();
    assertEquals(2, classChildren.size());
    assertEquals(2, children.get(3).getAllChildren().size());

    assertEquals("classField1", classChildren.get(0).getName());
    assertEquals("field2", classChildren.get(1).getName());
  }

  @Test
  public void testSymbolsInMethods() {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    var structures = adapter.parse("Hello = (\n"
        + "run: arg = ( | local | self block: [:a | arg] )\n"
        + ")\n", path);

    assertNull(structures.getDiagnostics());

    var symbols = adapter.documentSymbol(path);
    assertEquals(1, symbols.size());
    var classSymbol = symbols.get(0);
    assertEquals("Hello", classSymbol.getName());

    var children = classSymbol.getAllChildren();
    assertEquals(1, children.size());
    assertEquals("run:", children.get(0).getName());

    var mChildren = children.get(0).getAllChildren();
    assertEquals(3, mChildren.size());
    assertEquals("arg", mChildren.get(0).getName());
    assertEquals("local", mChildren.get(1).getName());
    assertTrue(mChildren.get(2).getName().startsWith("λrun"));
    assertEquals("[:a]", mChildren.get(2).getDetail());
    assertRange(1, 35, 1, 45, mChildren.get(2).getRange());

    var bChildren = mChildren.get(2).getAllChildren();
    assertEquals(1, bChildren.size());
    assertEquals("a", bChildren.get(0).getName());
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

    var children = classSymbol.getAllChildren();
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
    assertRange(0, 0, 0, 5, classSymbol.getSelectionRange());

    var children = classSymbol.getAllChildren();
    assertEquals(4, children.size());
    assertEquals("run", children.get(0).getName());
    assertRange(1, 0, 1, 3, children.get(0).getSelectionRange());

    assertEquals("run:", children.get(1).getName());
    assertRange(2, 0, 2, 4, children.get(1).getSelectionRange());

    assertEquals("+", children.get(2).getName());
    assertRange(3, 0, 3, 1, children.get(2).getSelectionRange());

    assertEquals("run:with:", children.get(3).getName());
    assertRange(4, 0, 4, 16, children.get(3).getSelectionRange());
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

    Hover hover = adapter.hover(path, new Position(2, 8));
    assertNotNull(hover);

    Range r = hover.getRange();

    assertEquals(2, r.getStart().getLine());
    assertEquals(7, r.getStart().getCharacter());

    assertEquals(2, r.getEnd().getLine());
    assertEquals(7 + "run:".length(), r.getEnd().getCharacter());

    assertEquals("plaintext", hover.getContents().getRight().getKind());
    assertEquals("run: arg\n", hover.getContents().getRight().getValue());
  }

  @Test
  public void testSignatureHelp() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    var structures = adapter.parse("Hello = (\n"
        + "run: arg with: arg2 = ()\n"
        + "run: arg = (\n"
        + "  self run: 123 )\n"
        + ")\n", path);

    assertNull(structures.getDiagnostics());

    SignatureHelp help = adapter.signatureHelp(path, new Position(3, 9), null);
    assertNotNull(help);

    assertNull(help.getActiveSignature());

    var signatures = help.getSignatures();
    assertNotNull(signatures);
    assertEquals(1, signatures.size());

    var sig = signatures.get(0);
    assertNotNull(sig);

    assertEquals("Hello>>#run:", sig.getLabel());

    var params = sig.getParameters();
    assertNotNull(params);
    assertEquals(1, params.size());

    var param = params.get(0);
    assertEquals("arg", param.getLabel().getLeft());
  }

  @Test
  public void testWorkspaceSymbols() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello1.som";
    var structures = adapter.parse(
        "Hello1 = (\n"
            + "m1 = ()\n"
            + ")",
        path);
    assertNull(structures.getDiagnostics());

    path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello2.som";
    structures = adapter.parse(
        "Hello2 = (\n"
            + "m2: arg = ()\n"
            + ")",
        path);
    assertNull(structures.getDiagnostics());

    List<SymbolInformation> results = new ArrayListIgnoreIfLastIdentical<>();
    adapter.workspaceSymbol(results, "");

    assertEquals(5, results.size());

    results = new ArrayListIgnoreIfLastIdentical<>();
    adapter.workspaceSymbol(results, "m");

    assertEquals(2, results.size());
  }

  @Test
  public void testGotoDefinition() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path1 = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello1.som";
    var structures = adapter.parse(
        "Hello1 = (\n"
            + "method1 = (\n"
            + " self method1.\n"
            + " self method2.\n"
            + ")\n"
            + "method2 = ()\n"
            + ")",
        path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello2.som";
    structures = adapter.parse(
        "Hello2 = Hello1 (\n"
            + "method2 = (\n"
            + " self method1\n"
            + ")\n"
            + ")",
        path2);
    assertNull(structures.getDiagnostics());

    var locations = adapter.getDefinitions(path2, new Position(2, 8));
    assertEquals(1, locations.size());

    var m1 = locations.get(0);
    assertEquals(path1, m1.getTargetUri());
    assertEquals(1, m1.getTargetSelectionRange().getStart().getLine());
    assertEquals(2, m1.getOriginSelectionRange().getStart().getLine());
  }

  @Test
  public void testGotoDefinitionInBlockNested() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path1 = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello1.som";
    var structures = adapter.parse(
        "Hello1 = (\n"
            + "method1: arg = (\n"
            + " [:arg |\n"
            + "    arg ]\n"
            + ")\n"
            + "method2: arg = (\n"
            + " arg )\n"
            + ")",
        path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello2.som";
    structures = adapter.parse(
        "Hello2 = (\n"
            + "method1: arg = (\n"
            + " arg\n"
            + ")\n"
            + ")",
        path2);
    assertNull(structures.getDiagnostics());

    var locations = adapter.getDefinitions(path1, new Position(3, 5));
    assertEquals(1, locations.size());

    var blockArg = locations.get(0);
    assertEquals(path1, blockArg.getTargetUri());
    assertEquals(2, blockArg.getTargetSelectionRange().getStart().getLine());
    assertEquals(3, blockArg.getOriginSelectionRange().getStart().getLine());

    locations = adapter.getDefinitions(path1, new Position(6, 2));
    assertEquals(1, locations.size());

    var m2Arg = locations.get(0);
    assertEquals(path1, blockArg.getTargetUri());
    assertEquals(5, m2Arg.getTargetSelectionRange().getStart().getLine());
    assertEquals(6, m2Arg.getOriginSelectionRange().getStart().getLine());
  }

  @Test
  public void testHighlightsFieldVsVariable() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path1 = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello1.som";
    var structures = adapter.parse(
        "Hello1 = (\n"
            + "| var |\n"
            + "method1: arg = (\n"
            + " [:arg |\n"
            + "    var ]\n"
            + ")\n"
            + "method2: arg = (\n"
            + " | var | var )\n"
            + ")",
        path1);
    assertNull(structures.getDiagnostics());

    // test from declaration
    List<DocumentHighlight> hs = adapter.getHighlight(path1, new Position(1, 3));
    assertNotNull(hs);

    assertEquals(2, hs.size());
    assertRange(1, 2, 1, 5, hs.get(0).getRange());
    assertRange(4, 4, 4, 7, hs.get(1).getRange());

    // test from use
    hs = adapter.getHighlight(path1, new Position(4, 5));
    assertNotNull(hs);

    assertEquals(2, hs.size());
    assertStart(1, 2, hs.get(0).getRange());
    assertStart(4, 4, hs.get(1).getRange());

    // test variable use
    hs = adapter.getHighlight(path1, new Position(7, 10));
    assertNotNull(hs);

    assertEquals(2, hs.size());
    assertStart(7, 3, hs.get(0).getRange());
    assertStart(7, 9, hs.get(1).getRange());
  }

  @Test
  public void testFieldReferencesIncludeDecls() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path1 = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello1.som";
    var structures = adapter.parse(
        "Hello1 = (\n"
            + "| var |\n"
            + "method1: arg = (\n"
            + " [:arg |\n"
            + "    var ].\n"
            + "  ^ var )\n"
            + "method2: arg = (\n"
            + " | var | var )\n"
            + ")",
        path1);

    assertNull(structures.getDiagnostics());

    List<Location> result = adapter.getReferences(path1, new Position(4, 5), true);

    assertEquals(3, result.size());
    assertEquals(path1, result.get(0).getUri());
    assertStart(1, 2, result.get(0).getRange());

    assertEquals(path1, result.get(1).getUri());
    assertStart(4, 4, result.get(1).getRange());

    assertEquals(path1, result.get(2).getUri());
    assertStart(5, 4, result.get(2).getRange());
  }

  @Test
  public void testFieldReferencesExcludeDecls() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path1 = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello1.som";
    var structures = adapter.parse(
        "Hello1 = (\n"
            + "| var |\n"
            + "method1: arg = (\n"
            + " [:arg |\n"
            + "    var ].\n"
            + "  ^ var )\n"
            + "method2: arg = (\n"
            + " | var | var )\n"
            + ")",
        path1);

    assertNull(structures.getDiagnostics());

    List<Location> result = adapter.getReferences(path1, new Position(4, 5), false);

    assertEquals(2, result.size());
    assertEquals(path1, result.get(0).getUri());
    assertStart(4, 4, result.get(0).getRange());

    assertEquals(path1, result.get(1).getUri());
    assertStart(5, 4, result.get(1).getRange());
  }

  @Test
  public void testReferencesToFieldsAcrossFiles() throws URISyntaxException {
    var adapter = new SomAdapter();
    String path1 = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello1.som";
    var structures = adapter.parse(
        "Hello1 = (\n"
            + "| var |\n"
            + "method1: arg = (\n"
            + " [:arg |\n"
            + "    var ].\n"
            + "  ^ var )\n"
            + "method2: arg = (\n"
            + " | var | var )\n"
            + ")",
        path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello2.som";
    structures = adapter.parse(
        "Hello2 = Hello1 (\n"
            + "method = (\n"
            + "  ^ var )\n"
            + "method2: arg = (\n"
            + " | var | var )\n"
            + ")",
        path2);

    assertNull(structures.getDiagnostics());

    List<Location> result = adapter.getReferences(path2, new Position(2, 5), true);

    assertEquals(4, result.size());
    assertEquals(path1, result.get(0).getUri());
    assertStart(1, 2, result.get(0).getRange());

    assertEquals(path1, result.get(1).getUri());
    assertStart(4, 4, result.get(1).getRange());

    assertEquals(path1, result.get(2).getUri());
    assertStart(5, 4, result.get(2).getRange());

    assertEquals(path2, result.get(3).getUri());
    assertStart(2, 4, result.get(3).getRange());
  }
  @Test
  public void testSyntaxErrors() {
    var adapter = new SomAdapter();
    String path = "file:" + SomAdapter.CORE_LIB_PATH + "/Hello.som";
    var structures = adapter.parse("Hello = (\n"
        + "run = (\n"
        + "  self run:  \n"
        + ")\n", path);

    var diag = structures.getDiagnostics();
    assertEquals(1, diag.size());

    assertTrue(diag.get(0).getMessage().contains("Unexpected symbol."));
    assertEquals(3, diag.get(0).getRange().getStart().getLine());
    assertEquals(0, diag.get(0).getRange().getStart().getCharacter());
  }
}
