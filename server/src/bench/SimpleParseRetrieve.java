package bench;

import awfy.LspRequest.Result;
import bench.generator.SimpleFragments;
import som.langserv.simple.SimpleAdapter;
import som.langserv.structure.LanguageElement;


public class SimpleParseRetrieve extends RetrieveBenchmark {

  public SimpleParseRetrieve() {
    super(SimpleFragments.basicFunctions, "", "", "/benchmark.sl", new SimpleAdapter());
  }

  @Override
  public boolean verifyResult(final Object result) {
    Result r = (Result) result;

    int s = r.symbols.size();
    int t = r.tokens.size();
    LanguageElement e = (LanguageElement) r.symbols.get(0).getRight();
    int all = e.getAllChildren().size();

    if (problemSize == 1) {
      return s == 1 && all == 3 && t == 40;
    }

    if (problemSize == 10) {
      return s == 4 && all == 3 && t == 150;
    }

    if (problemSize == 100) {
      return s == 18 && all == 3 && t == 1180;
    }

    if (problemSize == 1000) {
      return s == 165 && all == 3 && t == 11820;
    }

    if (problemSize == 10000) {
      return s == 1633 && all == 3 && t == 118730;
    }

    if (problemSize == 100000) {
      return s == 16700 && all == 3 && t == 1184130;
    }

    if (problemSize == 1000000) {
      return s == 166619 && all == 3 && t == 11818855;
    }

    System.out.println("Unsupported problemSize: " + problemSize);
    System.out.println("symbols: " + s);
    System.out.println("all: " + all);
    System.out.println("tokens: " + t);

    return false;
  }
}
