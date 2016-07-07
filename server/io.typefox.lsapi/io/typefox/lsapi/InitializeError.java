/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

@SuppressWarnings("all")
public interface InitializeError {
  /**
   * Indicates whether the client should retry to send the initialize request after showing the message provided
   * in the ResponseError.
   */
  public abstract boolean isRetry();
}
