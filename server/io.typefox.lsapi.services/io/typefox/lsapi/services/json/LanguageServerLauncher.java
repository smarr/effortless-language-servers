package io.typefox.lsapi.services.json;

import io.typefox.lsapi.services.LanguageServer;
import io.typefox.lsapi.services.json.LanguageServerToJsonAdapter;
import io.typefox.lsapi.services.json.LoggingJsonAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class LanguageServerLauncher {
  public static LanguageServerLauncher newLauncher(final LanguageServer languageServer, final SocketAddress socketAddress) {
    LanguageServerToJsonAdapter _languageServerToJsonAdapter = new LanguageServerToJsonAdapter(languageServer);
    return new LanguageServerLauncher(_languageServerToJsonAdapter, socketAddress);
  }
  
  public static LanguageServerLauncher newLoggingLauncher(final LanguageServer languageServer, final SocketAddress socketAddress) {
    final LoggingJsonAdapter server = new LoggingJsonAdapter(languageServer);
    PrintWriter _printWriter = new PrintWriter(System.err);
    server.setErrorLog(_printWriter);
    PrintWriter _printWriter_1 = new PrintWriter(System.out);
    server.setMessageLog(_printWriter_1);
    return new LanguageServerLauncher(server, socketAddress);
  }
  
  private final SocketAddress socketAddress;
  
  private final LanguageServerToJsonAdapter languageServer;
  
  public LanguageServerLauncher(final LanguageServerToJsonAdapter languageServer, final SocketAddress socketAddress) {
    this.socketAddress = socketAddress;
    this.languageServer = languageServer;
  }
  
  public void launch() {
    AsynchronousServerSocketChannel serverSocket = null;
    try {
      AsynchronousServerSocketChannel _open = AsynchronousServerSocketChannel.open();
      serverSocket = _open;
      serverSocket.bind(this.socketAddress);
      InputOutput.<String>println(("Listening to " + this.socketAddress));
      serverSocket.<Object>accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
        @Override
        public void completed(final AsynchronousSocketChannel channel, final Object attachment) {
          try {
            final InputStream in = Channels.newInputStream(channel);
            final OutputStream out = Channels.newOutputStream(channel);
            InputOutput.<String>println("Connection accepted");
            LanguageServerLauncher.this.languageServer.connect(in, out);
            LanguageServerLauncher.this.languageServer.join();
            channel.close();
            InputOutput.<String>println("Connection closed");
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        }
        
        @Override
        public void failed(final Throwable exc, final Object attachment) {
          exc.printStackTrace();
        }
      });
      while (true) {
        Thread.sleep(2000);
      }
    } catch (final Throwable _t) {
      if (_t instanceof Throwable) {
        final Throwable t = (Throwable)_t;
        t.printStackTrace();
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    } finally {
      if ((serverSocket != null)) {
        try {
          serverSocket.close();
        } catch (final Throwable _t_1) {
          if (_t_1 instanceof IOException) {
            final IOException e = (IOException)_t_1;
          } else {
            throw Exceptions.sneakyThrow(_t_1);
          }
        }
      }
    }
  }
}
