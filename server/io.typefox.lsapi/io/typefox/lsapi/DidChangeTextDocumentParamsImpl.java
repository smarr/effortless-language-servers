/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.DidChangeTextDocumentParams;
import io.typefox.lsapi.TextDocumentContentChangeEventImpl;
import io.typefox.lsapi.VersionedTextDocumentIdentifierImpl;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The document change notification is sent from the client to the server to signal changes to a text document.
 */
@SuppressWarnings("all")
public class DidChangeTextDocumentParamsImpl implements DidChangeTextDocumentParams {
  /**
   * The document that did change. The version number points to the version after all provided content changes have
   * been applied.
   */
  private VersionedTextDocumentIdentifierImpl textDocument;
  
  @Pure
  @Override
  public VersionedTextDocumentIdentifierImpl getTextDocument() {
    return this.textDocument;
  }
  
  public void setTextDocument(final VersionedTextDocumentIdentifierImpl textDocument) {
    this.textDocument = textDocument;
  }
  
  /**
   * Legacy property to support protocol version 1.0 requests.
   */
  private String uri;
  
  @Pure
  @Override
  @Deprecated
  public String getUri() {
    return this.uri;
  }
  
  public void setUri(final String uri) {
    this.uri = uri;
  }
  
  /**
   * The actual content changes.
   */
  private List<TextDocumentContentChangeEventImpl> contentChanges;
  
  @Pure
  @Override
  public List<TextDocumentContentChangeEventImpl> getContentChanges() {
    return this.contentChanges;
  }
  
  public void setContentChanges(final List<TextDocumentContentChangeEventImpl> contentChanges) {
    this.contentChanges = contentChanges;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("textDocument", this.textDocument);
    b.add("uri", this.uri);
    b.add("contentChanges", this.contentChanges);
    return b.toString();
  }
  
  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DidChangeTextDocumentParamsImpl other = (DidChangeTextDocumentParamsImpl) obj;
    if (this.textDocument == null) {
      if (other.textDocument != null)
        return false;
    } else if (!this.textDocument.equals(other.textDocument))
      return false;
    if (this.uri == null) {
      if (other.uri != null)
        return false;
    } else if (!this.uri.equals(other.uri))
      return false;
    if (this.contentChanges == null) {
      if (other.contentChanges != null)
        return false;
    } else if (!this.contentChanges.equals(other.contentChanges))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.textDocument== null) ? 0 : this.textDocument.hashCode());
    result = prime * result + ((this.uri== null) ? 0 : this.uri.hashCode());
    result = prime * result + ((this.contentChanges== null) ? 0 : this.contentChanges.hashCode());
    return result;
  }
}
