package bench;

import awfy.LspRequest;
import som.langserv.DocumentServiceImpl;
import som.langserv.LanguageAdapter;


public abstract class RetrieveBenchmark extends ParseBenchmark {

  protected final DocumentServiceImpl docServer;

  public RetrieveBenchmark(final String[] fragments, final String header, final String footer,
      final String path, final LanguageAdapter adapter) {
    super(fragments, header, footer, path);
    this.docServer = new DocumentServiceImpl(new LanguageAdapter[] {adapter});
  }

  @Override
  public final Object benchmark() {
    return LspRequest.doRequest(uri, fileToBeParsed, docServer);
  }
}
