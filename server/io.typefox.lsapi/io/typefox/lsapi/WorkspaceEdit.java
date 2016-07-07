/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.TextEdit;
import java.util.Map;

/**
 * A workspace edit represents changes to many resources managed in the workspace.
 */
@SuppressWarnings("all")
public interface WorkspaceEdit {
  /**
   * Holds changes to existing resources.
   */
  public abstract Map<String, ? extends TextEdit> getChanges();
}
