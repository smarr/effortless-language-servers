package som.langserv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;


public class ServerLauncher {

  private static boolean logToFile = false;
  private static PrintWriter err;
  private static PrintWriter msg;
  private static boolean acceptConnections = true;
  private static final int SERVER_PORT = 8123;
  private static final boolean TCP_CONNECTION;

  public static final boolean DEBUG;

  static {
    String transport = System.getProperty("som.langserv.transport", "");
    TCP_CONNECTION = "tcp".equals(transport.toLowerCase());
    DEBUG = TCP_CONNECTION;

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


  public static void main(final String[] args) {
    SomLanguageServer tls = new SomLanguageServer();

    if (TCP_CONNECTION) {
      try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
        msg.println("[SOMns LS] Server started and waiting on " + SERVER_PORT);
        while (acceptConnections) {
          try {
            Socket client = serverSocket.accept();
            Launcher<LanguageClient> launcher =  LSPLauncher.createServerLauncher(tls, client.getInputStream(), client.getOutputStream());
            tls.connect(launcher.getRemoteProxy());
            launcher.startListening();
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
      Launcher<LanguageClient> launcher =  LSPLauncher.createServerLauncher(tls, System.in, System.out);
      tls.connect(launcher.getRemoteProxy());
      Future<?> future = launcher.startListening();

      while (true) {
        try {
          future.get();
          return;
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
          e.printStackTrace(err);
          return;
        }
      }
    }
  }
}
