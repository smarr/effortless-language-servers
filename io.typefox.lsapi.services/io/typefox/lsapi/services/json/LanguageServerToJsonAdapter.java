/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.services.json;

import io.typefox.lsapi.CancelParams;
import io.typefox.lsapi.CodeActionParams;
import io.typefox.lsapi.CodeLens;
import io.typefox.lsapi.CodeLensParams;
import io.typefox.lsapi.CompletionItem;
import io.typefox.lsapi.DidChangeConfigurationParams;
import io.typefox.lsapi.DidChangeTextDocumentParams;
import io.typefox.lsapi.DidChangeWatchedFilesParams;
import io.typefox.lsapi.DidCloseTextDocumentParams;
import io.typefox.lsapi.DidOpenTextDocumentParams;
import io.typefox.lsapi.DidSaveTextDocumentParams;
import io.typefox.lsapi.DocumentFormattingParams;
import io.typefox.lsapi.DocumentOnTypeFormattingParams;
import io.typefox.lsapi.DocumentRangeFormattingParams;
import io.typefox.lsapi.DocumentSymbolParams;
import io.typefox.lsapi.InitializeParams;
import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.Message;
import io.typefox.lsapi.MessageParams;
import io.typefox.lsapi.NotificationMessage;
import io.typefox.lsapi.NotificationMessageImpl;
import io.typefox.lsapi.PublishDiagnosticsParams;
import io.typefox.lsapi.ReferenceParams;
import io.typefox.lsapi.RenameParams;
import io.typefox.lsapi.RequestMessage;
import io.typefox.lsapi.ResponseError;
import io.typefox.lsapi.ResponseErrorImpl;
import io.typefox.lsapi.ResponseMessageImpl;
import io.typefox.lsapi.ShowMessageRequestParams;
import io.typefox.lsapi.TextDocumentPositionParams;
import io.typefox.lsapi.WorkspaceSymbolParams;
import io.typefox.lsapi.services.LanguageServer;
import io.typefox.lsapi.services.TextDocumentService;
import io.typefox.lsapi.services.WindowService;
import io.typefox.lsapi.services.WorkspaceService;
import io.typefox.lsapi.services.json.AbstractJsonBasedServer;
import io.typefox.lsapi.services.json.InvalidMessageException;
import io.typefox.lsapi.services.json.LanguageServerProtocol;
import io.typefox.lsapi.services.json.MessageJsonHandler;
import io.typefox.lsapi.services.json.MessageMethods;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * Wraps a language server implementation and adapts it to the JSON-based protocol.
 */
@SuppressWarnings("all")
public class LanguageServerToJsonAdapter extends AbstractJsonBasedServer implements Consumer<Message> {
  @Accessors(AccessorType.PROTECTED_GETTER)
  private final LanguageServer delegate;
  
  private final Map<String, Future<?>> requestFutures = CollectionLiterals.<String, Future<?>>newHashMap();
  
  private AtomicBoolean shutdownReceived = new AtomicBoolean(false);
  
  public LanguageServerToJsonAdapter(final LanguageServer delegate) {
    this(delegate, new MessageJsonHandler());
  }
  
  public LanguageServerToJsonAdapter(final LanguageServer delegate, final MessageJsonHandler jsonHandler) {
    this(delegate, jsonHandler, Executors.newCachedThreadPool());
  }
  
  public LanguageServerToJsonAdapter(final LanguageServer delegate, final MessageJsonHandler jsonHandler, final ExecutorService executorService) {
    super(executorService);
    this.delegate = delegate;
    LanguageServerProtocol _createProtocol = this.createProtocol(jsonHandler);
    this.setProtocol(_createProtocol);
    TextDocumentService _textDocumentService = delegate.getTextDocumentService();
    final Consumer<PublishDiagnosticsParams> _function = (PublishDiagnosticsParams it) -> {
      this.sendNotification(MessageMethods.SHOW_DIAGNOSTICS, it);
    };
    _textDocumentService.onPublishDiagnostics(_function);
    WindowService _windowService = delegate.getWindowService();
    final Consumer<MessageParams> _function_1 = (MessageParams it) -> {
      this.sendNotification(MessageMethods.LOG_MESSAGE, it);
    };
    _windowService.onLogMessage(_function_1);
    WindowService _windowService_1 = delegate.getWindowService();
    final Consumer<MessageParams> _function_2 = (MessageParams it) -> {
      this.sendNotification(MessageMethods.SHOW_MESSAGE, it);
    };
    _windowService_1.onShowMessage(_function_2);
    WindowService _windowService_2 = delegate.getWindowService();
    final Consumer<ShowMessageRequestParams> _function_3 = (ShowMessageRequestParams it) -> {
      this.sendNotification(MessageMethods.SHOW_MESSAGE_REQUEST, it);
    };
    _windowService_2.onShowMessageRequest(_function_3);
  }
  
