package bench;

import bench.generator.SimpleFragments;
import som.langserv.simple.SimpleAdapter;
import som.langserv.structure.DocumentStructures;


public class SimpleParseLs extends LsBenchmark {

  public SimpleParseLs() {
    super(SimpleFragments.basicFunctions, "", "", "/benchmark.sl", new SimpleAdapter());
  }

  @Override
  public boolean verifyResult(final Object result) {
    DocumentStructures s = (DocumentStructures) result;

    int defs = s.getAllDefinitions().size();
    int refs = s.getAllReferences().size();

    if (problemSize == 1) {
      return defs == 4 && refs == 5;
    }

    if (problemSize == 10) {
      return defs == 8 && refs == 6;
    }

    if (problemSize == 100) {
      return defs == 34 && refs == 34;
    }

    if (problemSize == 1000) {
      return defs == 167 && refs == 34;
    }

    if (problemSize == 10000) {
      return defs == 1499 && refs == 34;
    }

    if (problemSize == 100000) {
      return defs == 15194 && refs == 34;
    }

    if (problemSize == 1000000) {
      return defs == 151521 && refs == 34;
    }

    System.out.println("Unsupported problemSize: " + problemSize);
    System.out.println("defs: " + defs);
    System.out.println("refs: " + refs);

    return false;
  }
}
