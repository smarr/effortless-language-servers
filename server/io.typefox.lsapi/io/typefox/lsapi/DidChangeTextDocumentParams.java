/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.TextDocumentContentChangeEvent;
import io.typefox.lsapi.VersionedTextDocumentIdentifier;
import java.util.List;

/**
 * The document change notification is sent from the client to the server to signal changes to a text document.
 */
@SuppressWarnings("all")
public interface DidChangeTextDocumentParams {
  /**
   * The document that did change. The version number points to the version after all provided content changes have
   * been applied.
   */
  public abstract VersionedTextDocumentIdentifier getTextDocument();
  
  /**
   * Legacy property to support protocol version 1.0 requests.
   */
  @Deprecated
  public abstract String getUri();
  
  /**
   * The actual content changes.
   */
  public abstract List<? extends TextDocumentContentChangeEvent> getContentChanges();
}
