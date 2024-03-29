/**
 * Copyright (c) 2016-2018 TypeFox and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.lsp4j;

import java.util.List;
import org.eclipse.lsp4j.AbstractTextDocumentRegistrationAndWorkDoneProgressOptions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CompletionRegistrationOptions extends AbstractTextDocumentRegistrationAndWorkDoneProgressOptions {
  /**
   * Most tools trigger completion request automatically without explicitly requesting
   * it using a keyboard shortcut (e.g. Ctrl+Space). Typically they do so when the user
   * starts to type an identifier. For example if the user types `c` in a JavaScript file
   * code complete will automatically pop up present `console` besides others as a
   * completion item. Characters that make up identifiers don't need to be listed here.
   * <p>
   * If code complete should automatically be trigger on characters not being valid inside
   * an identifier (for example `.` in JavaScript) list them in `triggerCharacters`.
   */
  private List<String> triggerCharacters;
  
  /**
   * The server provides support to resolve additional information for a completion item.
   */
  private Boolean resolveProvider;
  
  /**
   * The list of all possible characters that commit a completion. This field
   * can be used if clients don't support individual commit characters per
   * completion item. See client capability
   * {@link CompletionItemCapabilities#commitCharactersSupport}.
   * <p>
   * If a server provides both {@code allCommitCharacters} and commit characters on
   * an individual completion item the ones on the completion item win.
   * <p>
   * Since 3.2.0
   */
  private List<String> allCommitCharacters;
  
  public CompletionRegistrationOptions() {
  }
  
  public CompletionRegistrationOptions(final List<String> triggerCharacters, final Boolean resolveProvider) {
    this.triggerCharacters = triggerCharacters;
    this.resolveProvider = resolveProvider;
  }
  
  /**
   * Most tools trigger completion request automatically without explicitly requesting
   * it using a keyboard shortcut (e.g. Ctrl+Space). Typically they do so when the user
   * starts to type an identifier. For example if the user types `c` in a JavaScript file
   * code complete will automatically pop up present `console` besides others as a
   * completion item. Characters that make up identifiers don't need to be listed here.
   * <p>
   * If code complete should automatically be trigger on characters not being valid inside
   * an identifier (for example `.` in JavaScript) list them in `triggerCharacters`.
   */
  @Pure
  public List<String> getTriggerCharacters() {
    return this.triggerCharacters;
  }
  
  /**
   * Most tools trigger completion request automatically without explicitly requesting
   * it using a keyboard shortcut (e.g. Ctrl+Space). Typically they do so when the user
   * starts to type an identifier. For example if the user types `c` in a JavaScript file
   * code complete will automatically pop up present `console` besides others as a
   * completion item. Characters that make up identifiers don't need to be listed here.
   * <p>
   * If code complete should automatically be trigger on characters not being valid inside
   * an identifier (for example `.` in JavaScript) list them in `triggerCharacters`.
   */
  public void setTriggerCharacters(final List<String> triggerCharacters) {
    this.triggerCharacters = triggerCharacters;
  }
  
  /**
   * The server provides support to resolve additional information for a completion item.
   */
  @Pure
  public Boolean getResolveProvider() {
    return this.resolveProvider;
  }
  
  /**
   * The server provides support to resolve additional information for a completion item.
   */
  public void setResolveProvider(final Boolean resolveProvider) {
    this.resolveProvider = resolveProvider;
  }
  
  /**
   * The list of all possible characters that commit a completion. This field
   * can be used if clients don't support individual commit characters per
   * completion item. See client capability
   * {@link CompletionItemCapabilities#commitCharactersSupport}.
   * <p>
   * If a server provides both {@code allCommitCharacters} and commit characters on
   * an individual completion item the ones on the completion item win.
   * <p>
   * Since 3.2.0
   */
  @Pure
  public List<String> getAllCommitCharacters() {
    return this.allCommitCharacters;
  }
  
  /**
   * The list of all possible characters that commit a completion. This field
   * can be used if clients don't support individual commit characters per
   * completion item. See client capability
   * {@link CompletionItemCapabilities#commitCharactersSupport}.
   * <p>
   * If a server provides both {@code allCommitCharacters} and commit characters on
   * an individual completion item the ones on the completion item win.
   * <p>
   * Since 3.2.0
   */
  public void setAllCommitCharacters(final List<String> allCommitCharacters) {
    this.allCommitCharacters = allCommitCharacters;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("triggerCharacters", this.triggerCharacters);
    b.add("resolveProvider", this.resolveProvider);
    b.add("allCommitCharacters", this.allCommitCharacters);
    b.add("workDoneProgress", getWorkDoneProgress());
    b.add("documentSelector", getDocumentSelector());
    return b.toString();
  }
  
  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    if (!super.equals(obj))
      return false;
    CompletionRegistrationOptions other = (CompletionRegistrationOptions) obj;
    if (this.triggerCharacters == null) {
      if (other.triggerCharacters != null)
        return false;
    } else if (!this.triggerCharacters.equals(other.triggerCharacters))
      return false;
    if (this.resolveProvider == null) {
      if (other.resolveProvider != null)
        return false;
    } else if (!this.resolveProvider.equals(other.resolveProvider))
      return false;
    if (this.allCommitCharacters == null) {
      if (other.allCommitCharacters != null)
        return false;
    } else if (!this.allCommitCharacters.equals(other.allCommitCharacters))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((this.triggerCharacters== null) ? 0 : this.triggerCharacters.hashCode());
    result = prime * result + ((this.resolveProvider== null) ? 0 : this.resolveProvider.hashCode());
    return prime * result + ((this.allCommitCharacters== null) ? 0 : this.allCommitCharacters.hashCode());
  }
}
