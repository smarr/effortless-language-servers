import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import som.langserv.LanguageAdapter;
import som.langserv.newspeak.NewspeakAdapter;


public class TokenTest {

  @Test
  public void test() throws IOException, URISyntaxException {
    LanguageAdapter adapter = new NewspeakAdapter();
    // NewspeakAdapter na = new NewspeakAdapter();
    // what vs code tels you as the line and col needs to be - 1
    String testFilePath =
        "/home/hburchell/vscode/SOMns-vscode/server/libs/SOMns/core-lib/Hello.ns";

    List<String> lines = Files.readAllLines(Paths.get(testFilePath), Charset.defaultCharset());

    adapter.parse(
        "class Hello usingPlatform: platform = Value ()(\n" +
            "  public main: args = (\n" +
            "    'Hello World!' println.\n" +
            "    args from: 2 to: args size do: [ :arg | arg print. ' ' print ].\n" +
            "    '' println.\n" +
            "    ^ 0\n" +
            "  )\n" +
            ")\n" +
            "",
        testFilePath);

    List<Integer> tokens = adapter.getTokenPositions(testFilePath);

    List<Integer> exspectedTokens =
        Arrays.asList(1, 2, 6, 1, 0,
            1, 9, 4, 2, 0,
            1, 15, 4, 6, 0);

    assertTrue(tokens.containsAll(exspectedTokens));

  }

}
// for Hello.ns
//
// keyword class token 1 1 5
// class identifer token 1 7 11
// using keyword token 1 19 13
// identifier token 1 34 8
// varible token 1 45 5
// keyword 2 3 6
// method name 2 10 4
// varible 2 16 4
// literual string 3 5 14
// method 3 20 7
// varible 4 5 4
// keyword 4 10 4
// litural number 4 16 1
// keyword 4 18 2
// varible 4 22 4
// varible 4 27 4
// keyword 4 32 2
// varible 4 39 3
// varible 4 45 3
// method 4 49 5
// litural string 4 56 3
// method 4 60 5
// litural string 5 5 3
// method 5 8 15
