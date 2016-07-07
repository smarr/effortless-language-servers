/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.MarkedString;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class MarkedStringImpl implements MarkedString {
  private String language;
  
  @Pure
  @Override
  public String getLanguage() {
    return this.language;
  }
  
  public void setLanguage(final String language) {
    this.language = language;
  }
  
  private String value;
  
  @Pure
  @Override
  public String getValue() {
    return this.value;
  }
  
  public void setValue(final String value) {
    this.value = value;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("language", this.language);
    b.add("value", this.value);
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
    MarkedStringImpl other = (MarkedStringImpl) obj;
    if (this.language == null) {
      if (other.language != null)
        return false;
    } else if (!this.language.equals(other.language))
      return false;
    if (this.value == null) {
      if (other.value != null)
        return false;
    } else if (!this.value.equals(other.value))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.language== null) ? 0 : this.language.hashCode());
    result = prime * result + ((this.value== null) ? 0 : this.value.hashCode());
    return result;
  }
}
