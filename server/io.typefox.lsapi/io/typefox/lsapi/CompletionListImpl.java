/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.CompletionItemImpl;
import io.typefox.lsapi.CompletionList;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Represents a collection of completion items to be presented in the editor.
 */
@SuppressWarnings("all")
public class CompletionListImpl implements CompletionList {
  /**
   * This list it not complete. Further typing should result in recomputing this list.
   */
  private boolean incomplete;
  
  @Pure
  @Override
  public boolean isIncomplete() {
    return this.incomplete;
  }
  
  public void setIncomplete(final boolean incomplete) {
    this.incomplete = incomplete;
  }
  
  /**
   * The completion items.
   */
  private List<CompletionItemImpl> items;
  
  @Pure
  @Override
  public List<CompletionItemImpl> getItems() {
    return this.items;
  }
  
  public void setItems(final List<CompletionItemImpl> items) {
    this.items = items;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("incomplete", this.incomplete);
    b.add("items", this.items);
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
    CompletionListImpl other = (CompletionListImpl) obj;
    if (other.incomplete != this.incomplete)
      return false;
    if (this.items == null) {
      if (other.items != null)
        return false;
    } else if (!this.items.equals(other.items))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.incomplete ? 1231 : 1237);
    result = prime * result + ((this.items== null) ? 0 : this.items.hashCode());
    return result;
  }
}
