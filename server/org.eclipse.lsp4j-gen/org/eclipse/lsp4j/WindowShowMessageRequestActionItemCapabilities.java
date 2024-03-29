/**
 * Copyright (c) 2016-2018 TypeFox and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.lsp4j;

import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Capabilities specific to the {@link MessageActionItem} type of show message request.
 * <p>
 * Since 3.16.0
 */
@SuppressWarnings("all")
public class WindowShowMessageRequestActionItemCapabilities {
  /**
   * Whether the client supports additional attributes which
   * are preserved and sent back to the server in the
   * request's response.
   */
  private Boolean additionalPropertiesSupport;
  
  public WindowShowMessageRequestActionItemCapabilities() {
  }
  
  public WindowShowMessageRequestActionItemCapabilities(final Boolean additionalPropertiesSupport) {
    this.additionalPropertiesSupport = additionalPropertiesSupport;
  }
  
  /**
   * Whether the client supports additional attributes which
   * are preserved and sent back to the server in the
   * request's response.
   */
  @Pure
  public Boolean getAdditionalPropertiesSupport() {
    return this.additionalPropertiesSupport;
  }
  
  /**
   * Whether the client supports additional attributes which
   * are preserved and sent back to the server in the
   * request's response.
   */
  public void setAdditionalPropertiesSupport(final Boolean additionalPropertiesSupport) {
    this.additionalPropertiesSupport = additionalPropertiesSupport;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("additionalPropertiesSupport", this.additionalPropertiesSupport);
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
    WindowShowMessageRequestActionItemCapabilities other = (WindowShowMessageRequestActionItemCapabilities) obj;
    if (this.additionalPropertiesSupport == null) {
      if (other.additionalPropertiesSupport != null)
        return false;
    } else if (!this.additionalPropertiesSupport.equals(other.additionalPropertiesSupport))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.additionalPropertiesSupport== null) ? 0 : this.additionalPropertiesSupport.hashCode());
  }
}
