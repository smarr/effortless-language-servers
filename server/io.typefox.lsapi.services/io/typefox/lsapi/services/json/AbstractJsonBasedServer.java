/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.services.json;

import io.typefox.lsapi.services.json.LanguageServerProtocol;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Pure;

@FinalFieldsConstructor
@SuppressWarnings("all")
public abstract class AbstractJsonBasedServer {
  @Accessors(AccessorType.PROTECTED_GETTER)
  private final ExecutorService executorService;
  
  @Accessors({ AccessorType.PUBLIC_GETTER, AccessorType.PROTECTED_SETTER })
  private LanguageServerProtocol protocol;
  
  private Future<?> ioHandlerJoin;
  
  public synchronized void connect(final InputStream input, final OutputStream output) {
    boolean _isActive = this.isActive();
    if (_isActive) {
      throw new IllegalStateException("Cannot connect while active.");
    }
    LanguageServerProtocol.IOHandler _ioHandler = this.protocol.getIoHandler();
    _ioHandler.setOutput(output);
    LanguageServerProtocol.IOHandler _ioHandler_1 = this.protocol.getIoHandler();
    _ioHandler_1.setInput(input);
    LanguageServerProtocol.IOHandler _ioHandler_2 = this.protocol.getIoHandler();
    Future<?> _submit = this.executorService.submit(_ioHandler_2);
    this.ioHandlerJoin = _submit;
  }
  
  public synchronized void exit() {
    LanguageServerProtocol.IOHandler _ioHandler = this.protocol.getIoHandler();
    _ioHandler.stop();
  }
  
  public boolean isActive() {
    LanguageServerProtocol.IOHandler _ioHandler = this.protocol.getIoHandler();
    return _ioHandler.isRunning();
  }
  
  public void join() throws InterruptedException, ExecutionException {
    if ((this.ioHandlerJoin == null)) {
      throw new IllegalStateException("Cannot join before connected.");
    }
    try {
      this.ioHandlerJoin.get();
    } catch (final Throwable _t) {
      if (_t instanceof CancellationException) {
        final CancellationException e = (CancellationException)_t;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  public AbstractJsonBasedServer(final ExecutorService executorService) {
    super();
    this.executorService = executorService;
  }
  
  @Pure
  protected ExecutorService getExecutorService() {
    return this.executorService;
  }
  
  @Pure
  public LanguageServerProtocol getProtocol() {
    return this.protocol;
  }
  
  protected void setProtocol(final LanguageServerProtocol protocol) {
    this.protocol = protocol;
  }
}
