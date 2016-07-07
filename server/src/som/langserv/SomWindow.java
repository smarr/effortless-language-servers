package som.langserv;

import java.util.function.Consumer;

import io.typefox.lsapi.MessageParams;
import io.typefox.lsapi.ShowMessageRequestParams;
import io.typefox.lsapi.services.WindowService;


public class SomWindow implements WindowService {

  @Override
  public void onShowMessage(final Consumer<MessageParams> callback) {
    // TODO Auto-generated method stub
  }

  @Override
  public void onShowMessageRequest(final Consumer<ShowMessageRequestParams> callback) {
    // TODO Auto-generated method stub
  }

  @Override
  public void onLogMessage(final Consumer<MessageParams> callback) {
    // TODO Auto-generated method stub
  }
}
