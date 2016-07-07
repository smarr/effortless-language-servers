/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.DocumentFormattingParams;
import io.typefox.lsapi.Position;

/**
 * The document on type formatting request is sent from the client to the server to format parts of the document during typing.
 */
@SuppressWarnings("all")
public interface DocumentOnTypeFormattingParams extends DocumentFormattingParams {
  /**
   * The position at which this request was send.
   */
  public abstract Position getPosition();
  
  /**
   * The character that has been typed.
   */
  public abstract String getCh();
}
