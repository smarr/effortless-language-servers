package lsp;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.typefox.lsapi.CodeActionParams;
import io.typefox.lsapi.CodeLens;
import io.typefox.lsapi.CodeLensParams;
import io.typefox.lsapi.Command;
import io.typefox.lsapi.CompletionItem;
import io.typefox.lsapi.CompletionList;
import io.typefox.lsapi.CompletionOptionsImpl;
import io.typefox.lsapi.DiagnosticImpl;
import io.typefox.lsapi.DidChangeConfigurationParams;
import io.typefox.lsapi.DidChangeTextDocumentParams;
import io.typefox.lsapi.DidChangeWatchedFilesParams;
import io.typefox.lsapi.DidCloseTextDocumentParams;
import io.typefox.lsapi.DidOpenTextDocumentParams;
import io.typefox.lsapi.DidSaveTextDocumentParams;
import io.typefox.lsapi.DocumentFormattingParams;
import io.typefox.lsapi.DocumentHighlight;
import io.typefox.lsapi.DocumentOnTypeFormattingParams;
import io.typefox.lsapi.DocumentRangeFormattingParams;
import io.typefox.lsapi.DocumentSymbolParams;
import io.typefox.lsapi.Hover;
import io.typefox.lsapi.InitializeParams;
import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.InitializeResultImpl;
import io.typefox.lsapi.Location;
import io.typefox.lsapi.MessageParams;
import io.typefox.lsapi.PublishDiagnosticsParams;
import io.typefox.lsapi.PublishDiagnosticsParamsImpl;
import io.typefox.lsapi.ReferenceParams;
import io.typefox.lsapi.RenameParams;
import io.typefox.lsapi.ServerCapabilities;
import io.typefox.lsapi.ServerCapabilitiesImpl;
import io.typefox.lsapi.ShowMessageRequestParams;
import io.typefox.lsapi.SignatureHelp;
import io.typefox.lsapi.SymbolInformation;
import io.typefox.lsapi.TextDocumentContentChangeEvent;
import io.typefox.lsapi.TextDocumentPositionParams;
import io.typefox.lsapi.TextEdit;
import io.typefox.lsapi.WorkspaceEdit;
import io.typefox.lsapi.WorkspaceSymbolParams;
import io.typefox.lsapi.services.LanguageServer;
import io.typefox.lsapi.services.TextDocumentService;
import io.typefox.lsapi.services.WindowService;
import io.typefox.lsapi.services.WorkspaceService;

public class TruffleLanguageServer implements LanguageServer, WorkspaceService,
		WindowService, TextDocumentService {

  private InitializeParams params;

  private Consumer<PublishDiagnosticsParams> publishDiagnostics;

	@Override
	public CompletableFuture<InitializeResult> initialize(
			final InitializeParams params) {
	  this.params = params;

//	  if (params.getRootPath() == null) {
//      throw new IllegalArgumentException(
//          "Bad initialization request. rootPath must not be null.");
//    }

	  InitializeResultImpl result = new InitializeResultImpl();
	  ServerCapabilitiesImpl cap  = new ServerCapabilitiesImpl();
	  CompletionOptionsImpl compOpt = new CompletionOptionsImpl();

	  cap.setTextDocumentSync(ServerCapabilities.SYNC_FULL);

	  compOpt.setResolveProvider(true);
	  cap.setCompletionProvider(compOpt);

	  result.setCapabilities(cap);


//    result.supportedLanguages = newArrayList()
//    for (serviceProvider : languagesRegistry.extensionToFactoryMap.values.filter(IResourceServiceProvider).toSet) {
//        val extensionProvider = serviceProvider.get(FileExtensionProvider)
//        val mimeTypesProvider = serviceProvider.get(IMimeTypeProvider)
//        val langInfo = serviceProvider.get(LanguageInfo)
//        val highlightingProvider = serviceProvider.get(IEditorHighlightingConfigurationProvider)
//        val language = new LanguageDescriptionImpl => [
//            fileExtensions = extensionProvider.fileExtensions.toList
//            languageId = langInfo.languageName
//            mimeTypes = mimeTypesProvider.mimeTypes
//            if (highlightingProvider !== null)
//              highlightingConfiguration = highlightingProvider.getConfiguration(params.clientName)
//        ]
//        result.supportedLanguages.add(language)
//    }
//
//    requestManager.runWrite([ cancelIndicator |
//      val rootURI = URI.createFileURI(params.rootPath)
//      workspaceManager.initialize(rootURI, [this.publishDiagnostics($0, $1)], cancelIndicator)
//    ], CancellableIndicator.NullImpl)
//
//    return CompletableFuture.completedFuture(result)

	  return CompletableFuture.completedFuture(result);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
	}

	@Override
	public TextDocumentService getTextDocumentService() {
	  // TODO: perhaps break this out into separate object
		return this;
	}

	@Override
	public WorkspaceService getWorkspaceService() {
	  // TODO: perhaps break this out into separate object
    return this;
	}

	@Override
	public WindowService getWindowService() {
	  // TODO: perhaps break this out into separate object
    return this;
	}

	@Override
	public CompletableFuture<CompletionList> completion(
			final TextDocumentPositionParams position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<CompletionItem> resolveCompletionItem(
			final CompletionItem unresolved) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Hover> hover(final TextDocumentPositionParams position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<SignatureHelp> signatureHelp(
			final TextDocumentPositionParams position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<? extends Location>> definition(
			final TextDocumentPositionParams position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<? extends Location>> references(
			final ReferenceParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<DocumentHighlight> documentHighlight(
			final TextDocumentPositionParams position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(
			final DocumentSymbolParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<? extends Command>> codeAction(
			final CodeActionParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<? extends CodeLens>> codeLens(
			final CodeLensParams params) {
		// TODO Auto-generated method stub
		return null;
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
	  validateTextDocument(params.getTextDocument().getUri(),
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

	  validateTextDocument(documentUri, e.getText());
	}

	private void validateTextDocument(final String documentUri, final String text) {
	  try {
      ArrayList<DiagnosticImpl> diagnostics = SomAdapter.parse(text, documentUri);
      reportDiagnostics(diagnostics, documentUri);
    } catch (URISyntaxException ex) {
      ex.printStackTrace(ServerLauncher.errWriter());
    }
	}

	private void reportDiagnostics(final ArrayList<DiagnosticImpl> diagnostics, final String documentUri) {
	  if (diagnostics != null) {
      PublishDiagnosticsParamsImpl result = new PublishDiagnosticsParamsImpl();
      result.setDiagnostics(diagnostics);
      result.setUri(documentUri);
      publishDiagnostics.accept(result);
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
	public void onPublishDiagnostics(final Consumer<PublishDiagnosticsParams> callback) {
		this.publishDiagnostics = callback;
	}

	@Override
	public void onShowMessage(final Consumer<MessageParams> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowMessageRequest(
			final Consumer<ShowMessageRequestParams> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLogMessage(final Consumer<MessageParams> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public CompletableFuture<List<? extends SymbolInformation>> symbol(
			final WorkspaceSymbolParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void didChangeConfiguraton(final DidChangeConfigurationParams params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didChangeWatchedFiles(final DidChangeWatchedFilesParams params) {
		// TODO Auto-generated method stub

	}
}
