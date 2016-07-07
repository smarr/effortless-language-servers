/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.Position;

/**
 * A range in a text document expressed as (zero-based) start and end positions.
 */
@SuppressWarnings("all")
public interface Range {
  /**
   * The range's start position
   */
  public abstract Position getStart();
  
  /**
   * The range's end position
   */
  public abstract Position getEnd();
}
