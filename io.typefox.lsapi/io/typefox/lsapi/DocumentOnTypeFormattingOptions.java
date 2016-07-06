/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import java.util.List;

/**
 * Format document on type options
 */
@SuppressWarnings("all")
public interface DocumentOnTypeFormattingOptions {
  /**
   * A character on which formatting should be triggered, like `}`.
   */
  public abstract String getFirstTriggerCharacter();
  
  /**
   * More trigger characters.
   */
  public abstract List<String> getMoreTriggerCharacter();
}
