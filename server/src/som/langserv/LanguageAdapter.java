package som.langserv;

import static som.langserv.structure.SemanticTokens.combineTokensRemovingErroneousLine;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.CallHierarchyIncomingCall;
import org.eclipse.lsp4j.CallHierarchyItem;
import org.eclipse.lsp4j.CallHierarchyOutgoingCall;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.FoldingRange;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SelectionRange;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpContext;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;
import org.eclipse.lsp4j.services.LanguageClient;

import som.langserv.lens.FileLens;
import som.langserv.lint.FileLinter;
import som.langserv.lint.WorkspaceLinter;
import som.langserv.structure.DocumentStructures;
import som.langserv.structure.LanguageElement;
import som.langserv.structure.LanguageElementId;
import som.langserv.structure.Pair;
import som.langserv.structure.ParseContextKind;
import som.langserv.structure.Reference;
import som.langserv.structure.SemanticTokens;
import util.ArrayListSet;


public abstract class LanguageAdapter {
  protected LanguageClient client;

  private final Map<String, DocumentStructures> structures;

  private final Map<String, List<int[]>> semanticTokenCache;

  private final FileLinter[]      fileLinters;
  private final WorkspaceLinter[] workspaceLinters;

  private final FileLens[] fileLenses;

  public LanguageAdapter(final FileLinter[] fileLinters,
      final WorkspaceLinter[] workspaceLinters) {
    this(fileLinters, workspaceLinters, null);
  }

  public LanguageAdapter(final FileLinter[] fileLinters,
      final WorkspaceLinter[] workspaceLinters, final FileLens[] fileLenses) {
    this.structures = new LinkedHashMap<>();
    this.semanticTokenCache = new HashMap<>();
    this.fileLinters = fileLinters;
    this.workspaceLinters = workspaceLinters;
    this.fileLenses = fileLenses;
  }

  protected FileLinter[] getFileLinters() {
    return fileLinters;
  }

  protected WorkspaceLinter[] getWorkspaceLinters() {
    return workspaceLinters;
  }

  private static String withoutSchema(final String path) {
    if (path.startsWith("file:")) {
      return path.substring("file:".length());
    }
    return path;
  }

  /** Only for use when parsing succeeded in the language's parser. */
  public void putStructures(final DocumentStructures docStructures) {
    synchronized (structures) {
      String pathWithoutFileSchema = withoutSchema(docStructures.getNormalizedUri());
      structures.put(pathWithoutFileSchema, docStructures);
    }
  }

  protected void putStructures(final String normalizedPath,
      final DocumentStructures docStructures) {
    synchronized (structures) {
      structures.put(withoutSchema(normalizedPath), docStructures);
    }
  }

  protected DocumentStructures updateDiagnostics(final Diagnostic d,
      final DocumentStructures structures) {
    DocumentStructures old = getStructures(structures.getUri());

    DocumentStructures result = (old == null) ? structures : old;
    result.resetDiagnosticsAndAdd(d, old == null);
    if (old == null) {
      putStructures(structures.getNormalizedUri(), structures);
    }
    return result;
  }

  public abstract String getFileEnding();

  public boolean handlesUri(final String uri) {
    return uri.endsWith(getFileEnding());
  }

  public void connect(final LanguageClient client) {
    this.client = client;
  }

  public Object loadWorkspace(final String uri) throws URISyntaxException {
    if (uri == null) {
      return null;
    }

    URI workspaceUri = new URI(uri);
    File workspace = new File(workspaceUri);
    assert workspace.isDirectory();

    Thread t = new Thread(() -> loadWorkspaceAndLint(workspace));
    t.start();
    return t;
  }

  protected void loadWorkspaceAndLint(final File workspace) {
    loadFolder(workspace);

    for (WorkspaceLinter l : workspaceLinters) {
      l.lint(structures.values());
    }

    for (var s : structures.entrySet()) {
      DocumentServiceImpl.reportDiagnostics(s.getValue().getDiagnostics(), s.getKey(), client);
    }
  }

  public void loadFolder(final File folder) {
    // listFiles() has no guaranteed order, but files may define classes
    // with the same name, so make the loading order deterministic
    File[] files = folder.listFiles();
    Arrays.sort(files);
    for (File f : files) {
      if (f.isDirectory()) {
        loadFolder(f);
      } else if (f.getName().endsWith(getFileEnding())) {
        try {
          loadFile(f);
        } catch (IOException | URISyntaxException e) {
          // if loading fails, we don't do anything, just move on to the next file
        }
      }
    }
  }

  public DocumentStructures loadFile(final File f) throws IOException, URISyntaxException {
    byte[] content = Files.readAllBytes(f.toPath());
    String str = new String(content, StandardCharsets.UTF_8);
    String uri = f.toURI().toString();
    return parse(str, uri);
  }

