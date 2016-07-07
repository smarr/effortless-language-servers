/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.services.json;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.typefox.lsapi.CancelParamsImpl;
import io.typefox.lsapi.CodeActionParamsImpl;
import io.typefox.lsapi.CodeLensImpl;
import io.typefox.lsapi.CodeLensParamsImpl;
import io.typefox.lsapi.CommandImpl;
import io.typefox.lsapi.CompletionItemImpl;
import io.typefox.lsapi.CompletionListImpl;
import io.typefox.lsapi.DidChangeConfigurationParamsImpl;
import io.typefox.lsapi.DidChangeTextDocumentParamsImpl;
import io.typefox.lsapi.DidChangeWatchedFilesParamsImpl;
import io.typefox.lsapi.DidCloseTextDocumentParamsImpl;
import io.typefox.lsapi.DidOpenTextDocumentParamsImpl;
import io.typefox.lsapi.DidSaveTextDocumentParamsImpl;
import io.typefox.lsapi.DocumentFormattingParamsImpl;
import io.typefox.lsapi.DocumentHighlightImpl;
import io.typefox.lsapi.DocumentOnTypeFormattingParamsImpl;
import io.typefox.lsapi.DocumentRangeFormattingParamsImpl;
import io.typefox.lsapi.DocumentSymbolParamsImpl;
import io.typefox.lsapi.HoverImpl;
import io.typefox.lsapi.InitializeParamsImpl;
import io.typefox.lsapi.InitializeResultImpl;
import io.typefox.lsapi.LocationImpl;
import io.typefox.lsapi.Message;
import io.typefox.lsapi.MessageImpl;
import io.typefox.lsapi.MessageParamsImpl;
import io.typefox.lsapi.NotificationMessageImpl;
import io.typefox.lsapi.PublishDiagnosticsParamsImpl;
import io.typefox.lsapi.ReferenceParamsImpl;
import io.typefox.lsapi.RenameParamsImpl;
import io.typefox.lsapi.RequestMessageImpl;
import io.typefox.lsapi.ResponseErrorImpl;
import io.typefox.lsapi.ResponseMessageImpl;
import io.typefox.lsapi.ShowMessageRequestParamsImpl;
import io.typefox.lsapi.SignatureHelpImpl;
import io.typefox.lsapi.SymbolInformationImpl;
import io.typefox.lsapi.TextDocumentPositionParamsImpl;
import io.typefox.lsapi.TextEditImpl;
import io.typefox.lsapi.WorkspaceEditImpl;
import io.typefox.lsapi.WorkspaceSymbolParamsImpl;
import io.typefox.lsapi.services.json.InvalidMessageException;
import io.typefox.lsapi.services.json.MessageMethods;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Pair;

