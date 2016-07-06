/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.typefox.lsapi.util;

import io.typefox.lsapi.DidChangeTextDocumentParamsImpl;
import io.typefox.lsapi.DidChangeWatchedFilesParamsImpl;
import io.typefox.lsapi.DidCloseTextDocumentParamsImpl;
import io.typefox.lsapi.DidOpenTextDocumentParamsImpl;
import io.typefox.lsapi.DocumentSymbolParamsImpl;
import io.typefox.lsapi.FileEventImpl;
import io.typefox.lsapi.HoverImpl;
import io.typefox.lsapi.InitializeParamsImpl;
import io.typefox.lsapi.MarkedStringImpl;
import io.typefox.lsapi.ParameterInformationImpl;
import io.typefox.lsapi.Position;
import io.typefox.lsapi.PositionImpl;
import io.typefox.lsapi.Range;
import io.typefox.lsapi.RangeImpl;
import io.typefox.lsapi.ReferenceContextImpl;
import io.typefox.lsapi.ReferenceParamsImpl;
import io.typefox.lsapi.SignatureHelpImpl;
import io.typefox.lsapi.SignatureInformationImpl;
import io.typefox.lsapi.TextDocumentContentChangeEventImpl;
import io.typefox.lsapi.TextDocumentIdentifierImpl;
import io.typefox.lsapi.TextDocumentItemImpl;
import io.typefox.lsapi.TextDocumentPositionParamsImpl;
import io.typefox.lsapi.TextEditImpl;
import io.typefox.lsapi.VersionedTextDocumentIdentifierImpl;
import io.typefox.lsapi.WorkspaceSymbolParamsImpl;
import java.util.List;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
@SuppressWarnings("all")
public class LsapiFactories {
  public static PositionImpl newPosition(final int line, final int character) {
    final PositionImpl position = new PositionImpl();
    position.setLine(line);
    position.setCharacter(character);
    return position;
  }
  
  public static PositionImpl copyPosition(final Position position) {
    final PositionImpl copy = new PositionImpl();
    int _line = position.getLine();
    copy.setLine(_line);
    int _character = position.getCharacter();
    copy.setCharacter(_character);
    return copy;
  }
  
  public static RangeImpl newRange(final PositionImpl start, final PositionImpl end) {
    final RangeImpl range = new RangeImpl();
    range.setStart(start);
    range.setEnd(end);
    return range;
  }
  
  public static RangeImpl copyRange(final Range source) {
    final RangeImpl copy = new RangeImpl();
    Position _start = source.getStart();
    PositionImpl _copyPosition = LsapiFactories.copyPosition(_start);
    copy.setStart(_copyPosition);
    Position _end = source.getEnd();
    PositionImpl _copyPosition_1 = LsapiFactories.copyPosition(_end);
    copy.setEnd(_copyPosition_1);
    return copy;
  }
  
  public static TextEditImpl newTextEdit(final RangeImpl range, final String newText) {
    final TextEditImpl textEdit = new TextEditImpl();
    textEdit.setRange(range);
    textEdit.setNewText(newText);
    return textEdit;
  }
  
  public static TextDocumentIdentifierImpl newTextDocumentIdentifier(final String uri) {
    final TextDocumentIdentifierImpl identifier = new TextDocumentIdentifierImpl();
    identifier.setUri(uri);
    return identifier;
  }
  
  public static VersionedTextDocumentIdentifierImpl newVersionedTextDocumentIdentifier(final String uri, final int version) {
    final VersionedTextDocumentIdentifierImpl identifier = new VersionedTextDocumentIdentifierImpl();
    identifier.setUri(uri);
    identifier.setVersion(version);
    return identifier;
  }
  
  public static TextDocumentItemImpl newTextDocumentItem(final String uri, final String languageId, final int version, final String text) {
    final TextDocumentItemImpl item = new TextDocumentItemImpl();
    item.setUri(uri);
    item.setLanguageId(languageId);
    item.setVersion(version);
    item.setText(text);
    return item;
  }
  
  public static FileEventImpl newFileEvent(final String uri, final int type) {
    final FileEventImpl fileEvent = new FileEventImpl();
    fileEvent.setUri(uri);
    fileEvent.setType(type);
    return fileEvent;
  }
  
  public static TextDocumentContentChangeEventImpl newTextDocumentContentChangeEvent(final RangeImpl range, final Integer rangeLength, final String text) {
    final TextDocumentContentChangeEventImpl changeEvent = new TextDocumentContentChangeEventImpl();
    changeEvent.setRange(range);
    changeEvent.setRangeLength(rangeLength);
    changeEvent.setText(text);
    return changeEvent;
  }
  
  public static InitializeParamsImpl newInitializeParams(final int processId, final String rootPath) {
    final InitializeParamsImpl params = new InitializeParamsImpl();
    params.setProcessId(Integer.valueOf(processId));
    params.setRootPath(rootPath);
    return params;
  }
  
  protected static void initialize(final TextDocumentPositionParamsImpl params, final String uri, final int line, final int column) {
    TextDocumentIdentifierImpl _newTextDocumentIdentifier = LsapiFactories.newTextDocumentIdentifier(uri);
    params.setTextDocument(_newTextDocumentIdentifier);
    PositionImpl _newPosition = LsapiFactories.newPosition(line, column);
    params.setPosition(_newPosition);
  }
  
