/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import java.util.Map;

/**
 * Value-object describing what options formatting should use.
 */
@SuppressWarnings("all")
public interface FormattingOptions {
  /**
   * Size of a tab in spaces.
   */
  public abstract int getTabSize();
  
  /**
   * Prefer spaces over tabs.
   */
  public abstract boolean isInsertSpaces();
  
  /**
   * Signature for further properties.
   */
  public abstract Map<String, String> getProperties();
}
