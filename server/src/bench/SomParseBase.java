package bench;

import com.oracle.truffle.api.source.Source;

import bdt.basic.ProgramDefinitionError;
import bench.generator.SomFragments;
import som.langserv.som.SomAdapter;
import trufflesom.compiler.Parser;
import trufflesom.compiler.SourcecodeCompiler;
import trufflesom.compiler.SourcecodeCompiler.AstCompiler;
import trufflesom.interpreter.SomLanguage;
import trufflesom.vmobjects.SClass;
import trufflesom.vmobjects.SSymbol;


public class SomParseBase extends ParseBenchmark {

  private Source fileSource;

  private SourcecodeCompiler compiler;

  public SomParseBase() {
    super(SomFragments.methods,
        "BenchmarkClass = (", ")\n", "/BenchmarkClass.som");
  }

  @Override
  public void initialize(final int problemSize) {
    super.initialize(problemSize);

    this.fileSource = SomLanguage.getSyntheticSource(fileToBeParsed, "BenchmarkClass.som");

    compiler = new AstCompiler();
    SomAdapter.initializePolyglot(compiler, new SSymbol[16]);
  }

  @Override
  public Object benchmark() {
    try {
      Parser<?> parser = compiler.createParser(fileToBeParsed, fileSource, null);
      SClass result = SourcecodeCompiler.compile(parser, null);
      return result;
    } catch (ProgramDefinitionError e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean verifyResult(final Object result) {
    SClass clazz = (SClass) result;
    Object[] methods = clazz.getInstanceInvokables().getObjectStorage();

    if (problemSize == 1) {
      return methods.length == 1;
    }

    if (problemSize == 10) {
      return methods.length == 2;
    }

    if (problemSize == 100) {
      return methods.length == 20;
    }

    if (problemSize == 1000) {
      return methods.length == 159;
    }

    if (problemSize == 10000) {
      return methods.length == 1689;
    }

    if (problemSize == 100000) {
      return methods.length == 16726;
    }

    if (problemSize == 1000000) {
      return methods.length == 168675;
    }

    System.out.println("Unsupported problemSize: " + problemSize);
    System.out.println("methods: " + methods.length);

    return false;
  }
}
