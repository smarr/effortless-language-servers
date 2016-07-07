/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.TextDocumentIdentifier;
import io.typefox.lsapi.TextDocumentItem;

/**
 * The document open notification is sent from the client to the server to signal newly opened text documents.
 * The document's truth is now managed by the client and the server must not try to read the document's truth using
 * the document's uri.
 */
@SuppressWarnings("all")
public interface DidOpenTextDocumentParams extends TextDocumentIdentifier {
  /**
   * The document that was opened.
   */
  public abstract TextDocumentItem getTextDocument();
  
  /**
   * Legacy property to support protocol version 1.0 requests.
   */
  @Deprecated
  public abstract String getText();
}
