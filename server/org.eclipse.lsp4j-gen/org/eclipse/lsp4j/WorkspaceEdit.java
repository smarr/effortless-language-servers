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

import com.google.common.annotations.Beta;
import com.google.gson.annotations.JsonAdapter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.ChangeAnnotation;
import org.eclipse.lsp4j.ResourceChange;
import org.eclipse.lsp4j.ResourceOperation;
import org.eclipse.lsp4j.TextDocumentEdit;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.adapters.DocumentChangeListAdapter;
import org.eclipse.lsp4j.adapters.ResourceChangeListAdapter;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * A workspace edit represents changes to many resources managed in the workspace.
 * The edit should either provide {@link #changes} or {@link #documentChanges}.
 * If documentChanges are present they are preferred over changes
 * if the client can handle versioned document edits.
 */
@SuppressWarnings("all")
public class WorkspaceEdit {
  /**
   * Holds changes to existing resources.
   */
  private Map<String, List<TextEdit>> changes;
  
  /**
   * Depending on the client capability
   * {@link WorkspaceEditCapabilities#resourceOperations} document changes are either
   * an array of {@link TextDocumentEdit}s to express changes to n different text
   * documents where each text document edit addresses a specific version of
   * a text document. Or it can contain above {@link TextDocumentEdit}s mixed with
   * create, rename and delete file / folder operations.
   * <p>
   * Whether a client supports versioned document edits is expressed via
   * {@link WorkspaceEditCapabilities#documentChanges} client capability.
   * <p>
   * If a client neither supports {@link WorkspaceEditCapabilities#documentChanges} nor
   * {@link WorkspaceEditCapabilities#resourceOperations} then only plain {@link TextEdit}s
   * using the {@link #changes} property are supported.
   */
  @JsonAdapter(DocumentChangeListAdapter.class)
  private List<Either<TextDocumentEdit, ResourceOperation>> documentChanges;
  
  /**
   * If resource changes are supported the `WorkspaceEdit`
   * uses the property `resourceChanges` which are either a
   * rename, move, delete or content change.
   * <p>
   * These changes are applied in the order that they are supplied,
   * however clients may group the changes for optimization
   * 
   * @deprecated Since LSP introduces resource operations, use the {@link #documentChanges} instead
   */
  @Beta
  @JsonAdapter(ResourceChangeListAdapter.class)
  @Deprecated
  private List<Either<ResourceChange, TextDocumentEdit>> resourceChanges;
  
  /**
   * A map of change annotations that can be referenced in
   * {@link AnnotatedTextEdit}s or {@link ResourceOperation}s.
   * <p>
   * Client support depends on {@link WorkspaceEditCapabilities#changeAnnotationSupport}.
   * <p>
   * Since 3.16.0
   */
  private Map<String, ChangeAnnotation> changeAnnotations;
  
  public WorkspaceEdit() {
    LinkedHashMap<String, List<TextEdit>> _linkedHashMap = new LinkedHashMap<String, List<TextEdit>>();
    this.changes = _linkedHashMap;
  }
  
  public WorkspaceEdit(final Map<String, List<TextEdit>> changes) {
    this.changes = changes;
  }
  
  public WorkspaceEdit(final List<Either<TextDocumentEdit, ResourceOperation>> documentChanges) {
    this.documentChanges = documentChanges;
  }
  
  /**
   * Holds changes to existing resources.
   */
  @Pure
  public Map<String, List<TextEdit>> getChanges() {
    return this.changes;
  }
  
  /**
   * Holds changes to existing resources.
   */
  public void setChanges(final Map<String, List<TextEdit>> changes) {
    this.changes = changes;
  }
  
  /**
   * Depending on the client capability
   * {@link WorkspaceEditCapabilities#resourceOperations} document changes are either
   * an array of {@link TextDocumentEdit}s to express changes to n different text
   * documents where each text document edit addresses a specific version of
   * a text document. Or it can contain above {@link TextDocumentEdit}s mixed with
   * create, rename and delete file / folder operations.
   * <p>
   * Whether a client supports versioned document edits is expressed via
   * {@link WorkspaceEditCapabilities#documentChanges} client capability.
   * <p>
   * If a client neither supports {@link WorkspaceEditCapabilities#documentChanges} nor
   * {@link WorkspaceEditCapabilities#resourceOperations} then only plain {@link TextEdit}s
   * using the {@link #changes} property are supported.
   */
  @Pure
  public List<Either<TextDocumentEdit, ResourceOperation>> getDocumentChanges() {
    return this.documentChanges;
  }
  
  /**
   * Depending on the client capability
   * {@link WorkspaceEditCapabilities#resourceOperations} document changes are either
   * an array of {@link TextDocumentEdit}s to express changes to n different text
   * documents where each text document edit addresses a specific version of
   * a text document. Or it can contain above {@link TextDocumentEdit}s mixed with
   * create, rename and delete file / folder operations.
   * <p>
   * Whether a client supports versioned document edits is expressed via
   * {@link WorkspaceEditCapabilities#documentChanges} client capability.
   * <p>
   * If a client neither supports {@link WorkspaceEditCapabilities#documentChanges} nor
   * {@link WorkspaceEditCapabilities#resourceOperations} then only plain {@link TextEdit}s
   * using the {@link #changes} property are supported.
   */
  public void setDocumentChanges(final List<Either<TextDocumentEdit, ResourceOperation>> documentChanges) {
    this.documentChanges = documentChanges;
  }
  
  /**
   * If resource changes are supported the `WorkspaceEdit`
   * uses the property `resourceChanges` which are either a
   * rename, move, delete or content change.
   * <p>
   * These changes are applied in the order that they are supplied,
   * however clients may group the changes for optimization
   * 
   * @deprecated Since LSP introduces resource operations, use the {@link #documentChanges} instead
   */
  @Pure
  @Deprecated
  public List<Either<ResourceChange, TextDocumentEdit>> getResourceChanges() {
    return this.resourceChanges;
  }
  
  /**
   * If resource changes are supported the `WorkspaceEdit`
   * uses the property `resourceChanges` which are either a
   * rename, move, delete or content change.
   * <p>
   * These changes are applied in the order that they are supplied,
   * however clients may group the changes for optimization
   * 
   * @deprecated Since LSP introduces resource operations, use the {@link #documentChanges} instead
   */
  @Deprecated
  public void setResourceChanges(final List<Either<ResourceChange, TextDocumentEdit>> resourceChanges) {
    this.resourceChanges = resourceChanges;
  }
  
  /**
   * A map of change annotations that can be referenced in
   * {@link AnnotatedTextEdit}s or {@link ResourceOperation}s.
   * <p>
   * Client support depends on {@link WorkspaceEditCapabilities#changeAnnotationSupport}.
   * <p>
   * Since 3.16.0
   */
  @Pure
  public Map<String, ChangeAnnotation> getChangeAnnotations() {
    return this.changeAnnotations;
  }
  
  /**
   * A map of change annotations that can be referenced in
   * {@link AnnotatedTextEdit}s or {@link ResourceOperation}s.
   * <p>
   * Client support depends on {@link WorkspaceEditCapabilities#changeAnnotationSupport}.
   * <p>
   * Since 3.16.0
   */
  public void setChangeAnnotations(final Map<String, ChangeAnnotation> changeAnnotations) {
    this.changeAnnotations = changeAnnotations;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("changes", this.changes);
    b.add("documentChanges", this.documentChanges);
    b.add("resourceChanges", this.resourceChanges);
    b.add("changeAnnotations", this.changeAnnotations);
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
    WorkspaceEdit other = (WorkspaceEdit) obj;
    if (this.changes == null) {
      if (other.changes != null)
        return false;
    } else if (!this.changes.equals(other.changes))
      return false;
    if (this.documentChanges == null) {
      if (other.documentChanges != null)
        return false;
    } else if (!this.documentChanges.equals(other.documentChanges))
      return false;
    if (this.resourceChanges == null) {
      if (other.resourceChanges != null)
        return false;
    } else if (!this.resourceChanges.equals(other.resourceChanges))
      return false;
    if (this.changeAnnotations == null) {
      if (other.changeAnnotations != null)
        return false;
    } else if (!this.changeAnnotations.equals(other.changeAnnotations))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.changes== null) ? 0 : this.changes.hashCode());
    result = prime * result + ((this.documentChanges== null) ? 0 : this.documentChanges.hashCode());
    result = prime * result + ((this.resourceChanges== null) ? 0 : this.resourceChanges.hashCode());
    return prime * result + ((this.changeAnnotations== null) ? 0 : this.changeAnnotations.hashCode());
  }
}
