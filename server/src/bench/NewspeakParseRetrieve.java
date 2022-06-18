package bench;

import awfy.LspRequest.Result;
import bench.generator.NewspeakFragments;
import som.langserv.newspeak.NewspeakAdapter;
import som.langserv.structure.LanguageElement;


public class NewspeakParseRetrieve extends RetrieveBenchmark {

  public NewspeakParseRetrieve() {
    super(NewspeakFragments.methodFragments, "class BenchmarkClass = ()(", ")\n",
        "/BenchmarkClass.ns", new NewspeakAdapter());
  }

  @Override
  public boolean verifyResult(final Object result) {
    Result r = (Result) result;

    int s = r.symbols.size();
    int t = r.tokens.size();
    LanguageElement e = (LanguageElement) r.symbols.get(0).getRight();
    int allChildren = e.getAllChildren().size();

    if (problemSize == 1) {
      return s == 1 && allChildren == 1 && t == 80;
    }

    if (problemSize == 10) {
      return s == 1 && allChildren == 2 && t == 95;
    }

    if (problemSize == 100) {
      return s == 1 && allChildren == 14 && t == 1135;
    }

    if (problemSize == 1000) {
      return s == 1 && allChildren == 154 && t == 10590;
    }

    if (problemSize == 10000) {
      return s == 1 && allChildren == 1575 && t == 104820;
    }

    if (problemSize == 100000) {
      return s == 1 && allChildren == 15261 && t == 1058660;
    }

    if (problemSize == 1000000) {
      return s == 1 && allChildren == 153885 && t == 10556495;
    }

    System.out.println("Unsupported problemSize: " + problemSize);
    System.out.println("symbols: " + s);
    System.out.println("all: " + allChildren);
    System.out.println("tokens: " + t);

    return false;
  }
}
