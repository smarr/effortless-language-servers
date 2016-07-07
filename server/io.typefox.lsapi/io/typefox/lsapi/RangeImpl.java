/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.PositionImpl;
import io.typefox.lsapi.Range;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * A range in a text document expressed as (zero-based) start and end positions.
 */
@SuppressWarnings("all")
public class RangeImpl implements Range {
  /**
   * The range's start position
   */
  private PositionImpl start;
  
  @Pure
  @Override
  public PositionImpl getStart() {
    return this.start;
  }
  
  public void setStart(final PositionImpl start) {
    this.start = start;
  }
  
  /**
   * The range's end position
   */
  private PositionImpl end;
  
  @Pure
  @Override
  public PositionImpl getEnd() {
    return this.end;
  }
  
  public void setEnd(final PositionImpl end) {
    this.end = end;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("start", this.start);
    b.add("end", this.end);
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
    RangeImpl other = (RangeImpl) obj;
    if (this.start == null) {
      if (other.start != null)
        return false;
    } else if (!this.start.equals(other.start))
      return false;
    if (this.end == null) {
      if (other.end != null)
        return false;
    } else if (!this.end.equals(other.end))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.start== null) ? 0 : this.start.hashCode());
    result = prime * result + ((this.end== null) ? 0 : this.end.hashCode());
    return result;
  }
}
