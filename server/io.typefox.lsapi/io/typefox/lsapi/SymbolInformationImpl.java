/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.LocationImpl;
import io.typefox.lsapi.SymbolInformation;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Represents information about programming constructs like variables, classes, interfaces etc.
 */
@SuppressWarnings("all")
public class SymbolInformationImpl implements SymbolInformation {
  /**
   * The name of this symbol.
   */
  private String name;
  
  @Pure
  @Override
  public String getName() {
    return this.name;
  }
  
  public void setName(final String name) {
    this.name = name;
  }
  
  /**
   * The kind of this symbol.
   */
  private int kind;
  
  @Pure
  @Override
  public int getKind() {
    return this.kind;
  }
  
  public void setKind(final int kind) {
    this.kind = kind;
  }
  
  /**
   * The location of this symbol.
   */
  private LocationImpl location;
  
  @Pure
  @Override
  public LocationImpl getLocation() {
    return this.location;
  }
  
  public void setLocation(final LocationImpl location) {
    this.location = location;
  }
  
  /**
   * The name of the symbol containing this symbol.
   */
  private String container;
  
  @Pure
  @Override
  public String getContainer() {
    return this.container;
  }
  
  public void setContainer(final String container) {
    this.container = container;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("kind", this.kind);
    b.add("location", this.location);
    b.add("container", this.container);
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
    SymbolInformationImpl other = (SymbolInformationImpl) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (other.kind != this.kind)
      return false;
    if (this.location == null) {
      if (other.location != null)
        return false;
    } else if (!this.location.equals(other.location))
      return false;
    if (this.container == null) {
      if (other.container != null)
        return false;
    } else if (!this.container.equals(other.container))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + this.kind;
    result = prime * result + ((this.location== null) ? 0 : this.location.hashCode());
    result = prime * result + ((this.container== null) ? 0 : this.container.hashCode());
    return result;
  }
}
