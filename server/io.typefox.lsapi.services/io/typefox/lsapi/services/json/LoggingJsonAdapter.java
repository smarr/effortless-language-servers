package io.typefox.lsapi.services.json;

import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;

import io.typefox.lsapi.Message;
import io.typefox.lsapi.NotificationMessage;
import io.typefox.lsapi.RequestMessage;
import io.typefox.lsapi.ResponseMessage;
import io.typefox.lsapi.services.LanguageServer;

@SuppressWarnings("all")
public class LoggingJsonAdapter extends LanguageServerToJsonAdapter {
  @Accessors(AccessorType.PUBLIC_SETTER)
  private PrintWriter errorLog;

  @Accessors(AccessorType.PUBLIC_SETTER)
  private PrintWriter messageLog;

  public LoggingJsonAdapter(final LanguageServer server) {
    super(server, new MessageJsonHandler(), Executors.newCachedThreadPool());
  }

  public LoggingJsonAdapter(final LanguageServer server, final ExecutorService executorService) {
    super(server, new MessageJsonHandler(), executorService);
    LanguageServerProtocol _protocol = this.getProtocol();
    final Procedure2<String, Throwable> _function = (final String message, final Throwable throwable) -> {
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
    final Procedure2<Message, String> _function_1 = (final Message message, final String json) -> {
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
    final Procedure2<Message, String> _function_2 = (final Message message, final String json) -> {
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
