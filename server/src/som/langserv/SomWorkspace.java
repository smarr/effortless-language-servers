package som.langserv;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.services.WorkspaceService;


public class SomWorkspace implements WorkspaceService {

  private final SomAdapter som;

  public SomWorkspace(final SomAdapter som) {
    this.som = som;
  }

  @Override
  public CompletableFuture<List<? extends SymbolInformation>> symbol(
      final WorkspaceSymbolParams params) {
    // TODO: make this work for both langs
    List<? extends SymbolInformation> result = som.getAllSymbolInfo(params.getQuery());
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
      SomMinitest.executeTest(som, params.getArguments());
    }
    return CompletableFuture.completedFuture(new Object());
  }
}
