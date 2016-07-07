/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.TextDocumentIdentifierImpl;
import io.typefox.lsapi.VersionedTextDocumentIdentifier;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * An identifier to denote a specific version of a text document.
 */
@SuppressWarnings("all")
public class VersionedTextDocumentIdentifierImpl extends TextDocumentIdentifierImpl implements VersionedTextDocumentIdentifier {
  /**
   * The version number of this document.
   */
  private int version;
  
  @Pure
  @Override
  public int getVersion() {
    return this.version;
  }
  
  public void setVersion(final int version) {
    this.version = version;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("version", this.version);
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
    if (!super.equals(obj))
      return false;
    VersionedTextDocumentIdentifierImpl other = (VersionedTextDocumentIdentifierImpl) obj;
    if (other.version != this.version)
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + this.version;
    return result;
  }
}
