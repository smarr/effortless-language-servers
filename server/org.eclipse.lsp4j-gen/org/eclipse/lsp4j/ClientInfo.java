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

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Information about the client
 * <p>
 * Since 3.15.0
 */
@SuppressWarnings("all")
public class ClientInfo {
  /**
   * The name of the client as defined by the client.
   */
  @NonNull
  private String name;
  
  /**
   * The client's version as defined by the client.
   */
  private String version;
  
  public ClientInfo() {
  }
  
  public ClientInfo(@NonNull final String name) {
    this.name = Preconditions.<String>checkNotNull(name, "name");
  }
  
  public ClientInfo(@NonNull final String name, final String version) {
    this(name);
    this.version = version;
  }
  
  /**
   * The name of the client as defined by the client.
   */
  @Pure
  @NonNull
  public String getName() {
    return this.name;
  }
  
  /**
   * The name of the client as defined by the client.
   */
  public void setName(@NonNull final String name) {
    this.name = Preconditions.checkNotNull(name, "name");
  }
  
  /**
   * The client's version as defined by the client.
   */
  @Pure
  public String getVersion() {
    return this.version;
  }
  
  /**
   * The client's version as defined by the client.
   */
  public void setVersion(final String version) {
    this.version = version;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
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
    ClientInfo other = (ClientInfo) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.version == null) {
      if (other.version != null)
        return false;
    } else if (!this.version.equals(other.version))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    return prime * result + ((this.version== null) ? 0 : this.version.hashCode());
  }
}
