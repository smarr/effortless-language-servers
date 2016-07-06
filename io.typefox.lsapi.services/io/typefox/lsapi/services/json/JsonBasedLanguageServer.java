/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.services.json;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.typefox.lsapi.CancelParamsImpl;
import io.typefox.lsapi.CodeActionParams;
import io.typefox.lsapi.CodeLens;
import io.typefox.lsapi.CodeLensParams;
import io.typefox.lsapi.Command;
import io.typefox.lsapi.CompletionItem;
import io.typefox.lsapi.CompletionList;
import io.typefox.lsapi.DidChangeConfigurationParams;
import io.typefox.lsapi.DidChangeTextDocumentParams;
import io.typefox.lsapi.DidChangeWatchedFilesParams;
import io.typefox.lsapi.DidCloseTextDocumentParams;
import io.typefox.lsapi.DidOpenTextDocumentParams;
import io.typefox.lsapi.DidSaveTextDocumentParams;
import io.typefox.lsapi.DocumentFormattingParams;
import io.typefox.lsapi.DocumentHighlight;
import io.typefox.lsapi.DocumentOnTypeFormattingParams;
import io.typefox.lsapi.DocumentRangeFormattingParams;
import io.typefox.lsapi.DocumentSymbolParams;
import io.typefox.lsapi.Hover;
import io.typefox.lsapi.InitializeParams;
import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.Location;
import io.typefox.lsapi.Message;
import io.typefox.lsapi.MessageParams;
import io.typefox.lsapi.NotificationMessage;
import io.typefox.lsapi.NotificationMessageImpl;
import io.typefox.lsapi.PublishDiagnosticsParams;
import io.typefox.lsapi.ReferenceParams;
import io.typefox.lsapi.RenameParams;
import io.typefox.lsapi.RequestMessageImpl;
import io.typefox.lsapi.ResponseError;
import io.typefox.lsapi.ResponseMessage;
import io.typefox.lsapi.ShowMessageRequestParams;
import io.typefox.lsapi.SignatureHelp;
import io.typefox.lsapi.SymbolInformation;
import io.typefox.lsapi.TextDocumentPositionParams;
import io.typefox.lsapi.TextEdit;
import io.typefox.lsapi.WorkspaceEdit;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * A language server that delegates to an input and an output stream through the JSON-based protocol.
 */
@SuppressWarnings("all")
public class JsonBasedLanguageServer extends AbstractJsonBasedServer implements LanguageServer, Consumer<Message> {
  @FinalFieldsConstructor
  protected static class TextDocumentServiceImpl implements TextDocumentService {
    private final JsonBasedLanguageServer server;
    
    @Override
    public CompletableFuture<CompletionList> completion(final TextDocumentPositionParams position) {
      return this.server.<CompletionList>getPromise(MessageMethods.DOC_COMPLETION, position, CompletionList.class);
    }
    
    @Override
    public CompletableFuture<CompletionItem> resolveCompletionItem(final CompletionItem unresolved) {
      return this.server.<CompletionItem>getPromise(MessageMethods.RESOLVE_COMPLETION, unresolved, CompletionItem.class);
    }
    
    @Override
    public CompletableFuture<Hover> hover(final TextDocumentPositionParams position) {
      return this.server.<Hover>getPromise(MessageMethods.DOC_HOVER, position, Hover.class);
    }
    
    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(final TextDocumentPositionParams position) {
      return this.server.<SignatureHelp>getPromise(MessageMethods.DOC_SIGNATURE_HELP, position, SignatureHelp.class);
    }
    
    @Override
    public CompletableFuture<List<? extends Location>> definition(final TextDocumentPositionParams position) {
      return this.server.<Location>getListPromise(MessageMethods.DOC_DEFINITION, position, Location.class);
    }
    
    @Override
    public CompletableFuture<List<? extends Location>> references(final ReferenceParams params) {
      return this.server.<Location>getListPromise(MessageMethods.DOC_REFERENCES, params, Location.class);
    }
    