  public static String docUriToNormalizedPath(final String documentUri)
      throws URISyntaxException {
    URI uri = new URI(documentUri).normalize();
    return uri.getPath();
  }

  public abstract DocumentStructures parse(final String text, final String sourceUri)
      throws URISyntaxException;

  public final DocumentStructures getStructures(final String documentUri) {
    synchronized (structures) {
      try {
        return structures.get(docUriToNormalizedPath(documentUri));
      } catch (URISyntaxException e) {
        return null;
      }
    }
  }

  protected final Collection<DocumentStructures> getDocuments() {
    synchronized (structures) {
      return new ArrayList<>(structures.values());
    }
  }

  public void reportDiagnostics(final List<Diagnostic> diagnostics, final String documentUri) {
    DocumentServiceImpl.reportDiagnostics(diagnostics, documentUri, client);
  }

  public void reportError(final String msgStr) {
    MessageParams msg = new MessageParams();
    msg.setType(MessageType.Log);
    msg.setMessage(msgStr);

    client.logMessage(msg);

    ServerLauncher.logErr(msgStr);
  }

  public final List<CodeLens> getCodeLenses(final String documentUri) {
    DocumentStructures doc = getStructures(documentUri);
    if (doc == null || fileLenses == null) {
      return null;
    }

    List<CodeLens> codeLenses = new ArrayList<>();
    for (var lens : fileLenses) {
      var results = lens.getCodeLenses(doc);
      if (results != null) {
        codeLenses.addAll(results);
      }
    }

    return codeLenses;
  }

  public final void workspaceSymbol(final List<SymbolInformation> results,
      final String query) {
    var docs = getDocuments();
    for (var doc : docs) {
      doc.find(results, query);
    }
  }

  public final List<LanguageElement> documentSymbol(final String documentUri) {
    DocumentStructures doc = getStructures(documentUri);
    if (doc == null) {
      return null;
    }

    return doc.getRootSymbols();
  }

