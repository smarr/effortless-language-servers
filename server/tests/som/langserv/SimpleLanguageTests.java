package som.langserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static som.langserv.Helpers.assertRange;
import static som.langserv.Helpers.assertToken;
import static som.langserv.Helpers.printAllToken;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
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
}
