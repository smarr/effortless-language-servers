/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.ClientCapabilitiesImpl;
import io.typefox.lsapi.InitializeParams;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The initialize request is sent as the first request from the client to the server.
 */
@SuppressWarnings("all")
public class InitializeParamsImpl implements InitializeParams {
  /**
   * The process Id of the parent process that started the server.
   */
  private Integer processId;
  
  @Pure
  @Override
  public Integer getProcessId() {
    return this.processId;
  }
  
  public void setProcessId(final Integer processId) {
    this.processId = processId;
  }
  
  /**
   * The rootPath of the workspace. Is null if no folder is open.
   */
  private String rootPath;
  
  @Pure
  @Override
  public String getRootPath() {
    return this.rootPath;
  }
  
  public void setRootPath(final String rootPath) {
    this.rootPath = rootPath;
  }
  
  /**
   * The capabilities provided by the client (editor)
   */
  private ClientCapabilitiesImpl capabilities;
  
  @Pure
  @Override
  public ClientCapabilitiesImpl getCapabilities() {
    return this.capabilities;
  }
  
  public void setCapabilities(final ClientCapabilitiesImpl capabilities) {
    this.capabilities = capabilities;
  }
  
  /**
   * An optional extension to the protocol.
   * To tell the server what client (editor) is talking to it.
   */
  private String clientName;
  
  @Pure
  @Override
  public String getClientName() {
    return this.clientName;
  }
  
  public void setClientName(final String clientName) {
    this.clientName = clientName;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("processId", this.processId);
    b.add("rootPath", this.rootPath);
    b.add("capabilities", this.capabilities);
    b.add("clientName", this.clientName);
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
    InitializeParamsImpl other = (InitializeParamsImpl) obj;
    if (this.processId == null) {
      if (other.processId != null)
        return false;
    } else if (!this.processId.equals(other.processId))
      return false;
    if (this.rootPath == null) {
      if (other.rootPath != null)
        return false;
    } else if (!this.rootPath.equals(other.rootPath))
      return false;
    if (this.capabilities == null) {
      if (other.capabilities != null)
        return false;
    } else if (!this.capabilities.equals(other.capabilities))
      return false;
    if (this.clientName == null) {
      if (other.clientName != null)
        return false;
    } else if (!this.clientName.equals(other.clientName))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.processId== null) ? 0 : this.processId.hashCode());
    result = prime * result + ((this.rootPath== null) ? 0 : this.rootPath.hashCode());
    result = prime * result + ((this.capabilities== null) ? 0 : this.capabilities.hashCode());
    result = prime * result + ((this.clientName== null) ? 0 : this.clientName.hashCode());
    return result;
  }
}
