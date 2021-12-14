import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Language;
//import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.Test;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.sl.SLLanguage;
import com.oracle.truffle.sl.parser.SLNodeFactory;
import com.oracle.truffle.sl.parser.SimpleLanguageLexer;
import com.oracle.truffle.sl.parser.SimpleLanguageParser;


public class SimpleLanguageParserTest {

  @Test
  public void test() throws URISyntaxException, IOException {

    Context context = Context.newBuilder("sl").in(System.in).out(System.out)
                             .options(new HashMap<>()).build();
    context.enter();
    // SLLanguageProvider provider = new SLLanguageProvider();
    // TruffleLanguage<SLContext> langcontext = (TruffleLanguage<SLContext>) provider.create();
    // SLFunctionRegistry reg = langcontext.getContextReference().get().getFunctionRegistry();

    String text = "function main() {  \n" +
        "   \n" +
        "} ";
    String path = "/SOM-LS/libs/simplelanguage/language/tests/HelloWorld.sl";

    Source source = Source.newBuilder("sl", text, path).build();
    Value result = context.eval("sl", "function main() {}");
    Map<String, Language> map = context.getEngine().getLanguages();

    Language slinstance = map.get("sl");
    System.out.print(slinstance.getClass());

    // somthing to breakpoint onto
    SLLanguage ss = new SLLanguage().returnInstence();

    System.out.print("");

  }

  @Test
  public void test2() throws URISyntaxException, IOException {
    Context context = Context.newBuilder("sl").in(System.in).out(System.out)
                             .options(new HashMap<>()).build();
    context.enter();
    String text = "function main() {  \n" +
        "  println(\"Hello World!\");  \n" +
        "} ";
    String path = "/SOM-LS/libs/simplelanguage/language/tests/HelloWorld.sl";

    Source source = Source.newBuilder("sl", text, path).build();
    Value result = context.eval("sl", text);
    Map<String, Language> map = context.getEngine().getLanguages();

    Language slinstance = map.get("sl");

    SimpleLanguageLexer lexer =
        new SimpleLanguageLexer(CharStreams.fromString(text));
    SimpleLanguageParser parser = new SimpleLanguageParser(new CommonTokenStream(lexer));
    lexer.removeErrorListeners();
    parser.removeErrorListeners();
    // BailoutErrorListener listener = new BailoutErrorListener(source);
    // lexer.addErrorListener(listener);
    // parser.addErrorListener(listener);
    // String path = "/SOM-LS/libs/simplelanguage/language/tests/HelloWorld.sl";
    // Source source = Source.newBuilder("sl", text, path).build();
    parser.factory = new SLNodeFactory(SLLanguage.returnInstence(), source);
    // parser.source = source;
    parser.simplelanguage();
  }
}

/*
 * Builder builder = Universe.createContextBuilder();
 * String[] args = new String[] {""};
 * builder.arguments(SLLanguage.ID, args);
 * Context context = builder.build();
 * context.enter();
 */

/*
 * Source source2 = Source
 * .newBuilder(SLLanguage.ID, text,
 * path)
 * .name(path).mimeType(SLLanguage.MIME_TYPE)
 * .uri(new URI(path).normalize()).build();
 */
