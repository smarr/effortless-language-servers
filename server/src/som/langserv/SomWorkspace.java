package som.langserv;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.services.WorkspaceService;

import som.langserv.newspeak.Minitest;
import som.langserv.newspeak.NewspeakAdapter;


public class SomWorkspace implements WorkspaceService {

  private final NewspeakAdapter   som;
  private final TruffleSomAdapter tsom;

  public SomWorkspace(final NewspeakAdapter som, final TruffleSomAdapter truffleSom) {
    this.som = som;
    this.tsom = truffleSom;
  }

  @Override
  public CompletableFuture<List<? extends SymbolInformation>> symbol(
      final WorkspaceSymbolParams params) {
    ArrayList<SymbolInformation> result = new ArrayList<>();
    result.addAll(som.getAllSymbolInfo(params.getQuery()));
    result.addAll(tsom.getAllSymbolInfo(params.getQuery()));
    return CompletableFuture.completedFuture(result);
  }

  @Override
  public void didChangeConfiguration(final DidChangeConfigurationParams params) {
    // TODO Auto-generated method stub
  }

  @Override
  public void didChangeWatchedFiles(final DidChangeWatchedFilesParams params) {
    // TODO Auto-generated method stub
  }

  @Override
  public CompletableFuture<Object> executeCommand(final ExecuteCommandParams params) {
    if (params.getCommand().equals(params.getCommand())) {
      Minitest.executeTest(som, params.getArguments());
    }
    return CompletableFuture.completedFuture(new Object());
  }
}
