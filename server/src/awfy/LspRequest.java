package awfy;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import som.langserv.DocumentServiceImpl;


public class LspRequest {

  public static class Result {
    public final List<Either<SymbolInformation, DocumentSymbol>> symbols;
    public final List<Integer>                                   tokens;

    public Result(final List<Either<SymbolInformation, DocumentSymbol>> symbols,
        final List<Integer> tokens) {
      this.symbols = symbols;
      this.tokens = tokens;
    }
  }

  public static Result doRequest(final String uri, final String file,
      final DocumentServiceImpl docServer) {
    docServer.parseDocument(uri, file);

    TextDocumentIdentifier id = new TextDocumentIdentifier(uri);

    List<Either<SymbolInformation, DocumentSymbol>> symbols;
    List<Integer> tokens;
    try {
      symbols = docServer.documentSymbol(new DocumentSymbolParams(id)).get();
      tokens = docServer.semanticTokensFull(new SemanticTokensParams(id)).get().getData();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }

    return new Result(symbols, tokens);
  }

}
