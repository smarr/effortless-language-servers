/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

/**
 * An item to transfer a text document from the client to the server.
 */
@SuppressWarnings("all")
public interface TextDocumentItem {
  /**
   * The text document's uri.
   */
  public abstract String getUri();
  
  /**
   * The text document's language identifier
   */
  public abstract String getLanguageId();
  
  /**
   * The version number of this document (it will strictly increase after each change, including undo/redo).
   */
  public abstract int getVersion();
  
  /**
   * The content of the opened  text document.
   */
  public abstract String getText();
}
