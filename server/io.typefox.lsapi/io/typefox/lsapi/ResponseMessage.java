/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.Message;
import io.typefox.lsapi.ResponseError;

/**
 * Response Message send as a result of a request.
 */
@SuppressWarnings("all")
public interface ResponseMessage extends Message {
  /**
   * The request id.
   */
  public abstract String getId();
  
  /**
   * The result of a request. This can be omitted in the case of an error.
   */
  public abstract Object getResult();
  
  /**
   * The error object in case a request fails.
   */
  public abstract ResponseError getError();
}
