package som.langserv.simple;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DocumentSymbol;

import com.oracle.truffle.sl.parser.SLParseError;

import simple.SimpleLanguageLexer;
import som.langserv.LanguageAdapter;
import som.langserv.structure.SemanticTokens;


public class SimpleAdapter extends LanguageAdapter<SimpleStructures> {

  private final Map<String, SimpleStructures> structuralProbes;

  public SimpleAdapter() {
    this.structuralProbes = new LinkedHashMap<>();
  }

  @Override
  public String getFileEnding() {
    return ".sl";
  }

  @Override
  public void lintSends(final String docUri, final List<Diagnostic> diagnostics)
      throws URISyntaxException {
    // TODO Auto-generated method stub

  }

  @Override
  public List<Diagnostic> parse(final String text, final String sourceUri)
      throws URISyntaxException {
    String path = docUriToNormalizedPath(sourceUri);

    SimpleStructures newProbe = new SimpleStructures(text.length(), sourceUri, "file:" + path);
    List<Diagnostic> diagnostics = newProbe.getDiagnostics();
    try {
      // clean out old structural data
      synchronized (structuralProbes) {
        structuralProbes.remove(path);
      }
      synchronized (newProbe) {
        parse(text, newProbe);
      }
    } catch (SLParseError e) {
      return toDiagnostics(e, diagnostics);
    } catch (Throwable e) {
      return toDiagnostics(e, diagnostics);
    } finally {
      // set new probe once done with everything
      synchronized (structuralProbes) {
        structuralProbes.put(path, newProbe);
      }
    }
    return diagnostics;
  }

  private List<Diagnostic> toDiagnostics(final SLParseError e,
      final List<Diagnostic> diagnostics) {
    String[] msgParts = e.format.split(":");
    String msg = msgParts[2].trim();

    Diagnostic d = new Diagnostic();
    d.setRange(som.langserv.som.PositionConversion.toRange(e.line, e.col, e.length));

    d.setSeverity(DiagnosticSeverity.Error);

    d.setMessage(msg);
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  private List<Diagnostic> toDiagnostics(final Throwable e,
      final List<Diagnostic> diagnostics) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    d.setMessage(e.getMessage());

    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  public void parse(final String source, final SimpleStructures probe) {
    SimpleLanguageLexer lexer =
        new SimpleLanguageLexer(CharStreams.fromString(source));
    SimpleParser parser = new SimpleParser(lexer, probe);

    parser.parse();
  }

  @Override
  public SimpleStructures getProbe(final String documentUri) {
    synchronized (structuralProbes) {
      try {
        return structuralProbes.get(docUriToNormalizedPath(documentUri));
      } catch (URISyntaxException e) {
        return null;
      }
    }
  }

  @Override
  protected Collection<SimpleStructures> getProbes() {
    synchronized (structuralProbes) {
      return new ArrayList<>(structuralProbes.values());
    }
  }

  @Override
  public List<? extends Location> getDefinitions(final String docUri, final int line,
      final int character) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<int[]> getSemanticTokens(final String documentUri) {
    String path;
    try {
      path = docUriToNormalizedPath(documentUri);
      return getProbe(path).getSemanticTokens();
    } catch (URISyntaxException e) {
      return null;
    }
  }

  @Override
  public List<Integer> makeRelative(final List<int[]> tokens) {
    return SemanticTokens.makeRelative(tokens, 1, 0);
  }

  @Override
  public CompletionList getCompletions(final String docUri, final int line,
      final int character) {
    List<CompletionItem> results = new ArrayList<>(0);
    for (SimpleStructures s : structuralProbes.values()) {
      for (DocumentSymbol m : s.getMethods()) {
        CompletionItem c = new CompletionItem();
        c.setDetail(m.getDetail());
        c.setKind(CompletionItemKind.Function);
        c.setLabel(m.getName());
        results.add(c);
      }
    }
    return new CompletionList(true, results);
  }

  @Override
  public void getCodeLenses(final List<CodeLens> codeLenses, final String documentUri) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<Diagnostic> getDiagnostics(final String documentUri) {
    String path;
    try {
      path = docUriToNormalizedPath(documentUri);
      return getProbe(path).getDiagnostics();
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

}
