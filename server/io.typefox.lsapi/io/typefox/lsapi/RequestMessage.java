/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.Message;

/**
 * A request message to decribe a request between the client and the server. Every processed request must send a response back
 * to the sender of the request.
 */
@SuppressWarnings("all")
public interface RequestMessage extends Message {
  /**
   * The request id.
   */
  public abstract String getId();
  
  /**
   * The method to be invoked.
   */
  public abstract String getMethod();
  
  /**
   * The method's params.
   */
  public abstract Object getParams();
}
