/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.Position;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Position in a text document expressed as zero-based line and character offset.
 */
@SuppressWarnings("all")
public class PositionImpl implements Position {
  /**
   * Line position in a document (zero-based).
   */
  private int line;
  
  @Pure
  @Override
  public int getLine() {
    return this.line;
  }
  
  public void setLine(final int line) {
    this.line = line;
  }
  
  /**
   * Character offset on a line in a document (zero-based).
   */
  private int character;
  
  @Pure
  @Override
  public int getCharacter() {
    return this.character;
  }
  
  public void setCharacter(final int character) {
    this.character = character;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("line", this.line);
    b.add("character", this.character);
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
    PositionImpl other = (PositionImpl) obj;
    if (other.line != this.line)
      return false;
    if (other.character != this.character)
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.line;
    result = prime * result + this.character;
    return result;
  }
}
