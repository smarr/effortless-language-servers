/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.services;

import io.typefox.lsapi.MessageParams;
import io.typefox.lsapi.ShowMessageRequestParams;
import java.util.function.Consumer;

@SuppressWarnings("all")
public interface WindowService {
  /**
   * The show message notification is sent from a server to a client to ask the client to display a particular
   * message in the user interface.
   */
  public abstract void onShowMessage(final Consumer<MessageParams> callback);
  
  /**
   * The show message request is sent from a server to a client to ask the client to display a particular message
   * in the user interface. In addition to the show message notification the request allows to pass actions and
   * to wait for an answer from the client.
   */
  public abstract void onShowMessageRequest(final Consumer<ShowMessageRequestParams> callback);
  
  /**
   * The log message notification is send from the server to the client to ask the client to log a particular message.
   */
  public abstract void onLogMessage(final Consumer<MessageParams> callback);
}
