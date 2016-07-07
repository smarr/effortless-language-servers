/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.LanguageDescription;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class LanguageDescriptionImpl implements LanguageDescription {
  /**
   * The language id.
   */
  private String languageId;
  
  @Pure
  @Override
  public String getLanguageId() {
    return this.languageId;
  }
  
  public void setLanguageId(final String languageId) {
    this.languageId = languageId;
  }
  
  /**
   * The optional content types this language is associated with.
   */
  private List<String> mimeTypes;
  
  @Pure
  @Override
  public List<String> getMimeTypes() {
    return this.mimeTypes;
  }
  
  public void setMimeTypes(final List<String> mimeTypes) {
    this.mimeTypes = mimeTypes;
  }
  
  /**
   * The fileExtension this language is associated with. At least one extension must be provided.
   */
  private List<String> fileExtensions;
  
  @Pure
  @Override
  public List<String> getFileExtensions() {
    return this.fileExtensions;
  }
  
  public void setFileExtensions(final List<String> fileExtensions) {
    this.fileExtensions = fileExtensions;
  }
  
  /**
   * The optional highlighting configuration to support client side syntax highlighting.
   * The format is client (editor) dependent.
   */
  private String highlightingConfiguration;
  
  @Pure
  @Override
  public String getHighlightingConfiguration() {
    return this.highlightingConfiguration;
  }
  
  public void setHighlightingConfiguration(final String highlightingConfiguration) {
    this.highlightingConfiguration = highlightingConfiguration;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("languageId", this.languageId);
    b.add("mimeTypes", this.mimeTypes);
    b.add("fileExtensions", this.fileExtensions);
    b.add("highlightingConfiguration", this.highlightingConfiguration);
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
    LanguageDescriptionImpl other = (LanguageDescriptionImpl) obj;
    if (this.languageId == null) {
      if (other.languageId != null)
        return false;
    } else if (!this.languageId.equals(other.languageId))
      return false;
    if (this.mimeTypes == null) {
      if (other.mimeTypes != null)
        return false;
    } else if (!this.mimeTypes.equals(other.mimeTypes))
      return false;
    if (this.fileExtensions == null) {
      if (other.fileExtensions != null)
        return false;
    } else if (!this.fileExtensions.equals(other.fileExtensions))
      return false;
    if (this.highlightingConfiguration == null) {
      if (other.highlightingConfiguration != null)
        return false;
    } else if (!this.highlightingConfiguration.equals(other.highlightingConfiguration))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.languageId== null) ? 0 : this.languageId.hashCode());
    result = prime * result + ((this.mimeTypes== null) ? 0 : this.mimeTypes.hashCode());
    result = prime * result + ((this.fileExtensions== null) ? 0 : this.fileExtensions.hashCode());
    result = prime * result + ((this.highlightingConfiguration== null) ? 0 : this.highlightingConfiguration.hashCode());
    return result;
  }
}
