package som.langserv;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensOptions;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentFormattingParams;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentHighlightParams;
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams;
import org.eclipse.lsp4j.DocumentRangeFormattingParams;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.ExecuteCommandOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.RenameParams;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.SemanticTokensServerFull;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import com.google.common.collect.Lists;

import som.langserv.newspeak.Minitest;
import som.langserv.newspeak.NewspeakAdapter;
import som.langserv.simple.SimpleAdapter;
//import som.langserv.simple.SimpleAdapter;
import som.langserv.som.SomAdapter;


public class SomLanguageServer implements LanguageServer, TextDocumentService,
    LanguageClientAware {

  private final SomWorkspace       workspace;
  private final LanguageAdapter<?> adapters[];
  private LanguageClient           client;

  public SomLanguageServer() {
    adapters =
        new LanguageAdapter[] {new NewspeakAdapter(), new SomAdapter(), new SimpleAdapter()};
    // new SimpleAdapter()
    workspace = new SomWorkspace(adapters);
  }

  @Override
  public CompletableFuture<InitializeResult> initialize(final InitializeParams params) {
    InitializeResult result = new InitializeResult();
    ServerCapabilities cap = new ServerCapabilities();
    cap.setDocumentHighlightProvider(true);
    cap.setTextDocumentSync(TextDocumentSyncKind.Full);
    cap.setDocumentSymbolProvider(true);
    cap.setWorkspaceSymbolProvider(true);
    cap.setDefinitionProvider(true);
    cap.setCodeLensProvider(new CodeLensOptions(true));
    cap.setExecuteCommandProvider(
        new ExecuteCommandOptions(Lists.newArrayList(Minitest.COMMAND)));

    CompletionOptions completion = new CompletionOptions();
    List<String> autoComplTrigger = new ArrayList<>();
    autoComplTrigger.add("#"); // Smalltalk symbols
    autoComplTrigger.add(":"); // end of keywords, to complete arguments
    autoComplTrigger.add("="); // right-hand side of assignments
    completion.setTriggerCharacters(autoComplTrigger);
    completion.setResolveProvider(false); // TODO: look into that

    cap.setCompletionProvider(completion);

    SemanticTokensWithRegistrationOptions semanticTokens =
        new SemanticTokensWithRegistrationOptions();

    semanticTokens.setDocumentSelector(null);
    semanticTokens.setId(null);

    List<String> tokenTypes = new ArrayList<String>();
    tokenTypes.add("class"); // 0
    tokenTypes.add("keyword"); // 1
    tokenTypes.add("method"); // 2
    tokenTypes.add("string"); // 3
    tokenTypes.add("variable"); // 4
    tokenTypes.add("comment"); // 5
    tokenTypes.add("type"); // 6
    tokenTypes.add("property"); // 7
    tokenTypes.add("operator"); // 8
    tokenTypes.add("parameter"); // 9
    tokenTypes.add("function"); // 10
    tokenTypes.add("number"); // 11

    List<String> tokenModifiers = new ArrayList<String>();
    tokenModifiers.add("declaration");
    tokenModifiers.add("definition");
    SemanticTokensLegend legend = new SemanticTokensLegend(tokenTypes, tokenModifiers);

    semanticTokens.setLegend(legend);
    semanticTokens.setRange(false);

    SemanticTokensServerFull serverFull = new SemanticTokensServerFull();

    serverFull.setDelta(false);
    semanticTokens.setFull(serverFull);

    cap.setSemanticTokensProvider(semanticTokens);

    result.setCapabilities(cap);

    loadWorkspace(params);

    return CompletableFuture.completedFuture(result);
  }

  private void loadWorkspace(final InitializeParams params) {
    List<WorkspaceFolder> folders = params.getWorkspaceFolders();
    if (folders == null) {
      return;
    }

    for (LanguageAdapter<?> adapter : adapters) {
      for (WorkspaceFolder f : folders) {
        try {
          adapter.loadWorkspace(f.getUri());
        } catch (URISyntaxException e) {
          MessageParams msg = new MessageParams();
          msg.setType(MessageType.Error);
          msg.setMessage("Workspace root URI invalid: " + f.getUri());

          client.logMessage(msg);

          ServerLauncher.logErr(msg.getMessage());
        }
      }
    }
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    // NOOP for the moment
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void exit() {
    // NOOP for the moment
  }

  @Override
  public TextDocumentService getTextDocumentService() {
    // TODO: perhaps break this out into separate object
    return this;
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    return workspace;
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensFull(
      final SemanticTokensParams params) {

    String uri = params.getTextDocument().getUri();
    for (LanguageAdapter<?> adapter : adapters) {
      if (adapter.handlesUri(uri)) {

        return CompletableFuture.completedFuture(
            new SemanticTokens(configuretokens(adapter.getTokenPositions(uri))));

      } else {

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
  public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
      final DefinitionParams params) {
    String uri = params.getTextDocument().getUri();
    List<? extends Location> result = new ArrayList<>();

    for (LanguageAdapter<?> adapter : adapters) {
      if (adapter.handlesUri(uri)) {
        result = adapter.getDefinitions(
            params.getTextDocument().getUri(), params.getPosition().getLine(),
            params.getPosition().getCharacter());
        break;
      }
    }

    return CompletableFuture.completedFuture(Either.forLeft(result));
  }

  @Override
  public CompletableFuture<List<? extends Location>> references(final ReferenceParams params) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(
      final DocumentHighlightParams params) {
    // TODO: this is wrong, it should be something entirely different.
    // this feature is about marking the occurrences of a selected element
    // like a variable, where it is used.
    // so, this should actually return multiple results.
    // The spec is currently broken for that.
    String uri = params.getTextDocument().getUri();
    for (LanguageAdapter<?> adapter : adapters) {
      if (adapter.handlesUri(uri)) {
        DocumentHighlight result = adapter.getHighlight(params.getTextDocument().getUri(),
            params.getPosition().getLine() + 1, params.getPosition().getCharacter() + 1);
        ArrayList<DocumentHighlight> list = new ArrayList<>(1);
        list.add(result);
        return CompletableFuture.completedFuture(list);
      }
    }
    return CompletableFuture.completedFuture(new ArrayList<DocumentHighlight>());
  }

  @Override
  public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(
      final DocumentSymbolParams params) {
    String uri = params.getTextDocument().getUri();
    for (LanguageAdapter<?> adapter : adapters) {
      if (adapter.handlesUri(uri)) {
        List<? extends SymbolInformation> result =
            adapter.getSymbolInfo(params.getTextDocument().getUri());
        ArrayList<Either<SymbolInformation, DocumentSymbol>> eitherList =
            new ArrayList<>(result.size());
        for (SymbolInformation s : result) {
          eitherList.add(Either.forLeft(s));
        }
        return CompletableFuture.completedFuture(eitherList);
      }
    }
    return CompletableFuture.completedFuture(
        new ArrayList<Either<SymbolInformation, DocumentSymbol>>());
  }

  @Override
  public CompletableFuture<List<? extends CodeLens>> codeLens(final CodeLensParams params) {
    String uri = params.getTextDocument().getUri();
    for (LanguageAdapter<?> adapter : adapters) {
      if (adapter.handlesUri(uri)) {
        List<CodeLens> result = new ArrayList<>();
        adapter.getCodeLenses(result, params.getTextDocument().getUri());
        return CompletableFuture.completedFuture(result);
      }
    }
    return CompletableFuture.completedFuture(new ArrayList<CodeLens>());
  }

  @Override
  public CompletableFuture<CodeLens> resolveCodeLens(final CodeLens unresolved) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CompletableFuture<List<? extends TextEdit>> formatting(
      final DocumentFormattingParams params) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CompletableFuture<List<? extends TextEdit>> rangeFormatting(
      final DocumentRangeFormattingParams params) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(
      final DocumentOnTypeFormattingParams params) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CompletableFuture<WorkspaceEdit> rename(final RenameParams params) {
    // TODO Auto-generated method stub
    return null;
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

  private void validateTextDocument(final String documentUri,
      final List<? extends TextDocumentContentChangeEvent> list) {
    TextDocumentContentChangeEvent e = list.iterator().next();

    parseDocument(documentUri, e.getText());
  }

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

  @Override
  public void didClose(final DidCloseTextDocumentParams params) {
    // TODO Auto-generated method stub
  }

  @Override
  public void didSave(final DidSaveTextDocumentParams params) {
    // TODO Auto-generated method stub
  }

  @Override
  public void connect(final LanguageClient client) {
    for (LanguageAdapter<?> adapter : adapters) {
      adapter.connect(client);
    }
    this.client = client;
  }

  private static List<Integer> configuretokens(List<Integer> array) {
    array = sortByLineNum(array);
    array = sortByColNum(array);
    int tokenLine = array.get(array.size() - 5);
    int tokenstart = array.get(array.size() - 4);
    int linecount = 0;
    int startcount = 0;

    while (linecount + 5 != array.size()) {
      tokenLine = array.get(array.size() - (5 + linecount));
      int nextTokenLine = array.get(array.size() - (10 + linecount));
      tokenLine = tokenLine - nextTokenLine;
      if (tokenLine == 0) {
        tokenstart = array.get(array.size() - (4 + startcount));
        int nextTokenstart = array.get(array.size() - (9 + startcount));
        tokenstart = tokenstart - nextTokenstart;
      } else {
        tokenstart = array.get(array.size() - (4 + startcount));
      }
      array.set(array.size() - (5 + linecount), tokenLine);
      array.set(array.size() - (4 + startcount), tokenstart);
      linecount = linecount + 5;
      startcount = startcount + 5;
    }

    return array;
  }

  public static <T> List<List<T>> chunk(final List<T> input, final int chunkSize) {

    int inputSize = input.size();
    int chunkCount = (int) Math.ceil(inputSize / (double) chunkSize);

    Map<Integer, List<T>> map = new HashMap<>(chunkCount);
    List<List<T>> chunks = new ArrayList<>(chunkCount);

    for (int i = 0; i < inputSize; i++) {

      map.computeIfAbsent(i / chunkSize, (ignore) -> {

        List<T> chunk = new ArrayList<>();
        chunks.add(chunk);
        return chunk;

      }).add(input.get(i));
    }

    return chunks;
  }

  private static <T> List<T> twoDArrayToList(final T[][] twoDArray) {
    List<T> list = new ArrayList<T>();
    for (T[] array : twoDArray) {
      list.addAll(Arrays.asList(array));
    }
    return list;
  }

  private static List<Integer> sortByLineNum(final List<Integer> in) {
    List<List<Integer>> list2d = chunk(in, 5);
    Integer[][] arr;

    arr = list2d.stream().map(x -> x.toArray(new Integer[x.size()])).toArray(Integer[][]::new);

    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n - i - 1; j++) {
        if (arr[j][0].intValue() > arr[j + 1][0].intValue()) {

          Integer temp[] = arr[j];
          arr[j] = arr[j + 1];
          arr[j + 1] = temp;
        }
      }
    }
    return twoDArrayToList(arr);

  }

  private static List<Integer> sortByColNum(final List<Integer> in) {
    List<List<Integer>> list2d = chunk(in, 5);
    Integer[][] arr;

    arr = list2d.stream().map(x -> x.toArray(new Integer[x.size()])).toArray(Integer[][]::new);

    int n = arr.length;
    boolean didASwap = true;
    for (int i = 0; i < n - 1 || didASwap == true; i++) {
      didASwap = false;
      for (int j = 0; j < n - i - 1; j++) {
        if (arr[j][0].intValue() == arr[j + 1][0].intValue()
            && arr[j][1].intValue() > arr[j + 1][1].intValue()) {

          Integer temp[] = arr[j];
          arr[j] = arr[j + 1];
          arr[j + 1] = temp;
          didASwap = true;
        }
      }
    }
    return twoDArrayToList(arr);

  }
}
