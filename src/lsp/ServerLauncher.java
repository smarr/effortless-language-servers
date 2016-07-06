package lsp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;

import io.typefox.lsapi.services.json.LoggingJsonAdapter;

public class ServerLauncher {

	public static void main(final String[] args) {
	  TruffleLanguageServer tls = new TruffleLanguageServer();
	  LoggingJsonAdapter adapter = new LoggingJsonAdapter(tls);

	  try {
      adapter.setMessageLog(new PrintWriter("/Users/smarr/Projects/SOM/lsp-test/truffle-lang-server/msg.log"));
      adapter.setErrorLog(new PrintWriter("/Users/smarr/Projects/SOM/lsp-test/truffle-lang-server/err.log"));
    } catch (FileNotFoundException e1) { }

		adapter.connect(System.in, System.out);

		try {
      adapter.join();
    } catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	}
}