@SuppressWarnings("all")
public class MessageJsonHandler {
  private final static Map<String, Class<?>> REQUEST_PARAM_TYPES = Collections.<String, Class<?>>unmodifiableMap(CollectionLiterals.<String, Class<?>>newHashMap(Pair.<String, Class<InitializeParamsImpl>>of(MessageMethods.INITIALIZE, InitializeParamsImpl.class), Pair.<String, Class<TextDocumentPositionParamsImpl>>of(MessageMethods.DOC_COMPLETION, TextDocumentPositionParamsImpl.class), Pair.<String, Class<CompletionItemImpl>>of(MessageMethods.RESOLVE_COMPLETION, CompletionItemImpl.class), Pair.<String, Class<TextDocumentPositionParamsImpl>>of(MessageMethods.DOC_HOVER, TextDocumentPositionParamsImpl.class), Pair.<String, Class<TextDocumentPositionParamsImpl>>of(MessageMethods.DOC_SIGNATURE_HELP, TextDocumentPositionParamsImpl.class), Pair.<String, Class<TextDocumentPositionParamsImpl>>of(MessageMethods.DOC_DEFINITION, TextDocumentPositionParamsImpl.class), Pair.<String, Class<TextDocumentPositionParamsImpl>>of(MessageMethods.DOC_HIGHLIGHT, TextDocumentPositionParamsImpl.class), Pair.<String, Class<ReferenceParamsImpl>>of(MessageMethods.DOC_REFERENCES, ReferenceParamsImpl.class), Pair.<String, Class<DocumentSymbolParamsImpl>>of(MessageMethods.DOC_SYMBOL, DocumentSymbolParamsImpl.class), Pair.<String, Class<WorkspaceSymbolParamsImpl>>of(MessageMethods.WORKSPACE_SYMBOL, WorkspaceSymbolParamsImpl.class), Pair.<String, Class<CodeActionParamsImpl>>of(MessageMethods.DOC_CODE_ACTION, CodeActionParamsImpl.class), Pair.<String, Class<CodeLensParamsImpl>>of(MessageMethods.DOC_CODE_LENS, CodeLensParamsImpl.class), Pair.<String, Class<CodeLensImpl>>of(MessageMethods.RESOLVE_CODE_LENS, CodeLensImpl.class), Pair.<String, Class<DocumentFormattingParamsImpl>>of(MessageMethods.DOC_FORMATTING, DocumentFormattingParamsImpl.class), Pair.<String, Class<DocumentRangeFormattingParamsImpl>>of(MessageMethods.DOC_RANGE_FORMATTING, DocumentRangeFormattingParamsImpl.class), Pair.<String, Class<DocumentOnTypeFormattingParamsImpl>>of(MessageMethods.DOC_TYPE_FORMATTING, DocumentOnTypeFormattingParamsImpl.class), Pair.<String, Class<RenameParamsImpl>>of(MessageMethods.DOC_RENAME, RenameParamsImpl.class), Pair.<String, Class<ShowMessageRequestParamsImpl>>of(MessageMethods.SHOW_MESSAGE_REQUEST, ShowMessageRequestParamsImpl.class)));
  
  private final static Map<String, Class<?>> RESPONSE_RESULT_TYPES = Collections.<String, Class<?>>unmodifiableMap(CollectionLiterals.<String, Class<?>>newHashMap(Pair.<String, Class<InitializeResultImpl>>of(MessageMethods.INITIALIZE, InitializeResultImpl.class), Pair.<String, Class<CompletionListImpl>>of(MessageMethods.DOC_COMPLETION, CompletionListImpl.class), Pair.<String, Class<CompletionItemImpl>>of(MessageMethods.RESOLVE_COMPLETION, CompletionItemImpl.class), Pair.<String, Class<HoverImpl>>of(MessageMethods.DOC_HOVER, HoverImpl.class), Pair.<String, Class<SignatureHelpImpl>>of(MessageMethods.DOC_SIGNATURE_HELP, SignatureHelpImpl.class), Pair.<String, Class<LocationImpl>>of(MessageMethods.DOC_DEFINITION, LocationImpl.class), Pair.<String, Class<DocumentHighlightImpl>>of(MessageMethods.DOC_HIGHLIGHT, DocumentHighlightImpl.class), Pair.<String, Class<LocationImpl>>of(MessageMethods.DOC_REFERENCES, LocationImpl.class), Pair.<String, Class<SymbolInformationImpl>>of(MessageMethods.DOC_SYMBOL, SymbolInformationImpl.class), Pair.<String, Class<SymbolInformationImpl>>of(MessageMethods.WORKSPACE_SYMBOL, SymbolInformationImpl.class), Pair.<String, Class<CommandImpl>>of(MessageMethods.DOC_CODE_ACTION, CommandImpl.class), Pair.<String, Class<CodeLensImpl>>of(MessageMethods.DOC_CODE_LENS, CodeLensImpl.class), Pair.<String, Class<CodeLensImpl>>of(MessageMethods.RESOLVE_CODE_LENS, CodeLensImpl.class), Pair.<String, Class<TextEditImpl>>of(MessageMethods.DOC_FORMATTING, TextEditImpl.class), Pair.<String, Class<TextEditImpl>>of(MessageMethods.DOC_RANGE_FORMATTING, TextEditImpl.class), Pair.<String, Class<TextEditImpl>>of(MessageMethods.DOC_TYPE_FORMATTING, TextEditImpl.class), Pair.<String, Class<WorkspaceEditImpl>>of(MessageMethods.DOC_RENAME, WorkspaceEditImpl.class)));
  
