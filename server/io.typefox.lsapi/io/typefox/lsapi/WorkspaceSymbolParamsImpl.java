/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.WorkspaceSymbolParams;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The parameters of a Workspace Symbol Request.
 */
@SuppressWarnings("all")
public class WorkspaceSymbolParamsImpl implements WorkspaceSymbolParams {
  /**
   * A non-empty query string
   */
  private String query;
  
  @Pure
  @Override
  public String getQuery() {
    return this.query;
  }
  
  public void setQuery(final String query) {
    this.query = query;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("query", this.query);
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
    WorkspaceSymbolParamsImpl other = (WorkspaceSymbolParamsImpl) obj;
    if (this.query == null) {
      if (other.query != null)
        return false;
    } else if (!this.query.equals(other.query))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.query== null) ? 0 : this.query.hashCode());
    return result;
  }
}
