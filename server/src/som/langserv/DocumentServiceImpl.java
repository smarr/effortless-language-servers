package som.langserv;

import static som.langserv.structure.SemanticTokens.combineTokensRemovingErroneousLine;
import static som.langserv.structure.SemanticTokens.sort;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentHighlightParams;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;


/**
 * Implements things like completion, hover, signature help, etc.
 */
public class DocumentServiceImpl implements TextDocumentService {

  private final LanguageAdapter<?>[]         adapters;
  private final HashMap<String, List<int[]>> tokenCache;

  public DocumentServiceImpl(final LanguageAdapter<?>[] adapters) {
    this.adapters = adapters;
    this.tokenCache = new HashMap<>();
  }

  @Override
  public void didOpen(final DidOpenTextDocumentParams params) {
    parseDocument(params.getTextDocument().getUri(),
        params.getTextDocument().getText());
  }

  @Override
  public void didChange(final DidChangeTextDocumentParams params) {
    validateTextDocument(params.getTextDocument().getUri(),
        params.getContentChanges());
  }

  @Override
  public void didClose(final DidCloseTextDocumentParams params) {}

  @Override
  public void didSave(final DidSaveTextDocumentParams params) {}

  private void parseDocument(final String documentUri, final String text) {
    try {
      for (LanguageAdapter<?> adapter : adapters) {
        if (adapter.handlesUri(documentUri)) {
          List<Diagnostic> diagnostics = adapter.parse(text, documentUri);
          adapter.lintSends(documentUri, diagnostics);
          adapter.reportDiagnostics(diagnostics, documentUri);
          return;
        }
      }
      assert false : "LanguageServer does not support file type: " + documentUri;
    } catch (URISyntaxException ex) {
      ex.printStackTrace(ServerLauncher.errWriter());
    }
  }

  private void validateTextDocument(final String documentUri,
      final List<? extends TextDocumentContentChangeEvent> list) {
    TextDocumentContentChangeEvent e = list.iterator().next();

    parseDocument(documentUri, e.getText());
  }

  private static Diagnostic getErrorOrNull(final List<Diagnostic> diagnostics) {
    for (Diagnostic d : diagnostics) {
      if (d.getSeverity() == DiagnosticSeverity.Error) {
        return d;
      }
    }
    return null;
  }

  private static Position to1based(final Position p) {
    return new Position(p.getLine() + 1, p.getCharacter() + 1);
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensFull(
      final SemanticTokensParams params) {
    String uri = params.getTextDocument().getUri();
    for (LanguageAdapter<?> adapter : adapters) {
      if (adapter.handlesUri(uri)) {
        List<int[]> sortedTokenList = sort(adapter.getSemanticTokens(uri));

        Diagnostic error = getErrorOrNull(adapter.getDiagnostics(uri));
        if (error != null) {
          List<int[]> prevTokens = tokenCache.get(uri);

          if (prevTokens != null) {
            List<int[]> withOldAndWithoutError =
                combineTokensRemovingErroneousLine(
                    to1based(error.getRange().getStart()), prevTokens, sortedTokenList);
            List<Integer> tokens = adapter.makeRelative(withOldAndWithoutError);
            return CompletableFuture.completedFuture(new SemanticTokens(tokens));
          }
        }

        tokenCache.put(uri, sortedTokenList);

        return CompletableFuture.completedFuture(
            new SemanticTokens(adapter.makeRelative(sortedTokenList)));
      }
    }

    return null;
  }

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
      final CompletionParams position) {
    String uri = position.getTextDocument().getUri();

    for (LanguageAdapter<?> adapter : adapters) {
      if (adapter.handlesUri(uri)) {
        CompletionList result = adapter.getCompletions(
            position.getTextDocument().getUri(), position.getPosition().getLine(),
            position.getPosition().getCharacter());
        return CompletableFuture.completedFuture(Either.forRight(result));
      }
    }

    return CompletableFuture.completedFuture(Either.forRight(new CompletionList()));
  }

  @Override
  public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(
      final DocumentHighlightParams params) {
    var adapter = getResponsibleAdapter(params.getTextDocument());
    if (adapter != null) {
      String uri = params.getTextDocument().getUri();
      List<DocumentHighlight> highlights =
          adapter.getHighlight(uri, params.getPosition());
      return CompletableFuture.completedFuture(highlights);
    }

    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<List<? extends Location>> references(
      final ReferenceParams params) {
    var adapter = getResponsibleAdapter(params.getTextDocument());
    if (adapter != null) {
      List<Location> result = adapter.getReferences(params.getTextDocument().getUri(),
          params.getPosition(), params.getContext().isIncludeDeclaration());
      return CompletableFuture.completedFuture(result);
    }

    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(
      final DocumentSymbolParams params) {
    var adapter = getResponsibleAdapter(params.getTextDocument());
    if (adapter != null) {
      var result = adapter.documentSymbol(params.getTextDocument().getUri());

      ArrayList<Either<SymbolInformation, DocumentSymbol>> eitherList =
          new ArrayList<>(result.size());
      for (DocumentSymbol s : result) {
        eitherList.add(Either.forRight(s));
      }
      return CompletableFuture.completedFuture(eitherList);
    }

    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<List<? extends CodeLens>> codeLens(final CodeLensParams params) {
    var adapter = getResponsibleAdapter(params.getTextDocument());
    if (adapter != null) {
      List<CodeLens> result = new ArrayList<>();
      String uri = params.getTextDocument().getUri();
      adapter.getCodeLenses(result, uri);
      return CompletableFuture.completedFuture(result);
    }
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<Hover> hover(final HoverParams params) {
    var adapter = getResponsibleAdapter(params.getTextDocument());
    if (adapter != null) {
      String uri = params.getTextDocument().getUri();
      Hover result = adapter.hover(uri, params.getPosition());
      return CompletableFuture.completedFuture(result);
    }

    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<SignatureHelp> signatureHelp(final SignatureHelpParams params) {
    var adapter = getResponsibleAdapter(params.getTextDocument());
    if (adapter != null) {
      String uri = params.getTextDocument().getUri();
      SignatureHelp help =
          adapter.signatureHelp(uri, params.getPosition(), params.getContext());
      return CompletableFuture.completedFuture(help);
    }

    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
      final DefinitionParams params) {
    var adapter = getResponsibleAdapter(params.getTextDocument());
    if (adapter == null) {
      return CompletableFuture.completedFuture(null);
    }

    String uri = params.getTextDocument().getUri();
    List<? extends LocationLink> result = adapter.getDefinitions(uri, params.getPosition());

    return CompletableFuture.completedFuture(Either.forRight(result));
  }

  private LanguageAdapter<?> getResponsibleAdapter(final TextDocumentIdentifier docId) {
    String uri = docId.getUri();
    for (LanguageAdapter<?> adapter : adapters) {
      if (adapter.handlesUri(uri)) {
        return adapter;
      }
    }

    return null;
  }
}
