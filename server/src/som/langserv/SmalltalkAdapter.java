package som.langserv;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.SymbolInformation;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.PolyglotEngine;

import trufflesom.compiler.Parser;
import trufflesom.compiler.SourcecodeCompiler;
import trufflesom.compiler.Parser.ParseError;
import trufflesom.interpreter.SomLanguage;
import trufflesom.vm.Universe;
import trufflesom.vmobjects.SClass;

import com.oracle.truffle.api.vm.PolyglotEngine.Builder;
import com.oracle.truffle.api.vm.PolyglotEngine.Language;

import bd.basic.ProgramDefinitionError;

public class SmalltalkAdapter extends Adapter {

  public final static String CORE_LIB_PATH = System.getProperty("smalltalk.core-lib");

  private final Map<String, SomStructures> structuralProbes;
  private final SmalltalkCompiler          compiler;
  private final Universe                   universe;

  public SmalltalkAdapter() {
    this.universe = initializePolyglot();
    this.compiler = new SmalltalkCompiler();
    this.structuralProbes = new HashMap<>();
  }

  @Override
  public String getFileEnding() {
    return ".som";
  }

  private Universe initializePolyglot() {
    Universe universe = new Universe(new SomLanguage());

    Builder builder = PolyglotEngine.newBuilder();
    builder.config(SomLanguage.MIME_TYPE, "vm-arguments", universe);

    PolyglotEngine engine = builder.build();
    engine.getRuntime().getInstruments().values().forEach(i -> i.setEnabled(false));

    // Trigger object system initialization
//    Map<String, ? extends Language> langs = engine.getLanguages();
//    langs.get(SomLanguage.MIME_TYPE).getGlobalObject();

    return universe;
  }

  @Override
  public void lintSends(String docUri, List<Diagnostic> diagnostics) throws URISyntaxException {
    SomStructures probe;
    synchronized (structuralProbes) {
      probe = structuralProbes.get(docUriToNormalizedPath(docUri));
    }
    SomLint.checkSends(structuralProbes, probe, diagnostics);
  }

  @Override
  public List<Diagnostic> parse(String text, String sourceUri) throws URISyntaxException {
    String path = docUriToNormalizedPath(sourceUri);
    Source source = Source.newBuilder(text).name(path).mimeType(SomLanguage.MIME_TYPE)
                          .uri(new URI(sourceUri).normalize()).build();

    SomStructures newProbe = new SomStructures(source);
    List<Diagnostic> diagnostics = newProbe.getDiagnostics();
    try {
      // clean out old structural data
      synchronized (structuralProbes) {
        structuralProbes.remove(path);
      }
      synchronized (newProbe) {
        try {
          SClass def = compiler.compileClass(source, universe);
//          SomLint.checkModuleName(path, def, diagnostics);
        } catch (ParseError e) {
          return toDiagnostics(e, diagnostics);
        } catch (Throwable e) {
          return toDiagnostics(e.getMessage(), diagnostics);
        }
      }
    } finally {
      // set new probe once done with everything
      synchronized (structuralProbes) {
        structuralProbes.put(path, newProbe);
      }
    }
    return diagnostics;
  }

  @Override
  public List<? extends SymbolInformation> getSymbolInfo(String documentUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<? extends SymbolInformation> getAllSymbolInfo(String query) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<? extends Location> getDefinitions(String docUri, int line, int character) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CompletionList getCompletions(String docUri, int line, int character) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void getCodeLenses(List<CodeLens> codeLenses, String documentUri) {
    // TODO Auto-generated method stub
  }

  private List<Diagnostic> toDiagnostics(final ParseError e,
      final List<Diagnostic> diagnostics) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    // TODO: source coords for errors
//    SourceCoordinate coord = e.getSourceCoordinate();
    d.setMessage(e.getMessage());
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  private List<Diagnostic> toDiagnostics(final String msg,
      final List<Diagnostic> diagnostics) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    d.setMessage(msg == null ? "" : msg);
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  private static final class SmalltalkCompiler extends SourcecodeCompiler {

    // TODO: add structural probes into this later...
    public SClass compileClass(final Source source, final Universe universe) throws ProgramDefinitionError {
      Parser parser = new Parser(source.getReader(), source.getLength(), source, universe);

      return compile(parser, null, universe);
    }

  }
}
