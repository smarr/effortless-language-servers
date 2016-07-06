/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.MessageParams;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The show message notification is sent from a server to a client to ask the client to display a particular message
 * in the user interface.
 * 
 * The log message notification is send from the server to the client to ask the client to log a particular message.
 */
@SuppressWarnings("all")
public class MessageParamsImpl implements MessageParams {
  /**
   * The message type.
   */
  private int type;
  
  @Pure
  @Override
  public int getType() {
    return this.type;
  }
  
  public void setType(final int type) {
    this.type = type;
  }
  
  /**
   * The actual message.
   */
  private String message;
  
  @Pure
  @Override
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(final String message) {
    this.message = message;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("type", this.type);
    b.add("message", this.message);
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
    MessageParamsImpl other = (MessageParamsImpl) obj;
    if (other.type != this.type)
      return false;
    if (this.message == null) {
      if (other.message != null)
        return false;
    } else if (!this.message.equals(other.message))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.type;
    result = prime * result + ((this.message== null) ? 0 : this.message.hashCode());
    return result;
  }
}
