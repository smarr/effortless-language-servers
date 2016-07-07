package lsp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;

import io.typefox.lsapi.services.json.LoggingJsonAdapter;

public class ServerLauncher {

  private static PrintWriter err;
  private static PrintWriter msg;

  static {
    try {
      FileWriter fw = new FileWriter("/Users/smarr/Projects/SOM/lsp-test/truffle-lang-server/err.log", true);
      err = new PrintWriter(fw, true);
      fw = new FileWriter("/Users/smarr/Projects/SOM/lsp-test/truffle-lang-server/msg.log", true);
      msg = new PrintWriter(fw, true);
    } catch (IOException e) { }

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
    SomAdapter.initializePolyglot();

	  SomLanguageServer tls = new SomLanguageServer();

	  LoggingJsonAdapter adapter = new LoggingJsonAdapter(tls);

    adapter.setMessageLog(msg);
    adapter.setErrorLog(err);

		adapter.connect(System.in, System.out);

		try {
      adapter.join();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace(err);
    }
	}
}
