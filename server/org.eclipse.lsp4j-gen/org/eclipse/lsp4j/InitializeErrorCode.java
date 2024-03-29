/**
 * Copyright (c) 2016-2018 TypeFox and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.lsp4j;

/**
 * Known error codes for an {@link InitializeError}
 */
@SuppressWarnings("all")
public interface InitializeErrorCode {
  /**
   * If the protocol version provided by the client can't be handled by the server.
   * 
   * @deprecated This initialize error got replaced by client capabilities.
   * There is no version handshake in version 3.0x
   */
  @Deprecated
  static final int unknownProtocolVersion = 1;
}
