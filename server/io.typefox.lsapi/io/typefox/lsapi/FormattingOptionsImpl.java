/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.FormattingOptions;
import java.util.Map;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Value-object describing what options formatting should use.
 */
@SuppressWarnings("all")
public class FormattingOptionsImpl implements FormattingOptions {
  /**
   * Size of a tab in spaces.
   */
  private int tabSize;
  
  @Pure
  @Override
  public int getTabSize() {
    return this.tabSize;
  }
  
  public void setTabSize(final int tabSize) {
    this.tabSize = tabSize;
  }
  
  /**
   * Prefer spaces over tabs.
   */
  private boolean insertSpaces;
  
  @Pure
  @Override
  public boolean isInsertSpaces() {
    return this.insertSpaces;
  }
  
  public void setInsertSpaces(final boolean insertSpaces) {
    this.insertSpaces = insertSpaces;
  }
  
  /**
   * Signature for further properties.
   */
  private Map<String, String> properties;
  
  @Pure
  @Override
  public Map<String, String> getProperties() {
    return this.properties;
  }
  
  public void setProperties(final Map<String, String> properties) {
    this.properties = properties;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("tabSize", this.tabSize);
    b.add("insertSpaces", this.insertSpaces);
    b.add("properties", this.properties);
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
    FormattingOptionsImpl other = (FormattingOptionsImpl) obj;
    if (other.tabSize != this.tabSize)
      return false;
    if (other.insertSpaces != this.insertSpaces)
      return false;
    if (this.properties == null) {
      if (other.properties != null)
        return false;
    } else if (!this.properties.equals(other.properties))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.tabSize;
    result = prime * result + (this.insertSpaces ? 1231 : 1237);
    result = prime * result + ((this.properties== null) ? 0 : this.properties.hashCode());
    return result;
  }
}
