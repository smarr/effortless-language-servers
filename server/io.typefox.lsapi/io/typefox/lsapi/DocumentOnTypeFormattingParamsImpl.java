/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.DocumentFormattingParamsImpl;
import io.typefox.lsapi.DocumentOnTypeFormattingParams;
import io.typefox.lsapi.PositionImpl;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The document on type formatting request is sent from the client to the server to format parts of the document during typing.
 */
@SuppressWarnings("all")
public class DocumentOnTypeFormattingParamsImpl extends DocumentFormattingParamsImpl implements DocumentOnTypeFormattingParams {
  /**
   * The position at which this request was send.
   */
  private PositionImpl position;
  
  @Pure
  @Override
  public PositionImpl getPosition() {
    return this.position;
  }
  
  public void setPosition(final PositionImpl position) {
    this.position = position;
  }
  
  /**
   * The character that has been typed.
   */
  private String ch;
  
  @Pure
  @Override
  public String getCh() {
    return this.ch;
  }
  
  public void setCh(final String ch) {
    this.ch = ch;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("position", this.position);
    b.add("ch", this.ch);
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
    if (!super.equals(obj))
      return false;
    DocumentOnTypeFormattingParamsImpl other = (DocumentOnTypeFormattingParamsImpl) obj;
    if (this.position == null) {
      if (other.position != null)
        return false;
    } else if (!this.position.equals(other.position))
      return false;
    if (this.ch == null) {
      if (other.ch != null)
        return false;
    } else if (!this.ch.equals(other.ch))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((this.position== null) ? 0 : this.position.hashCode());
    result = prime * result + ((this.ch== null) ? 0 : this.ch.hashCode());
    return result;
  }
}
