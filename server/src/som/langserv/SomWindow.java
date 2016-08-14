package som.langserv;

import java.util.function.Consumer;

import io.typefox.lsapi.MessageParams;
import io.typefox.lsapi.ShowMessageRequestParams;
import io.typefox.lsapi.services.WindowService;


public class SomWindow implements WindowService {

  private Consumer<MessageParams> showCallback;
  private Consumer<MessageParams> logCallback;
  private Consumer<ShowMessageRequestParams> showRequestCallback;

  @Override
  public void onShowMessage(final Consumer<MessageParams> callback) {
    this.showCallback = callback;
  }

  @Override
  public void onShowMessageRequest(final Consumer<ShowMessageRequestParams> callback) {
    this.showRequestCallback = callback;
  }

  @Override
  public void onLogMessage(final Consumer<MessageParams> callback) {
    this.logCallback = callback;
  }

  public void show(final MessageParams msg) {
    if (showCallback == null) {
      ServerLauncher.logErr("[SomWindow] showCallback == null on show(.)");
      return;
    }

    showCallback.accept(msg);
  }

  public void log(final MessageParams msg) {
    if (logCallback == null) {
      ServerLauncher.logErr("[SomWindow] logCallback == null on log(.)");
      return;
    }

    logCallback.accept(msg);
  }
}
