package bench;

import org.antlr.v4.runtime.CharStreams;

import awfy.SimpleBaseParser;
import bench.generator.SimpleFragments;
import simple.SimpleLanguageLexer;


public class SimpleParseBase extends ParseBenchmark {

  public SimpleParseBase() {
    super(SimpleFragments.basicFunctions, "", "", "/benchmark.sl");
  }

  @Override
  public Object benchmark() {
    SimpleLanguageLexer lexer =
        new SimpleLanguageLexer(CharStreams.fromString(fileToBeParsed));
    SimpleBaseParser parser = new SimpleBaseParser(lexer);

    parser.parse();

    return parser.getState();
  }

  @Override
  public boolean verifyResult(final Object result) {
    int state = (Integer) result;
    return state == -1;
  }
}
