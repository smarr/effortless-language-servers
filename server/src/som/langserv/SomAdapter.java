package som.langserv;

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
import org.graalvm.collections.EconomicMap;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.Value;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.source.SourceCoordinate;
import bd.tools.nodes.Invocation;
import som.Launcher;
import som.VM;
import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Parser.ParseError;
import som.compiler.SemanticDefinitionError;
import som.compiler.SourcecodeCompiler;
import som.interpreter.SomLanguage;
import som.interpreter.nodes.ArgumentReadNode.LocalArgumentReadNode;
import som.interpreter.nodes.ArgumentReadNode.NonLocalArgumentReadNode;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.LocalVariableNode;
import som.interpreter.nodes.NonLocalVariableNode;
import som.interpreter.nodes.dispatch.Dispatchable;
import som.interpreter.objectstorage.StorageAccessor;
import som.vm.Primitives;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;
import tools.language.StructuralProbe;


public class SomAdapter extends LanguageAdapter {

  public final static String CORE_LIB_PATH = System.getProperty("som.langserv.core-lib");

  private final Map<String, SomStructures> structuralProbes;
  private final SomCompiler                compiler;

  public SomAdapter() {
    VM vm = initializePolyglot();
    this.compiler = new SomCompiler(vm.getLanguage());
    this.structuralProbes = new HashMap<>();
    registerVmMirrorPrimitives(vm);
  }

  @Override
  public String getFileEnding() {
    return ".ns";
  }

  private void registerVmMirrorPrimitives(final VM vm) {
    Primitives prims = new Primitives(vm.getLanguage());

    @SuppressWarnings({"rawtypes", "unchecked"})
    EconomicMap<SSymbol, SInvokable> ps = (EconomicMap) prims.takeVmMirrorPrimitives();

    SomStructures primProbe =
        new SomStructures(Source.newBuilder(SomLanguage.LANG_ID, "vmMirror", "vmMirror")
                                .mimeType(SomLanguage.MIME_TYPE).build());
    for (SInvokable i : ps.getValues()) {
      primProbe.recordNewMethod(i);
    }

    structuralProbes.put("vmMirror", primProbe);
  }