  public static TextDocumentPositionParamsImpl newTextDocumentPositionParams(final String uri, final int line, final int column) {
    final TextDocumentPositionParamsImpl params = new TextDocumentPositionParamsImpl();
    LsapiFactories.initialize(params, uri, line, column);
    return params;
  }
  
  public static DocumentSymbolParamsImpl newDocumentSymbolParams(final String uri) {
    final DocumentSymbolParamsImpl params = new DocumentSymbolParamsImpl();
    TextDocumentIdentifierImpl _newTextDocumentIdentifier = LsapiFactories.newTextDocumentIdentifier(uri);
    params.setTextDocument(_newTextDocumentIdentifier);
    return params;
  }
  
  public static WorkspaceSymbolParamsImpl newWorkspaceSymbolParams(final String query) {
    final WorkspaceSymbolParamsImpl params = new WorkspaceSymbolParamsImpl();
    params.setQuery(query);
    return params;
  }
  
  public static ReferenceParamsImpl newReferenceParams(final String uri, final int line, final int column, final ReferenceContextImpl context) {
    final ReferenceParamsImpl params = new ReferenceParamsImpl();
    LsapiFactories.initialize(params, uri, line, column);
    params.setContext(context);
    return params;
  }
  
  public static DidOpenTextDocumentParamsImpl newDidOpenTextDocumentParams(final String uri, final String languageId, final int version, final String text) {
    final DidOpenTextDocumentParamsImpl params = new DidOpenTextDocumentParamsImpl();
    params.setUri(uri);
    TextDocumentItemImpl _newTextDocumentItem = LsapiFactories.newTextDocumentItem(uri, languageId, version, text);
    params.setTextDocument(_newTextDocumentItem);
    return params;
  }
  
  public static DidCloseTextDocumentParamsImpl newDidCloseTextDocumentParams(final String uri) {
    final DidCloseTextDocumentParamsImpl params = new DidCloseTextDocumentParamsImpl();
    TextDocumentIdentifierImpl _newTextDocumentIdentifier = LsapiFactories.newTextDocumentIdentifier(uri);
    params.setTextDocument(_newTextDocumentIdentifier);
    return params;
  }
  
  public static DidChangeWatchedFilesParamsImpl newDidChangeWatchedFilesParams(final List<FileEventImpl> changes) {
    final DidChangeWatchedFilesParamsImpl params = new DidChangeWatchedFilesParamsImpl();
    params.setChanges(changes);
    return params;
  }
  
  public static DidChangeTextDocumentParamsImpl newDidChangeTextDocumentParamsImpl(final String uri, final int version, final List<TextDocumentContentChangeEventImpl> contentChanges) {
    final DidChangeTextDocumentParamsImpl params = new DidChangeTextDocumentParamsImpl();
    VersionedTextDocumentIdentifierImpl _newVersionedTextDocumentIdentifier = LsapiFactories.newVersionedTextDocumentIdentifier(uri, version);
    params.setTextDocument(_newVersionedTextDocumentIdentifier);
    params.setContentChanges(contentChanges);
    return params;
  }
  
  public static MarkedStringImpl newMarkedString(final String value, final String language) {
    final MarkedStringImpl markedString = new MarkedStringImpl();
    markedString.setValue(value);
    markedString.setLanguage(language);
    return markedString;
  }
  
  public static HoverImpl emptyHover() {
    List<MarkedStringImpl> _emptyList = CollectionLiterals.<MarkedStringImpl>emptyList();
    return LsapiFactories.newHover(_emptyList, null);
  }
  
  public static HoverImpl newHover(final List<MarkedStringImpl> contents, final RangeImpl range) {
    final HoverImpl hover = new HoverImpl();
    hover.setContents(contents);
    hover.setRange(range);
    return hover;
  }
  
  public static SignatureHelpImpl emptySignatureHelp() {
    List<SignatureInformationImpl> _emptyList = CollectionLiterals.<SignatureInformationImpl>emptyList();
    return LsapiFactories.newSignatureHelp(_emptyList, null, null);
  }
  
  public static SignatureHelpImpl newSignatureHelp(final List<SignatureInformationImpl> signatures, final Integer activeSignature, final Integer activeParameter) {
    final SignatureHelpImpl signatureHelp = new SignatureHelpImpl();
    signatureHelp.setSignatures(signatures);
    signatureHelp.setActiveSignature(activeSignature);
    signatureHelp.setActiveParameter(activeParameter);
    return signatureHelp;
  }
  
  public static SignatureInformationImpl newSignatureInformation(final String label, final String documentation, final List<ParameterInformationImpl> parameters) {
    final SignatureInformationImpl signatureInformation = new SignatureInformationImpl();
    signatureInformation.setLabel(label);
    signatureInformation.setDocumentation(documentation);
    signatureInformation.setParameters(parameters);
    return signatureInformation;
  }
  
  public static ParameterInformationImpl newParameterInformation(final String label, final String documentation) {
    final ParameterInformationImpl parameterInformation = new ParameterInformationImpl();
    parameterInformation.setLabel(label);
    parameterInformation.setDocumentation(documentation);
    return parameterInformation;
  }
}
