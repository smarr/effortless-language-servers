/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.Range;

/**
 * Represents a diagnostic, such as a compiler error or warning. Diagnostic objects are only valid in the scope of a resource.
 */
@SuppressWarnings("all")
public interface Diagnostic {
  /**
   * Reports an error.
   */
  public final static int SEVERITY_ERROR = 1;
  
  /**
   * Reports a warning.
   */
  public final static int SEVERITY_WARNING = 2;
  
  /**
   * Reports an information.
   */
  public final static int SEVERITY_INFO = 3;
  
  /**
   * Reports a hint.
   */
  public final static int SEVERITY_HINT = 5;
  
  /**
   * The range at which the message applies
   */
  public abstract Range getRange();
  
  /**
   * The diagnostic's severity. Can be omitted. If omitted it is up to the client to interpret diagnostics as error,
   * warning, info or hint.
   */
  public abstract Integer getSeverity();
  
  /**
   * The diagnostic's code. Can be omitted.
   */
  public abstract String getCode();
  
  /**
   * A human-readable string describing the source of this diagnostic, e.g. 'typescript' or 'super lint'.
   */
  public abstract String getSource();
  
  /**
   * The diagnostic's message.
   */
  public abstract String getMessage();
}
