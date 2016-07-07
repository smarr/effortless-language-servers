/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.TextEdit;

/**
 * The Completion request is sent from the client to the server to compute completion items at a given cursor position.
 * Completion items are presented in the IntelliSense user interface. If computing complete completion items is expensive
 * servers can additional provide a handler for the resolve completion item request. This request is send when a
 * completion item is selected in the user interface.
 */
@SuppressWarnings("all")
public interface CompletionItem {
  public final static int KIND_TEXT = 1;
  
  public final static int KIND_METHOD = 2;
  
  public final static int KIND_FUNCTION = 3;
  
  public final static int KIND_CONSTRUCTOR = 4;
  
  public final static int KIND_FIELD = 5;
  
  public final static int KIND_VARIABLE = 6;
  
  public final static int KIND_CLASS = 7;
  
  public final static int KIND_INTERFACE = 8;
  
  public final static int KIND_MODULE = 9;
  
  public final static int KIND_PROPERTY = 10;
  
  public final static int KIND_UNIT = 11;
  
  public final static int KIND_VALUE = 12;
  
  public final static int KIND_ENUM = 13;
  
  public final static int KIND_KEYWORD = 14;
  
  public final static int KIND_SNIPPET = 15;
  
  public final static int KIND_COLOR = 16;
  
  public final static int KIND_FILE = 17;
  
  public final static int KIND_REFERENCE = 18;
  
  /**
   * The label of this completion item. By default also the text that is inserted when selecting this completion.
   */
  public abstract String getLabel();
  
  /**
   * The kind of this completion item. Based of the kind an icon is chosen by the editor.
   */
  public abstract Integer getKind();
  
  /**
   * A human-readable string with additional information about this item, like type or symbol information.
   */
  public abstract String getDetail();
  
  /**
   * A human-readable string that represents a doc-comment.
   */
  public abstract String getDocumentation();
  
  /**
   * A string that shoud be used when comparing this item with other items. When `falsy` the label is used.
   */
  public abstract String getSortText();
  
  /**
   * A string that should be used when filtering a set of completion items. When `falsy` the label is used.
   */
  public abstract String getFilterText();
  
  /**
   * A string that should be inserted a document when selecting this completion. When `falsy` the label is used.
   */
  public abstract String getInsertText();
  
  /**
   * An edit which is applied to a document when selecting this completion. When an edit is provided the value of
   * insertText is ignored.
   */
  public abstract TextEdit getTextEdit();
  
  /**
   * An data entry field that is preserved on a completion item between a completion and a completion resolve request.
   */
  public abstract Object getData();
}
