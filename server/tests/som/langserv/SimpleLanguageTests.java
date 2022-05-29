package som.langserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static som.langserv.Helpers.assertRange;
import static som.langserv.Helpers.assertToken;
import static som.langserv.Helpers.printAllToken;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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


public class SimpleLanguageTests {

  private static File getRootForSimpleLanguageExamples() throws URISyntaxException {
    File f = new File(
        SimpleLanguageParser.class.getProtectionDomain().getCodeSource()
                                  .getLocation().toURI().getPath());

    return new File(f.getParentFile().getAbsolutePath() + File.separator + "src");
  }

  @Test
  public void testLoadFile() throws IOException, URISyntaxException {
    var adapter = new SimpleAdapter();
    var diagnostics =
        adapter.loadFile(
            new File(getRootForSimpleLanguageExamples() + File.separator + "HelloWorld.sl"));

    assertEquals(0, diagnostics.size());
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

    List<int[]> tokens = adapter.getSemanticTokens(path);
    printAllToken(tokens);

    assertToken(6, 1, "function", SemanticTokenType.KEYWORD, tokens.get(0));
    assertToken(6, 10, "main", SemanticTokenType.FUNCTION, tokens.get(1));

    assertToken(7, 3, "println", SemanticTokenType.VARIABLE, tokens.get(2));
    assertToken(7, 11, "\"Hello World!\"", SemanticTokenType.STRING,
        tokens.get(3));
    assertToken(7, 3, "println", SemanticTokenType.FUNCTION, tokens.get(4));

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

    List<int[]> tokens = adapter.getSemanticTokens(path);
    printAllToken(tokens);

    assertToken(1, 1, "function", SemanticTokenType.KEYWORD, tokens.get(0));
    assertToken(1, 10, "recursion", SemanticTokenType.FUNCTION, tokens.get(1));
    assertToken(1, 20, "n", SemanticTokenType.PARAMETER, tokens.get(2));

    assertToken(2, 3, "if", SemanticTokenType.KEYWORD, tokens.get(3));
    assertToken(2, 7, "n", SemanticTokenType.VARIABLE, tokens.get(4));
    assertToken(2, 11, "0", SemanticTokenType.NUMBER, tokens.get(5));
    assertToken(2, 9, ">", SemanticTokenType.OPERATOR, tokens.get(6));

    for (int i = 0; i < 3; i += 1) {
      assertToken(3 + i, 5, "recursion", SemanticTokenType.VARIABLE, tokens.get(7 + (i * 5)));
      assertToken(3 + i, 15, "n", SemanticTokenType.VARIABLE, tokens.get(8 + (i * 5)));
      assertToken(3 + i, 19, "1", SemanticTokenType.NUMBER, tokens.get(9 + (i * 5)));
      assertToken(3 + i, 17, "-", SemanticTokenType.OPERATOR, tokens.get(10 + (i * 5)));
      assertToken(3 + i, 5, "recursion", SemanticTokenType.FUNCTION, tokens.get(11 + (i * 5)));
    }

    assertToken(7, 12, "n", SemanticTokenType.VARIABLE, tokens.get(22));
    assertToken(7, 5, "return", SemanticTokenType.KEYWORD, tokens.get(23));

    assertToken(6, 5, "else", SemanticTokenType.KEYWORD, tokens.get(24));

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

    assertRange(1, 10, 1, 13, add.getSelectionRange());
    assertRange(1, 10, 3, 2, add.getRange());

    DocumentSymbol loop = symbols.get(1);
    assertEquals("loop(n)", loop.getDetail());
    assertRange(4, 10, 4, 14, loop.getSelectionRange());
    assertRange(4, 10, 7, 2, loop.getRange());
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

    Hover hover = adapter.hover(path, new Position(7, 12));
    assertNotNull(hover);

    Range r = hover.getRange();

    assertEquals(7, r.getStart().getLine());
    assertEquals(11, r.getStart().getCharacter());

    assertEquals(7, r.getEnd().getLine());
    assertEquals(11 + "loop".length(), r.getEnd().getCharacter());

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

    SignatureHelp help = adapter.signatureHelp(path, new Position(7, 12), null);
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
    var diag = adapter.parse(
        "function loop(  n  ) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path);
    assertTrue(diag.isEmpty());

    path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    diag = adapter.parse(
        "function loop(n, b, c) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function baz() {\n"
            + "  println(loop(1000, 1, 2));  \n"
            + "}",
        path);
    assertTrue(diag.isEmpty());

    List<SymbolInformation> results = new ArrayList<>();
    adapter.workspaceSymbol(results, "");

    assertEquals(4, results.size());

    results = new ArrayList<>();
    adapter.workspaceSymbol(results, "l");

    assertEquals(2, results.size());
  }

  @Test
  public void testGotoDefinition() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path1 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var diag = adapter.parse(
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
    assertTrue(diag.isEmpty());

    String path2 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    diag = adapter.parse(
        "function loop(n, b, c) {\n"
            + "  i = 0;\n"
            + "  return i;\n"
            + "}\n"
            + "function baz() {\n"
            + "  println(loop(1000, 1, 2));  \n"
            + "}",
        path2);
    assertTrue(diag.isEmpty());

    var locations = adapter.getDefinitions(path2, new Position(6, 12));
    assertEquals(2, locations.size());

    var loop1 = locations.get(0);
    assertEquals(path2, loop1.getTargetUri());
    assertEquals(1, loop1.getTargetSelectionRange().getStart().getLine());
    assertEquals(6, loop1.getOriginSelectionRange().getStart().getLine());

    var loop2 = locations.get(1);
    assertEquals(path1, loop2.getTargetUri());
    assertEquals(2, loop2.getTargetSelectionRange().getStart().getLine());
    assertEquals(6, loop2.getOriginSelectionRange().getStart().getLine());
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

    List<DocumentHighlight> hs = adapter.getHighlight(path, new Position(8, 2));
    assertNotNull(hs);

    assertEquals(4, hs.size());

    assertEquals(1, hs.get(0).getRange().getStart().getLine());
    assertEquals(10, hs.get(0).getRange().getStart().getCharacter());

    assertEquals(7, hs.get(1).getRange().getStart().getLine());
    assertEquals(1, hs.get(1).getRange().getStart().getCharacter());

    assertEquals(8, hs.get(2).getRange().getStart().getLine());
    assertEquals(1, hs.get(2).getRange().getStart().getCharacter());

    assertEquals(9, hs.get(3).getRange().getStart().getLine());
    assertEquals(11, hs.get(3).getRange().getStart().getCharacter());
  }

  @Test
  public void testReferencesIncludeDecls() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path1 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var diag = adapter.parse(
        "function loop(  n  ) {\n"
            + "  loop(1);\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path1);
    assertTrue(diag.isEmpty());

    String path2 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    diag = adapter.parse(
        "function loop(n, b, c) {\n"
            + "}\n"
            + "function baz() {\n"
            + "  println(loop(1000, 1, 2));  \n"
            + "}",
        path2);
    assertTrue(diag.isEmpty());

    List<Location> result = adapter.getReferences(path2, new Position(4, 12), true);

    assertEquals(5, result.size());

    assertEquals(path1, result.get(0).getUri());
    assertEquals(1, result.get(0).getRange().getStart().getLine());

    assertEquals(path1, result.get(1).getUri());
    assertEquals(2, result.get(1).getRange().getStart().getLine());

    assertEquals(path1, result.get(2).getUri());
    assertEquals(6, result.get(2).getRange().getStart().getLine());

    assertEquals(path2, result.get(3).getUri());
    assertEquals(1, result.get(3).getRange().getStart().getLine());

    assertEquals(path2, result.get(4).getUri());
    assertEquals(4, result.get(4).getRange().getStart().getLine());
  }

  @Test
  public void testReferencesExcludeDecls() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path1 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var diag = adapter.parse(
        "function loop(  n  ) {\n"
            + "  loop(1);\n"
            + "}\n"
            + "function main(  ) {\n"
            + "  i = 0;\n"
            + "  println(loop(1000));  \n"
            + "}",
        path1);
    assertTrue(diag.isEmpty());

    String path2 = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test2.sl";
    diag = adapter.parse(
        "function loop(n, b, c) {\n"
            + "}\n"
            + "function baz() {\n"
            + "  println(loop(1000, 1, 2));  \n"
            + "}",
        path2);
    assertTrue(diag.isEmpty());

    List<Location> result = adapter.getReferences(path2, new Position(4, 12), false);

    assertEquals(3, result.size());

    assertEquals(path1, result.get(0).getUri());
    assertEquals(2, result.get(0).getRange().getStart().getLine());

    assertEquals(path1, result.get(1).getUri());
    assertEquals(6, result.get(1).getRange().getStart().getLine());

    assertEquals(path2, result.get(2).getUri());
    assertEquals(4, result.get(2).getRange().getStart().getLine());
  }

  @Test
  public void testSyntaxErrors() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    var diag = adapter.parse(
        "function main() {\n"
            + "  i = 0;\n"
            + "  println(loop 1000));  \n"
            + "}",
        path);

    // since we don't bail on parsing errors, there will be multiple
    assertEquals(3, diag.size());

    assertEquals("missing ';' at '('", diag.get(0).getMessage());
    assertEquals(3, diag.get(0).getRange().getStart().getLine());
    assertEquals(10, diag.get(0).getRange().getStart().getCharacter());
  }
}
