/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.MessageImpl;
import io.typefox.lsapi.ResponseErrorImpl;
import io.typefox.lsapi.ResponseMessage;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Response Message send as a result of a request.
 */
@SuppressWarnings("all")
public class ResponseMessageImpl extends MessageImpl implements ResponseMessage {
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
   * The result of a request. This can be omitted in the case of an error.
   */
  private Object result;
  
  @Pure
  @Override
  public Object getResult() {
    return this.result;
  }
  
  public void setResult(final Object result) {
    this.result = result;
  }
  
  /**
   * The error object in case a request fails.
   */
  private ResponseErrorImpl error;
  
  @Pure
  @Override
  public ResponseErrorImpl getError() {
    return this.error;
  }
  
  public void setError(final ResponseErrorImpl error) {
    this.error = error;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("id", this.id);
    b.add("result", this.result);
    b.add("error", this.error);
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
    ResponseMessageImpl other = (ResponseMessageImpl) obj;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
      return false;
    if (this.result == null) {
      if (other.result != null)
        return false;
    } else if (!this.result.equals(other.result))
      return false;
    if (this.error == null) {
      if (other.error != null)
        return false;
    } else if (!this.error.equals(other.error))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.result== null) ? 0 : this.result.hashCode());
    result = prime * result + ((this.error== null) ? 0 : this.error.hashCode());
    return result;
  }
}
