package som.langserv.newspeak;

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
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.SymbolKind;
import org.graalvm.collections.EconomicMap;
import org.graalvm.collections.EconomicSet;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.Value;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.tools.nodes.Invocation;
import bd.tools.structure.StructuralProbe;
import som.Launcher;
import som.VM;
import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Parser.ParseError;
import som.compiler.SemanticDefinitionError;
import som.compiler.SourcecodeCompiler;
import som.compiler.Variable;
import som.interpreter.SomLanguage;
import som.interpreter.nodes.ArgumentReadNode.LocalArgumentReadNode;
import som.interpreter.nodes.ArgumentReadNode.NonLocalArgumentReadNode;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.LocalVariableNode;
import som.interpreter.nodes.NonLocalVariableNode;
import som.interpreter.nodes.dispatch.Dispatchable;
import som.interpreter.objectstorage.StorageAccessor;
import som.langserv.LanguageAdapter;
import som.langserv.ServerLauncher;
import som.langserv.structure.SemanticTokens;
import som.vm.Primitives;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;


/**
 * Provides Newspeak/SOMns specific functionality.
 */
public class NewspeakAdapter extends LanguageAdapter<NewspeakStructures> {

  private final static String CORE_LIB_PROP = "som.langserv.somns-core-lib";
  public final static String  CORE_LIB_PATH = System.getProperty(CORE_LIB_PROP);

  private final Map<String, NewspeakStructures> structuralProbes;
  private final SomCompiler                     compiler;

  public NewspeakAdapter() {
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

    NewspeakStructures primProbe =
        new NewspeakStructures(Source.newBuilder(SomLanguage.LANG_ID, "vmMirror", "vmMirror")
                                     .mimeType(SomLanguage.MIME_TYPE).build());
    for (SInvokable i : ps.getValues()) {
      primProbe.recordNewMethod(i.getIdentifier(), i);
    }

    structuralProbes.put("vmMirror", primProbe);
  }

  private VM initializePolyglot() {
    String coreLib = CORE_LIB_PATH;
    if (coreLib == null) {
      throw new IllegalArgumentException(
          "The " + CORE_LIB_PROP + " system property needs to be set. For instance: -D"
              + CORE_LIB_PROP + "=/SOMns/core-lib");
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
    NewspeakStructures probe;
    synchronized (structuralProbes) {
      probe = structuralProbes.get(docUriToNormalizedPath(docUri));
    }
    Lint.checkSends(structuralProbes, probe, diagnostics);
  }

  @Override
  protected NewspeakStructures getProbe(final String documentUri) {
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
  protected Collection<NewspeakStructures> getProbes() {
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

    NewspeakStructures newProbe = new NewspeakStructures(source);
    List<Diagnostic> diagnostics = newProbe.getDiagnostics();
    try {
      // clean out old structural data
      synchronized (structuralProbes) {
        structuralProbes.remove(path);
      }
      synchronized (newProbe) {
        MixinDefinition def = compiler.compileModule(source, newProbe);
        Lint.checkModuleName(path, def, diagnostics);
        Lint.checkLastChar(text, diagnostics);
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

    d.setRange(toRangeMax(e.getLine(), e.getColumn()));
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
  protected void addAllSymbols(final List<DocumentSymbol> results, final String query,
      final NewspeakStructures probe) {
    synchronized (probe) {
      EconomicSet<MixinDefinition> classes = probe.getClasses();
      for (MixinDefinition m : classes) {
        assert sameDocument(probe.getDocumentUri(), m.getSourceSection());
        addSymbolInfo(m, query, results);
      }

      EconomicSet<SInvokable> methods = probe.getMethods();
      for (SInvokable m : methods) {
        assert sameDocument(probe.getDocumentUri(), m.getSourceSection());

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
    return fuzzyMatch(m.getSignature().getString(), query);
  }

  private static boolean matchQuery(final String query, final MixinDefinition m) {
    return fuzzyMatch(m.getName().getString(), query);
  }

  private static boolean matchQuery(final String query, final SlotDefinition s) {
    return fuzzyMatch(s.getName().getString(), query);
  }

  private static DocumentSymbol getSymbolInfo(final SInvokable m) {
    DocumentSymbol sym = new DocumentSymbol();
    sym.setName(m.getSignature().toString());
    sym.setKind(SymbolKind.Method);
    if (null != m.getSourceSection()) {
      sym.setRange(toRange(m.getSourceSection()));
    }
    if (m.getHolderUnsafe() != null) {
      sym.setContainerName(m.getHolder().getName().getString());
    }
    return sym;
  }

  private static void addSymbolInfo(final MixinDefinition m, final String query,
      final List<DocumentSymbol> results) {
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

  private static DocumentSymbol getSymbolInfo(final SlotDefinition d,
      final MixinDefinition m) {
    DocumentSymbol sym = new DocumentSymbol();
    sym.setName(d.getName().getString());
    SymbolKind kind = m.isModule() ? SymbolKind.Constant
        : SymbolKind.Property;
    sym.setKind(kind);
    sym.setContainerName(m.getName().getString());
    return sym;
  }

  private static DocumentSymbol getSymbolInfo(final MixinDefinition m) {
    DocumentSymbol sym = new DocumentSymbol();
    sym.setName(m.getName().getString());
    SymbolKind kind = m.isModule() ? SymbolKind.Module
        : SymbolKind.Class;
    sym.setKind(kind);
    sym.setRange(toRange(m.getSourceSection()));

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
    NewspeakStructures probe = getProbe(docUri);
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
      for (NewspeakStructures s : structuralProbes.values()) {
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

    NewspeakStructures probe = getProbe(docUri);
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
      Collection<NewspeakStructures> probes = getProbes();

      for (NewspeakStructures s : probes) {
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
        final StructuralProbe<SSymbol, MixinDefinition, SInvokable, SlotDefinition, Variable> structuralProbe)
        throws bd.basic.ProgramDefinitionError {
      NewspeakParser parser =
          new NewspeakParser(source.getCharacters().toString(), source,
              (NewspeakStructures) structuralProbe, language);
      return compile(parser, source);
    }
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
  public void getCodeLenses(final List<CodeLens> codeLenses, final String documentUri) {
    NewspeakStructures probe = getProbe(documentUri);
    if (probe == null) {
      return;
    }

    for (MixinDefinition c : probe.getClasses()) {
      Minitest.checkForTests(c, codeLenses, documentUri);
    }
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

  @Override
  public List<Integer> makeRelative(final List<int[]> tokens) {
    return SemanticTokens.makeRelativeTo11(tokens);
  }
}
