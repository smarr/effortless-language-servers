package som.langserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static som.langserv.Helpers.assertRange;
import static som.langserv.Helpers.assertToken;
import static som.langserv.Helpers.printAllToken;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SymbolInformation;
import org.junit.Test;

import simple.SimpleLanguageParser;
import som.langserv.simple.SimpleAdapter;
import som.langserv.structure.SemanticTokenType;
import util.ArrayListIgnoreIfLastIdentical;


public class SimpleLanguageTests {

  public static File getRootForSimpleLanguageExamples() throws URISyntaxException {
    File f = new File(
        SimpleLanguageParser.class.getProtectionDomain().getCodeSource()
                                  .getLocation().toURI().getPath());

    return new File(f.getParentFile().getAbsolutePath() + File.separator + "src");
  }

  @Test
  public void testSemanticHighlightingInSmallExample() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Hello.sl";
    adapter.parse("/*\n"
        + " * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.\n"
        + " * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.\n"
        + " */\n"
        + "\n"
        + "function main() {  \n"
        + "  println(\"Hello World!\");  \n"
        + "}  \n", path);

    List<int[]> tokens =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokens);

    assertToken(5, 0, "function", SemanticTokenType.KEYWORD, tokens.get(0));
    assertToken(5, 9, "main", SemanticTokenType.FUNCTION, tokens.get(1));

    assertToken(6, 2, "println", SemanticTokenType.VARIABLE, tokens.get(2));
    assertToken(6, 2, "println", SemanticTokenType.FUNCTION, tokens.get(3));

    assertToken(6, 10, "\"Hello World!\"", SemanticTokenType.STRING,
        tokens.get(4));

    assertEquals(5, tokens.size());
  }

  @Test
  public void testSemanticHighlighting() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Hello.sl";
    adapter.parse("function recursion(n) {\n"
        + "  if (n > 0) {\n"
        + "    recursion(n - 1);\n"
        + "    recursion(n - 1);\n"
        + "    recursion(n - 1);\n"
        + "  } else {\n"
        + "    return n;\n"
        + "  }\n"
        + "}", path);

    List<int[]> tokens =
        adapter.getStructures(path).getSemanticTokens().getSemanticTokens();
    printAllToken(tokens);

    assertToken(0, 0, "function", SemanticTokenType.KEYWORD, tokens.get(0));
    assertToken(0, 9, "recursion", SemanticTokenType.FUNCTION, tokens.get(1));
    assertToken(0, 19, "n", SemanticTokenType.PARAMETER, tokens.get(2));

    assertToken(1, 2, "if", SemanticTokenType.KEYWORD, tokens.get(3));
    assertToken(1, 6, "n", SemanticTokenType.VARIABLE, tokens.get(4));
    assertToken(1, 8, ">", SemanticTokenType.OPERATOR, tokens.get(5));
    assertToken(1, 10, "0", SemanticTokenType.NUMBER, tokens.get(6));

    for (int i = 0; i < 3; i += 1) {
      assertToken(2 + i, 4, "recursion", SemanticTokenType.VARIABLE, tokens.get(7 + (i * 5)));
      assertToken(2 + i, 4, "recursion", SemanticTokenType.FUNCTION, tokens.get(8 + (i * 5)));
      assertToken(2 + i, 14, "n", SemanticTokenType.VARIABLE, tokens.get(9 + (i * 5)));
      assertToken(2 + i, 16, "-", SemanticTokenType.OPERATOR, tokens.get(10 + (i * 5)));
      assertToken(2 + i, 18, "1", SemanticTokenType.NUMBER, tokens.get(11 + (i * 5)));
    }

    assertToken(5, 4, "else", SemanticTokenType.KEYWORD, tokens.get(22));
    assertToken(6, 4, "return", SemanticTokenType.KEYWORD, tokens.get(23));
    assertToken(6, 11, "n", SemanticTokenType.VARIABLE, tokens.get(24));

    assertEquals(25, tokens.size());
  }

  @Test
  public void testSymbols() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function add(a, b) {\n"
            + "  return a + b;\n"
            + "}\n"
            + "function loop(n) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main() {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path);

    var symbols = adapter.documentSymbol(path);

    assertEquals(3, symbols.size());
    assertEquals("add", symbols.get(0).getName());
    assertEquals("loop", symbols.get(1).getName());
    assertEquals("main", symbols.get(2).getName());
  }

  @Test
  public void testSymbolDetails() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function add( a , b   ) {\n"
            + "  return a + b;\n"
            + "}\n"
            + "function loop(  n  ) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path);

    var symbols = adapter.documentSymbol(path);
    assertEquals(3, symbols.size());
    assertEquals("add(a, b)", symbols.get(0).getDetail());
    assertEquals("loop(n)", symbols.get(1).getDetail());
    assertEquals("main()", symbols.get(2).getDetail());
  }

  @Test
  public void testSymbolLineAndPositionInfo() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function add( a , b   ) {\n"
            + "  return a + b;\n"
            + "}\n"
            + "function loop(  n  ) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path);

    var symbols = adapter.documentSymbol(path);
    assertEquals(3, symbols.size());

    DocumentSymbol add = symbols.get(0);
    assertEquals("add(a, b)", add.getDetail());

    assertRange(0, 9, 0, 12, add.getSelectionRange());
    assertRange(0, 9, 2, 1, add.getRange());

    DocumentSymbol loop = symbols.get(1);
    assertEquals("loop(n)", loop.getDetail());
    assertRange(3, 9, 3, 13, loop.getSelectionRange());
    assertRange(3, 9, 6, 1, loop.getRange());
  }

  @Test
  public void testHover() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function loop(  n  ) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path);

    Hover hover = adapter.hover(path, new Position(6, 11));
    assertNotNull(hover);

    Range r = hover.getRange();

    assertEquals(6, r.getStart().getLine());
    assertEquals(10, r.getStart().getCharacter());

    assertEquals(6, r.getEnd().getLine());
    assertEquals(10 + "loop".length(), r.getEnd().getCharacter());

    assertEquals("plaintext", hover.getContents().getRight().getKind());
    assertEquals("loop(n)\n", hover.getContents().getRight().getValue());
  }

  @Test
  public void testHoverAfterSymbol() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function loop(  n  ) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path);

    Hover hover = adapter.hover(path, new Position(6, 10 + "loop".length()));
    assertNotNull(hover);

    Range r = hover.getRange();

    assertEquals(6, r.getStart().getLine());
    assertEquals(10, r.getStart().getCharacter());

    assertEquals(6, r.getEnd().getLine());
    assertEquals(10 + "loop".length(), r.getEnd().getCharacter());

    assertEquals("plaintext", hover.getContents().getRight().getKind());
    assertEquals("loop(n)\n", hover.getContents().getRight().getValue());
  }

  @Test
  public void testSignatureHelp() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function loop(  n  ) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path);

    SignatureHelp help = adapter.signatureHelp(path, new Position(6, 11), null);
    assertNotNull(help);

    assertNull(help.getActiveSignature());

    var signatures = help.getSignatures();
    assertNotNull(signatures);
    assertEquals(1, signatures.size());

    var sig = signatures.get(0);
    assertNotNull(sig);

    assertEquals("loop(n)", sig.getLabel());

    var params = sig.getParameters();
    assertNotNull(params);
    assertEquals(1, params.size());

    var param = params.get(0);
    assertEquals("n", param.getLabel().getLeft());
  }

  @Test
  public void testWorkspaceSymbols() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var structures = adapter.parse(
        "function loop(  n  ) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path);
    assertNull(structures.getDiagnostics());

    path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    structures = adapter.parse(
        "function loop(n, b, c) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function baz() {\n"
            + "  println(loop(1000, 1, 2));  \n"
            + "}",
        path);
    assertNull(structures.getDiagnostics());

    List<SymbolInformation> results = new ArrayListIgnoreIfLastIdentical<>();
    adapter.workspaceSymbol(results, "");

    assertEquals(11, results.size());

    results = new ArrayListIgnoreIfLastIdentical<>();
    adapter.workspaceSymbol(results, "l");

    assertEquals(2, results.size());
  }

  @Test
  public void testGotoDefinition() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path1 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var structures = adapter.parse(
        "\n" +
            "function loop(  n  ) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    structures = adapter.parse(
        "function loop(n, b, c) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function baz() {\n"
            + "  println(loop(1000, 1, 2));  \n"
            + "}",
        path2);
    assertNull(structures.getDiagnostics());

    var locations = adapter.getDefinitions(path2, new Position(5, 11));
    assertEquals(2, locations.size());

    var loop1 = locations.get(0);
    assertEquals(path2, loop1.getTargetUri());
    assertEquals(0, loop1.getTargetSelectionRange().getStart().getLine());
    assertEquals(5, loop1.getOriginSelectionRange().getStart().getLine());

    var loop2 = locations.get(1);
    assertEquals(path1, loop2.getTargetUri());
    assertEquals(1, loop2.getTargetSelectionRange().getStart().getLine());
    assertEquals(5, loop2.getOriginSelectionRange().getStart().getLine());
  }

  @Test
  public void testHighlights() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function loop(  n  ) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "loop(1);\n"
            + "loop(2);\n"
            + "  println(loop(1000));  \n"
            + "}",
        path);

    List<DocumentHighlight> hs = adapter.getHighlight(path, new Position(7, 1));
    assertNotNull(hs);

    assertEquals(4, hs.size());

    assertEquals(0, hs.get(0).getRange().getStart().getLine());
    assertEquals(9, hs.get(0).getRange().getStart().getCharacter());

    assertEquals(6, hs.get(1).getRange().getStart().getLine());
    assertEquals(0, hs.get(1).getRange().getStart().getCharacter());

    assertEquals(7, hs.get(2).getRange().getStart().getLine());
    assertEquals(0, hs.get(2).getRange().getStart().getCharacter());

    assertEquals(8, hs.get(3).getRange().getStart().getLine());
    assertEquals(10, hs.get(3).getRange().getStart().getCharacter());
  }

  @Test
  public void testReferencesIncludeDecls() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path1 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var structures = adapter.parse(
        "function loop(  n  ) {\n"
            + "  loop(1);\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    structures = adapter.parse(
        "function loop(n, b, c) {\n"
            + "}\n"
            + "function baz() {\n"
            + "  println(loop(1000, 1, 2));  \n"
            + "}",
        path2);
    assertNull(structures.getDiagnostics());

    List<Location> result = adapter.getReferences(path2, new Position(3, 11), true);

    assertEquals(5, result.size());

    assertEquals(path1, result.get(0).getUri());
    assertEquals(0, result.get(0).getRange().getStart().getLine());

    assertEquals(path1, result.get(1).getUri());
    assertEquals(1, result.get(1).getRange().getStart().getLine());

    assertEquals(path1, result.get(2).getUri());
    assertEquals(5, result.get(2).getRange().getStart().getLine());

    assertEquals(path2, result.get(3).getUri());
    assertEquals(0, result.get(3).getRange().getStart().getLine());

    assertEquals(path2, result.get(4).getUri());
    assertEquals(3, result.get(4).getRange().getStart().getLine());
  }

  @Test
  public void testReferencesExcludeDecls() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path1 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var structures = adapter.parse(
        "function loop(  n  ) {\n"
            + "  loop(1);\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    structures = adapter.parse(
        "function loop(n, b, c) {\n"
            + "}\n"
            + "function baz() {\n"
            + "  println(loop(1000, 1, 2));  \n"
            + "}",
        path2);
    assertNull(structures.getDiagnostics());

    List<Location> result = adapter.getReferences(path2, new Position(3, 11), false);

    assertEquals(3, result.size());

    assertEquals(path1, result.get(0).getUri());
    assertEquals(1, result.get(0).getRange().getStart().getLine());

    assertEquals(path1, result.get(1).getUri());
    assertEquals(5, result.get(1).getRange().getStart().getLine());

    assertEquals(path2, result.get(2).getUri());
    assertEquals(3, result.get(2).getRange().getStart().getLine());
  }

  @Test
  public void testSyntaxErrors() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var doc = adapter.parse(
        "function main() {\n"
            + "  i = 0;\n"
            + "  println(loop 1000));  \n"
            + "}",
        path);
    var diag = doc.getDiagnostics();

    // since we don't bail on parsing errors, there will be multiple
    assertEquals(3, diag.size());

    assertEquals("missing ';' at '('", diag.get(0).getMessage());
    assertEquals(2, diag.get(0).getRange().getStart().getLine());
    assertEquals(9, diag.get(0).getRange().getStart().getCharacter());
  }

  @Test
  public void testCompletionGlobals() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function loop(n,   b,   c) {}\n"
            + "function main() {\n"
            + "  i = 0;\n"
            + "  println(lo",
        path);

    CompletionList result = adapter.getCompletions(path, new Position(3, 12));
    assertFalse(result.isIncomplete());

    var items = result.getItems();
    assertEquals(1, items.size());

    var i = items.get(0);
    assertEquals(CompletionItemKind.Function, i.getKind());
    assertEquals("loop(n, b, c)", i.getDetail());
    assertEquals("loop", i.getLabel());
  }

  @Test
  public void testCompletionLocals() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path1 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var structures = adapter.parse(
        "function loop(ii) {\n"
            + "  loop(1);\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(i);  \n"
            + "}",
        path1);
    assertNull(structures.getDiagnostics());

    String path2 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    structures = adapter.parse("function loop(iii) {}", path2);
    assertNull(structures.getDiagnostics());

    CompletionList result = adapter.getCompletions(path1, new Position(5, 10));
    assertFalse(result.isIncomplete());

    var items = result.getItems();
    assertEquals(1, items.size());

    var i = items.get(0);
    assertEquals(CompletionItemKind.Variable, i.getKind());
    assertEquals("i", i.getLabel());
  }

  @Test
  public void testCompletionProperties() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path1 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var structures = adapter.parse(
        "function loop() {\n"
            + "  o = new(); \n"
            + "  o.prop2 = 32;\n"
            + "}\n"
            + "function main() {\n"
            + "  o = new();\n"
            + "  o.prop3 = 434;  \n"
            + "  o.  \n"
            + "}",
        path1);

    String path2 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    structures = adapter.parse("function baz() {\n"
        + " o = new();\n"
        + " o.prop1 = 3;\n"
        + "}", path2);
    assertNull(structures.getDiagnostics());

    // TODO: figure out how to make getPossiblyIncompleteElement find that we actually just
    // parsed a dot
    // this might not be easily accessible, maybe I need to intercept the parser error

    CompletionList result = adapter.getCompletions(path1, new Position(7, 4));
    assertFalse(result.isIncomplete());

    var items = result.getItems();
    assertEquals(3, items.size());

    var i = items.get(0);
    assertEquals(CompletionItemKind.Property, i.getKind());
    assertEquals("prop2", i.getLabel());

    i = items.get(1);
    assertEquals(CompletionItemKind.Property, i.getKind());
    assertEquals("prop3", i.getLabel());

    i = items.get(2);
    assertEquals(CompletionItemKind.Property, i.getKind());
    assertEquals("prop1", i.getLabel());
  }
}
