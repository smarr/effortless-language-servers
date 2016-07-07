/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.MessageImpl;
import io.typefox.lsapi.NotificationMessage;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * A notification message. A processed notification message must not send a response back. They work like events.
 */
@SuppressWarnings("all")
public class NotificationMessageImpl extends MessageImpl implements NotificationMessage {
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
   * The notification's params.
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
    NotificationMessageImpl other = (NotificationMessageImpl) obj;
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
    result = prime * result + ((this.method== null) ? 0 : this.method.hashCode());
    result = prime * result + ((this.params== null) ? 0 : this.params.hashCode());
    return result;
  }
}
