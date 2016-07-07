/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.CompletionItem;
import java.util.List;

/**
 * Represents a collection of completion items to be presented in the editor.
 */
@SuppressWarnings("all")
public interface CompletionList {
  /**
   * This list it not complete. Further typing should result in recomputing this list.
   */
  public abstract boolean isIncomplete();
  
  /**
   * The completion items.
   */
  public abstract List<? extends CompletionItem> getItems();
}
