/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.TextDocumentItem;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * An item to transfer a text document from the client to the server.
 */
@SuppressWarnings("all")
public class TextDocumentItemImpl implements TextDocumentItem {
  /**
   * The text document's uri.
   */
  private String uri;
  
  @Pure
  @Override
  public String getUri() {
    return this.uri;
  }
  
  public void setUri(final String uri) {
    this.uri = uri;
  }
  
  /**
   * The text document's language identifier
   */
  private String languageId;
  
  @Pure
  @Override
  public String getLanguageId() {
    return this.languageId;
  }
  
  public void setLanguageId(final String languageId) {
    this.languageId = languageId;
  }
  
  /**
   * The version number of this document (it will strictly increase after each change, including undo/redo).
   */
  private int version;
  
  @Pure
  @Override
  public int getVersion() {
    return this.version;
  }
  
  public void setVersion(final int version) {
    this.version = version;
  }
  
  /**
   * The content of the opened  text document.
   */
  private String text;
  
  @Pure
  @Override
  public String getText() {
    return this.text;
  }
  
  public void setText(final String text) {
    this.text = text;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("uri", this.uri);
    b.add("languageId", this.languageId);
    b.add("version", this.version);
    b.add("text", this.text);
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
    TextDocumentItemImpl other = (TextDocumentItemImpl) obj;
    if (this.uri == null) {
      if (other.uri != null)
        return false;
    } else if (!this.uri.equals(other.uri))
      return false;
    if (this.languageId == null) {
      if (other.languageId != null)
        return false;
    } else if (!this.languageId.equals(other.languageId))
      return false;
    if (other.version != this.version)
      return false;
    if (this.text == null) {
      if (other.text != null)
        return false;
    } else if (!this.text.equals(other.text))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.uri== null) ? 0 : this.uri.hashCode());
    result = prime * result + ((this.languageId== null) ? 0 : this.languageId.hashCode());
    result = prime * result + this.version;
    result = prime * result + ((this.text== null) ? 0 : this.text.hashCode());
    return result;
  }
}
