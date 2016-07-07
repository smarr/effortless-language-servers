/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.InitializeError;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class InitializeErrorImpl implements InitializeError {
  /**
   * Indicates whether the client should retry to send the initialize request after showing the message provided
   * in the ResponseError.
   */
  private boolean retry;
  
  @Pure
  @Override
  public boolean isRetry() {
    return this.retry;
  }
  
  public void setRetry(final boolean retry) {
    this.retry = retry;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("retry", this.retry);
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
    InitializeErrorImpl other = (InitializeErrorImpl) obj;
    if (other.retry != this.retry)
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.retry ? 1231 : 1237);
    return result;
  }
}
