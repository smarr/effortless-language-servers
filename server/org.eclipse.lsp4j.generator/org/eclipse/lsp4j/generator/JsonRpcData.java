/**
 * Copyright (c) 2016 TypeFox and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.lsp4j.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.eclipse.lsp4j.generator.JsonRpcDataProcessor;
import org.eclipse.xtend.lib.macro.Active;

/**
 * Generates getters and setters for all fields as well as {@code equals} and {@code hashCode} implementations.
 * All JSON-RPC protocol classes that are written in Xtend should be annotated with this.
 */
@Target(ElementType.TYPE)
@Active(JsonRpcDataProcessor.class)
@Retention(RetentionPolicy.SOURCE)
@SuppressWarnings("all")
public @interface JsonRpcData {
}
