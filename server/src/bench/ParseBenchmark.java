package bench;

import awfy.Benchmark;
import bench.generator.FileGenerator;


public abstract class ParseBenchmark extends Benchmark {

  protected String fileToBeParsed;
  protected int    problemSize;

  protected final String   header;
  protected final String   footer;
  protected final String[] fragments;
  protected final String   path;

  protected final String uri;

  public ParseBenchmark(final String[] fragments, final String header, final String footer,
      final String path) {
    this.header = header;
    this.footer = footer;
    this.fragments = fragments;
    this.path = path;
    this.uri = "file:" + path;
  }

  @Override
  public void initialize(final int problemSize) {
    this.problemSize = problemSize;
    generateFile(problemSize);
  }

  @Override
  public final boolean innerBenchmarkLoop(final int unused) {
    if (!verifyResult(benchmark())) {
      return false;
    }

    return true;
  }

  private void generateFile(final int problemSize) {
    this.fileToBeParsed = FileGenerator.generateFile(problemSize, fragments, header, footer);
  }

}
