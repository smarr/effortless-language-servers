/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.MessageActionItemImpl;
import io.typefox.lsapi.MessageParamsImpl;
import io.typefox.lsapi.ShowMessageRequestParams;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The show message request is sent from a server to a client to ask the client to display a particular message in the
 * user interface. In addition to the show message notification the request allows to pass actions and to wait for an
 * answer from the client.
 */
@SuppressWarnings("all")
public class ShowMessageRequestParamsImpl extends MessageParamsImpl implements ShowMessageRequestParams {
  /**
   * The message action items to present.
   */
  private List<MessageActionItemImpl> actions;
  
  @Pure
  @Override
  public List<MessageActionItemImpl> getActions() {
    return this.actions;
  }
  
  public void setActions(final List<MessageActionItemImpl> actions) {
    this.actions = actions;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("actions", this.actions);
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
    ShowMessageRequestParamsImpl other = (ShowMessageRequestParamsImpl) obj;
    if (this.actions == null) {
      if (other.actions != null)
        return false;
    } else if (!this.actions.equals(other.actions))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((this.actions== null) ? 0 : this.actions.hashCode());
    return result;
  }
}
