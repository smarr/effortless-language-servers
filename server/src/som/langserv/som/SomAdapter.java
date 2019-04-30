package som.langserv.som;

import static som.langserv.Matcher.fuzzyMatch;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;
import org.graalvm.collections.EconomicSet;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.basic.ProgramDefinitionError;
import bd.source.SourceCoordinate;
import bd.tools.nodes.Invocation;
import bd.tools.structure.StructuralProbe;
import som.langserv.LanguageAdapter;
import som.langserv.ServerLauncher;
import trufflesom.compiler.Field;
import trufflesom.compiler.Parser.ParseError;
import trufflesom.compiler.SourcecodeCompiler;
import trufflesom.compiler.Variable;
import trufflesom.interpreter.Method;
import trufflesom.interpreter.SomLanguage;
import trufflesom.interpreter.nodes.ExpressionNode;
import trufflesom.interpreter.nodes.FieldNode.FieldWriteNode;
import trufflesom.interpreter.nodes.UninitializedVariableNode.UninitializedVariableWriteNode;
import trufflesom.vm.Universe;
import trufflesom.vmobjects.SClass;
import trufflesom.vmobjects.SInvokable;
import trufflesom.vmobjects.SSymbol;


public class SomAdapter extends LanguageAdapter<SomStructures> {

  public final static String CORE_LIB_PATH =
      System.getProperty("som.langserv.som-core-lib");

  private final Map<String, SomStructures> structuralProbes;
  private final TruffleSomCompiler         compiler;
  private final Universe                   universe;

  public SomAdapter() {
    this.structuralProbes = new HashMap<>();
    this.universe = initializePolyglot();
    this.compiler = new TruffleSomCompiler(universe.getLanguage());
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

    Universe universe = SomLanguage.getCurrent().getUniverse();
    universe.setupClassPath(CORE_LIB_PATH + "/Smalltalk");

    SomStructures systemClassProbe = new SomStructures(
        Source.newBuilder(SomLanguage.SOM, "systemClasses", null).internal(true).build());
    universe.setSystemClassProbe(systemClassProbe);
    structuralProbes.put("systemClasses", systemClassProbe);

    universe.initializeObjectSystem();

    return universe;
  }

  @Override
  public void lintSends(final String docUri, final List<Diagnostic> diagnostics)
      throws URISyntaxException {
    // TODO: implement linting
    SomStructures probe;
    synchronized (structuralProbes) {
      probe = structuralProbes.get(docUriToNormalizedPath(docUri));
    }
    // SomLint.checkSends(structuralProbes, probe, diagnostics);
  }

  @Override
  protected SomStructures getProbe(final String documentUri) {
    synchronized (structuralProbes) {
      try {
        return structuralProbes.get(docUriToNormalizedPath(documentUri));
      } catch (URISyntaxException e) {
        return null;
      }
    }
  }

