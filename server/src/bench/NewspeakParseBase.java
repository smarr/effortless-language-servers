package bench;

import java.net.URISyntaxException;

import com.oracle.truffle.api.source.Source;

import bd.basic.ProgramDefinitionError;
import bench.generator.NewspeakFragments;
import som.VM;
import som.compiler.MixinDefinition;
import som.compiler.SourcecodeCompiler;
import som.langserv.newspeak.NewspeakAdapter;


public class NewspeakParseBase extends ParseBenchmark {

  private Source fileSource;

  private SourcecodeCompiler compiler;

  public NewspeakParseBase() {
    super(NewspeakFragments.methodFragments, "class BenchmarkClass = ()(", ")\n",
        "BenchmarkClass.ns");
  }

  @Override
  public void initialize(final int problemSize) {
    super.initialize(problemSize);

    try {
      this.fileSource = NewspeakAdapter.createSource(fileToBeParsed, "file:/BenchmarkClass.ns",
          "/BenchmarkClass.ns");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    VM vm = NewspeakAdapter.initializePolyglot();
    compiler = new SourcecodeCompiler(vm.getLanguage());
  }

  @Override
  public Object benchmark() {
    try {
      return compiler.compileModule(fileSource, null);
    } catch (ProgramDefinitionError e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean verifyResult(final Object result) {
    MixinDefinition module = (MixinDefinition) result;
    int methods = module.getInstanceDispatchables().size();

    if (problemSize == 1) {
      return methods == 1;
    }

    if (problemSize == 10) {
      return methods == 2;
    }

    if (problemSize == 100) {
      return methods == 14;
    }

    if (problemSize == 1000) {
      return methods == 154;
    }

    if (problemSize == 10000) {
      return methods == 1575;
    }

    if (problemSize == 100000) {
      return methods == 15261;
    }

    if (problemSize == 1000000) {
      return methods == 153885;
    }

    System.out.println("Unsupported problemSize: " + problemSize);
    System.out.println("methods: " + methods);

    return false;
  }
}
