/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.CodeLensOptions;
import io.typefox.lsapi.CompletionOptions;
import io.typefox.lsapi.DocumentOnTypeFormattingOptions;
import io.typefox.lsapi.SignatureHelpOptions;

@SuppressWarnings("all")
public interface ServerCapabilities {
  /**
   * Documents should not be synced at all.
   */
  public final static int SYNC_NONE = 0;
  
  /**
   * Documents are synced by always sending the full content of the document.
   */
  public final static int SYNC_FULL = 1;
  
  /**
   * Documents are synced by sending the full content on open. After that only incremental
   * updates to the document are send.
   */
  public final static int SYNC_INCREMENTAL = 2;
  
  /**
   * Defines how text documents are synced.
   */
  public abstract Integer getTextDocumentSync();
  
  /**
   * The server provides hover support.
   */
  public abstract Boolean isHoverProvider();
  
  /**
   * The server provides completion support.
   */
  public abstract CompletionOptions getCompletionProvider();
  
  /**
   * The server provides signature help support.
   */
  public abstract SignatureHelpOptions getSignatureHelpProvider();
  
  /**
   * The server provides goto definition support.
   */
  public abstract Boolean isDefinitionProvider();
  
  /**
   * The server provides find references support.
   */
  public abstract Boolean isReferencesProvider();
  
  /**
   * The server provides document highlight support.
   */
  public abstract Boolean isDocumentHighlightProvider();
  
  /**
   * The server provides document symbol support.
   */
  public abstract Boolean isDocumentSymbolProvider();
  
  /**
   * The server provides workspace symbol support.
   */
  public abstract Boolean isWorkspaceSymbolProvider();
  
  /**
   * The server provides code actions.
   */
  public abstract Boolean isCodeActionProvider();
  
  /**
   * The server provides code lens.
   */
  public abstract CodeLensOptions getCodeLensProvider();
  
  /**
   * The server provides document formatting.
   */
  public abstract Boolean isDocumentFormattingProvider();
  
  /**
   * The server provides document range formatting.
   */
  public abstract Boolean isDocumentRangeFormattingProvider();
  
  /**
   * The server provides document formatting on typing.
   */
  public abstract DocumentOnTypeFormattingOptions getDocumentOnTypeFormattingProvider();
  
  /**
   * The server provides rename support.
   */
  public abstract Boolean isRenameProvider();
}
