/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.Location;

/**
 * Represents information about programming constructs like variables, classes, interfaces etc.
 */
@SuppressWarnings("all")
public interface SymbolInformation {
  public final static int KIND_FILE = 1;
  
  public final static int KIND_MODULE = 2;
  
  public final static int KIND_NAMESPACE = 3;
  
  public final static int KIND_PACKAGE = 4;
  
  public final static int KIND_CLASS = 5;
  
  public final static int KIND_METHOD = 6;
  
  public final static int KIND_PROPERTY = 7;
  
  public final static int KIND_FIELD = 8;
  
  public final static int KIND_CONSTRUCTOR = 9;
  
  public final static int KIND_ENUM = 10;
  
  public final static int KIND_INTERFACE = 11;
  
  public final static int KIND_FUNCTION = 12;
  
  public final static int KIND_VARIABLE = 13;
  
  public final static int KIND_CONSTANT = 14;
  
  public final static int KIND_STRING = 15;
  
  public final static int KIND_NUMBER = 16;
  
  public final static int KIND_BOOLEAN = 17;
  
  public final static int KIND_ARRAY = 18;
  
  /**
   * The name of this symbol.
   */
  public abstract String getName();
  
  /**
   * The kind of this symbol.
   */
  public abstract int getKind();
  
  /**
   * The location of this symbol.
   */
  public abstract Location getLocation();
  
  /**
   * The name of the symbol containing this symbol.
   */
  public abstract String getContainer();
}
