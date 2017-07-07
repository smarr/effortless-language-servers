package som.langserv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.typefox.lsapi.services.json.LoggingJsonAdapter;

public class ServerLauncher {

  private static boolean logToFile = false;
  private static PrintWriter err;
  private static PrintWriter msg;
  private static boolean acceptConnections = true;
  private static final ExecutorService executor = Executors.newCachedThreadPool();
  private static final int SERVER_PORT = 8123;
  private static final boolean TCP_CONNECTION;

  static {
    String transport = System.getProperty("som.langserv.transport");
    TCP_CONNECTION = "tcp".equals(transport);

    if (logToFile) {
      try {
        FileWriter fw = new FileWriter("/Users/smarr/Projects/SOM/SOMns-vscode/server/som-language-server-err.log", true);
        err = new PrintWriter(fw, true);
        fw = new FileWriter("/Users/smarr/Projects/SOM/SOMns-vscode/server/som-language-server-msg.log", true);
        msg = new PrintWriter(fw, true);
      } catch (IOException e) { }
    } else {
      err = new PrintWriter(System.err, true);

      if (TCP_CONNECTION) {
        msg = new PrintWriter(System.out, true);
      } else {
        msg = new PrintWriter(System.err, true);
      }
    }

    Thread.setDefaultUncaughtExceptionHandler(
        (final Thread t, final Throwable e) -> e.printStackTrace(err));
  }

  public static void logErr(final String msg) {
    err.println(msg);
  }

  public static PrintWriter errWriter() {
    return err;
  }

  private final static class LangServerConnection implements Runnable {
    private final InputStream in;
    private final OutputStream out;
    private final SomLanguageServer langServer;

    LangServerConnection(final InputStream in, final OutputStream out,
        final SomLanguageServer langServer) {
      this.in = in;
      this.out = out;
      this.langServer = langServer;
    }

    @Override
    public void run() {
      LoggingJsonAdapter adapter = new LoggingJsonAdapter(langServer, executor);

      adapter.setMessageLog(msg);
      adapter.setErrorLog(err);
      try {
        adapter.connect(in, out);
        adapter.join();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace(err);
      }
    }
  }

  public static void main(final String[] args) {
    SomLanguageServer tls = new SomLanguageServer();

    if (TCP_CONNECTION) {
      try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
        msg.println("[SOMns LS] Server started and waiting on " + SERVER_PORT);
        while (acceptConnections) {
          try {
            Socket client = serverSocket.accept();
            executor.submit(new LangServerConnection(client.getInputStream(), client.getOutputStream(), tls));
          } catch (IOException e) {
            err.println("[SOMns LS] Error while connecting to client.");
            e.printStackTrace(err);
          }
        }
      } catch (IOException e) {
        err.println("[SOMns LS] Failed to open port: " + SERVER_PORT);
        e.printStackTrace(err);
      }
    } else {
      msg.println("[SOMns LS] Server started using stdin/stdout");
      LangServerConnection lsc = new LangServerConnection(System.in, System.out, tls);
      lsc.run();
    }
  }
}
