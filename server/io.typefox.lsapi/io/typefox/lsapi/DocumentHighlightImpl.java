/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.DocumentHighlight;
import io.typefox.lsapi.RangeImpl;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * A document highlight is a range inside a text document which deserves special attention. Usually a document highlight
 * is visualized by changing the background color of its range.
 */
@SuppressWarnings("all")
public class DocumentHighlightImpl implements DocumentHighlight {
  /**
   * The range this highlight applies to.
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
   * The highlight kind, default is KIND_TEXT.
   */
  private Integer kind;
  
  @Pure
  @Override
  public Integer getKind() {
    return this.kind;
  }
  
  public void setKind(final Integer kind) {
    this.kind = kind;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("range", this.range);
    b.add("kind", this.kind);
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
    DocumentHighlightImpl other = (DocumentHighlightImpl) obj;
    if (this.range == null) {
      if (other.range != null)
        return false;
    } else if (!this.range.equals(other.range))
      return false;
    if (this.kind == null) {
      if (other.kind != null)
        return false;
    } else if (!this.kind.equals(other.kind))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.range== null) ? 0 : this.range.hashCode());
    result = prime * result + ((this.kind== null) ? 0 : this.kind.hashCode());
    return result;
  }
}
