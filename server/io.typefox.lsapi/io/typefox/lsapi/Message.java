/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

/**
 * A general message as defined by JSON-RPC. The language server protocol always uses "2.0" as the jsonrpc version.
 */
@SuppressWarnings("all")
public interface Message {
  public abstract String getJsonrpc();
}
