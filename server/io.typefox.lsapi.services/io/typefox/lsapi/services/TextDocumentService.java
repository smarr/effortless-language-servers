/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.services;

import io.typefox.lsapi.CodeActionParams;
import io.typefox.lsapi.CodeLens;
import io.typefox.lsapi.CodeLensParams;
import io.typefox.lsapi.Command;
import io.typefox.lsapi.CompletionItem;
import io.typefox.lsapi.CompletionList;
import io.typefox.lsapi.DidChangeTextDocumentParams;
import io.typefox.lsapi.DidCloseTextDocumentParams;
import io.typefox.lsapi.DidOpenTextDocumentParams;
import io.typefox.lsapi.DidSaveTextDocumentParams;
import io.typefox.lsapi.DocumentFormattingParams;
import io.typefox.lsapi.DocumentHighlight;
import io.typefox.lsapi.DocumentOnTypeFormattingParams;
import io.typefox.lsapi.DocumentRangeFormattingParams;
import io.typefox.lsapi.DocumentSymbolParams;
import io.typefox.lsapi.Hover;
import io.typefox.lsapi.Location;
import io.typefox.lsapi.PublishDiagnosticsParams;
import io.typefox.lsapi.ReferenceParams;
import io.typefox.lsapi.RenameParams;
import io.typefox.lsapi.SignatureHelp;
import io.typefox.lsapi.SymbolInformation;
import io.typefox.lsapi.TextDocumentPositionParams;
import io.typefox.lsapi.TextEdit;
import io.typefox.lsapi.WorkspaceEdit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("all")
public interface TextDocumentService {
  /**
   * The Completion request is sent from the client to the server to compute completion items at a given cursor position.
   * Completion items are presented in the IntelliSense user interface. If computing complete completion items is expensive
   * servers can additional provide a handler for the resolve completion item request. This request is sent when a completion
   * item is selected in the user interface.
   */
  public abstract CompletableFuture<CompletionList> completion(final TextDocumentPositionParams position);
  
  /**
   * The request is sent from the client to the server to resolve additional information for a given completion item.
   */
  public abstract CompletableFuture<CompletionItem> resolveCompletionItem(final CompletionItem unresolved);
  
  /**
   * The hover request is sent from the client to the server to request hover information at a given text document position.
   */
  public abstract CompletableFuture<Hover> hover(final TextDocumentPositionParams position);
  
  /**
   * The signature help request is sent from the client to the server to request signature information at a given cursor
   * position.
   */
  public abstract CompletableFuture<SignatureHelp> signatureHelp(final TextDocumentPositionParams position);
  
  /**
   * The goto definition request is sent from the client to the server to to resolve the defintion location of a symbol
   * at a given text document position.
   */
  public abstract CompletableFuture<List<? extends Location>> definition(final TextDocumentPositionParams position);
  
  /**
   * The references request is sent from the client to the server to resolve project-wide references for the symbol
   * denoted by the given text document position.
   */
  public abstract CompletableFuture<List<? extends Location>> references(final ReferenceParams params);
  
  /**
   * The document highlight request is sent from the client to the server to to resolve a document highlights for a
   * given text document position.
   */
  public abstract CompletableFuture<DocumentHighlight> documentHighlight(final TextDocumentPositionParams position);
  
  /**
   * The document symbol request is sent from the client to the server to list all symbols found in a given text document.
   */
  public abstract CompletableFuture<List<? extends SymbolInformation>> documentSymbol(final DocumentSymbolParams params);
  
  /**
   * The code action request is sent from the client to the server to compute commands for a given text document and
   * range. The request is trigger when the user moves the cursor into an problem marker in the editor or presses the
   * lightbulb associated with a marker.
   */
  public abstract CompletableFuture<List<? extends Command>> codeAction(final CodeActionParams params);
  
  /**
   * The code lens request is sent from the client to the server to compute code lenses for a given text document.
   */
  public abstract CompletableFuture<List<? extends CodeLens>> codeLens(final CodeLensParams params);
  
  /**
   * The code lens resolve request is sent from the clien to the server to resolve the command for a given code lens item.
   */
  public abstract CompletableFuture<CodeLens> resolveCodeLens(final CodeLens unresolved);
  
  /**
   * The document formatting request is sent from the client to the server to format a whole document.
   */
  public abstract CompletableFuture<List<? extends TextEdit>> formatting(final DocumentFormattingParams params);
  
  /**
   * The document range formatting request is sent from the client to the server to format a given range in a document.
   */
  public abstract CompletableFuture<List<? extends TextEdit>> rangeFormatting(final DocumentRangeFormattingParams params);
  
  /**
   * The document on type formatting request is sent from the client to the server to format parts of the document during typing.
   */
  public abstract CompletableFuture<List<? extends TextEdit>> onTypeFormatting(final DocumentOnTypeFormattingParams params);
  
  /**
   * The rename request is sent from the client to the server to do a workspace wide rename of a symbol.
   */
  public abstract CompletableFuture<WorkspaceEdit> rename(final RenameParams params);
  
  /**
   * The document open notification is sent from the client to the server to signal newly opened text documents.
   * The document's truth is now managed by the client and the server must not try to read the document's
   * truth using the document's uri.
   */
  public abstract void didOpen(final DidOpenTextDocumentParams params);
  
  /**
   * The document change notification is sent from the client to the server to signal changes to a text document.
   */
  public abstract void didChange(final DidChangeTextDocumentParams params);
  
  /**
   * The document close notification is sent from the client to the server when the document got closed in the
   * client. The document's truth now exists where the document's uri points to (e.g. if the document's uri is
   * a file uri the truth now exists on disk).
   */
  public abstract void didClose(final DidCloseTextDocumentParams params);
  
  /**
   * The document save notification is sent from the client to the server when the document for saved in the client.
   */
  public abstract void didSave(final DidSaveTextDocumentParams params);
  
  /**
   * Diagnostics notifications are sent from the server to the client to signal results of validation runs.
   */
  public abstract void onPublishDiagnostics(final Consumer<PublishDiagnosticsParams> callback);
}
