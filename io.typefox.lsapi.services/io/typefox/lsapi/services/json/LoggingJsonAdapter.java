package io.typefox.lsapi.services.json;

import io.typefox.lsapi.Message;
import io.typefox.lsapi.NotificationMessage;
import io.typefox.lsapi.RequestMessage;
import io.typefox.lsapi.ResponseMessage;
import io.typefox.lsapi.services.LanguageServer;
import io.typefox.lsapi.services.json.LanguageServerProtocol;
import io.typefox.lsapi.services.json.LanguageServerToJsonAdapter;
import java.io.PrintWriter;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;

@SuppressWarnings("all")
public class LoggingJsonAdapter extends LanguageServerToJsonAdapter {
  @Accessors(AccessorType.PUBLIC_SETTER)
  private PrintWriter errorLog;
  
  @Accessors(AccessorType.PUBLIC_SETTER)
  private PrintWriter messageLog;
  
  public LoggingJsonAdapter(final LanguageServer server) {
    super(server);
    LanguageServerProtocol _protocol = this.getProtocol();
    final Procedure2<String, Throwable> _function = (String message, Throwable throwable) -> {
      if ((this.errorLog != null)) {
        if ((throwable != null)) {
          throwable.printStackTrace(this.errorLog);
        } else {
          if ((message != null)) {
            this.errorLog.println(message);
          }
        }
        this.errorLog.flush();
      }
    };
    _protocol.addErrorListener(_function);
    LanguageServerProtocol _protocol_1 = this.getProtocol();
    final Procedure2<Message, String> _function_1 = (Message message, String json) -> {
      if ((this.messageLog != null)) {
        boolean _matched = false;
        if (message instanceof RequestMessage) {
          _matched=true;
          this.messageLog.println(("Client Request:\n\t" + json));
        }
        if (!_matched) {
          if (message instanceof NotificationMessage) {
            _matched=true;
            this.messageLog.println(("Client Notification:\n\t" + json));
          }
        }
        this.messageLog.flush();
      }
    };
    _protocol_1.addIncomingMessageListener(_function_1);
    LanguageServerProtocol _protocol_2 = this.getProtocol();
    final Procedure2<Message, String> _function_2 = (Message message, String json) -> {
      if ((this.messageLog != null)) {
        boolean _matched = false;
        if (message instanceof ResponseMessage) {
          _matched=true;
          this.messageLog.println(("Server Response:\n\t" + json));
        }
        if (!_matched) {
          if (message instanceof NotificationMessage) {
            _matched=true;
            this.messageLog.println(("Server Notification:\n\t" + json));
          }
        }
        this.messageLog.flush();
      }
    };
    _protocol_2.addOutgoingMessageListener(_function_2);
  }
  
  public void setErrorLog(final PrintWriter errorLog) {
    this.errorLog = errorLog;
  }
  
  public void setMessageLog(final PrintWriter messageLog) {
    this.messageLog = messageLog;
  }
}