    @Override
    public CompletableFuture<DocumentHighlight> documentHighlight(final TextDocumentPositionParams position) {
      return this.server.<DocumentHighlight>getPromise(MessageMethods.DOC_HIGHLIGHT, position, DocumentHighlight.class);
    }
    
    @Override
    public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(final DocumentSymbolParams params) {
      return this.server.<SymbolInformation>getListPromise(MessageMethods.DOC_SYMBOL, params, SymbolInformation.class);
    }
    
    @Override
    public CompletableFuture<List<? extends Command>> codeAction(final CodeActionParams params) {
      return this.server.<Command>getListPromise(MessageMethods.DOC_CODE_ACTION, params, Command.class);
    }
    
    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(final CodeLensParams params) {
      return this.server.<CodeLens>getListPromise(MessageMethods.DOC_CODE_LENS, params, CodeLens.class);
    }
    
    @Override
    public CompletableFuture<CodeLens> resolveCodeLens(final CodeLens unresolved) {
      return this.server.<CodeLens>getPromise(MessageMethods.RESOLVE_CODE_LENS, unresolved, CodeLens.class);
    }
    
    @Override
    public CompletableFuture<List<? extends TextEdit>> formatting(final DocumentFormattingParams params) {
      return this.server.<TextEdit>getListPromise(MessageMethods.DOC_FORMATTING, params, TextEdit.class);
    }
    
    @Override
    public CompletableFuture<List<? extends TextEdit>> rangeFormatting(final DocumentRangeFormattingParams params) {
      return this.server.<TextEdit>getListPromise(MessageMethods.DOC_RANGE_FORMATTING, params, TextEdit.class);
    }
    
    @Override
    public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(final DocumentOnTypeFormattingParams params) {
      return this.server.<TextEdit>getListPromise(MessageMethods.DOC_TYPE_FORMATTING, params, TextEdit.class);
    }
    
    @Override
    public CompletableFuture<WorkspaceEdit> rename(final RenameParams params) {
      return this.server.<WorkspaceEdit>getPromise(MessageMethods.DOC_RENAME, params, WorkspaceEdit.class);
    }
    
    @Override
    public void didOpen(final DidOpenTextDocumentParams params) {
      this.server.sendNotification(MessageMethods.DID_OPEN_DOC, params);
    }
    
    @Override
    public void didChange(final DidChangeTextDocumentParams params) {
      this.server.sendNotification(MessageMethods.DID_CHANGE_DOC, params);
    }
    
    @Override
    public void didClose(final DidCloseTextDocumentParams params) {
      this.server.sendNotification(MessageMethods.DID_CLOSE_DOC, params);
    }
    
    @Override
    public void didSave(final DidSaveTextDocumentParams params) {
      this.server.sendNotification(MessageMethods.DID_SAVE_DOC, params);
    }
    
    @Override
    public void onPublishDiagnostics(final Consumer<PublishDiagnosticsParams> callback) {
      this.server.<PublishDiagnosticsParams>addCallback(MessageMethods.SHOW_DIAGNOSTICS, callback, PublishDiagnosticsParams.class);
    }
    
    public TextDocumentServiceImpl(final JsonBasedLanguageServer server) {
      super();
      this.server = server;
    }
  }
  
  @FinalFieldsConstructor
  protected static class WindowServiceImpl implements WindowService {
    private final JsonBasedLanguageServer server;
    
    @Override
    public void onShowMessage(final Consumer<MessageParams> callback) {
      this.server.<MessageParams>addCallback(MessageMethods.SHOW_MESSAGE, callback, MessageParams.class);
    }
    
    @Override
    public void onShowMessageRequest(final Consumer<ShowMessageRequestParams> callback) {
      this.server.<ShowMessageRequestParams>addCallback(MessageMethods.SHOW_MESSAGE_REQUEST, callback, ShowMessageRequestParams.class);
    }
    
