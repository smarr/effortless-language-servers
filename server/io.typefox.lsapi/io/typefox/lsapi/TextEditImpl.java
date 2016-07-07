/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.RangeImpl;
import io.typefox.lsapi.TextEdit;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * A textual edit applicable to a text document.
 */
@SuppressWarnings("all")
public class TextEditImpl implements TextEdit {
  /**
   * The range of the text document to be manipulated. To insert text into a document create a range where start === end.
   */
  private RangeImpl range;
  
  @Pure
  @Override
  public RangeImpl getRange() {
    return this.range;
  }
  
  public void setRange(final RangeImpl range) {
    this.range = range;
  }
  
  /**
   * The string to be inserted. For delete operations use an empty string.
   */
  private String newText;
  
  @Pure
  @Override
  public String getNewText() {
    return this.newText;
  }
  
  public void setNewText(final String newText) {
    this.newText = newText;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("range", this.range);
    b.add("newText", this.newText);
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
    TextEditImpl other = (TextEditImpl) obj;
    if (this.range == null) {
      if (other.range != null)
        return false;
    } else if (!this.range.equals(other.range))
      return false;
    if (this.newText == null) {
      if (other.newText != null)
        return false;
    } else if (!this.newText.equals(other.newText))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.range== null) ? 0 : this.range.hashCode());
    result = prime * result + ((this.newText== null) ? 0 : this.newText.hashCode());
    return result;
  }
}
