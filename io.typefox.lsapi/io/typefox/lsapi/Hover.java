/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.MarkedString;
import io.typefox.lsapi.Range;
import java.util.List;

/**
 * The result of a hover request.
 */
@SuppressWarnings("all")
public interface Hover {
  /**
   * The hover's content
   */
  public abstract List<? extends MarkedString> getContents();
  
  /**
   * An optional range
   */
  public abstract Range getRange();
}