    @Override
    public void onLogMessage(final Consumer<MessageParams> callback) {
      this.server.<MessageParams>addCallback(MessageMethods.LOG_MESSAGE, callback, MessageParams.class);
    }
    
    public WindowServiceImpl(final JsonBasedLanguageServer server) {
      super();
      this.server = server;
    }
  }
  
  @FinalFieldsConstructor
  protected static class WorkspaceServiceImpl implements WorkspaceService {
    private final JsonBasedLanguageServer server;
    
    @Override
    public CompletableFuture<List<? extends SymbolInformation>> symbol(final WorkspaceSymbolParams params) {
      return this.server.<SymbolInformation>getListPromise(MessageMethods.WORKSPACE_SYMBOL, params, SymbolInformation.class);
    }
    
    @Override
    public void didChangeConfiguraton(final DidChangeConfigurationParams params) {
      this.server.sendNotification(MessageMethods.DID_CHANGE_CONF, params);
    }
    
    @Override
    public void didChangeWatchedFiles(final DidChangeWatchedFilesParams params) {
      this.server.sendNotification(MessageMethods.DID_CHANGE_FILES, params);
    }
    
    public WorkspaceServiceImpl(final JsonBasedLanguageServer server) {
      super();
      this.server = server;
    }
  }
  
  @FinalFieldsConstructor
  protected static class RequestHandler<T extends Object> implements Supplier<T> {
    @Accessors
    private final String methodId;
    
    @Accessors(AccessorType.PROTECTED_GETTER)
    private final String messageId;
    
    private final Object parameter;
    
    @Accessors(AccessorType.PROTECTED_GETTER)
    private final Class<?> resultType;
    
    private final JsonBasedLanguageServer server;
    
    @Accessors(AccessorType.PROTECTED_GETTER)
    private Object result;
    
    @Override
    public T get() {
      try {
        RequestMessageImpl _requestMessageImpl = new RequestMessageImpl();
        final Procedure1<RequestMessageImpl> _function = (RequestMessageImpl it) -> {
          it.setJsonrpc(LanguageServerProtocol.JSONRPC_VERSION);
          it.setId(this.messageId);
          it.setMethod(this.methodId);
          it.setParams(this.parameter);
        };
        final RequestMessageImpl message = ObjectExtensions.<RequestMessageImpl>operator_doubleArrow(_requestMessageImpl, _function);
        LanguageServerProtocol _protocol = this.server.getProtocol();
        _protocol.accept(message);
        synchronized (this) {
          while ((this.result == null)) {
            this.wait();
          }
        }
        return this.<T>convertResult();
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    }
    
    protected <T extends Object> T convertResult() {
      if ((this.result instanceof ResponseError)) {
        String _message = ((ResponseError)this.result).getMessage();
        int _code = ((ResponseError)this.result).getCode();
        throw new InvalidMessageException(_message, this.messageId, _code);
      } else {
        boolean _isInstance = this.resultType.isInstance(this.result);
        if (_isInstance) {
          return ((T) this.result);
        } else {
          if ((!(this.result instanceof CancellationException))) {
            throw new InvalidMessageException("No valid response received from server.", this.messageId);
          }
        }
      }
      return null;
    }
    
    public void accept(final ResponseMessage message) {
      Object _result = message.getResult();
      boolean _tripleNotEquals = (_result != null);
      if (_tripleNotEquals) {
        Object _result_1 = message.getResult();
        this.result = _result_1;
      } else {
        ResponseError _error = message.getError();
        boolean _tripleNotEquals_1 = (_error != null);
        if (_tripleNotEquals_1) {
          ResponseError _error_1 = message.getError();
          this.result = _error_1;
        } else {
          Object _object = new Object();
          this.result = _object;
        }
      }
      synchronized (this) {
        this.notify();
      }
    }
    
    public void cancel() {
      CancellationException _cancellationException = new CancellationException();
      this.result = _cancellationException;
      synchronized (this) {
        this.notify();
      }
    }
    
    @Pure
    public String getMethodId() {
      return this.methodId;
    }
    
    @Pure
    protected String getMessageId() {
      return this.messageId;
    }
    
    @Pure
    protected Class<?> getResultType() {
      return this.resultType;
    }
    
    @Pure
    protected Object getResult() {
      return this.result;
    }
    
    public RequestHandler(final String methodId, final String messageId, final Object parameter, final Class<?> resultType, final JsonBasedLanguageServer server) {
      super();
      this.methodId = methodId;
      this.messageId = messageId;
      this.parameter = parameter;
      this.resultType = resultType;
      this.server = server;
    }
  }
  
