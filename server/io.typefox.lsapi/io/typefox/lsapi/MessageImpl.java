/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.Message;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * A general message as defined by JSON-RPC. The language server protocol always uses "2.0" as the jsonrpc version.
 */
@SuppressWarnings("all")
public class MessageImpl implements Message {
  private String jsonrpc;
  
  @Pure
  @Override
  public String getJsonrpc() {
    return this.jsonrpc;
  }
  
  public void setJsonrpc(final String jsonrpc) {
    this.jsonrpc = jsonrpc;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("jsonrpc", this.jsonrpc);
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
    MessageImpl other = (MessageImpl) obj;
    if (this.jsonrpc == null) {
      if (other.jsonrpc != null)
        return false;
    } else if (!this.jsonrpc.equals(other.jsonrpc))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.jsonrpc== null) ? 0 : this.jsonrpc.hashCode());
    return result;
  }
}
