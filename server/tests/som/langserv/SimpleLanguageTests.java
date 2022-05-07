package som.langserv;

import static org.junit.Assert.assertEquals;
import static som.langserv.Helpers.assertToken;
import static som.langserv.Helpers.printAllToken;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import simple.SimpleLanguageParser;
import som.langserv.simple.SimpleAdapter;


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

    assertToken(6, 0, "function", SemanticTokenType.KEYWORD, tokens.get(0));
    assertToken(6, 9, "main", SemanticTokenType.FUNCTION, tokens.get(1));

    assertToken(7, 2, "println", SemanticTokenType.VARIABLE, tokens.get(2));
    assertToken(7, 10, "\"Hello World!\"", SemanticTokenType.STRING,
        tokens.get(3));
    assertToken(7, 2, "println", SemanticTokenType.FUNCTION, tokens.get(4));

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

    assertToken(1, 0, "function", SemanticTokenType.KEYWORD, tokens.get(0));
    assertToken(1, 9, "recursion", SemanticTokenType.FUNCTION, tokens.get(1));
    assertToken(1, 19, "n", SemanticTokenType.PARAMETER, tokens.get(2));

    assertToken(2, 2, "if", SemanticTokenType.KEYWORD, tokens.get(3));
    assertToken(2, 6, "n", SemanticTokenType.VARIABLE, tokens.get(4));
    assertToken(2, 10, "0", SemanticTokenType.NUMBER, tokens.get(5));
    assertToken(2, 8, ">", SemanticTokenType.OPERATOR, tokens.get(6));

    for (int i = 0; i < 3; i += 1) {
      assertToken(3 + i, 4, "recursion", SemanticTokenType.VARIABLE, tokens.get(7 + (i * 5)));
      assertToken(3 + i, 14, "n", SemanticTokenType.VARIABLE, tokens.get(8 + (i * 5)));
      assertToken(3 + i, 18, "1", SemanticTokenType.NUMBER, tokens.get(9 + (i * 5)));
      assertToken(3 + i, 16, "-", SemanticTokenType.OPERATOR, tokens.get(10 + (i * 5)));
      assertToken(3 + i, 4, "recursion", SemanticTokenType.FUNCTION, tokens.get(11 + (i * 5)));
    }

    assertToken(7, 11, "n", SemanticTokenType.VARIABLE, tokens.get(22));
    assertToken(7, 4, "return", SemanticTokenType.KEYWORD, tokens.get(23));

    assertToken(6, 4, "else", SemanticTokenType.KEYWORD, tokens.get(24));

    assertEquals(25, tokens.size());
  }
}