  @FinalFieldsConstructor
  protected static class ListRequestHandler<T extends Object> extends JsonBasedLanguageServer.RequestHandler<List<? extends T>> {
    @Override
    protected List<? extends T> convertResult() {
      Object _xblockexpression = null;
      {
        final Object result = this.getResult();
        final Class<?> resultType = this.getResultType();
        Object _xifexpression = null;
        if ((result instanceof ResponseError)) {
          String _message = ((ResponseError)result).getMessage();
          String _messageId = this.getMessageId();
          int _code = ((ResponseError)result).getCode();
          throw new InvalidMessageException(_message, _messageId, _code);
        } else {
          Object _xifexpression_1 = null;
          boolean _isInstance = resultType.isInstance(result);
          if (_isInstance) {
            return Collections.<T>unmodifiableList(CollectionLiterals.<T>newArrayList(((T) result)));
          } else {
            Object _xifexpression_2 = null;
            if (((result instanceof List<?>) && IterableExtensions.forall(((List<?>) result), ((Function1<Object, Boolean>) (Object it) -> {
              return Boolean.valueOf(resultType.isInstance(it));
            })))) {
              return ((List<T>) result);
            } else {
              Object _xifexpression_3 = null;
              if ((!(result instanceof CancellationException))) {
                String _messageId_1 = this.getMessageId();
                throw new InvalidMessageException("No valid response received from server.", _messageId_1);
              }
              _xifexpression_2 = _xifexpression_3;
            }
            _xifexpression_1 = _xifexpression_2;
          }
          _xifexpression = _xifexpression_1;
        }
        _xblockexpression = _xifexpression;
      }
      return ((List<? extends T>)_xblockexpression);
    }
    
    public ListRequestHandler(final String methodId, final String messageId, final Object parameter, final Class<?> resultType, final JsonBasedLanguageServer server) {
      super(methodId, messageId, parameter, resultType, server);
    }
  }
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private final JsonBasedLanguageServer.TextDocumentServiceImpl textDocumentService = new JsonBasedLanguageServer.TextDocumentServiceImpl(this);
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private final JsonBasedLanguageServer.WindowServiceImpl windowService = new JsonBasedLanguageServer.WindowServiceImpl(this);
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private final JsonBasedLanguageServer.WorkspaceServiceImpl workspaceService = new JsonBasedLanguageServer.WorkspaceServiceImpl(this);
  
  private final AtomicInteger nextRequestId = new AtomicInteger();
  
  private final Map<String, JsonBasedLanguageServer.RequestHandler<?>> requestHandlerMap = CollectionLiterals.<String, JsonBasedLanguageServer.RequestHandler<?>>newHashMap();
  
  private final Multimap<String, Pair<Class<?>, Consumer<?>>> notificationCallbackMap = HashMultimap.<String, Pair<Class<?>, Consumer<?>>>create();
  
  public JsonBasedLanguageServer() {
    this(new MessageJsonHandler());
  }
  
  public JsonBasedLanguageServer(final MessageJsonHandler jsonHandler) {
    this(jsonHandler, Executors.newCachedThreadPool());
  }
  
