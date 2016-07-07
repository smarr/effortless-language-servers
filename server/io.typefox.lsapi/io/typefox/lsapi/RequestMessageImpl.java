/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.MessageImpl;
import io.typefox.lsapi.RequestMessage;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * A request message to decribe a request between the client and the server. Every processed request must send a response back
 * to the sender of the request.
 */
@SuppressWarnings("all")
public class RequestMessageImpl extends MessageImpl implements RequestMessage {
  /**
   * The request id.
   */
  private String id;
  
  @Pure
  @Override
  public String getId() {
    return this.id;
  }
  
  public void setId(final String id) {
    this.id = id;
  }
  
  /**
   * The method to be invoked.
   */
  private String method;
  
  @Pure
  @Override
  public String getMethod() {
    return this.method;
  }
  
  public void setMethod(final String method) {
    this.method = method;
  }
  
  /**
   * The method's params.
   */
  private Object params;
  
  @Pure
  @Override
  public Object getParams() {
    return this.params;
  }
  
  public void setParams(final Object params) {
    this.params = params;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("id", this.id);
    b.add("method", this.method);
    b.add("params", this.params);
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
    RequestMessageImpl other = (RequestMessageImpl) obj;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
      return false;
    if (this.method == null) {
      if (other.method != null)
        return false;
    } else if (!this.method.equals(other.method))
      return false;
    if (this.params == null) {
      if (other.params != null)
        return false;
    } else if (!this.params.equals(other.params))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.method== null) ? 0 : this.method.hashCode());
    result = prime * result + ((this.params== null) ? 0 : this.params.hashCode());
    return result;
  }
}
