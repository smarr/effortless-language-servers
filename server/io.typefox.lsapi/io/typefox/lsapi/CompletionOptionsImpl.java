/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.CompletionOptions;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Completion options.
 */
@SuppressWarnings("all")
public class CompletionOptionsImpl implements CompletionOptions {
  /**
   * The server provides support to resolve additional information for a completion item.
   */
  private boolean resolveProvider;
  
  @Pure
  @Override
  public boolean getResolveProvider() {
    return this.resolveProvider;
  }
  
  public void setResolveProvider(final boolean resolveProvider) {
    this.resolveProvider = resolveProvider;
  }
  
  /**
   * The characters that trigger completion automatically.
   */
  private List<String> triggerCharacters;
  
  @Pure
  @Override
  public List<String> getTriggerCharacters() {
    return this.triggerCharacters;
  }
  
  public void setTriggerCharacters(final List<String> triggerCharacters) {
    this.triggerCharacters = triggerCharacters;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("resolveProvider", this.resolveProvider);
    b.add("triggerCharacters", this.triggerCharacters);
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
    CompletionOptionsImpl other = (CompletionOptionsImpl) obj;
    if (other.resolveProvider != this.resolveProvider)
      return false;
    if (this.triggerCharacters == null) {
      if (other.triggerCharacters != null)
        return false;
    } else if (!this.triggerCharacters.equals(other.triggerCharacters))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.resolveProvider ? 1231 : 1237);
    result = prime * result + ((this.triggerCharacters== null) ? 0 : this.triggerCharacters.hashCode());
    return result;
  }
}