  private VM initializePolyglot() {
    String coreLib = CORE_LIB_PATH;
    if (coreLib == null) {
      throw new IllegalArgumentException(
          "The som.langserv.core-lib system property needs to be set. For instance: -Dsom.langserv.core-lib=/SOMns/core-lib");
    }

    String[] args = new String[] {"--kernel", coreLib + "/Kernel.ns",
        "--platform", coreLib + "/Platform.ns", coreLib + "/Hello.ns"};

    Builder builder = Launcher.createContextBuilder(args);
    Context context = builder.build();

    // Needed to be able to execute SOMns initialization
    StorageAccessor.initAccessors();

    // Initialize SomLanguage object
    Value trueVal = context.eval(Launcher.INIT);
    assert trueVal.as(
        Boolean.class) : "INIT is exected to return true after initializing the Context";

    return SomLanguage.getCurrent().getVM();
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

  private SomStructures getProbe(final String documentUri) {
    synchronized (structuralProbes) {
      try {
        return structuralProbes.get(docUriToNormalizedPath(documentUri));
      } catch (URISyntaxException e) {
        return null;
      }
    }
  }

  /** Create a copy to work on safely. */
  private Collection<SomStructures> getProbes() {
    synchronized (structuralProbes) {
      return new ArrayList<>(structuralProbes.values());
    }
  }

  @Override
  public List<Diagnostic> parse(final String text, final String sourceUri)
      throws URISyntaxException {
    String path = docUriToNormalizedPath(sourceUri);
    Source source = Source.newBuilder(SomLanguage.LANG_ID, text, path)
                          .mimeType(SomLanguage.MIME_TYPE)
                          .uri(new URI(sourceUri).normalize()).build();

    SomStructures newProbe = new SomStructures(source);
    List<Diagnostic> diagnostics = newProbe.getDiagnostics();
    try {
      // clean out old structural data
      synchronized (structuralProbes) {
        structuralProbes.remove(path);
      }
      synchronized (newProbe) {
        MixinDefinition def = compiler.compileModule(source, newProbe);
        SomLint.checkModuleName(path, def, diagnostics);
      }
    } catch (ParseError e) {
      return toDiagnostics(e, diagnostics);
    } catch (SemanticDefinitionError e) {
      return toDiagnostics(e, diagnostics);
    } catch (Throwable e) {
      return toDiagnostics(e.getMessage(), diagnostics);
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

  private List<Diagnostic> toDiagnostics(final String msg,
      final List<Diagnostic> diagnostics) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    d.setMessage(msg);
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  @Override
  public List<? extends SymbolInformation> getSymbolInfo(final String documentUri) {
    SomStructures probe = getProbe(documentUri);
    ArrayList<SymbolInformation> results = new ArrayList<>();
    if (probe == null) {
      return results;
    }

    addAllSymbols(results, null, probe, documentUri);
    return results;
  }

  @Override
  public List<? extends SymbolInformation> getAllSymbolInfo(final String query) {
    Collection<SomStructures> probes = getProbes();

    ArrayList<SymbolInformation> results = new ArrayList<>();

    for (SomStructures probe : probes) {
      addAllSymbols(results, query, probe, probe.getDocumentUri());
    }

    return results;
  }

  private void addAllSymbols(final ArrayList<SymbolInformation> results, final String query,
      final SomStructures probe, final String documentUri) {
    synchronized (probe) {
      Set<MixinDefinition> classes = probe.getClasses();
      for (MixinDefinition m : classes) {
        assert sameDocument(documentUri, m.getSourceSection());
        addSymbolInfo(m, query, results);
      }

      Set<SInvokable> methods = probe.getMethods();
      for (SInvokable m : methods) {
        assert sameDocument(documentUri, m.getSourceSection());

        if (matchQuery(query, m)) {
          results.add(getSymbolInfo(m));
        }
      }
    }
  }

  private boolean sameDocument(final String documentUri, final SourceSection ss) {
    if (documentUri == null) {
      return ss == null;
    }

    if (ss == null) {
      return documentUri.endsWith("vmMirror");
    }

    try {
      return ss.getSource().getURI().getPath().equals(new URI(documentUri).getPath());
    } catch (URISyntaxException e) {
      return false;
    }
  }

  private static boolean matchQuery(final String query, final SInvokable m) {
    return SomStructures.fuzzyMatches(m.getSignature().getString(), query);
  }

  private static boolean matchQuery(final String query, final MixinDefinition m) {
    return SomStructures.fuzzyMatches(m.getName().getString(), query);
  }

  private static boolean matchQuery(final String query, final SlotDefinition s) {
    return SomStructures.fuzzyMatches(s.getName().getString(), query);
  }

  private static SymbolInformation getSymbolInfo(final SInvokable m) {
    SymbolInformation sym = new SymbolInformation();
    sym.setName(m.getSignature().toString());
    sym.setKind(SymbolKind.Method);
    if (null != m.getSourceSection()) {
      sym.setLocation(getLocation(m.getSourceSection()));
    }
    if (m.getHolderUnsafe() != null) {
      sym.setContainerName(m.getHolder().getName().getString());
    }
    return sym;
  }

  private static void addSymbolInfo(final MixinDefinition m, final String query,
      final ArrayList<SymbolInformation> results) {
    if (matchQuery(query, m)) {
      results.add(getSymbolInfo(m));
    }

    // We add the slots here, because we have more context at this point
    for (Dispatchable d : m.getInstanceDispatchables().getValues()) {
      // needs to be exact test to avoid duplicate info
      if (d.getClass() == SlotDefinition.class) {
        if (matchQuery(query, (SlotDefinition) d)) {
          results.add(getSymbolInfo((SlotDefinition) d, m));
        }
      }
    }
  }

  private static SymbolInformation getSymbolInfo(final SlotDefinition d,
      final MixinDefinition m) {
    SymbolInformation sym = new SymbolInformation();
    sym.setName(d.getName().getString());
    SymbolKind kind = m.isModule() ? SymbolKind.Constant
        : SymbolKind.Property;
    sym.setKind(kind);
    sym.setLocation(getLocation(d.getSourceSection()));
    sym.setContainerName(m.getName().getString());
    return sym;
  }

  private static SymbolInformation getSymbolInfo(final MixinDefinition m) {
    SymbolInformation sym = new SymbolInformation();
    sym.setName(m.getName().getString());
    SymbolKind kind = m.isModule() ? SymbolKind.Module
        : SymbolKind.Class;
    sym.setKind(kind);
    sym.setLocation(getLocation(m.getSourceSection()));

    MixinDefinition outer = m.getOuterMixinDefinition();
    if (outer != null) {
      sym.setContainerName(outer.getName().getString());
    }
    return sym;
  }

  @Override
  public List<? extends Location> getDefinitions(final String docUri,
      final int line, final int character) {
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
    } else if (node instanceof NonLocalVariableNode) {
      result.add(getLocation(((NonLocalVariableNode) node).getLocal().source));
    } else if (node instanceof LocalVariableNode) {
      result.add(getLocation(((LocalVariableNode) node).getLocal().source));
    } else if (node instanceof LocalArgumentReadNode) {
      result.add(getLocation(((LocalArgumentReadNode) node).getArg().source));
    } else if (node instanceof NonLocalArgumentReadNode) {
      result.add(getLocation(((NonLocalArgumentReadNode) node).getArg().source));
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

  private static final class SomCompiler extends SourcecodeCompiler {

    public SomCompiler(final SomLanguage language) {
      super(language);
      assert language != null;
    }

    @Override
    public MixinDefinition compileModule(final Source source,
        final StructuralProbe structuralProbe) throws bd.basic.ProgramDefinitionError {
      SomParser parser = new SomParser(source.getCharacters().toString(), source.getLength(),
          source, (SomStructures) structuralProbe, language);
      return compile(parser, source);
    }
  }

  @Override
  public void getCodeLenses(final List<CodeLens> codeLenses,
      final String documentUri) {
    String path;
    try {
      path = docUriToNormalizedPath(documentUri);
    } catch (URISyntaxException e) {
      return;
    }

    SomStructures probe;
    synchronized (path) {
      probe = structuralProbes.get(path);
    }

    if (probe != null) {
      for (MixinDefinition c : probe.getClasses()) {
        SomMinitest.checkForTests(c, codeLenses, documentUri);
      }
    }
  }
}
