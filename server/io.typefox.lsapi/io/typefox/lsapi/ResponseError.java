/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

@SuppressWarnings("all")
public interface ResponseError {
  public final static int PARSE_ERROR = (-32700);
  
  public final static int INVALID_REQUEST = (-32600);
  
  public final static int METHOD_NOT_FOUND = (-32601);
  
  public final static int INVALID_PARAMS = (-32602);
  
  public final static int INTERNAL_ERROR = (-32603);
  
  public final static int SERVER_ERROR_START = (-32099);
  
  public final static int SERVER_ERROR_END = (-32000);
  
  /**
   * A number indicating the error type that occured.
   */
  public abstract int getCode();
  
  /**
   * A string providing a short decription of the error.
   */
  public abstract String getMessage();
  
  /**
   * A Primitive or Structured value that contains additional information about the error. Can be omitted.
   */
  public abstract Object getData();
}
