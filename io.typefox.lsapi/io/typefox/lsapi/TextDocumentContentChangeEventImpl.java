/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.RangeImpl;
import io.typefox.lsapi.TextDocumentContentChangeEvent;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * An event describing a change to a text document. If range and rangeLength are omitted the new text is considered
 * to be the full content of the document.
 */
@SuppressWarnings("all")
public class TextDocumentContentChangeEventImpl implements TextDocumentContentChangeEvent {
  /**
   * The range of the document that changed.
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
   * The length of the range that got replaced.
   */
  private Integer rangeLength;
  
  @Pure
  @Override
  public Integer getRangeLength() {
    return this.rangeLength;
  }
  
  public void setRangeLength(final Integer rangeLength) {
    this.rangeLength = rangeLength;
  }
  
  /**
   * The new text of the document.
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
    b.add("range", this.range);
    b.add("rangeLength", this.rangeLength);
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
    TextDocumentContentChangeEventImpl other = (TextDocumentContentChangeEventImpl) obj;
    if (this.range == null) {
      if (other.range != null)
        return false;
    } else if (!this.range.equals(other.range))
      return false;
    if (this.rangeLength == null) {
      if (other.rangeLength != null)
        return false;
    } else if (!this.rangeLength.equals(other.rangeLength))
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
    result = prime * result + ((this.range== null) ? 0 : this.range.hashCode());
    result = prime * result + ((this.rangeLength== null) ? 0 : this.rangeLength.hashCode());
    result = prime * result + ((this.text== null) ? 0 : this.text.hashCode());
    return result;
  }
}
