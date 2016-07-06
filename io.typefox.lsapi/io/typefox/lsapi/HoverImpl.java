/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.Hover;
import io.typefox.lsapi.MarkedStringImpl;
import io.typefox.lsapi.RangeImpl;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The result of a hover request.
 */
@SuppressWarnings("all")
public class HoverImpl implements Hover {
  /**
   * The hover's content
   */
  private List<MarkedStringImpl> contents;
  
  @Pure
  @Override
  public List<MarkedStringImpl> getContents() {
    return this.contents;
  }
  
  public void setContents(final List<MarkedStringImpl> contents) {
    this.contents = contents;
  }
  
  /**
   * An optional range
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
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("contents", this.contents);
    b.add("range", this.range);
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
    HoverImpl other = (HoverImpl) obj;
    if (this.contents == null) {
      if (other.contents != null)
        return false;
    } else if (!this.contents.equals(other.contents))
      return false;
    if (this.range == null) {
      if (other.range != null)
        return false;
    } else if (!this.range.equals(other.range))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.contents== null) ? 0 : this.contents.hashCode());
    result = prime * result + ((this.range== null) ? 0 : this.range.hashCode());
    return result;
  }
}
