/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.ParameterInformationImpl;
import io.typefox.lsapi.SignatureInformation;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Represents the signature of something callable. A signature can have a label, like a function-name, a doc-comment, and
 * a set of parameters.
 */
@SuppressWarnings("all")
public class SignatureInformationImpl implements SignatureInformation {
  /**
   * The label of this signature. Will be shown in the UI.
   */
  private String label;
  
  @Pure
  @Override
  public String getLabel() {
    return this.label;
  }
  
  public void setLabel(final String label) {
    this.label = label;
  }
  
  /**
   * The human-readable doc-comment of this signature. Will be shown in the UI but can be omitted.
   */
  private String documentation;
  
  @Pure
  @Override
  public String getDocumentation() {
    return this.documentation;
  }
  
  public void setDocumentation(final String documentation) {
    this.documentation = documentation;
  }
  
  /**
   * The parameters of this signature.
   */
  private List<ParameterInformationImpl> parameters;
  
  @Pure
  @Override
  public List<ParameterInformationImpl> getParameters() {
    return this.parameters;
  }
  
  public void setParameters(final List<ParameterInformationImpl> parameters) {
    this.parameters = parameters;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("label", this.label);
    b.add("documentation", this.documentation);
    b.add("parameters", this.parameters);
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
    SignatureInformationImpl other = (SignatureInformationImpl) obj;
    if (this.label == null) {
      if (other.label != null)
        return false;
    } else if (!this.label.equals(other.label))
      return false;
    if (this.documentation == null) {
      if (other.documentation != null)
        return false;
    } else if (!this.documentation.equals(other.documentation))
      return false;
    if (this.parameters == null) {
      if (other.parameters != null)
        return false;
    } else if (!this.parameters.equals(other.parameters))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.label== null) ? 0 : this.label.hashCode());
    result = prime * result + ((this.documentation== null) ? 0 : this.documentation.hashCode());
    result = prime * result + ((this.parameters== null) ? 0 : this.parameters.hashCode());
    return result;
  }
}
