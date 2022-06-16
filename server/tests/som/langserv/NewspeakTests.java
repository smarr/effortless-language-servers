package som.langserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SymbolInformation;
import org.junit.Test;

import som.langserv.newspeak.NewspeakAdapter;
import som.langserv.structure.SemanticTokenType;
import util.ArrayListIgnoreIfLastIdentical;


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
    assertEquals(11, errors);
    assertEquals(48, warnings);
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
  public void testSemanticHighlightingFields() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    adapter.parse("class Hello usingPlatform: platform = Value (\n"
        + "| public mySlot = 1. |\n"
        + ")(\n"
        + "  public main = (\n"
        + "    ^ mySlot\n"
        + "  )\n"
        + ")\n"
        + "\n", path);

    List<int[]> tokenTuples =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokenTuples);

    assertToken(1, 2, "public", SemanticTokenType.MODIFIER, tokenTuples.get(5));
    assertToken(1, 9, "mySlot", SemanticTokenType.PROPERTY, tokenTuples.get(6));
    assertToken(1, 18, "1", SemanticTokenType.NUMBER, tokenTuples.get(7));

    // yeah, that's newspeak semantics,
    // we can't really know better at this point without doing the lookup
    assertToken(4, 6, "mySlot", SemanticTokenType.METHOD, tokenTuples.get(10));

    assertEquals(11, tokenTuples.size());
  }

  @Test
  public void testSemanticHighlightingArgAndLocal() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    adapter.parse("class Hello usingPlatform: platform = Value ()(\n"
        + "  public main: args = (\n"
        + "    | local |\n"
        + "    ^ local\n"
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

    assertToken(2, 6, "local", SemanticTokenType.VARIABLE, tokenTuples.get(8));

    assertToken(3, 6, "local", SemanticTokenType.VARIABLE, tokenTuples.get(9));

    assertEquals(10, tokenTuples.size());
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

    assertNotNull(children.get(2).getRange());
    assertNotNull(children.get(2).getSelectionRange());

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
  public void testSymbolDetails() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    var structures = adapter.parse("class Hello usingPlatform: platform = Value ()(\n"
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
    assertEquals(5, children.size());
    assertEquals("usingPlatform:", children.get(0).getName());
    assertEquals("usingPlatform: platform", children.get(0).getDetail());

    assertEquals("run", children.get(1).getName());
    assertEquals("run", children.get(1).getDetail());

    assertEquals("run:", children.get(2).getName());
    assertEquals("run: arg", children.get(2).getDetail());

    assertEquals("+", children.get(3).getName());
    assertEquals("+ arg", children.get(3).getDetail());

    assertEquals("run:with:", children.get(4).getName());
    assertEquals("run: arg with: arg2", children.get(4).getDetail());
  }

  @Test
  public void testSymbolLineAndPositionInfo() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    var structures = adapter.parse("class Hello usingPlatform: platform = Value ()(\n"
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
    assertRange(0, 6, 0, 11, classSymbol.getSelectionRange());

    var children = classSymbol.getAllChildren();
    assertEquals(5, children.size());
    assertEquals("usingPlatform:", children.get(0).getName());
    assertRange(0, 12, 0, 26, children.get(0).getSelectionRange());

    assertEquals("run", children.get(1).getName());
    assertRange(1, 0, 1, 3, children.get(1).getSelectionRange());

    assertEquals("run:", children.get(2).getName());
    assertRange(2, 0, 2, 4, children.get(2).getSelectionRange());

    assertEquals("+", children.get(3).getName());
    assertRange(3, 0, 3, 1, children.get(3).getSelectionRange());

    assertEquals("run:with:", children.get(4).getName());
    assertRange(4, 0, 4, 16, children.get(4).getSelectionRange());
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

  @Test
  public void testSignatureHelp() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello.ns";
    var structures = adapter.parse("class Hello usingPlatform: platform = Value ()(\n"
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
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value ()(\n"
        + "run1: arg1 with: arg2 = ()\n"
        + "run2: arg3 = (\n"
        + "  self run: 123 )\n"
        + ")\n", path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello2.ns";
    structures = adapter.parse("class Hello2 usingPlatform: platform = Value ()(\n"
        + "run3: arg4 with: arg5 = ()\n"
        + "run4: arg6 = (\n"
        + "  self run: 123 )\n"
        + ")\n", path2);
    assertNull(structures.getDiagnostics());

    List<SymbolInformation> results = new ArrayListIgnoreIfLastIdentical<>();
    adapter.workspaceSymbol(results, "");

    assertEquals(16, results.size());

    results = new ArrayListIgnoreIfLastIdentical<>();
    adapter.workspaceSymbol(results, "run");

    assertEquals(4, results.size());
  }

  @Test
  public void testGotoDefinition() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value ()(\n"
        + "method1 = (\n"
        + " self method1.\n"
        + " self method2.\n"
        + ")\n"
        + "method2 = ()\n"
        + ")\n", path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello2.ns";
    structures = adapter.parse("class Hello2 usingPlatform: platform = Value ()(\n"
        + "method2 = (\n"
        + " self method1\n"
        + ")\n"
        + ")\n", path2);
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
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value ()(\n"
        + "method1: arg = (\n"
        + " [:arg |\n"
        + "    arg ]\n"
        + ")\n"
        + "method2: arg = (\n"
        + " arg )\n"
        + ")\n", path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello2.ns";
    structures = adapter.parse("class Hello2 usingPlatform: platform = Value ()(\n"
        + "method1: arg = (\n"
        + " arg\n"
        + ")\n"
        + ")\n", path2);
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
  public void testReferencesToFieldsAcrossFiles() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value (\n"
        + "| var |)(\n"
        + "method1: arg = (\n"
        + " [:arg |\n"
        + "    var ].\n"
        + "  ^ var )\n"
        + "method2: arg = (\n"
        + " | var | var )\n"
        + ")\n", path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello2.ns";
    structures = adapter.parse("class Hello2 usingPlatform: platform = Value ()(\n"
        + "method = (\n"
        + "  ^ var )\n"
        + "method2: arg = (\n"
        + " | var | var )\n"
        + ")\n", path2);
    assertNull(structures.getDiagnostics());

    List<Location> result = adapter.getReferences(path2, new Position(2, 5), true);

    assertEquals(path1, result.get(0).getUri());
    assertStart(1, 2, result.get(0).getRange());

    assertEquals(path1, result.get(1).getUri());
    assertStart(4, 4, result.get(1).getRange());

    assertEquals(path1, result.get(2).getUri());
    assertStart(5, 4, result.get(2).getRange());

    assertEquals(path2, result.get(3).getUri());
    assertStart(2, 4, result.get(3).getRange());

    assertEquals(4, result.size());

  }

  @Test
  public void testReferencesToMethodsAcrossFiles() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value ()(\n"
        + "method1: arg = (\n"
        + " arg method1: arg )\n"
        + "method1 = (\n"
        + " self method1: 2 )\n"
        + ")\n", path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello2.ns";
    structures = adapter.parse("class Hello2 usingPlatform: platform = Value ()(\n"
        + "method1: arg = ()\n"
        + "method = (\n"
        + "  ^ self method1: self )\n"
        + ")\n", path2);
    assertNull(structures.getDiagnostics());

    List<Location> result = adapter.getReferences(path2, new Position(3, 12), true);

    assertEquals(5, result.size());
    assertEquals(path1, result.get(0).getUri());
    assertStart(1, 0, result.get(0).getRange());

    assertEquals(path1, result.get(1).getUri());
    assertStart(2, 5, result.get(1).getRange());

    assertEquals(path1, result.get(2).getUri());
    assertStart(4, 6, result.get(2).getRange());

    assertEquals(path2, result.get(3).getUri());
    assertStart(1, 0, result.get(3).getRange());

    assertEquals(path2, result.get(4).getUri());
    assertStart(3, 9, result.get(4).getRange());
  }

  @Test
  public void testSyntaxErrors() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value ()(\n"
        + "method1: arg = (\n"
        + " [:arg |\n"
        + ")\n", path1);

    var diag = structures.getDiagnostics();
    assertEquals(1, diag.size());

    assertTrue(diag.get(0).getMessage().contains("Unexpected symbol."));
    assertEquals(3, diag.get(0).getRange().getStart().getLine());
    assertEquals(0, diag.get(0).getRange().getStart().getCharacter());
  }

  @Test
  public void testCompletionGlobalsInMethod() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value ()(\n"
        + "method = ()\n"
        + "run = (\n"
        + " self me\n"
        + ")\n"
        + ")\n", path1);
    assertNull(structures.getDiagnostics());

    CompletionList result = adapter.getCompletions(path1, new Position(3, 8));
    assertFalse(result.isIncomplete());

    var items = result.getItems();
    assertEquals(1, items.size());

    var i = items.get(0);
    assertEquals(CompletionItemKind.Method, i.getKind());
    assertEquals("method", i.getDetail());
    assertEquals("method", i.getLabel());
  }

  @Test
  public void testCompletionLocals() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value ()(\n"
        + "run = (\n"
        + "| local |\n"
        + " lo\n"
        + ")\n"
        + ")\n", path1);
    assertNull(structures.getDiagnostics());

    CompletionList result = adapter.getCompletions(path1, new Position(3, 3));
    assertFalse(result.isIncomplete());

    var items = result.getItems();
    assertEquals(1, items.size());

    var i = items.get(0);
    assertEquals(CompletionItemKind.Variable, i.getKind());
    assertEquals("local", i.getLabel());
  }

  @Test
  public void testCompletionProperties() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value (\n"
        + "| field |)(\n"
        + "run = (\n"
        + " fi\n"
        + ")\n"
        + ")\n", path1);
    assertNull(structures.getDiagnostics());

    CompletionList result = adapter.getCompletions(path1, new Position(3, 3));
    assertFalse(result.isIncomplete());

    var items = result.getItems();
    assertEquals(1, items.size());

    var i = items.get(0);
    assertEquals(CompletionItemKind.Property, i.getKind());
    assertEquals("field", i.getLabel());
  }

  @Test
  public void testCompletionMethods() throws URISyntaxException {
    var adapter = new NewspeakAdapter();
    String path1 = "file:" + NewspeakAdapter.CORE_LIB_PATH + "/Hello1.ns";
    var structures = adapter.parse("class Hello1 usingPlatform: platform = Value ()(\n"
        + "method = (\n"
        + " self me\n"
        + ")\n"
        + ")\n", path1);
    assertNull(structures.getDiagnostics());

    CompletionList result = adapter.getCompletions(path1, new Position(2, 8));
    assertFalse(result.isIncomplete());

    var items = result.getItems();
    assertEquals(1, items.size());

    var i = items.get(0);
    assertEquals(CompletionItemKind.Method, i.getKind());
    assertEquals("method", i.getDetail());
    assertEquals("method", i.getLabel());
  }
}
