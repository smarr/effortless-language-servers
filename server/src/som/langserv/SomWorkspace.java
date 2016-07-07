package som.langserv;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.typefox.lsapi.DidChangeConfigurationParams;
import io.typefox.lsapi.DidChangeWatchedFilesParams;
import io.typefox.lsapi.SymbolInformation;
import io.typefox.lsapi.WorkspaceSymbolParams;
import io.typefox.lsapi.services.WorkspaceService;


public class SomWorkspace implements WorkspaceService {

  @Override
  public CompletableFuture<List<? extends SymbolInformation>> symbol(
      WorkspaceSymbolParams params) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void didChangeConfiguraton(DidChangeConfigurationParams params) {
    // TODO Auto-generated method stub

  }

  @Override
  public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
    // TODO Auto-generated method stub

  }

}
