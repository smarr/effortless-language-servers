/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.SignatureInformation;
import java.util.List;

/**
 * Signature help represents the signature of something callable. There can be multiple signature but only one
 * active and only one active parameter.
 */
@SuppressWarnings("all")
public interface SignatureHelp {
  /**
   * One or more signatures.
   */
  public abstract List<? extends SignatureInformation> getSignatures();
  
  /**
   * The active signature.
   */
  public abstract Integer getActiveSignature();
  
  /**
   * The active parameter of the active signature.
   */
  public abstract Integer getActiveParameter();
}