  public JsonBasedLanguageServer(final MessageJsonHandler jsonHandler, final ExecutorService executorService) {
    super(executorService);
    final Function1<String, String> _function = (String id) -> {
      String _xsynchronizedexpression = null;
      synchronized (this.requestHandlerMap) {
        JsonBasedLanguageServer.RequestHandler<?> _get = this.requestHandlerMap.get(id);
        String _methodId = null;
        if (_get!=null) {
          _methodId=_get.methodId;
        }
        _xsynchronizedexpression = _methodId;
      }
      return _xsynchronizedexpression;
    };
    jsonHandler.setResponseMethodResolver(_function);
    LanguageServerProtocol _createProtocol = this.createProtocol(jsonHandler);
    this.setProtocol(_createProtocol);
  }
  
  protected LanguageServerProtocol createProtocol(final MessageJsonHandler jsonHandler) {
    return new LanguageServerProtocol(jsonHandler, this);
  }
  
  @Override
  public void accept(final Message message) {
    if ((message instanceof ResponseMessage)) {
      synchronized (this.requestHandlerMap) {
        String _id = ((ResponseMessage)message).getId();
        final JsonBasedLanguageServer.RequestHandler<?> handler = this.requestHandlerMap.remove(_id);
        if ((handler != null)) {
          handler.accept(((ResponseMessage)message));
        } else {
          LanguageServerProtocol _protocol = this.getProtocol();
          String _id_1 = ((ResponseMessage)message).getId();
          String _plus = ("No matching request for response with id " + _id_1);
          _protocol.logError(_plus, null);
        }
      }
    } else {
      if ((message instanceof NotificationMessage)) {
        List<Consumer<?>> _xsynchronizedexpression = null;
        synchronized (this.notificationCallbackMap) {
          String _method = ((NotificationMessage)message).getMethod();
          Collection<Pair<Class<?>, Consumer<?>>> _get = this.notificationCallbackMap.get(_method);
          final Function1<Pair<Class<?>, Consumer<?>>, Boolean> _function = (Pair<Class<?>, Consumer<?>> it) -> {
            Class<?> _key = it.getKey();
            Object _params = ((NotificationMessage)message).getParams();
            return Boolean.valueOf(_key.isInstance(_params));
          };
          Iterable<Pair<Class<?>, Consumer<?>>> _filter = IterableExtensions.<Pair<Class<?>, Consumer<?>>>filter(_get, _function);
          final Function1<Pair<Class<?>, Consumer<?>>, Consumer<?>> _function_1 = (Pair<Class<?>, Consumer<?>> it) -> {
            return it.getValue();
          };
          Iterable<Consumer<?>> _map = IterableExtensions.<Pair<Class<?>, Consumer<?>>, Consumer<?>>map(_filter, _function_1);
          _xsynchronizedexpression = IterableExtensions.<Consumer<?>>toList(_map);
        }
        final List<Consumer<?>> callbacks = _xsynchronizedexpression;
        for (final Consumer<?> callback : callbacks) {
          Object _params = ((NotificationMessage)message).getParams();
          ((Consumer<Object>) callback).accept(_params);
        }
      }
    }
  }
  
  protected void sendRequest(final String methodId, final Object parameter) {
    RequestMessageImpl _requestMessageImpl = new RequestMessageImpl();
    final Procedure1<RequestMessageImpl> _function = (RequestMessageImpl it) -> {
      it.setJsonrpc(LanguageServerProtocol.JSONRPC_VERSION);
      int _andIncrement = this.nextRequestId.getAndIncrement();
      String _string = Integer.toString(_andIncrement);
      it.setId(_string);
      it.setMethod(methodId);
      it.setParams(parameter);
    };
    final RequestMessageImpl message = ObjectExtensions.<RequestMessageImpl>operator_doubleArrow(_requestMessageImpl, _function);
    LanguageServerProtocol _protocol = this.getProtocol();
    _protocol.accept(message);
  }
  
