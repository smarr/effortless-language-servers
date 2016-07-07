/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.SignatureHelp;
import io.typefox.lsapi.SignatureInformationImpl;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Signature help represents the signature of something callable. There can be multiple signature but only one
 * active and only one active parameter.
 */
@SuppressWarnings("all")
public class SignatureHelpImpl implements SignatureHelp {
  /**
   * One or more signatures.
   */
  private List<SignatureInformationImpl> signatures;
  
  @Pure
  @Override
  public List<SignatureInformationImpl> getSignatures() {
    return this.signatures;
  }
  
  public void setSignatures(final List<SignatureInformationImpl> signatures) {
    this.signatures = signatures;
  }
  
  /**
   * The active signature.
   */
  private Integer activeSignature;
  
  @Pure
  @Override
  public Integer getActiveSignature() {
    return this.activeSignature;
  }
  
  public void setActiveSignature(final Integer activeSignature) {
    this.activeSignature = activeSignature;
  }
  
  /**
   * The active parameter of the active signature.
   */
  private Integer activeParameter;
  
  @Pure
  @Override
  public Integer getActiveParameter() {
    return this.activeParameter;
  }
  
  public void setActiveParameter(final Integer activeParameter) {
    this.activeParameter = activeParameter;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("signatures", this.signatures);
    b.add("activeSignature", this.activeSignature);
    b.add("activeParameter", this.activeParameter);
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
    SignatureHelpImpl other = (SignatureHelpImpl) obj;
    if (this.signatures == null) {
      if (other.signatures != null)
        return false;
    } else if (!this.signatures.equals(other.signatures))
      return false;
    if (this.activeSignature == null) {
      if (other.activeSignature != null)
        return false;
    } else if (!this.activeSignature.equals(other.activeSignature))
      return false;
    if (this.activeParameter == null) {
      if (other.activeParameter != null)
        return false;
    } else if (!this.activeParameter.equals(other.activeParameter))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.signatures== null) ? 0 : this.signatures.hashCode());
    result = prime * result + ((this.activeSignature== null) ? 0 : this.activeSignature.hashCode());
    result = prime * result + ((this.activeParameter== null) ? 0 : this.activeParameter.hashCode());
    return result;
  }
}
