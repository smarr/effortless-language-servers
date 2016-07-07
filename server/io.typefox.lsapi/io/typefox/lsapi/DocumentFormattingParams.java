/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.FormattingOptions;
import io.typefox.lsapi.TextDocumentIdentifier;

/**
 * The document formatting resquest is sent from the server to the client to format a whole document.
 */
@SuppressWarnings("all")
public interface DocumentFormattingParams {
  /**
   * The document to format.
   */
  public abstract TextDocumentIdentifier getTextDocument();
  
  /**
   * The format options
   */
  public abstract FormattingOptions getOptions();
}
