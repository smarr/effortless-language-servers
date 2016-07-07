/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.services.json;

import io.typefox.lsapi.Message;
import io.typefox.lsapi.MessageImpl;
import io.typefox.lsapi.RequestMessage;
import io.typefox.lsapi.ResponseError;
import io.typefox.lsapi.ResponseErrorImpl;
import io.typefox.lsapi.ResponseMessage;
import io.typefox.lsapi.ResponseMessageImpl;
import io.typefox.lsapi.services.json.InvalidMessageException;
import io.typefox.lsapi.services.json.MessageJsonHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.eclipse.xtext.xbase.lib.Pure;

@FinalFieldsConstructor
@SuppressWarnings("all")
public class LanguageServerProtocol implements Consumer<Message> {
  private static class Headers {
    private int contentLength = (-1);
    
    private String charset = "UTF-8";
  }
  
  public static class IOHandler implements Runnable {
    private final LanguageServerProtocol protocol;
    
    @Accessors(AccessorType.PUBLIC_SETTER)
    private InputStream input;
    
    @Accessors(AccessorType.PUBLIC_SETTER)
    private OutputStream output;
    
    private final Object outputLock = new Object();
    
    @Accessors(AccessorType.PUBLIC_GETTER)
    private boolean isRunning;
    
    private boolean keepRunning;
    
    private Thread thread;
    
    protected IOHandler(final LanguageServerProtocol protocol) {
      this.protocol = protocol;
    }
    
