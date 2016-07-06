/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.DocumentOnTypeFormattingOptions;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Format document on type options
 */
@SuppressWarnings("all")
public class DocumentOnTypeFormattingOptionsImpl implements DocumentOnTypeFormattingOptions {
  /**
   * A character on which formatting should be triggered, like `}`.
   */
  private String firstTriggerCharacter;
  
  @Pure
  @Override
  public String getFirstTriggerCharacter() {
    return this.firstTriggerCharacter;
  }
  
  public void setFirstTriggerCharacter(final String firstTriggerCharacter) {
    this.firstTriggerCharacter = firstTriggerCharacter;
  }
  
  /**
   * More trigger characters.
   */
  private List<String> moreTriggerCharacter;
  
  @Pure
  @Override
  public List<String> getMoreTriggerCharacter() {
    return this.moreTriggerCharacter;
  }
  
  public void setMoreTriggerCharacter(final List<String> moreTriggerCharacter) {
    this.moreTriggerCharacter = moreTriggerCharacter;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("firstTriggerCharacter", this.firstTriggerCharacter);
    b.add("moreTriggerCharacter", this.moreTriggerCharacter);
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
    DocumentOnTypeFormattingOptionsImpl other = (DocumentOnTypeFormattingOptionsImpl) obj;
    if (this.firstTriggerCharacter == null) {
      if (other.firstTriggerCharacter != null)
        return false;
    } else if (!this.firstTriggerCharacter.equals(other.firstTriggerCharacter))
      return false;
    if (this.moreTriggerCharacter == null) {
      if (other.moreTriggerCharacter != null)
        return false;
    } else if (!this.moreTriggerCharacter.equals(other.moreTriggerCharacter))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.firstTriggerCharacter== null) ? 0 : this.firstTriggerCharacter.hashCode());
    result = prime * result + ((this.moreTriggerCharacter== null) ? 0 : this.moreTriggerCharacter.hashCode());
    return result;
  }
}
