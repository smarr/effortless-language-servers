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

import som.langserv.lens.Minitest;
import som.langserv.newspeak.NewspeakAdapter;


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
    assert adapters != null && adapters.length > 0
        && adapters[0] instanceof NewspeakAdapter : "Currently only the Newspeak adapter supports this, so it is hardcoded";
    if (params.getCommand().equals(params.getCommand())) {
      Minitest.executeTest((NewspeakAdapter) adapters[0], params.getArguments());
    }
    return CompletableFuture.completedFuture(new Object());
  }
}
