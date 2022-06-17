package som.langserv;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.services.LanguageClient;


public class TestLanguageClient implements LanguageClient {
  public final List<PublishDiagnosticsParams> diagnostics = new ArrayList<>();

  @Override
  public void telemetryEvent(final Object object) {}

  @Override
  public void publishDiagnostics(final PublishDiagnosticsParams diagnostics) {
    if (!diagnostics.getDiagnostics().isEmpty()) {
      this.diagnostics.add(diagnostics);
    }
  }

  @Override
  public void showMessage(final MessageParams messageParams) {}

  @Override
  public CompletableFuture<MessageActionItem> showMessageRequest(
      final ShowMessageRequestParams requestParams) {
    return null;
  }

  @Override
  public void logMessage(final MessageParams message) {}

}