  private final static Map<String, Class<?>> NOTIFICATION_PARAM_TYPES = Collections.<String, Class<?>>unmodifiableMap(CollectionLiterals.<String, Class<?>>newHashMap(Pair.<String, Class<PublishDiagnosticsParamsImpl>>of(MessageMethods.SHOW_DIAGNOSTICS, PublishDiagnosticsParamsImpl.class), Pair.<String, Class<DidChangeConfigurationParamsImpl>>of(MessageMethods.DID_CHANGE_CONF, DidChangeConfigurationParamsImpl.class), Pair.<String, Class<DidOpenTextDocumentParamsImpl>>of(MessageMethods.DID_OPEN_DOC, DidOpenTextDocumentParamsImpl.class), Pair.<String, Class<DidChangeTextDocumentParamsImpl>>of(MessageMethods.DID_CHANGE_DOC, DidChangeTextDocumentParamsImpl.class), Pair.<String, Class<DidCloseTextDocumentParamsImpl>>of(MessageMethods.DID_CLOSE_DOC, DidCloseTextDocumentParamsImpl.class), Pair.<String, Class<DidChangeWatchedFilesParamsImpl>>of(MessageMethods.DID_CHANGE_FILES, DidChangeWatchedFilesParamsImpl.class), Pair.<String, Class<DidSaveTextDocumentParamsImpl>>of(MessageMethods.DID_SAVE_DOC, DidSaveTextDocumentParamsImpl.class), Pair.<String, Class<MessageParamsImpl>>of(MessageMethods.SHOW_MESSAGE, MessageParamsImpl.class), Pair.<String, Class<MessageParamsImpl>>of(MessageMethods.LOG_MESSAGE, MessageParamsImpl.class), Pair.<String, Class<ShowMessageRequestParamsImpl>>of(MessageMethods.SHOW_MESSAGE_REQUEST, ShowMessageRequestParamsImpl.class), Pair.<String, Class<CancelParamsImpl>>of(MessageMethods.CANCEL, CancelParamsImpl.class)));
  
  private final JsonParser jsonParser = new JsonParser();
  
  private final Gson gson;
  
  @Accessors(AccessorType.PUBLIC_SETTER)
  private Function1<? super String, ? extends String> responseMethodResolver;
  
  public MessageJsonHandler() {
    Gson _gson = new Gson();
    this.gson = _gson;
  }
  
  public MessageJsonHandler(final Gson gson) {
    this.gson = gson;
  }
  
  public Message parseMessage(final CharSequence input) {
    String _string = input.toString();
    StringReader _stringReader = new StringReader(_string);
    return this.parseMessage(_stringReader);
  }
  
  public Message parseMessage(final Reader input) {
    JsonElement _parse = this.jsonParser.parse(input);
    final JsonObject json = _parse.getAsJsonObject();
    final JsonElement idElement = json.get("id");
    final JsonElement methodElement = json.get("method");
    MessageImpl result = null;
    if (((idElement != null) && (methodElement != null))) {
      String _asString = idElement.getAsString();
      String _asString_1 = methodElement.getAsString();
      RequestMessageImpl _parseRequest = this.parseRequest(json, _asString, _asString_1);
      result = _parseRequest;
    } else {
      if (((idElement != null) && ((json.get("result") != null) || (json.get("error") != null)))) {
        String _asString_2 = idElement.getAsString();
        ResponseMessageImpl _parseResponse = this.parseResponse(json, _asString_2);
        result = _parseResponse;
      } else {
        if ((methodElement != null)) {
          String _asString_3 = methodElement.getAsString();
          NotificationMessageImpl _parseNotification = this.parseNotification(json, _asString_3);
          result = _parseNotification;
        } else {
          MessageImpl _messageImpl = new MessageImpl();
          result = _messageImpl;
        }
      }
    }
    JsonElement _get = json.get("jsonrpc");
    String _asString_4 = null;
    if (_get!=null) {
      _asString_4=_get.getAsString();
    }
    result.setJsonrpc(_asString_4);
    return result;
  }
  
