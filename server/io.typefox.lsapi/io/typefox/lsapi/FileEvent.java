/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

/**
 * An event describing a file change.
 */
@SuppressWarnings("all")
public interface FileEvent {
  /**
   * The file got created.
   */
  public final static int TYPE_CREATED = 1;
  
  /**
   * The file got changed.
   */
  public final static int TYPE_CHANGED = 2;
  
  /**
   * The file got deleted.
   */
  public final static int TYPE_DELETED = 3;
  
  /**
   * The file's uri.
   */
  public abstract String getUri();
  
  /**
   * The change type.
   */
  public abstract int getType();
}
