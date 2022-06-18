package bench;

import awfy.LspRequest.Result;
import bench.generator.SomFragments;
import som.langserv.som.SomAdapter;
import som.langserv.structure.LanguageElement;


public class SomParseRetrieve extends RetrieveBenchmark {

  public SomParseRetrieve() {
    super(SomFragments.methods,
        "BenchmarkClass = (", ")\n", "/BenchmarkClass.som", new SomAdapter());
  }

  @Override
  public boolean verifyResult(final Object result) {
    Result r = (Result) result;

    int s = r.symbols.size();
    int t = r.tokens.size();
    LanguageElement e = (LanguageElement) r.symbols.get(0).getRight();
    int allChildren = e.getAllChildren().size();

    if (problemSize == 1) {
      return s == 1 && allChildren == 1 && t == 25;
    }

    if (problemSize == 10) {
      return s == 1 && allChildren == 2 && t == 120;
    }

    if (problemSize == 100) {
      return s == 1 && allChildren == 20 && t == 960;
    }

    if (problemSize == 1000) {
      return s == 1 && allChildren == 159 && t == 10745;
    }

    if (problemSize == 10000) {
      return s == 1 && allChildren == 1689 && t == 103810;
    }

    if (problemSize == 100000) {
      return s == 1 && allChildren == 16726 && t == 1055020;
    }

    if (problemSize == 1000000) {
      return s == 1 && allChildren == 168675 && t == 10518020;
    }

    System.out.println("Unsupported problemSize: " + problemSize);
    System.out.println("symbols: " + s);
    System.out.println("all: " + allChildren);
    System.out.println("tokens: " + t);

    return false;
  }
}