  protected RequestMessageImpl parseRequest(final JsonObject json, final String requestId, final String method) {
    try {
      final RequestMessageImpl result = new RequestMessageImpl();
      result.setId(requestId);
      result.setMethod(method);
      JsonElement _get = json.get("params");
      JsonObject _asJsonObject = null;
      if (_get!=null) {
        _asJsonObject=_get.getAsJsonObject();
      }
      final JsonObject params = _asJsonObject;
      if ((params != null)) {
        final Class<?> paramType = MessageJsonHandler.REQUEST_PARAM_TYPES.get(method);
        if ((paramType != null)) {
          Object _fromJson = this.gson.fromJson(params, paramType);
          result.setParams(_fromJson);
        }
      }
      return result;
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e = (Exception)_t;
        String _message = e.getMessage();
        String _plus = ("Could not parse request: " + _message);
        throw new InvalidMessageException(_plus, requestId, e);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  protected ResponseMessageImpl parseResponse(final JsonObject json, final String responseId) {
    if ((this.responseMethodResolver == null)) {
      throw new IllegalStateException("Response methods are not accepted.");
    }
    try {
      final ResponseMessageImpl result = new ResponseMessageImpl();
      result.setId(responseId);
      final JsonElement resultElem = json.get("result");
      if ((resultElem != null)) {
        final String method = this.responseMethodResolver.apply(responseId);
        if ((method != null)) {
          final Class<?> resultType = MessageJsonHandler.RESPONSE_RESULT_TYPES.get(method);
          if ((resultType != null)) {
            boolean _isJsonArray = resultElem.isJsonArray();
            if (_isJsonArray) {
              final JsonArray arrayElem = resultElem.getAsJsonArray();
              int _size = arrayElem.size();
              final ArrayList<Object> list = Lists.<Object>newArrayListWithExpectedSize(_size);
              for (final JsonElement e : arrayElem) {
                Object _fromJson = this.gson.fromJson(e, resultType);
                list.add(_fromJson);
              }
              result.setResult(list);
            } else {
              Object _fromJson_1 = this.gson.fromJson(resultElem, resultType);
              result.setResult(_fromJson_1);
            }
          }
        }
      } else {
        JsonElement _get = json.get("error");
        JsonObject _asJsonObject = null;
        if (_get!=null) {
          _asJsonObject=_get.getAsJsonObject();
        }
        final JsonObject error = _asJsonObject;
        if ((error != null)) {
          ResponseErrorImpl _fromJson_2 = this.gson.<ResponseErrorImpl>fromJson(error, ResponseErrorImpl.class);
          result.setError(_fromJson_2);
        }
      }
      return result;
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e_1 = (Exception)_t;
        String _message = e_1.getMessage();
        String _plus = ("Could not parse response: " + _message);
        throw new InvalidMessageException(_plus, responseId, e_1);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  protected NotificationMessageImpl parseNotification(final JsonObject json, final String method) {
    try {
      final NotificationMessageImpl result = new NotificationMessageImpl();
      result.setMethod(method);
      JsonElement _get = json.get("params");
      JsonObject _asJsonObject = null;
      if (_get!=null) {
        _asJsonObject=_get.getAsJsonObject();
      }
      final JsonObject params = _asJsonObject;
      if ((params != null)) {
        final Class<?> paramType = MessageJsonHandler.NOTIFICATION_PARAM_TYPES.get(method);
        if ((paramType != null)) {
          Object _fromJson = this.gson.fromJson(params, paramType);
          result.setParams(_fromJson);
        }
      }
      return result;
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e = (Exception)_t;
        String _message = e.getMessage();
        String _plus = ("Could not parse notification: " + _message);
        throw new InvalidMessageException(_plus, null, e);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  public String serialize(final Message message) {
    final StringWriter writer = new StringWriter();
    this.serialize(message, writer);
    return writer.toString();
  }
  
  public void serialize(final Message message, final Writer output) {
    this.gson.toJson(message, output);
  }
  
  public void setResponseMethodResolver(final Function1<? super String, ? extends String> responseMethodResolver) {
    this.responseMethodResolver = responseMethodResolver;
  }
}