  protected <T extends Object> CompletableFuture<T> getPromise(final String methodId, final Object parameter, final Class<T> resultType) {
    int _andIncrement = this.nextRequestId.getAndIncrement();
    final String messageId = Integer.toString(_andIncrement);
    final JsonBasedLanguageServer.RequestHandler<T> handler = new JsonBasedLanguageServer.RequestHandler<T>(methodId, messageId, parameter, resultType, this);
    synchronized (this.requestHandlerMap) {
      this.requestHandlerMap.put(messageId, handler);
    }
    ExecutorService _executorService = this.getExecutorService();
    final CompletableFuture<T> promise = CompletableFuture.<T>supplyAsync(handler, _executorService);
    final BiConsumer<T, Throwable> _function = (T result, Throwable throwable) -> {
      boolean _isCancelled = promise.isCancelled();
      if (_isCancelled) {
        handler.cancel();
        CancelParamsImpl _cancelParamsImpl = new CancelParamsImpl();
        final Procedure1<CancelParamsImpl> _function_1 = (CancelParamsImpl it) -> {
          it.setId(messageId);
        };
        CancelParamsImpl _doubleArrow = ObjectExtensions.<CancelParamsImpl>operator_doubleArrow(_cancelParamsImpl, _function_1);
        this.sendNotification(MessageMethods.CANCEL, _doubleArrow);
      }
    };
    promise.whenComplete(_function);
    return promise;
  }
  
  protected <T extends Object> CompletableFuture<List<? extends T>> getListPromise(final String methodId, final Object parameter, final Class<T> resultType) {
    int _andIncrement = this.nextRequestId.getAndIncrement();
    final String messageId = Integer.toString(_andIncrement);
    final JsonBasedLanguageServer.ListRequestHandler<T> handler = new JsonBasedLanguageServer.ListRequestHandler<T>(methodId, messageId, parameter, resultType, this);
    synchronized (this.requestHandlerMap) {
      this.requestHandlerMap.put(messageId, handler);
    }
    ExecutorService _executorService = this.getExecutorService();
    return CompletableFuture.<List<? extends T>>supplyAsync(handler, _executorService);
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
  
  protected <T extends Object> void addCallback(final String methodId, final Consumer<T> callback, final Class<T> parameterType) {
    synchronized (this.notificationCallbackMap) {
      Pair<Class<?>, Consumer<?>> _mappedTo = Pair.<Class<?>, Consumer<?>>of(parameterType, callback);
      this.notificationCallbackMap.put(methodId, _mappedTo);
    }
  }
  
  @Override
  public CompletableFuture<InitializeResult> initialize(final InitializeParams params) {
    return this.<InitializeResult>getPromise(MessageMethods.INITIALIZE, params, InitializeResult.class);
  }
  
  @Override
  public void shutdown() {
    try {
      this.sendRequest(MessageMethods.SHUTDOWN, null);
    } finally {
      ExecutorService _executorService = this.getExecutorService();
      _executorService.shutdown();
    }
  }
  
  @Override
  public void exit() {
    try {
      this.sendRequest(MessageMethods.EXIT, null);
    } finally {
      ExecutorService _executorService = this.getExecutorService();
      _executorService.shutdownNow();
      synchronized (this.requestHandlerMap) {
        Collection<JsonBasedLanguageServer.RequestHandler<?>> _values = this.requestHandlerMap.values();
        for (final JsonBasedLanguageServer.RequestHandler<?> handler : _values) {
          handler.cancel();
        }
      }
      super.exit();
    }
  }
  
  public void onError(final Procedure2<? super String, ? super Throwable> callback) {
    LanguageServerProtocol _protocol = this.getProtocol();
    _protocol.addErrorListener(callback);
  }
  
  @Pure
  public TextDocumentService getTextDocumentService() {
    return this.textDocumentService;
  }
  
  @Pure
  public WindowService getWindowService() {
    return this.windowService;
  }
  
  @Pure
  public WorkspaceService getWorkspaceService() {
    return this.workspaceService;
  }
}