    @Override
    public void run() {
      if (this.isRunning) {
        throw new IllegalStateException("The I/O handler is already running.");
      }
      Thread _currentThread = Thread.currentThread();
      this.thread = _currentThread;
      this.isRunning = true;
      try {
        this.run(this.input);
      } catch (final Throwable _t) {
        if (_t instanceof ClosedChannelException) {
          final ClosedChannelException e = (ClosedChannelException)_t;
        } else if (_t instanceof Exception) {
          final Exception e_1 = (Exception)_t;
          this.protocol.logError(e_1);
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      } finally {
        this.isRunning = false;
        this.thread = null;
      }
    }
    
    public void stop() {
      this.keepRunning = false;
      if (this.thread!=null) {
        this.thread.interrupt();
      }
    }
    
    protected void run(final InputStream input) throws IOException {
      this.keepRunning = true;
      StringBuilder headerBuilder = null;
      StringBuilder debugBuilder = null;
      boolean newLine = false;
      LanguageServerProtocol.Headers headers = new LanguageServerProtocol.Headers();
      while (this.keepRunning) {
        try {
          final int c = input.read();
          if ((c == (-1))) {
            this.keepRunning = false;
          } else {
            if ((debugBuilder == null)) {
              StringBuilder _stringBuilder = new StringBuilder();
              debugBuilder = _stringBuilder;
            }
            debugBuilder.append(((char) c));
            boolean _matches = this.matches(c, '\n');
            if (_matches) {
              if (newLine) {
                if ((headers.contentLength < 0)) {
                  IllegalStateException _illegalStateException = new IllegalStateException(
                    (((("Missing header " + LanguageServerProtocol.H_CONTENT_LENGTH) + " in input \"") + debugBuilder) + "\""));
                  this.protocol.logError(_illegalStateException);
                } else {
                  final boolean result = this.handleMessage(input, headers);
                  if ((!result)) {
                    this.keepRunning = false;
                  }
                  newLine = false;
                }
                LanguageServerProtocol.Headers _headers = new LanguageServerProtocol.Headers();
                headers = _headers;
                debugBuilder = null;
              } else {
                if ((headerBuilder != null)) {
                  String _string = headerBuilder.toString();
                  this.parseHeader(_string, headers);
                  headerBuilder = null;
                }
              }
              newLine = true;
            } else {
              boolean _matches_1 = this.matches(c, '\r');
              boolean _not = (!_matches_1);
              if (_not) {
                if ((headerBuilder == null)) {
                  StringBuilder _stringBuilder_1 = new StringBuilder();
                  headerBuilder = _stringBuilder_1;
                }
                headerBuilder.append(((char) c));
                newLine = false;
              }
            }
          }
        } catch (final Throwable _t) {
          if (_t instanceof InterruptedIOException) {
            final InterruptedIOException exception = (InterruptedIOException)_t;
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
      }
    }
    
    private boolean matches(final int c1, final char c2) {
      return (c1 == c2);
    }
    
    protected void parseHeader(final String line, final LanguageServerProtocol.Headers headers) {
      final int sepIndex = line.indexOf(":");
      if ((sepIndex >= 0)) {
        String _substring = line.substring(0, sepIndex);
        final String key = _substring.trim();
        switch (key) {
          case LanguageServerProtocol.H_CONTENT_LENGTH:
            try {
              String _substring_1 = line.substring((sepIndex + 1));
              String _trim = _substring_1.trim();
              int _parseInt = Integer.parseInt(_trim);
              headers.contentLength = _parseInt;
            } catch (final Throwable _t) {
              if (_t instanceof NumberFormatException) {
                final NumberFormatException e = (NumberFormatException)_t;
                this.protocol.logError(e);
              } else {
                throw Exceptions.sneakyThrow(_t);
              }
            }
            break;
          case LanguageServerProtocol.H_CONTENT_TYPE:
            final int charsetIndex = line.indexOf("charset=");
            if ((charsetIndex >= 0)) {
              String _substring_2 = line.substring((charsetIndex + 8));
              String _trim_1 = _substring_2.trim();
              headers.charset = _trim_1;
            }
            break;
        }
      }
    }
    
    protected boolean handleMessage(final InputStream input, final LanguageServerProtocol.Headers headers) {
      try {
        try {
          final int contentLength = headers.contentLength;
          final byte[] buffer = new byte[contentLength];
          int bytesRead = 0;
          while ((bytesRead < contentLength)) {
            {
              final int readResult = input.read(buffer, bytesRead, (contentLength - bytesRead));
              if ((readResult == (-1))) {
                return false;
              }
              int _bytesRead = bytesRead;
              bytesRead = (_bytesRead + readResult);
            }
          }
          String _string = new String(buffer, headers.charset);
          this.protocol.handleMessage(_string);
        } catch (final Throwable _t) {
          if (_t instanceof UnsupportedEncodingException) {
            final UnsupportedEncodingException e = (UnsupportedEncodingException)_t;
            this.protocol.logError(e);
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
        return true;
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    }
    
    public void send(final String content, final String charset) {
      this.send(this.output, content, charset);
    }
    
    protected void send(final OutputStream output, final String content, final String charset) {
      try {
        final byte[] responseBytes = content.getBytes(charset);
        final StringBuilder headerBuilder = new StringBuilder();
        StringBuilder _append = headerBuilder.append(LanguageServerProtocol.H_CONTENT_LENGTH);
        StringBuilder _append_1 = _append.append(": ");
        int _length = responseBytes.length;
        StringBuilder _append_2 = _append_1.append(_length);
        _append_2.append("\r\n");
        if ((charset != "UTF-8")) {
          StringBuilder _append_3 = headerBuilder.append(LanguageServerProtocol.H_CONTENT_TYPE);
          StringBuilder _append_4 = _append_3.append(": ");
          StringBuilder _append_5 = _append_4.append(LanguageServerProtocol.CT_JSON);
          StringBuilder _append_6 = _append_5.append("; charset=");
          StringBuilder _append_7 = _append_6.append(charset);
          _append_7.append("\r\n");
        }
        headerBuilder.append("\r\n");
        synchronized (this.outputLock) {
          String _string = headerBuilder.toString();
          byte[] _bytes = _string.getBytes();
          output.write(_bytes);
          output.write(responseBytes);
          output.flush();
        }
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    }
    
    public void setInput(final InputStream input) {
      this.input = input;
    }
    
    public void setOutput(final OutputStream output) {
      this.output = output;
    }
    
    @Pure
    public boolean isRunning() {
      return this.isRunning;
    }
  }
  
  public final static String JSONRPC_VERSION = "2.0";
  
  public final static String H_CONTENT_LENGTH = "Content-Length";
  
  public final static String H_CONTENT_TYPE = "Content-Type";
  
  private final static String CT_JSON = "application/json";
  
  private final MessageJsonHandler jsonHandler;
  
  private final Consumer<Message> incomingMessageAcceptor;
  
  @Accessors
  private final LanguageServerProtocol.IOHandler ioHandler = new LanguageServerProtocol.IOHandler(this);
  
  @Accessors
  private String outputEncoding = "UTF-8";
  
  private final List<Procedure2<? super String, ? super Throwable>> errorListeners = CollectionLiterals.<Procedure2<? super String, ? super Throwable>>newArrayList();
  
  private final List<Procedure2<? super Message, ? super String>> incomingMessageListeners = CollectionLiterals.<Procedure2<? super Message, ? super String>>newArrayList();
  
  private final List<Procedure2<? super Message, ? super String>> outgoingMessageListeners = CollectionLiterals.<Procedure2<? super Message, ? super String>>newArrayList();
  
  public void addErrorListener(final Procedure2<? super String, ? super Throwable> listener) {
    this.errorListeners.add(listener);
  }
  
  public void addIncomingMessageListener(final Procedure2<? super Message, ? super String> listener) {
    this.incomingMessageListeners.add(listener);
  }
  
  public void addOutgoingMessageListener(final Procedure2<? super Message, ? super String> listener) {
    this.outgoingMessageListeners.add(listener);
  }
  
  protected void handleMessage(final String content) throws IOException {
    String requestId = null;
    try {
      final Message message = this.jsonHandler.parseMessage(content);
      if ((message instanceof RequestMessage)) {
        String _id = ((RequestMessage)message).getId();
        requestId = _id;
      }
      this.logIncomingMessage(message, content);
      this.incomingMessageAcceptor.accept(message);
    } catch (final Throwable _t) {
      if (_t instanceof InvalidMessageException) {
        final InvalidMessageException e = (InvalidMessageException)_t;
        this.logError(e);
        String _message = e.getMessage();
        int _errorCode = e.getErrorCode();
        String _requestId = e.getRequestId();
        ResponseMessage _createErrorResponse = this.createErrorResponse(_message, _errorCode, _requestId);
        this.accept(_createErrorResponse);
      } else if (_t instanceof Exception) {
        final Exception e_1 = (Exception)_t;
        this.logError(e_1);
        String _message_1 = e_1.getMessage();
        ResponseMessage _createErrorResponse_1 = this.createErrorResponse(_message_1, ResponseError.INTERNAL_ERROR, requestId);
        this.accept(_createErrorResponse_1);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  protected void logIncomingMessage(final Message message, final String json) {
    for (final Procedure2<? super Message, ? super String> l : this.incomingMessageListeners) {
      l.apply(message, json);
    }
  }
  
  @Override
  public void accept(final Message message) {
    try {
      if (((message.getJsonrpc() == null) && (message instanceof MessageImpl))) {
        ((MessageImpl) message).setJsonrpc(LanguageServerProtocol.JSONRPC_VERSION);
      }
      final String content = this.jsonHandler.serialize(message);
      this.ioHandler.send(content, this.outputEncoding);
      this.logOutgoingMessage(message, content);
    } catch (final Throwable _t) {
      if (_t instanceof IOException) {
        final IOException e = (IOException)_t;
        this.logError(e);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  protected ResponseMessage createErrorResponse(final String errorMessage, final int errorCode, final String requestId) {
    final ResponseMessageImpl response = new ResponseMessageImpl();
    response.setJsonrpc(LanguageServerProtocol.JSONRPC_VERSION);
    if ((requestId != null)) {
      response.setId(requestId);
    }
    ResponseErrorImpl _responseErrorImpl = new ResponseErrorImpl();
    final Procedure1<ResponseErrorImpl> _function = (ResponseErrorImpl it) -> {
      it.setMessage(errorMessage);
      it.setCode(errorCode);
    };
    ResponseErrorImpl _doubleArrow = ObjectExtensions.<ResponseErrorImpl>operator_doubleArrow(_responseErrorImpl, _function);
    response.setError(_doubleArrow);
    return response;
  }
  
  protected void logOutgoingMessage(final Message message, final String json) {
    for (final Procedure2<? super Message, ? super String> l : this.outgoingMessageListeners) {
      l.apply(message, json);
    }
  }
  
  protected void logError(final Throwable throwable) {
    String _message = throwable.getMessage();
    this.logError(_message, throwable);
  }
  
  protected void logError(final String message, final Throwable throwable) {
    for (final Procedure2<? super String, ? super Throwable> l : this.errorListeners) {
      l.apply(message, throwable);
    }
  }
  
  public LanguageServerProtocol(final MessageJsonHandler jsonHandler, final Consumer<Message> incomingMessageAcceptor) {
    super();
    this.jsonHandler = jsonHandler;
    this.incomingMessageAcceptor = incomingMessageAcceptor;
  }
  
  @Pure
  public LanguageServerProtocol.IOHandler getIoHandler() {
    return this.ioHandler;
  }
  
  @Pure
  public String getOutputEncoding() {
    return this.outputEncoding;
  }
  
  public void setOutputEncoding(final String outputEncoding) {
    this.outputEncoding = outputEncoding;
  }
}
