/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi;

import io.typefox.lsapi.CodeLensOptionsImpl;
import io.typefox.lsapi.CompletionOptionsImpl;
import io.typefox.lsapi.DocumentOnTypeFormattingOptionsImpl;
import io.typefox.lsapi.ServerCapabilities;
import io.typefox.lsapi.SignatureHelpOptionsImpl;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ServerCapabilitiesImpl implements ServerCapabilities {
  /**
   * Defines how text documents are synced.
   */
  private Integer textDocumentSync;
  
  @Pure
  @Override
  public Integer getTextDocumentSync() {
    return this.textDocumentSync;
  }
  
  public void setTextDocumentSync(final Integer textDocumentSync) {
    this.textDocumentSync = textDocumentSync;
  }
  
  /**
   * The server provides hover support.
   */
  private Boolean hoverProvider;
  
  @Pure
  @Override
  public Boolean isHoverProvider() {
    return this.hoverProvider;
  }
  
  public void setHoverProvider(final Boolean hoverProvider) {
    this.hoverProvider = hoverProvider;
  }
  
  /**
   * The server provides completion support.
   */
  private CompletionOptionsImpl completionProvider;
  
  @Pure
  @Override
  public CompletionOptionsImpl getCompletionProvider() {
    return this.completionProvider;
  }
  
  public void setCompletionProvider(final CompletionOptionsImpl completionProvider) {
    this.completionProvider = completionProvider;
  }
  
  /**
   * The server provides signature help support.
   */
  private SignatureHelpOptionsImpl signatureHelpProvider;
  
  @Pure
  @Override
  public SignatureHelpOptionsImpl getSignatureHelpProvider() {
    return this.signatureHelpProvider;
  }
  
  public void setSignatureHelpProvider(final SignatureHelpOptionsImpl signatureHelpProvider) {
    this.signatureHelpProvider = signatureHelpProvider;
  }
  
  /**
   * The server provides goto definition support.
   */
  private Boolean definitionProvider;
  
  @Pure
  @Override
  public Boolean isDefinitionProvider() {
    return this.definitionProvider;
  }
  
  public void setDefinitionProvider(final Boolean definitionProvider) {
    this.definitionProvider = definitionProvider;
  }
  
  /**
   * The server provides find references support.
   */
  private Boolean referencesProvider;
  
  @Pure
  @Override
  public Boolean isReferencesProvider() {
    return this.referencesProvider;
  }
  
  public void setReferencesProvider(final Boolean referencesProvider) {
    this.referencesProvider = referencesProvider;
  }
  
  /**
   * The server provides document highlight support.
   */
  private Boolean documentHighlightProvider;
  
  @Pure
  @Override
  public Boolean isDocumentHighlightProvider() {
    return this.documentHighlightProvider;
  }
  
  public void setDocumentHighlightProvider(final Boolean documentHighlightProvider) {
    this.documentHighlightProvider = documentHighlightProvider;
  }
  
  /**
   * The server provides document symbol support.
   */
  private Boolean documentSymbolProvider;
  
  @Pure
  @Override
  public Boolean isDocumentSymbolProvider() {
    return this.documentSymbolProvider;
  }
  
  public void setDocumentSymbolProvider(final Boolean documentSymbolProvider) {
    this.documentSymbolProvider = documentSymbolProvider;
  }
  
  /**
   * The server provides workspace symbol support.
   */
  private Boolean workspaceSymbolProvider;
  
  @Pure
  @Override
  public Boolean isWorkspaceSymbolProvider() {
    return this.workspaceSymbolProvider;
  }
  
  public void setWorkspaceSymbolProvider(final Boolean workspaceSymbolProvider) {
    this.workspaceSymbolProvider = workspaceSymbolProvider;
  }
  
  /**
   * The server provides code actions.
   */
  private Boolean codeActionProvider;
  
  @Pure
  @Override
  public Boolean isCodeActionProvider() {
    return this.codeActionProvider;
  }
  
  public void setCodeActionProvider(final Boolean codeActionProvider) {
    this.codeActionProvider = codeActionProvider;
  }
  
  /**
   * The server provides code lens.
   */
  private CodeLensOptionsImpl codeLensProvider;
  
  @Pure
  @Override
  public CodeLensOptionsImpl getCodeLensProvider() {
    return this.codeLensProvider;
  }
  
  public void setCodeLensProvider(final CodeLensOptionsImpl codeLensProvider) {
    this.codeLensProvider = codeLensProvider;
  }
  
  /**
   * The server provides document formatting.
   */
  private Boolean documentFormattingProvider;
  
  @Pure
  @Override
  public Boolean isDocumentFormattingProvider() {
    return this.documentFormattingProvider;
  }
  
  public void setDocumentFormattingProvider(final Boolean documentFormattingProvider) {
    this.documentFormattingProvider = documentFormattingProvider;
  }
  
  /**
   * The server provides document range formatting.
   */
  private Boolean documentRangeFormattingProvider;
  
  @Pure
  @Override
  public Boolean isDocumentRangeFormattingProvider() {
    return this.documentRangeFormattingProvider;
  }
  
  public void setDocumentRangeFormattingProvider(final Boolean documentRangeFormattingProvider) {
    this.documentRangeFormattingProvider = documentRangeFormattingProvider;
  }
  
  /**
   * The server provides document formatting on typing.
   */
  private DocumentOnTypeFormattingOptionsImpl documentOnTypeFormattingProvider;
  
  @Pure
  @Override
  public DocumentOnTypeFormattingOptionsImpl getDocumentOnTypeFormattingProvider() {
    return this.documentOnTypeFormattingProvider;
  }
  
  public void setDocumentOnTypeFormattingProvider(final DocumentOnTypeFormattingOptionsImpl documentOnTypeFormattingProvider) {
    this.documentOnTypeFormattingProvider = documentOnTypeFormattingProvider;
  }
  
  /**
   * The server provides rename support.
   */
  private Boolean renameProvider;
  
  @Pure
  @Override
  public Boolean isRenameProvider() {
    return this.renameProvider;
  }
  
  public void setRenameProvider(final Boolean renameProvider) {
    this.renameProvider = renameProvider;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("textDocumentSync", this.textDocumentSync);
    b.add("hoverProvider", this.hoverProvider);
    b.add("completionProvider", this.completionProvider);
    b.add("signatureHelpProvider", this.signatureHelpProvider);
    b.add("definitionProvider", this.definitionProvider);
    b.add("referencesProvider", this.referencesProvider);
    b.add("documentHighlightProvider", this.documentHighlightProvider);
    b.add("documentSymbolProvider", this.documentSymbolProvider);
    b.add("workspaceSymbolProvider", this.workspaceSymbolProvider);
    b.add("codeActionProvider", this.codeActionProvider);
    b.add("codeLensProvider", this.codeLensProvider);
    b.add("documentFormattingProvider", this.documentFormattingProvider);
    b.add("documentRangeFormattingProvider", this.documentRangeFormattingProvider);
    b.add("documentOnTypeFormattingProvider", this.documentOnTypeFormattingProvider);
    b.add("renameProvider", this.renameProvider);
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
    ServerCapabilitiesImpl other = (ServerCapabilitiesImpl) obj;
    if (this.textDocumentSync == null) {
      if (other.textDocumentSync != null)
        return false;
    } else if (!this.textDocumentSync.equals(other.textDocumentSync))
      return false;
    if (this.hoverProvider == null) {
      if (other.hoverProvider != null)
        return false;
    } else if (!this.hoverProvider.equals(other.hoverProvider))
      return false;
    if (this.completionProvider == null) {
      if (other.completionProvider != null)
        return false;
    } else if (!this.completionProvider.equals(other.completionProvider))
      return false;
    if (this.signatureHelpProvider == null) {
      if (other.signatureHelpProvider != null)
        return false;
    } else if (!this.signatureHelpProvider.equals(other.signatureHelpProvider))
      return false;
    if (this.definitionProvider == null) {
      if (other.definitionProvider != null)
        return false;
    } else if (!this.definitionProvider.equals(other.definitionProvider))
      return false;
    if (this.referencesProvider == null) {
      if (other.referencesProvider != null)
        return false;
    } else if (!this.referencesProvider.equals(other.referencesProvider))
      return false;
    if (this.documentHighlightProvider == null) {
      if (other.documentHighlightProvider != null)
        return false;
    } else if (!this.documentHighlightProvider.equals(other.documentHighlightProvider))
      return false;
    if (this.documentSymbolProvider == null) {
      if (other.documentSymbolProvider != null)
        return false;
    } else if (!this.documentSymbolProvider.equals(other.documentSymbolProvider))
      return false;
    if (this.workspaceSymbolProvider == null) {
      if (other.workspaceSymbolProvider != null)
        return false;
    } else if (!this.workspaceSymbolProvider.equals(other.workspaceSymbolProvider))
      return false;
    if (this.codeActionProvider == null) {
      if (other.codeActionProvider != null)
        return false;
    } else if (!this.codeActionProvider.equals(other.codeActionProvider))
      return false;
    if (this.codeLensProvider == null) {
      if (other.codeLensProvider != null)
        return false;
    } else if (!this.codeLensProvider.equals(other.codeLensProvider))
      return false;
    if (this.documentFormattingProvider == null) {
      if (other.documentFormattingProvider != null)
        return false;
    } else if (!this.documentFormattingProvider.equals(other.documentFormattingProvider))
      return false;
    if (this.documentRangeFormattingProvider == null) {
      if (other.documentRangeFormattingProvider != null)
        return false;
    } else if (!this.documentRangeFormattingProvider.equals(other.documentRangeFormattingProvider))
      return false;
    if (this.documentOnTypeFormattingProvider == null) {
      if (other.documentOnTypeFormattingProvider != null)
        return false;
    } else if (!this.documentOnTypeFormattingProvider.equals(other.documentOnTypeFormattingProvider))
      return false;
    if (this.renameProvider == null) {
      if (other.renameProvider != null)
        return false;
    } else if (!this.renameProvider.equals(other.renameProvider))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.textDocumentSync== null) ? 0 : this.textDocumentSync.hashCode());
    result = prime * result + ((this.hoverProvider== null) ? 0 : this.hoverProvider.hashCode());
    result = prime * result + ((this.completionProvider== null) ? 0 : this.completionProvider.hashCode());
    result = prime * result + ((this.signatureHelpProvider== null) ? 0 : this.signatureHelpProvider.hashCode());
    result = prime * result + ((this.definitionProvider== null) ? 0 : this.definitionProvider.hashCode());
    result = prime * result + ((this.referencesProvider== null) ? 0 : this.referencesProvider.hashCode());
    result = prime * result + ((this.documentHighlightProvider== null) ? 0 : this.documentHighlightProvider.hashCode());
    result = prime * result + ((this.documentSymbolProvider== null) ? 0 : this.documentSymbolProvider.hashCode());
    result = prime * result + ((this.workspaceSymbolProvider== null) ? 0 : this.workspaceSymbolProvider.hashCode());
    result = prime * result + ((this.codeActionProvider== null) ? 0 : this.codeActionProvider.hashCode());
    result = prime * result + ((this.codeLensProvider== null) ? 0 : this.codeLensProvider.hashCode());
    result = prime * result + ((this.documentFormattingProvider== null) ? 0 : this.documentFormattingProvider.hashCode());
    result = prime * result + ((this.documentRangeFormattingProvider== null) ? 0 : this.documentRangeFormattingProvider.hashCode());
    result = prime * result + ((this.documentOnTypeFormattingProvider== null) ? 0 : this.documentOnTypeFormattingProvider.hashCode());
    result = prime * result + ((this.renameProvider== null) ? 0 : this.renameProvider.hashCode());
    return result;
  }
}
