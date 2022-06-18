package bench;

import som.langserv.DocumentServiceImpl;
import som.langserv.LanguageAdapter;


public abstract class LsBenchmark extends ParseBenchmark {

  protected final DocumentServiceImpl docServer;

  public LsBenchmark(final String[] fragments, final String header, final String footer,
      final String path, final LanguageAdapter adapter) {
    super(fragments, header, footer, path);
    this.docServer = new DocumentServiceImpl(new LanguageAdapter[] {adapter});
  }

  @Override
  public final Object benchmark() {
    return docServer.parseDocument(uri, fileToBeParsed);
  }
}
