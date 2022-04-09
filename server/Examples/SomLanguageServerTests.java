package som.langserv;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.lsp4j.Diagnostic;
import org.junit.Test;
//import org.junit.Test;

import som.langserv.newspeak.NewspeakAdapter;
import trufflesom.tests.TruffleTestSetup;


public class SomLanguageServerTests {
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


  @Test
  public void semanticTokensTest1() throws URISyntaxException, IOException {
    // test
    NewspeakAdapter na = new NewspeakAdapter();
    String testFilePath = "/home/hburchell/vscode/SOMns-vscode/test/examples/Hello.ns";

    List<String> lines = Files.readAllLines(Paths.get(testFilePath),Charset.defaultCharset());

    na.parse(lines.toString(),testFilePath);

    List<Integer> tokens = na.getTokenPositions(testFilePath);


    assertEquals(null, parser);
  }

}
