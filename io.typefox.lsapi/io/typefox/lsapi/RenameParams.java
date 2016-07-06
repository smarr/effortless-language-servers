/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.Position;
import io.typefox.lsapi.TextDocumentIdentifier;

/**
 * The rename request is sent from the client to the server to do a workspace wide rename of a symbol.
 */
@SuppressWarnings("all")
public interface RenameParams {
  /**
   * The document in which to find the symbol.
   */
  public abstract TextDocumentIdentifier getTextDocument();
  
  /**
   * The position at which this request was send.
   */
  public abstract Position getPosition();
  
  /**
   * The new name of the symbol. If the given name is not valid the request must return a
   * ResponseError with an appropriate message set.
   */
  public abstract String getNewName();
}
