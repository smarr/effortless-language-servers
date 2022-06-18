package bench;

import bench.generator.NewspeakFragments;
import som.langserv.newspeak.NewspeakAdapter;
import som.langserv.structure.DocumentStructures;


public class NewspeakParseLs extends LsBenchmark {

  public NewspeakParseLs() {
    super(NewspeakFragments.methodFragments, "class BenchmarkClass = ()(", ")\n",
        "/BenchmarkClass.ns", new NewspeakAdapter());
  }

  @Override
  public boolean verifyResult(final Object result) {
    DocumentStructures structures = (DocumentStructures) result;
    int defs = structures.getAllDefinitions().size();

    if (problemSize == 1) {
      return defs == 4;
    }

    if (problemSize == 10) {
      return defs == 5;
    }

    if (problemSize == 100) {
      return defs == 52;
    }

    if (problemSize == 1000) {
      return defs == 496;
    }

    if (problemSize == 10000) {
      return defs == 4933;
    }

    if (problemSize == 100000) {
      return defs == 49598;
    }

    if (problemSize == 1000000) {
      return defs == 495367;
    }

    System.out.println("Unsupported problemSize: " + problemSize);
    System.out.println("defs: " + defs);

    return false;
  }
}