  /** Create a copy to work on safely. */
  @Override
  protected Collection<SomStructures> getProbes() {
    synchronized (structuralProbes) {
      return new ArrayList<>(structuralProbes.values());
    }
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
          SClass def = compiler.compileClass(source, universe, newProbe);
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
  protected void addAllSymbols(final List<SymbolInformation> results, final String query,
      final SomStructures probe) {
    synchronized (probe) {
      EconomicSet<SClass> classes = probe.getClasses();
      for (SClass m : classes) {
        // assert sameDocument(documentUri, m.getSourceSection());
        addSymbolInfo(m, query, results);
      }

      EconomicSet<SInvokable> methods = probe.getMethods();
      for (SInvokable m : methods) {
        assert sameDocument(probe.getDocumentUri(), m.getInvokable().getSourceSection());

        if (matchQuery(query, m)) {
          results.add(getSymbolInfo(m));
        }
      }

      EconomicSet<Field> fields = probe.getSlots();
      for (Field f : fields) {
        if (matchQuery(query, f)) {
          results.add(getSymbolInfo(f));
        }
      }

      EconomicSet<Variable> variables = probe.getVariables();
      for (Variable v : variables) {
        if (matchQuery(query, v)) {
          results.add(getSymbolInfo(v));
        }
      }
    }
  }

  private boolean sameDocument(final String documentUri, final SourceSection ss) {
    if (documentUri == null) {
      return ss == null;
    }

    try {
      return ss.getSource().getURI().getPath().equals(new URI(documentUri).getPath());
    } catch (URISyntaxException e) {
      return false;
    }
  }

  private static boolean matchQuery(final String query, final Field f) {
    return fuzzyMatch(f.getName().getString(), query);
  }

  private static boolean matchQuery(final String query, final Variable v) {
    return fuzzyMatch(v.name.getString(), query);
  }

  private static boolean matchQuery(final String query, final SInvokable m) {
    return fuzzyMatch(m.getSignature().getString(), query);
  }

  private static boolean matchQuery(final String query, final SClass c) {
    return fuzzyMatch(c.getName().getString(), query);
  }

  @Override
  public List<? extends Location> getDefinitions(final String docUri, final int line,
      final int character) {
    ArrayList<Location> result = new ArrayList<>();
    SomStructures probe = getProbe(docUri);
    if (probe == null) {
      return result;
    }

    // +1 to get to one based index
    ExpressionNode node = probe.getElementAt(line + 1, character);

    if (node == null) {
      return result;
    }

    if (ServerLauncher.DEBUG) {
      reportError(
          "Node at " + (line + 1) + ":" + character + " " + node.getClass().getSimpleName());
    }

    if (node instanceof Invocation<?>) {
      @SuppressWarnings("unchecked")
      SSymbol name = ((Invocation<SSymbol>) node).getInvocationIdentifier();
      addAllDefinitions(result, name);
    } else if (node instanceof UninitializedVariableWriteNode) {
      result.add(getLocation(((UninitializedVariableWriteNode) node).getLocal().source));
    } else if (node instanceof FieldWriteNode) {
      Method method = (Method) node.getRootNode();
      SInvokable si = getEncompassingInvokable(method, probe.getMethods());
      if (si == null) {
        si = getEncompassingInvokable(method, probe.getMethods());
      }
      int fieldIndex = ((FieldWriteNode) node).getFieldIndex();
      Field field = si.getHolder().getInstanceFieldDefinitions()[fieldIndex];
      result.add(getLocation(field.getSourceSection()));
    } else {
      if (ServerLauncher.DEBUG) {
        reportError("GET DEFINITION, unsupported node: " + node.getClass().getSimpleName());
      }
    }
    return result;
  }

  private void addAllDefinitions(final ArrayList<Location> result, final SSymbol name) {
    synchronized (structuralProbes) {
      for (SomStructures s : structuralProbes.values()) {
        s.getDefinitionsFor(name, result);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public CompletionList getCompletions(final String docUri, final int line,
      final int character) {
    CompletionList result = new CompletionList();
    result.setIsIncomplete(true);

    SomStructures probe = getProbe(docUri);
    if (probe == null) {
      return result;
    }

    // TODO: this expects that this can be parsed without issues...
    // +1 to get to one based index, - 1 to get back into the element
    ExpressionNode node = probe.getElementAt(line + 1, Math.max(character - 1, 0));
    if (node == null) {
      return result;
    }

    SSymbol sym = null;
    if (node instanceof Invocation<?>) {
      sym = ((Invocation<SSymbol>) node).getInvocationIdentifier();
    } else {
      if (ServerLauncher.DEBUG) {
        reportError("GET COMPLETIONS, unsupported node: " + node.getClass().getSimpleName());
      }
    }

    if (sym != null) {
      Set<CompletionItem> completion = new HashSet<>();
      Collection<SomStructures> probes = getProbes();

      for (SomStructures s : probes) {
        s.getCompletions(sym, completion);
      }
      result.setItems(new ArrayList<>(completion));
    }

    return result;
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

  // private static SymbolInformation getSymbolInfo(final SInvokable d,
  // final SClass c) {
  // SymbolInformation sym = new SymbolInformation();
  // sym.setName(d.getInvokable().getName());
  // sym.setKind(SymbolKind.Method);
  // if (null != d.getInvokable().getSourceSection()) {
  // sym.setLocation(getLocation(d.getInvokable().getSourceSection()));
  // }
  // if (null != c) {
  // sym.setContainerName(c.getName().getString());
  // }
  // return sym;
  // }

  private static SymbolInformation getSymbolInfo(final Field f) {
    SymbolInformation sym = new SymbolInformation();
    sym.setName(f.getName().getString());
    sym.setKind(SymbolKind.Field);
    sym.setLocation(getLocation(f.getSourceSection()));
    return sym;
  }

  private static SymbolInformation getSymbolInfo(final Variable v) {
    SymbolInformation sym = new SymbolInformation();
    sym.setName(v.name.getString());
    sym.setKind(SymbolKind.Variable);
    sym.setLocation(getLocation(v.source));
    return sym;
  }

  private static SymbolInformation getSymbolInfo(final SInvokable d) {
    SymbolInformation sym = new SymbolInformation();
    sym.setName(d.getSignature().toString());
    sym.setKind(SymbolKind.Method);
    if (null != d.getSourceSection()) {
      sym.setLocation(getLocation(d.getSourceSection()));
    }
    if (null != d.getHolder()) {
      String holderName = d.getHolder().getName().getString();
      if (holderName.endsWith(" class")) {
        holderName = holderName.substring(0, holderName.length() - 6);
      }
      sym.setContainerName(holderName);
    }
    return sym;
  }

  private static SymbolInformation getSymbolInfo(final SClass c) {
    SymbolInformation sym = new SymbolInformation();
    sym.setName(c.getName().getString());
    sym.setKind(SymbolKind.Module);
    if (c.getSourceSection() != null) {
      sym.setLocation(getLocation(c.getSourceSection()));
    }
    // MixinDefinition outer = c.getOuterMixinDefinition();
    // if (outer != null) {
    // sym.setContainerName(outer.getName().getString());
    // }
    return sym;
  }

  private static void addSymbolInfo(final SClass c, final String query,
      final List<SymbolInformation> results) {
    if (matchQuery(query, c)) {
      results.add(getSymbolInfo(c));
    }
  }

  private static SInvokable getEncompassingInvokable(final Method method,
      final EconomicSet<SInvokable> invokables) {
    for (SInvokable i : invokables) {
      if (i.getInvokable().equals(method)) {
        return i;
      }
    }
    return null;
  }

  private final class TruffleSomCompiler extends SourcecodeCompiler {

    public TruffleSomCompiler(final SomLanguage language) {
      super(language);
    }

    public SClass compileClass(final Source source, final Universe universe,
        final StructuralProbe<SSymbol, SClass, SInvokable, Field, Variable> structuralProbe)
        throws ProgramDefinitionError {
      SomParser parser = new SomParser(source.getReader(), source.getLength(),
          source, (SomStructures) structuralProbe, universe);

      return compile(parser, null, universe);
    }

  }
}
