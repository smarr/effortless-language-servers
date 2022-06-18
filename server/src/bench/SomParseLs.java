package bench;

import bench.generator.SomFragments;
import som.langserv.som.SomAdapter;
import som.langserv.structure.DocumentStructures;


public class SomParseLs extends LsBenchmark {

  public SomParseLs() {
    super(SomFragments.methods, "BenchmarkClass = (", ")\n", "/BenchmarkClass.som",
        new SomAdapter());
  }

  @Override
  public boolean verifyResult(final Object result) {
    DocumentStructures s = (DocumentStructures) result;

    int defs = s.getAllDefinitions().size();
    int refs = s.getAllReferences().size();

    if (problemSize == 1) {
      return defs == 2 && refs == 2;
    }

    if (problemSize == 10) {
      return defs == 7 && refs == 9;
    }

    if (problemSize == 100) {
      return defs == 58 && refs == 65;
    }

    if (problemSize == 1000) {
      return defs == 577 && refs == 349;
    }

    if (problemSize == 10000) {
      return defs == 5837 && refs == 2849;
    }

    if (problemSize == 100000) {
      return defs == 58695 && refs == 28168;
    }

    if (problemSize == 1000000) {
      return defs == 586133 && refs == 279603;
    }

    System.out.println("Unsupported problemSize: " + problemSize);
    System.out.println("defs: " + defs);
    System.out.println("refs: " + refs);

    return false;
  }
}
