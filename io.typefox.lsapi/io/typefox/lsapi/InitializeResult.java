/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.LanguageDescription;
import io.typefox.lsapi.ServerCapabilities;
import java.util.List;

@SuppressWarnings("all")
public interface InitializeResult {
  /**
   * The capabilities the language server provides.
   */
  public abstract ServerCapabilities getCapabilities();
  
  /**
   * An optional extension to the protocol,
   * that allows to provide information about the supported languages.
   */
  public abstract List<? extends LanguageDescription> getSupportedLanguages();
}
