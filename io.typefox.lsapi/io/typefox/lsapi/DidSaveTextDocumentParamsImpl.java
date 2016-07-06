/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.DidSaveTextDocumentParams;
import io.typefox.lsapi.TextDocumentIdentifierImpl;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The document save notification is sent from the client to the server when the document for saved in the clinet.
 */
@SuppressWarnings("all")
public class DidSaveTextDocumentParamsImpl implements DidSaveTextDocumentParams {
  /**
   * The document that was closed.
   */
  private TextDocumentIdentifierImpl textDocument;
  
  @Pure
  @Override
  public TextDocumentIdentifierImpl getTextDocument() {
    return this.textDocument;
  }
  
  public void setTextDocument(final TextDocumentIdentifierImpl textDocument) {
    this.textDocument = textDocument;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("textDocument", this.textDocument);
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
    DidSaveTextDocumentParamsImpl other = (DidSaveTextDocumentParamsImpl) obj;
    if (this.textDocument == null) {
      if (other.textDocument != null)
        return false;
    } else if (!this.textDocument.equals(other.textDocument))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.textDocument== null) ? 0 : this.textDocument.hashCode());
    return result;
  }
}
