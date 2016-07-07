/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.DocumentFormattingParams;
import io.typefox.lsapi.FormattingOptionsImpl;
import io.typefox.lsapi.TextDocumentIdentifierImpl;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The document formatting resquest is sent from the server to the client to format a whole document.
 */
@SuppressWarnings("all")
public class DocumentFormattingParamsImpl implements DocumentFormattingParams {
  /**
   * The document to format.
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
  
  /**
   * The format options
   */
  private FormattingOptionsImpl options;
  
  @Pure
  @Override
  public FormattingOptionsImpl getOptions() {
    return this.options;
  }
  
  public void setOptions(final FormattingOptionsImpl options) {
    this.options = options;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("textDocument", this.textDocument);
    b.add("options", this.options);
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
    DocumentFormattingParamsImpl other = (DocumentFormattingParamsImpl) obj;
    if (this.textDocument == null) {
      if (other.textDocument != null)
        return false;
    } else if (!this.textDocument.equals(other.textDocument))
      return false;
    if (this.options == null) {
      if (other.options != null)
        return false;
    } else if (!this.options.equals(other.options))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.textDocument== null) ? 0 : this.textDocument.hashCode());
    result = prime * result + ((this.options== null) ? 0 : this.options.hashCode());
    return result;
  }
}
