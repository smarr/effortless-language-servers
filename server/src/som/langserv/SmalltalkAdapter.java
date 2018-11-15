package som.langserv;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;

import com.oracle.truffle.api.source.Source;

import bd.basic.ProgramDefinitionError;
import trufflesom.compiler.Parser;
import trufflesom.compiler.Parser.ParseError;
import trufflesom.compiler.SourcecodeCompiler;
import trufflesom.interpreter.SomLanguage;
import trufflesom.tools.SourceCoordinate;
import trufflesom.vm.Universe;
import trufflesom.vmobjects.SClass;
import trufflesom.vmobjects.SInvokable;


public class SmalltalkAdapter extends Adapter {

  public final static String CORE_LIB_PATH =
      System.getProperty("trufflesom.langserv.core-lib");

  private final Map<String, SomStructures> structuralProbes;
  private final SmalltalkCompiler          compiler;
  private final Universe                   universe;
  private SClass                           current;

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
    if (CORE_LIB_PATH == null) {
      throw new IllegalArgumentException(
          "The trufflesom.langserv.core-lib system property needs to be set. For instance: -Dtrufflesom.langserv.core-lib=/TruffleSOM/core-lib");
    }
    String[] args = new String[] {"-cp", CORE_LIB_PATH + "/Smalltalk"};

    Builder builder = Universe.createContextBuilder(args);
    Context context = builder.build();

    context.eval(SomLanguage.INIT);

    Universe universe = SomLanguage.getCurrent();
    universe.setupClassPath(CORE_LIB_PATH + "/Smalltalk");
    universe.initializeObjectSystem();

    return universe;
  }

  @Override
  public void lintSends(final String docUri, final List<Diagnostic> diagnostics)
      throws URISyntaxException {
    SomStructures probe;
    synchronized (structuralProbes) {
      probe = structuralProbes.get(docUriToNormalizedPath(docUri));
    }
    SomLint.checkSends(structuralProbes, probe, diagnostics);
  }

  @Override
  public List<Diagnostic> parse(final String text, final String sourceUri)
      throws URISyntaxException {
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
          current = def;
          // SomLint.checkModuleName(path, def, diagnostics);
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
  public List<? extends SymbolInformation> getSymbolInfo(final String documentUri) {
    if (current == null) {
      return new ArrayList<>();
    }

    List<SymbolInformation> symbolInfos = new ArrayList<>();

    SymbolInformation clazz = new SymbolInformation();
    clazz.setKind(SymbolKind.Class);
    clazz.setName(current.getName().getString());
    clazz.setLocation(
        new Location(documentUri, new Range(new Position(0, 0), new Position(0, 1))));
    symbolInfos.add(clazz);

    List<SInvokable> invokables = new ArrayList<>();
    for (int i = 0; i < current.getNumberOfInstanceInvokables(); i++) {
      invokables.add(current.getInstanceInvokable(i));
    }

    for (SInvokable invokable : invokables) {
      SymbolInformation symbolInfo = new SymbolInformation();
      symbolInfo.setName(invokable.getSignature().getString());
      symbolInfo.setKind(SymbolKind.Method);
      symbolInfo.setContainerName(clazz.getName());
      symbolInfo.setLocation(
          new Location(documentUri, new Range(new Position(1, 0), new Position(1, 1))));

      symbolInfos.add(symbolInfo);
    }

    return symbolInfos;
  }

  @Override
  public List<? extends SymbolInformation> getAllSymbolInfo(final String query) {
    if (current == null) {
      return new ArrayList<>();
    }

    List<SymbolInformation> symbolInfos = new ArrayList<>();

    List<SInvokable> invokables = new ArrayList<>();
    for (int i = 0; i < current.getNumberOfInstanceInvokables(); i++) {
      invokables.add(current.getInstanceInvokable(i));
    }

    for (SInvokable invokable : invokables) {
      SymbolInformation symbolInfo = new SymbolInformation();
      symbolInfo.setName(invokable.getSignature().getString());
      symbolInfo.setKind(SymbolKind.Method);

      symbolInfos.add(symbolInfo);
    }

    // return symbolInfos;
    return new ArrayList<>();
  }

  @Override
  public List<? extends Location> getDefinitions(final String docUri, final int line,
      final int character) {
    if (current == null) {
      return new ArrayList<>();
    }

    List<SymbolInformation> symbolInfos = new ArrayList<>();

    List<SInvokable> invokables = new ArrayList<>();
    for (int i = 0; i < current.getNumberOfInstanceInvokables(); i++) {
      invokables.add(current.getInstanceInvokable(i));
    }

    for (SInvokable invokable : invokables) {
      SymbolInformation symbolInfo = new SymbolInformation();
      symbolInfo.setName(invokable.getSignature().getString());
      symbolInfo.setKind(SymbolKind.Method);

      symbolInfos.add(symbolInfo);
    }

    // return symbolInfos;
    return new ArrayList<>();
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

  private List<Diagnostic> toDiagnostics(final ParseError e,
      final List<Diagnostic> diagnostics) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    SourceCoordinate coord = e.getSourceCoordinate();
    d.setRange(toRangeMax(coord.startLine, coord.startColumn));
    d.setMessage(e.toString());
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
    public SClass compileClass(final Source source, final Universe universe)
        throws ProgramDefinitionError {
      Parser parser = new Parser(source.getReader(), source.getLength(), source, universe);

      return compile(parser, null, universe);
    }

  }
}
