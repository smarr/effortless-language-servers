/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.LanguageDescriptionImpl;
import io.typefox.lsapi.ServerCapabilitiesImpl;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class InitializeResultImpl implements InitializeResult {
  /**
   * The capabilities the language server provides.
   */
  private ServerCapabilitiesImpl capabilities;
  
  @Pure
  @Override
  public ServerCapabilitiesImpl getCapabilities() {
    return this.capabilities;
  }
  
  public void setCapabilities(final ServerCapabilitiesImpl capabilities) {
    this.capabilities = capabilities;
  }
  
  /**
   * An optional extension to the protocol,
   * that allows to provide information about the supported languages.
   */
  private List<LanguageDescriptionImpl> supportedLanguages;
  
  @Pure
  @Override
  public List<LanguageDescriptionImpl> getSupportedLanguages() {
    return this.supportedLanguages;
  }
  
  public void setSupportedLanguages(final List<LanguageDescriptionImpl> supportedLanguages) {
    this.supportedLanguages = supportedLanguages;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("capabilities", this.capabilities);
    b.add("supportedLanguages", this.supportedLanguages);
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
    InitializeResultImpl other = (InitializeResultImpl) obj;
    if (this.capabilities == null) {
      if (other.capabilities != null)
        return false;
    } else if (!this.capabilities.equals(other.capabilities))
      return false;
    if (this.supportedLanguages == null) {
      if (other.supportedLanguages != null)
        return false;
    } else if (!this.supportedLanguages.equals(other.supportedLanguages))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.capabilities== null) ? 0 : this.capabilities.hashCode());
    result = prime * result + ((this.supportedLanguages== null) ? 0 : this.supportedLanguages.hashCode());
    return result;
  }
}
