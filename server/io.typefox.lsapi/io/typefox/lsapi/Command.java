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
 * Represents a reference to a command. Provides a title which will be used to represent a command in the UI and,
 * optionally, an array of arguments which will be passed to the command handler function when invoked.
 */
@SuppressWarnings("all")
public interface Command {
  /**
   * Title of the command, like `save`.
   */
  public abstract String getTitle();
  
  /**
   * The identifier of the actual command handler.
   */
  public abstract String getCommand();
  
  /**
   * Arguments that the command handler should be invoked with.
   */
  public abstract List<Object> getArguments();
}
