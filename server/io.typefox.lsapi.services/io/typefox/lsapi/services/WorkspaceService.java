/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.services;

import io.typefox.lsapi.DidChangeConfigurationParams;
import io.typefox.lsapi.DidChangeWatchedFilesParams;
import io.typefox.lsapi.SymbolInformation;
import io.typefox.lsapi.WorkspaceSymbolParams;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("all")
public interface WorkspaceService {
  /**
   * The workspace symbol request is sent from the client to the server to list project-wide symbols matching
   * the query string.
   */
  public abstract CompletableFuture<List<? extends SymbolInformation>> symbol(final WorkspaceSymbolParams params);
  
  /**
   * A notification sent from the client to the server to signal the change of configuration settings.
   */
  public abstract void didChangeConfiguraton(final DidChangeConfigurationParams params);
  
  /**
   * The watched files notification is sent from the client to the server when the client detects changes to
   * file watched by the language client.
   */
  public abstract void didChangeWatchedFiles(final DidChangeWatchedFilesParams params);
}