  protected LanguageServerProtocol createProtocol(final MessageJsonHandler jsonHandler) {
    return new LanguageServerProtocol(jsonHandler, this);
  }
  
  protected void checkAlive(final Integer processId) {
    if ((processId == null)) {
      return;
    }
    final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    final Runnable _function = () -> {
      boolean _isAlive = this.isAlive((processId).intValue());
      boolean _not = (!_isAlive);
      if (_not) {
        this.exit();
      }
    };
    executor.scheduleAtFixedRate(_function, 3000, 3000, TimeUnit.MILLISECONDS);
  }
  
  protected boolean isAlive(final int processId) {
    try {
      Runtime _runtime = Runtime.getRuntime();
      String _valueOf = String.valueOf(processId);
      final Process process = _runtime.exec(new String[] { "kill", "-0", _valueOf });
      final int exitCode = process.waitFor();
      return (exitCode == 0);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public void exit() {
    try {
      this.delegate.exit();
      ExecutorService _executorService = this.getExecutorService();
      _executorService.shutdown();
      super.exit();
    } finally {
      if (this.afterExit!=null) {
        boolean _get = this.shutdownReceived.get();
        this.afterExit.accept(Boolean.valueOf(_get));
      }
    }
  }
  
  @Accessors
  private Consumer<Boolean> afterExit = ((Consumer<Boolean>) (Boolean shutdownReceived) -> {
    int _xifexpression = (int) 0;
    if ((shutdownReceived).booleanValue()) {
      _xifexpression = 0;
    } else {
      _xifexpression = 1;
    }
    System.exit(_xifexpression);
  });
  
  @Override
  public void accept(final Message message) {
    this.doAccept(message);
  }
  
  protected void _doAccept(final RequestMessage message) {
    try {
      CompletableFuture<?> _switchResult = null;
      String _method = message.getMethod();
      switch (_method) {
        case MessageMethods.INITIALIZE:
          CompletableFuture<InitializeResult> _xblockexpression = null;
          {
            Object _params = message.getParams();
            final InitializeParams params = ((InitializeParams) _params);
            Integer _processId = params.getProcessId();
            this.checkAlive(_processId);
            _xblockexpression = this.delegate.initialize(params);
          }
          _switchResult = _xblockexpression;
          break;
        case MessageMethods.DOC_COMPLETION:
          TextDocumentService _textDocumentService = this.delegate.getTextDocumentService();
          Object _params = message.getParams();
          _switchResult = _textDocumentService.completion(((TextDocumentPositionParams) _params));
          break;
        case MessageMethods.RESOLVE_COMPLETION:
          TextDocumentService _textDocumentService_1 = this.delegate.getTextDocumentService();
          Object _params_1 = message.getParams();
          _switchResult = _textDocumentService_1.resolveCompletionItem(((CompletionItem) _params_1));
          break;
        case MessageMethods.DOC_HOVER:
          TextDocumentService _textDocumentService_2 = this.delegate.getTextDocumentService();
          Object _params_2 = message.getParams();
          _switchResult = _textDocumentService_2.hover(((TextDocumentPositionParams) _params_2));
          break;
        case MessageMethods.DOC_SIGNATURE_HELP:
          TextDocumentService _textDocumentService_3 = this.delegate.getTextDocumentService();
          Object _params_3 = message.getParams();
          _switchResult = _textDocumentService_3.signatureHelp(((TextDocumentPositionParams) _params_3));
          break;
        case MessageMethods.DOC_DEFINITION:
          TextDocumentService _textDocumentService_4 = this.delegate.getTextDocumentService();
          Object _params_4 = message.getParams();
          _switchResult = _textDocumentService_4.definition(((TextDocumentPositionParams) _params_4));
          break;
        case MessageMethods.DOC_HIGHLIGHT:
          TextDocumentService _textDocumentService_5 = this.delegate.getTextDocumentService();
          Object _params_5 = message.getParams();
          _switchResult = _textDocumentService_5.documentHighlight(((TextDocumentPositionParams) _params_5));
          break;
        case MessageMethods.DOC_REFERENCES:
          TextDocumentService _textDocumentService_6 = this.delegate.getTextDocumentService();
          Object _params_6 = message.getParams();
          _switchResult = _textDocumentService_6.references(((ReferenceParams) _params_6));
          break;
        case MessageMethods.DOC_SYMBOL:
          TextDocumentService _textDocumentService_7 = this.delegate.getTextDocumentService();
          Object _params_7 = message.getParams();
          _switchResult = _textDocumentService_7.documentSymbol(((DocumentSymbolParams) _params_7));
          break;
        case MessageMethods.DOC_CODE_ACTION:
          TextDocumentService _textDocumentService_8 = this.delegate.getTextDocumentService();
          Object _params_8 = message.getParams();
          _switchResult = _textDocumentService_8.codeAction(((CodeActionParams) _params_8));
          break;
        case MessageMethods.DOC_CODE_LENS:
          TextDocumentService _textDocumentService_9 = this.delegate.getTextDocumentService();
          Object _params_9 = message.getParams();
          _switchResult = _textDocumentService_9.codeLens(((CodeLensParams) _params_9));
          break;
        case MessageMethods.RESOLVE_CODE_LENS:
          TextDocumentService _textDocumentService_10 = this.delegate.getTextDocumentService();
          Object _params_10 = message.getParams();
          _switchResult = _textDocumentService_10.resolveCodeLens(((CodeLens) _params_10));
          break;
        case MessageMethods.DOC_FORMATTING:
          TextDocumentService _textDocumentService_11 = this.delegate.getTextDocumentService();
          Object _params_11 = message.getParams();
          _switchResult = _textDocumentService_11.formatting(((DocumentFormattingParams) _params_11));
          break;
        case MessageMethods.DOC_RANGE_FORMATTING:
          TextDocumentService _textDocumentService_12 = this.delegate.getTextDocumentService();
          Object _params_12 = message.getParams();
          _switchResult = _textDocumentService_12.rangeFormatting(((DocumentRangeFormattingParams) _params_12));
          break;
        case MessageMethods.DOC_TYPE_FORMATTING:
          TextDocumentService _textDocumentService_13 = this.delegate.getTextDocumentService();
          Object _params_13 = message.getParams();
          _switchResult = _textDocumentService_13.onTypeFormatting(((DocumentOnTypeFormattingParams) _params_13));
          break;
        case MessageMethods.DOC_RENAME:
          TextDocumentService _textDocumentService_14 = this.delegate.getTextDocumentService();
          Object _params_14 = message.getParams();
          _switchResult = _textDocumentService_14.rename(((RenameParams) _params_14));
          break;
        case MessageMethods.WORKSPACE_SYMBOL:
          WorkspaceService _workspaceService = this.delegate.getWorkspaceService();
          Object _params_15 = message.getParams();
          _switchResult = _workspaceService.symbol(((WorkspaceSymbolParams) _params_15));
          break;
        case MessageMethods.SHUTDOWN:
          Object _xblockexpression_1 = null;
          {
            this.shutdownReceived.set(true);
            this.delegate.shutdown();
            _xblockexpression_1 = null;
          }
          _switchResult = ((CompletableFuture<?>)_xblockexpression_1);
          break;
        case MessageMethods.EXIT:
          Object _xblockexpression_2 = null;
          {
            this.exit();
            _xblockexpression_2 = null;
          }
          _switchResult = ((CompletableFuture<?>)_xblockexpression_2);
          break;
        default:
          Object _xblockexpression_3 = null;
          {
            String _id = message.getId();
            String _method_1 = message.getMethod();
            String _plus = ("Invalid method: " + _method_1);
            this.sendResponseError(_id, _plus, ResponseError.METHOD_NOT_FOUND);
            _xblockexpression_3 = null;
          }
          _switchResult = ((CompletableFuture<?>)_xblockexpression_3);
          break;
      }
      final CompletableFuture<?> future = _switchResult;
      if ((future != null)) {
        synchronized (this.requestFutures) {
          String _id = message.getId();
          this.requestFutures.put(_id, future);
        }
        final BiConsumer<Object, Throwable> _function = (Object result, Throwable exception) -> {
          synchronized (this.requestFutures) {
            String _id = message.getId();
            this.requestFutures.remove(_id);
          }
          boolean _isCancelled = future.isCancelled();
          boolean _not = (!_isCancelled);
          if (_not) {
            if ((result != null)) {
              String _id = message.getId();
              this.sendResponse(_id, result);
            } else {
              if ((exception instanceof CompletionException)) {
                Throwable _cause = ((CompletionException)exception).getCause();
                this.handleRequestError(_cause, message);
              } else {
                if ((exception != null)) {
                  this.handleRequestError(exception, message);
                }
              }
            }
          }
        };
        future.whenComplete(_function);
      }
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e = (Exception)_t;
        this.handleRequestError(e, message);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  protected void handleRequestError(final Throwable exception, final RequestMessage message) {
    if ((exception instanceof InvalidMessageException)) {
      String _id = message.getId();
      String _message = ((InvalidMessageException)exception).getMessage();
      int _errorCode = ((InvalidMessageException)exception).getErrorCode();
      this.sendResponseError(_id, _message, _errorCode);
    } else {
      if ((!((exception instanceof CancellationException) || (exception instanceof InterruptedException)))) {
        LanguageServerProtocol _protocol = this.getProtocol();
        _protocol.logError(exception);
        String _id_1 = message.getId();
        Class<? extends Throwable> _class = exception.getClass();
        String _name = _class.getName();
        String _plus = (_name + ":");
        String _message_1 = exception.getMessage();
        String _plus_1 = (_plus + _message_1);
        this.sendResponseError(_id_1, _plus_1, ResponseError.INTERNAL_ERROR);
      }
    }
  }
  
  protected void _doAccept(final NotificationMessage message) {
    try {
      String _method = message.getMethod();
      switch (_method) {
        case MessageMethods.DID_OPEN_DOC:
          TextDocumentService _textDocumentService = this.delegate.getTextDocumentService();
          Object _params = message.getParams();
          _textDocumentService.didOpen(((DidOpenTextDocumentParams) _params));
          break;
        case MessageMethods.DID_CHANGE_DOC:
          TextDocumentService _textDocumentService_1 = this.delegate.getTextDocumentService();
          Object _params_1 = message.getParams();
          _textDocumentService_1.didChange(((DidChangeTextDocumentParams) _params_1));
          break;
        case MessageMethods.DID_CLOSE_DOC:
          TextDocumentService _textDocumentService_2 = this.delegate.getTextDocumentService();
          Object _params_2 = message.getParams();
          _textDocumentService_2.didClose(((DidCloseTextDocumentParams) _params_2));
          break;
        case MessageMethods.DID_SAVE_DOC:
          TextDocumentService _textDocumentService_3 = this.delegate.getTextDocumentService();
          Object _params_3 = message.getParams();
          _textDocumentService_3.didSave(((DidSaveTextDocumentParams) _params_3));
          break;
        case MessageMethods.DID_CHANGE_CONF:
          WorkspaceService _workspaceService = this.delegate.getWorkspaceService();
          Object _params_4 = message.getParams();
          _workspaceService.didChangeConfiguraton(((DidChangeConfigurationParams) _params_4));
          break;
        case MessageMethods.DID_CHANGE_FILES:
          WorkspaceService _workspaceService_1 = this.delegate.getWorkspaceService();
          Object _params_5 = message.getParams();
          _workspaceService_1.didChangeWatchedFiles(((DidChangeWatchedFilesParams) _params_5));
          break;
        case MessageMethods.CANCEL:
          Object _params_6 = message.getParams();
          this.cancel(((CancelParams) _params_6));
          break;
      }
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e = (Exception)_t;
        LanguageServerProtocol _protocol = this.getProtocol();
        _protocol.logError(e);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  protected boolean cancel(final CancelParams params) {
    boolean _xsynchronizedexpression = false;
    synchronized (this.requestFutures) {
      boolean _xblockexpression = false;
      {
        String _id = params.getId();
        final Future<?> future = this.requestFutures.get(_id);
        boolean _xifexpression = false;
        if ((future != null)) {
          _xifexpression = future.cancel(true);
        }
        _xblockexpression = _xifexpression;
      }
      _xsynchronizedexpression = _xblockexpression;
    }
    return _xsynchronizedexpression;
  }
  
  protected void sendNotification(final String methodId, final Object parameter) {
    NotificationMessageImpl _notificationMessageImpl = new NotificationMessageImpl();
    final Procedure1<NotificationMessageImpl> _function = (NotificationMessageImpl it) -> {
      it.setJsonrpc(LanguageServerProtocol.JSONRPC_VERSION);
      it.setMethod(methodId);
      it.setParams(parameter);
    };
    final NotificationMessageImpl message = ObjectExtensions.<NotificationMessageImpl>operator_doubleArrow(_notificationMessageImpl, _function);
    LanguageServerProtocol _protocol = this.getProtocol();
    _protocol.accept(message);
  }
  
  protected void sendResponse(final String responseId, final Object resultValue) {
    ResponseMessageImpl _responseMessageImpl = new ResponseMessageImpl();
    final Procedure1<ResponseMessageImpl> _function = (ResponseMessageImpl it) -> {
      it.setJsonrpc(LanguageServerProtocol.JSONRPC_VERSION);
      it.setId(responseId);
      it.setResult(resultValue);
    };
    final ResponseMessageImpl message = ObjectExtensions.<ResponseMessageImpl>operator_doubleArrow(_responseMessageImpl, _function);
    LanguageServerProtocol _protocol = this.getProtocol();
    _protocol.accept(message);
  }
  
  protected void sendResponseError(final String responseId, final String errorMessage, final int errorCode) {
    this.sendResponseError(responseId, errorMessage, errorCode, null);
  }
  
  protected void sendResponseError(final String responseId, final String errorMessage, final int errorCode, final Object errorData) {
    ResponseMessageImpl _responseMessageImpl = new ResponseMessageImpl();
    final Procedure1<ResponseMessageImpl> _function = (ResponseMessageImpl it) -> {
      it.setJsonrpc(LanguageServerProtocol.JSONRPC_VERSION);
      it.setId(responseId);
      ResponseErrorImpl _responseErrorImpl = new ResponseErrorImpl();
      final Procedure1<ResponseErrorImpl> _function_1 = (ResponseErrorImpl it_1) -> {
        it_1.setMessage(errorMessage);
        it_1.setCode(errorCode);
        it_1.setData(errorData);
      };
      ResponseErrorImpl _doubleArrow = ObjectExtensions.<ResponseErrorImpl>operator_doubleArrow(_responseErrorImpl, _function_1);
      it.setError(_doubleArrow);
    };
    final ResponseMessageImpl message = ObjectExtensions.<ResponseMessageImpl>operator_doubleArrow(_responseMessageImpl, _function);
    LanguageServerProtocol _protocol = this.getProtocol();
    _protocol.accept(message);
  }
  
  protected void doAccept(final Message message) {
    if (message instanceof NotificationMessage) {
      _doAccept((NotificationMessage)message);
      return;
    } else if (message instanceof RequestMessage) {
      _doAccept((RequestMessage)message);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(message).toString());
    }
  }
  
  @Pure
  protected LanguageServer getDelegate() {
    return this.delegate;
  }
  
  @Pure
  public Consumer<Boolean> getAfterExit() {
    return this.afterExit;
  }
  
  public void setAfterExit(final Consumer<Boolean> afterExit) {
    this.afterExit = afterExit;
  }
}
