package som.langserv.simple;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.SymbolInformation;
import org.graalvm.polyglot.Context;

import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.sl.SLLanguage;
import com.oracle.truffle.sl.parser.SLParseError;
import com.oracle.truffle.sl.parser.SimpleLanguageLexer;

import bd.source.SourceCoordinate;
import som.compiler.Parser.ParseError;
import som.compiler.SemanticDefinitionError;
import som.langserv.LanguageAdapter;


public class SimpleAdapter extends LanguageAdapter<SimpleStructures> {

  private final SLLanguage                    currentSLLanguageInstence;
  private final Map<String, SimpleStructures> structuralProbes;

  public SimpleAdapter() {
    currentSLLanguageInstence = initializePolyglot();
    this.structuralProbes = new HashMap<>();

  }

  private SLLanguage initializePolyglot() {
    Context context = Context.newBuilder("sl").in(System.in).out(System.out)
                             .options(new HashMap<>()).build();
    context.enter();
    context.eval("sl", "function main() {}");
    return SLLanguage.current();
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
    Source source = Source.newBuilder(SLLanguage.ID, text, path)
                          .mimeType(SLLanguage.MIME_TYPE)
                          .uri(new URI(sourceUri).normalize()).build();

    SimpleStructures newProbe = new SimpleStructures(source);
    List<Diagnostic> diagnostics = newProbe.getDiagnostics();
    try {
      // clean out old structural data
      synchronized (structuralProbes) {
        structuralProbes.remove(path);
      }
      synchronized (newProbe) {
        compileModule(source, newProbe);

      }
    } catch (ParseError e) {
      return toDiagnostics(e, diagnostics);
    } catch (SLParseError e) {
      return toDiagnostics(e, diagnostics);
    } catch (SemanticDefinitionError e) {
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

  private List<Diagnostic> toDiagnostics(final ParseError e,
      final List<Diagnostic> diagnostics) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    SourceCoordinate coord = e.getSourceCoordinate();
    d.setRange(toRangeMax(coord.startLine, coord.startColumn));
    d.setMessage(e.getMessage());
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  private List<Diagnostic> toDiagnostics(final SemanticDefinitionError e,
      final List<Diagnostic> diagnostics) {
    SourceSection source = e.getSourceSection();

    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);
    d.setRange(toRange(source));
    d.setMessage(e.getMessage());
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  private List<Diagnostic> toDiagnostics(final SLParseError e,
      final List<Diagnostic> diagnostics) {
    SourceSection source;
    Diagnostic d = new Diagnostic();
    try {
      source = e.getSourceSection();
      d.setRange(toRange(source));
    } catch (UnsupportedMessageException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    d.setSeverity(DiagnosticSeverity.Error);

    d.setMessage(e.getMessage());
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

  public void compileModule(final Source source, final SimpleStructures Probe)
      throws bd.basic.ProgramDefinitionError {
    SimpleLanguageLexer lexer =
        new SimpleLanguageLexer(CharStreams.fromString(source.getCharacters().toString()));
    SimpleParser parser = new SimpleParser(new CommonTokenStream(lexer), Probe,
        currentSLLanguageInstence, source);
    // parser.parse(Probe, currentSLLanguageInstence, source);

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
  protected void addAllSymbols(final List<SymbolInformation> results, final String query,
      final SimpleStructures probe) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<? extends Location> getDefinitions(final String docUri, final int line,
      final int character) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Integer> getTokenPositions(final String documentUri) {
    String path;
    try {
      path = docUriToNormalizedPath(documentUri);
      return getProbe(path).getTokenPositions();
    } catch (URISyntaxException e) {
      return null;
    }
  }

  @Override
  public CompletionList getCompletions(final String docUri, final int line,
      final int character) {
    // TODO Auto-generated method stub
    return null;
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
