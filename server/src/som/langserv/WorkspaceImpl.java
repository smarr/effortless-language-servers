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


public class WorkspaceImpl implements WorkspaceService {

  private final LanguageAdapter adapters[];

  public WorkspaceImpl(final LanguageAdapter languageAdapters[]) {
    this.adapters = languageAdapters;
  }

  @Override
  public CompletableFuture<List<? extends SymbolInformation>> symbol(
      final WorkspaceSymbolParams params) {
    ArrayList<SymbolInformation> result = new ArrayList<>();

    for (LanguageAdapter adapter : adapters) {
      adapter.workspaceSymbol(result, params.getQuery());
    }
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
    // The only command is Minitest.COMMAND, which needs SOMns,
    // and SOMns support is currently disabled.
    return CompletableFuture.completedFuture(new Object());
  }
}
