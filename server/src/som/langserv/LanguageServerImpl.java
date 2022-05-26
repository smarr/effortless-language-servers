package som.langserv;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeLensOptions;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.ExecuteCommandOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensServerFull;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import com.google.common.collect.Lists;

import som.langserv.newspeak.Minitest;
import som.langserv.newspeak.NewspeakAdapter;
import som.langserv.simple.SimpleAdapter;
import som.langserv.som.SomAdapter;
import som.langserv.structure.SemanticTokenModifier;
import som.langserv.structure.SemanticTokenType;


public class LanguageServerImpl implements LanguageServer, LanguageClientAware {

  private final SomWorkspace       workspace;
  private final LanguageAdapter<?> adapters[];

  private LanguageClient client;

  private final DocumentServiceImpl documentService;

  public LanguageServerImpl() {
    adapters = new LanguageAdapter[] {
        new NewspeakAdapter(), new SomAdapter(), new SimpleAdapter()};

    workspace = new SomWorkspace(adapters);
    this.documentService = new DocumentServiceImpl(adapters);
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

    cap.setSemanticTokensProvider(createSemanticTokenProviderConfig());

    result.setCapabilities(cap);

    loadWorkspace(params);

    return CompletableFuture.completedFuture(result);
  }

  private SemanticTokensWithRegistrationOptions createSemanticTokenProviderConfig() {
    SemanticTokensWithRegistrationOptions semanticTokens =
        new SemanticTokensWithRegistrationOptions();

    semanticTokens.setDocumentSelector(null);
    semanticTokens.setId(null);

    List<String> tokenTypes = new ArrayList<String>();
    for (var t : SemanticTokenType.values()) {
      tokenTypes.add(t.name);
    }

    List<String> tokenModifiers = new ArrayList<String>();
    for (var m : SemanticTokenModifier.values()) {
      tokenModifiers.add(m.name);
    }

    SemanticTokensLegend legend = new SemanticTokensLegend(tokenTypes, tokenModifiers);

    semanticTokens.setLegend(legend);
    semanticTokens.setRange(false);

    SemanticTokensServerFull serverFull = new SemanticTokensServerFull();

    serverFull.setDelta(false);
    semanticTokens.setFull(serverFull);

    return semanticTokens;
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
    return documentService;
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    return workspace;
  }

  @Override
  public void connect(final LanguageClient client) {
    for (LanguageAdapter<?> adapter : adapters) {
      adapter.connect(client);
    }
    this.client = client;
  }
}
