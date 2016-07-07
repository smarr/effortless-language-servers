/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import java.util.List;

@SuppressWarnings("all")
public interface LanguageDescription {
  /**
   * The language id.
   */
  public abstract String getLanguageId();
  
  /**
   * The optional content types this language is associated with.
   */
  public abstract List<String> getMimeTypes();
  
  /**
   * The fileExtension this language is associated with. At least one extension must be provided.
   */
  public abstract List<String> getFileExtensions();
  
  /**
   * The optional highlighting configuration to support client side syntax highlighting.
   * The format is client (editor) dependent.
   */
  public abstract String getHighlightingConfiguration();
}