  public final Hover hover(final String uri, final Position position) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    return doc.getHover(position);
  }

  public final List<FoldingRange> getFoldingRanges(final String uri) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    return doc.getFoldingRanges();
  }

  /** Whether the element can be called, and is thus part of a call hierarchy. */
  protected boolean isCallable(final LanguageElement e) {
    return e.getKind() == SymbolKind.Method || e.getKind() == SymbolKind.Function;
  }

  private static CallHierarchyItem createCallHierarchyItem(final LanguageElement e,
      final String uri) {
    var item = new CallHierarchyItem(
        e.getName(), e.getKind(), uri, e.getRange(), e.getSelectionRange());
    item.setDetail(e.getDetail());
    return item;
  }

  /** All callable definitions of the element, in all documents. */
  private List<CallHierarchyItem> findCallables(final LanguageElementId id) {
    List<CallHierarchyItem> result = new ArrayList<>();
    for (DocumentStructures d : getDocuments()) {
      var allDefs = d.getAllDefinitions();
      var defs = allDefs == null ? null : allDefs.get(id);
      if (defs != null) {
        for (var e : defs) {
          if (isCallable(e)) {
            result.add(createCallHierarchyItem(e, d.getUri()));
          }
        }
      }
    }
    return result;
  }

  public final List<CallHierarchyItem> prepareCallHierarchy(final String uri,
      final Position position) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    var element = doc.getElement(position);
    if (element == null || element.v1 == null) {
      return null;
    }

    // on a definition, it's only this one, otherwise all with the same name
    var definition = doc.findElement(element.v2, e -> isCallable(e));
    if (definition != null) {
      return List.of(createCallHierarchyItem(definition, doc.getUri()));
    }

    var result = findCallables(element.v1);
    return result.isEmpty() ? null : result;
  }

  private LanguageElement getCallable(final CallHierarchyItem item) {
    DocumentStructures doc = getStructures(item.getUri());
    if (doc == null) {
      return null;
    }
    return doc.findElement(item.getSelectionRange(), e -> isCallable(e));
  }

  public final List<CallHierarchyIncomingCall> getIncomingCalls(
      final CallHierarchyItem item) {
    var callee = getCallable(item);
    if (callee == null) {
      return List.of();
    }

    List<CallHierarchyIncomingCall> result = new ArrayList<>();
    for (DocumentStructures d : getDocuments()) {
      var allRefs = d.getAllReferences();
      var refs = allRefs == null ? null : allRefs.get(callee.getId());
      if (refs == null) {
        continue;
      }

      Map<LanguageElement, List<Range>> callers = new LinkedHashMap<>();
      for (var ref : refs) {
        var caller = d.findEnclosing(ref.getRange().getStart(), e -> isCallable(e));
        if (caller != null) {
          callers.computeIfAbsent(caller, k -> new ArrayList<>()).add(ref.getRange());
        }
      }

      for (var c : callers.entrySet()) {
        result.add(new CallHierarchyIncomingCall(
            createCallHierarchyItem(c.getKey(), d.getUri()), c.getValue()));
      }
    }
    return result;
  }

  public final List<CallHierarchyOutgoingCall> getOutgoingCalls(
      final CallHierarchyItem item) {
    var caller = getCallable(item);
    if (caller == null) {
      return List.of();
    }

    // nested callables make their own calls
    List<Reference> refs = new ArrayList<>();
    DocumentStructures.collectReferences(caller, e -> e != caller && isCallable(e), refs);

    Map<LanguageElementId, List<Range>> calls = new LinkedHashMap<>();
    for (var ref : refs) {
      calls.computeIfAbsent(ref.getId(), k -> new ArrayList<>()).add(ref.getRange());
    }

    List<CallHierarchyOutgoingCall> result = new ArrayList<>();
    for (var c : calls.entrySet()) {
      for (var callee : findCallables(c.getKey())) {
        result.add(new CallHierarchyOutgoingCall(callee, c.getValue()));
      }
    }
    return result;
  }

  public final List<CodeAction> getCodeActions(final String uri,
      final List<Diagnostic> diagnostics) {
    List<CodeAction> result = new ArrayList<>();
    for (Diagnostic d : diagnostics) {
      for (FileLinter linter : fileLinters) {
        result.addAll(linter.getQuickFixes(uri, d));
      }
    }
    return result;
  }

  public final List<SelectionRange> getSelectionRanges(final String uri,
      final List<Position> positions) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    List<SelectionRange> result = new ArrayList<>(positions.size());
    for (Position p : positions) {
      result.add(doc.getSelectionRange(p));
    }
    return result;
  }

  public final SignatureHelp signatureHelp(final String uri, final Position position,
      final SignatureHelpContext context) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    return doc.getSignatureHelp(position, context);
  }

  public final List<? extends LocationLink> getDefinitions(final String uri,
      final Position pos) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    var element = doc.getElement(pos);
    if (element == null) {
      return null;
    }

    List<LocationLink> definitions = new ArrayList<>();
    doc.lookupDefinitions(element, definitions);

    for (DocumentStructures d : getDocuments()) {
      // we already have those, so, skip this one
      if (doc == d) {
        continue;
      }

      d.lookupDefinitions(element, definitions);
    }

    return definitions;
  }

  public final List<DocumentHighlight> getHighlight(final String uri,
      final Position position) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    return doc.getHighlight(position);
  }

  public final List<Location> getReferences(final String uri, final Position position,
      final boolean includeDeclaration) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    var element = doc.getElement(position);

    if (element == null) {
      return null;
    }

    List<Location> result = new ArrayListSet<>();

    for (DocumentStructures d : getDocuments()) {
      if (includeDeclaration) {
        d.lookupDefinitionsLocation(element, result);
      }

      d.lookupReferences(element, result);
    }

    return result;
  }

  public final CompletionList getCompletions(final String uri, final Position position) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    Pair<ParseContextKind, String> element = doc.getPossiblyIncompleteElement(position);

    if (element == null) {
      return null;
    }

    CompletionList completion = new CompletionList();
    completion.setIsIncomplete(false);

    List<CompletionItem> items = new ArrayListSet<>();
    completion.setItems(items);

    doc.find(element.v2, element.v1, position, items);

    for (DocumentStructures d : getDocuments()) {
      if (d == doc) {
        continue;
      }

      d.find(element.v2, element.v1, position, items);
    }
    return completion;
  }

  public final List<Integer> getSemanticTokensFull(final String uri) {
    List<int[]> tokens = getAbsoluteSemanticTokens(uri);
    if (tokens == null) {
      return null;
    }
    return SemanticTokens.makeRelativeTo00(tokens);
  }

  public final List<Integer> getSemanticTokensRange(final String uri, final Range range) {
    List<int[]> tokens = getAbsoluteSemanticTokens(uri);
    if (tokens == null) {
      return null;
    }
    return SemanticTokens.makeRelativeTo00(SemanticTokens.inRange(tokens, range));
  }

  /** Tokens with absolute positions, using the previous ones for an erroneous line. */
  private List<int[]> getAbsoluteSemanticTokens(final String uri) {
    DocumentStructures doc = getStructures(uri);
    if (doc == null) {
      return null;
    }

    List<int[]> tokens = doc.getSemanticTokens().getSemanticTokens();

    Diagnostic error = doc.getFirstErrorOrNull();
    if (error == null) {
      semanticTokenCache.put(uri, tokens);
      return tokens;
    }

    List<int[]> prevTokens = semanticTokenCache.get(uri);
    if (prevTokens == null) {
      return null;
    }

    return combineTokensRemovingErroneousLine(
        error.getRange().getStart(), prevTokens, tokens);
  }
}
