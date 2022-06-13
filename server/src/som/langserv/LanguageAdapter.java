package som.langserv;

import static som.langserv.structure.SemanticTokens.combineTokensRemovingErroneousLine;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpContext;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.services.LanguageClient;

import som.langserv.lense.FileLens;
import som.langserv.lint.FileLinter;
import som.langserv.lint.WorkspaceLinter;
import som.langserv.structure.DocumentStructures;
import som.langserv.structure.LanguageElement;
import som.langserv.structure.Pair;
import som.langserv.structure.ParseContextKind;
import som.langserv.structure.SemanticTokens;
import util.ArrayListIgnoreIfLastIdentical;


public abstract class LanguageAdapter {
  private LanguageClient client;

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

  protected void putStructures(final String normalizedPath,
      final DocumentStructures docStructures) {
    synchronized (structures) {
      structures.put(normalizedPath, docStructures);
    }
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
    for (File f : folder.listFiles()) {
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
      codeLenses.addAll(results);
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
    return doc.getRootSymbols();
  }

  public final Hover hover(final String uri, final Position position) {
    DocumentStructures doc = getStructures(uri);
    return doc.getHover(position);
  }

  public final SignatureHelp signatureHelp(final String uri, final Position position,
      final SignatureHelpContext context) {
    DocumentStructures doc = getStructures(uri);
    return doc.getSignatureHelp(position, context);
  }

  public final List<? extends LocationLink> getDefinitions(final String uri,
      final Position pos) {
    DocumentStructures doc = getStructures(uri);
    var element = doc.getElement(pos);

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
    return doc.getHighlight(position);
  }

  public final List<Location> getReferences(final String uri, final Position position,
      final boolean includeDeclaration) {
    DocumentStructures doc = getStructures(uri);
    var element = doc.getElement(position);

    if (element == null) {
      return null;
    }

    List<Location> result = new ArrayListIgnoreIfLastIdentical<>();

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
    Pair<ParseContextKind, String> element = doc.getPossiblyIncompleteElement(position);

    if (element == null) {
      return null;
    }

    CompletionList completion = new CompletionList();
    completion.setIsIncomplete(false);

    List<CompletionItem> items = new ArrayListIgnoreIfLastIdentical<>();
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
    DocumentStructures doc = getStructures(uri);
    List<int[]> tokens = doc.getSemanticTokens().getSemanticTokens();

    Diagnostic error = doc.getFirstErrorOrNull();
    if (error == null) {
      semanticTokenCache.put(uri, tokens);
      return SemanticTokens.makeRelativeTo11(tokens);
    }

    List<int[]> prevTokens = semanticTokenCache.get(uri);
    if (prevTokens == null) {
      return null;
    }

    List<int[]> withOldAndWithoutError =
        combineTokensRemovingErroneousLine(
            error.getRange().getStart(), prevTokens, tokens);
    return SemanticTokens.makeRelativeTo11(withOldAndWithoutError);
  }
}
