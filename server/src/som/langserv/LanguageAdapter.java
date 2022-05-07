package som.langserv;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.services.LanguageClient;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.source.SourceCoordinate;


public abstract class LanguageAdapter<Probe> {
  private LanguageClient client;

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
    Map<String, List<Diagnostic>> allDiagnostics = new HashMap<>();
    loadFolder(workspace, allDiagnostics);

    for (Entry<String, List<Diagnostic>> e : allDiagnostics.entrySet()) {
      try {
        lintSends(e.getKey(), e.getValue());
      } catch (URISyntaxException ex) {
        /*
         * at this point, there is nothing to be done anymore,
         * would have been problematic earlier
         */
      }

      reportDiagnostics(e.getValue(), e.getKey());
    }
  }

  public void loadFolder(final File folder,
      final Map<String, List<Diagnostic>> allDiagnostics) {
    for (File f : folder.listFiles()) {
      if (f.isDirectory()) {
        loadFolder(f, allDiagnostics);
      } else if (f.getName().endsWith(getFileEnding())) {
        try {
          List<Diagnostic> diagnostics = loadFile(f);
          String uri = f.toURI().toString();
          allDiagnostics.put(uri, diagnostics);
        } catch (IOException | URISyntaxException e) {
          // if loading fails, we don't do anything, just move on to the next file
        }
      }
    }
  }

  public List<Diagnostic> loadFile(final File f) throws IOException, URISyntaxException {
    byte[] content = Files.readAllBytes(f.toPath());
    String str = new String(content, StandardCharsets.UTF_8);
    String uri = f.toURI().toString();
    return parse(str, uri);
  }

  public abstract void lintSends(final String docUri, final List<Diagnostic> diagnostics)
      throws URISyntaxException;

  public static String docUriToNormalizedPath(final String documentUri)
      throws URISyntaxException {
    URI uri = new URI(documentUri).normalize();
    return uri.getPath();
  }

  public abstract List<Diagnostic> parse(final String text, final String sourceUri)
      throws URISyntaxException;

  public static Position pos(final int startLine, final int startChar) {
    Position pos = new Position();
    pos.setLine(startLine - 1);
    pos.setCharacter(startChar - 1);
    return pos;
  }

  public DocumentHighlight getHighlight(final String documentUri,
      final int line, final int character) {
    // TODO: this is wrong, it should be something entierly different.
    // this feature is about marking the occurrences of a selected element
    // like a variable, where it is used.
    // so, this should actually return multiple results.
    // The spec is currently broken for that.

    // XXX: the code here doesn't make any sense for what it is supposed to do

    // Map<SourceSection, Set<Class<? extends Tags>>> sections = Highlight.
    // getSourceSections();
    // SourceSection[] all = sections.entrySet().stream().map(e -> e.getKey()).toArray(size ->
    // new SourceSection[size]);
    //
    // Stream<Entry<SourceSection, Set<Class<? extends Tags>>>> filtered = sections.
    // entrySet().stream().filter(
    // (final Entry<SourceSection, Set<Class<? extends Tags>>> e) -> in(e.getKey(), line,
    // character));
    //
    // @SuppressWarnings("rawtypes")
    // Entry[] matching = filtered.toArray(size -> new Entry[size]);
    //
    // for (Entry<SourceSection, Set<Class<? extends Tags>>> e : matching) {
    // int kind;
    // if (e.getValue().contains(LiteralTag.class)) {
    // kind = DocumentHighlight.KIND_READ;
    // } else {
    // kind = DocumentHighlight.KIND_TEXT;
    // }
    // DocumentHighlightImpl highlight = new DocumentHighlightImpl();
    // highlight.setKind(kind);
    // highlight.setRange(getRange(e.getKey()));
    // return highlight;
    // }
    //
    // DocumentHighlightImpl highlight = new DocumentHighlightImpl();
    // highlight.setKind(DocumentHighlight.KIND_TEXT);
    // RangeImpl range = new RangeImpl();
    // range.setStart(pos(line, character));
    // range.setEnd(pos(line, character + 1));
    // highlight.setRange(range);
    // return highlight;
    return null;
  }

  public static Range toRange(final Source source, final long coord) {
    return toRange(SourceCoordinate.createSourceSection(source, coord));
  }

  public static Range toRange(final SourceSection ss) {
    Range range = new Range();
    range.setStart(pos(ss.getStartLine(), ss.getStartColumn()));
    range.setEnd(pos(ss.getEndLine(), ss.getEndColumn() + 1));
    return range;
  }

  public static Range toRange(final int line, final int col, final int length) {
    Range range = new Range();
    range.setStart(pos(line, col));
    range.setEnd(pos(line, col + length));
    return range;
  }

  public static Range toRangeMax(final int startLine, final int startColumn) {
    Range range = new Range();
    range.setStart(pos(startLine, startColumn));
    range.setEnd(pos(startLine, Integer.MAX_VALUE));
    return range;
  }

  public static Location getLocation(final Source source, final long coord) {
    Location loc = new Location();
    loc.setUri(source.getURI().toString());
    loc.setRange(toRange(source, coord));
    return loc;
  }

  public static Location getLocation(final SourceSection ss) {
    Location loc = new Location();
    loc.setUri(ss.getSource().getURI().toString());
    loc.setRange(toRange(ss));
    return loc;
  }

  protected abstract Probe getProbe(String documentUri);

  protected abstract Collection<Probe> getProbes();

  public final List<? extends SymbolInformation> getSymbolInfo(final String documentUri) {
    Probe probe = getProbe(documentUri);
    ArrayList<SymbolInformation> results = new ArrayList<>();
    if (probe == null) {
      return results;
    }

    addAllSymbols(results, null, probe);
    return results;
  }

  protected abstract void addAllSymbols(
      List<SymbolInformation> results, String query, Probe probe);

  public final List<? extends SymbolInformation> getAllSymbolInfo(final String query) {
    Collection<Probe> probes = getProbes();

    ArrayList<SymbolInformation> results = new ArrayList<>();

    for (Probe probe : probes) {
      addAllSymbols(results, query, probe);
    }

    return results;
  }

  public abstract List<? extends Location> getDefinitions(final String docUri, final int line,
      final int character);

  public void reportError(final String msgStr) {
    MessageParams msg = new MessageParams();
    msg.setType(MessageType.Log);
    msg.setMessage(msgStr);

    client.logMessage(msg);

    ServerLauncher.logErr(msgStr);
  }

  public void reportDiagnostics(final List<Diagnostic> diagnostics,
      final String documentUri) {
    if (diagnostics != null) {
      PublishDiagnosticsParams result = new PublishDiagnosticsParams();
      result.setDiagnostics(diagnostics);
      result.setUri(documentUri);
      client.publishDiagnostics(result);
    }
  }

  public abstract List<Diagnostic> getDiagnostics(final String documentUri);

  public abstract List<int[]> getSemanticTokens(final String documentUri);

  public abstract List<Integer> makeRelative(List<int[]> tokens);

  public abstract CompletionList getCompletions(final String docUri, final int line,
      final int character);

  public abstract void getCodeLenses(final List<CodeLens> codeLenses,
      final String documentUri);
}
