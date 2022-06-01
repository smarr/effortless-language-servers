package som.langserv.som;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.SymbolKind;
import org.graalvm.collections.EconomicSet;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bdt.basic.ProgramDefinitionError;
import bdt.source.SourceCoordinate;
import bdt.tools.nodes.Invocation;
import bdt.tools.structure.StructuralProbe;
import som.langserv.LanguageAdapter;
import som.langserv.ServerLauncher;
import som.langserv.structure.DocumentStructures;
import trufflesom.compiler.Field;
import trufflesom.compiler.Parser;
import trufflesom.compiler.Parser.ParseError;
import trufflesom.compiler.SourcecodeCompiler;
import trufflesom.compiler.Variable;
import trufflesom.interpreter.Method;
import trufflesom.interpreter.SomLanguage;
import trufflesom.interpreter.nodes.ExpressionNode;
import trufflesom.interpreter.nodes.FieldNode.FieldWriteNode;
import trufflesom.interpreter.nodes.LocalVariableNode;
import trufflesom.interpreter.nodes.NonLocalVariableNode;
import trufflesom.vm.Universe;
import trufflesom.vmobjects.SClass;
import trufflesom.vmobjects.SInvokable;
import trufflesom.vmobjects.SSymbol;


public class SomAdapter extends LanguageAdapter {
  private final static String CORE_LIB_PROP = "som.langserv.som-core-lib";
  public final static String  CORE_LIB_PATH = System.getProperty(CORE_LIB_PROP);

  private final Map<String, SomStructures> structuralProbes;

  private Context context;

  private final ForkJoinPool pool;

  private final SomCompiler somCompiler;

  public SomAdapter() {
    this.structuralProbes = new LinkedHashMap<>();
    this.pool = new ForkJoinPool(1);
    this.somCompiler = new SomCompiler();

    ForkJoinTask<?> task = this.pool.submit(() -> initializePolyglot());
    task.join();
  }

  @Override
  public String getFileEnding() {
    return ".som";
  }

  private void initializePolyglot() {
    if (CORE_LIB_PATH == null) {
      throw new IllegalArgumentException(
          "The " + CORE_LIB_PROP + " system property needs to be set. For instance: -D"
              + CORE_LIB_PROP + "=/TruffleSOM/core-lib");
    }
    String[] args = new String[] {"-cp", CORE_LIB_PATH + "/Smalltalk"};

    Universe.setSourceCompiler(somCompiler);
    Builder builder = Universe.createContextBuilder();
    builder.arguments(SomLanguage.LANG_ID, args);
    context = builder.build();

    context.eval(SomLanguage.INIT);

    Universe.selfSource = SomLanguage.getSyntheticSource("self", "self");
    Universe.selfCoord = SourceCoordinate.createEmpty();

    context.enter();

    Universe.setupClassPath(constructClassPath(CORE_LIB_PATH));

    SomStructures systemClassProbe = new SomStructures(
        Source.newBuilder(SomLanguage.LANG_ID, "systemClasses", null).internal(true).build(),
        null, null);
    Universe.setStructuralProbe(systemClassProbe);
    structuralProbes.put("systemClasses", systemClassProbe);

    Universe.initializeObjectSystem();
    context.leave();
  }

  private String constructClassPath(final String rootPath) {
    List<File> allFolders = new ArrayList<>();

    File root = new File(rootPath);
    findClassPathFolders(root, allFolders);

    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (File f : allFolders) {
      if (first) {
        first = false;
      } else {
        builder.append(File.pathSeparator);
      }
      builder.append(f.toPath().toString());
    }

    return builder.toString();
  }

  private void findClassPathFolders(final File root, final List<File> allFolders) {
    for (File f : root.listFiles()) {
      if (f.isDirectory()) {
        allFolders.add(f);
        findClassPathFolders(f, allFolders);
      }
    }
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
  public ForkJoinTask<?> loadWorkspace(final String uri) throws URISyntaxException {
    if (uri == null) {
      return null;
    }

    URI workspaceUri = new URI(uri);
    File workspace = new File(workspaceUri);
    assert workspace.isDirectory();

    return pool.submit(() -> loadWorkspaceAndLint(workspace));
  }

  @Override
  protected void loadWorkspaceAndLint(final File workspace) {
    context.enter();
    try {
      super.loadWorkspaceAndLint(workspace);
    } finally {
      context.leave();
    }
  }

  @Override
  public DocumentStructures loadFile(final File f) throws IOException, URISyntaxException {
    byte[] content = Files.readAllBytes(f.toPath());
    String str = new String(content, StandardCharsets.UTF_8);
    String uri = f.toURI().toString();
    return parseSync(str, uri);
  }

  @Override
  public DocumentStructures parse(final String text, final String sourceUri) {
    try {
      return pool.submit(() -> parseEnterLeave(text, sourceUri)).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public DocumentStructures parseEnterLeave(final String text, final String sourceUri)
      throws URISyntaxException {
    try {
      context.enter();
      return parseSync(text, sourceUri);
    } finally {
      context.leave();
    }
  }

  public DocumentStructures parseSync(final String text, final String sourceUri)
      throws URISyntaxException {
    String path = docUriToNormalizedPath(sourceUri);
    Source source =
        Source.newBuilder(SomLanguage.LANG_ID, text, path).name(path)
              .mimeType(SomLanguage.MIME_TYPE)
              .uri(new URI(sourceUri).normalize()).build();

    SomStructures newProbe = new SomStructures(source, sourceUri, "file:" + path);
    List<Diagnostic> diagnostics = newProbe.getDiagnostics();
    try {
      // clean out old structural data
      synchronized (structuralProbes) {
        structuralProbes.remove(path);
      }

      try {
        SClass def = somCompiler.compileClass(text, source, newProbe);
        // SomLint.checkModuleName(path, def, diagnostics);
      } catch (ParseError e) {
        return toDiagnostics(e, diagnostics);
      } catch (Throwable e) {
        String msg = e.getMessage();
        if (msg == null) {
          msg = getStackTrace(e);
        }
        return toDiagnostics(msg, diagnostics);
      }
    } finally {
      // set new probe once done with everything
      synchronized (structuralProbes) {
        structuralProbes.put(path, newProbe);
      }
    }

    return diagnostics;
  }

  void addNewProbe(final Source source, final SomStructures probe) {
    synchronized (structuralProbes) {
      structuralProbes.put(source.getPath(), probe);
    }
  }

  private String getStackTrace(final Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }

  @Override
  protected void addAllSymbols(final List<DocumentSymbol> results, final String query,
      final SomStructures probe) {
    synchronized (probe) {
      EconomicSet<SClass> classes = probe.getClasses();
      for (SClass m : classes) {
        // assert sameDocument(documentUri, m.getSourceSection());
        addSymbolInfo(m, query, results);
      }

      EconomicSet<SInvokable> methods = probe.getMethods();
      for (SInvokable m : methods) {
        // does not hold, because we have all systems classes in one probe
        // assert sameDocument(probe.getDocumentUri(), m.getInvokable().getSourceSection());

        if (matchQuery(query, m)) {
          results.add(getSymbolInfo(m));
        }
      }

      EconomicSet<Field> fields = probe.getSlots();
      for (Field f : fields) {
        if (matchQuery(query, f)) {
          results.add(getSymbolInfo(probe.source, f));
        }
      }

      EconomicSet<Variable> variables = probe.getVariables();
      for (Variable v : variables) {
        if (v.name.getString().equals("self") || v.name.getString().equals("$blockSelf")) {
          // don't show self or $blockSelf
          continue;
        }
        if (matchQuery(query, v)) {
          results.add(getSymbolInfo(probe.source, v));
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
    } else if (node instanceof LocalVariableNode) {
      LocalVariableNode local = (LocalVariableNode) node;
      result.add(PositionConversion.getLocation(local.getSource(), local.getLocal().coord));
    } else if (node instanceof NonLocalVariableNode) {
      NonLocalVariableNode local = (NonLocalVariableNode) node;
      result.add(PositionConversion.getLocation(local.getSource(), local.getLocal().coord));
    } else if (node instanceof FieldWriteNode) {
      Method method = (Method) node.getRootNode();
      SInvokable si = getEncompassingInvokable(method, probe.getMethods());
      if (si == null) {
        si = getEncompassingInvokable(method, probe.getMethods());
      }
      int fieldIndex = ((FieldWriteNode) node).getFieldIndex();
      Field field = si.getHolder().getInstanceFieldDefinitions()[fieldIndex];
      result.add(PositionConversion.getLocation(si.getSource(), field.getSourceCoordinate()));
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
    d.setRange(PositionConversion.toRangeMax(e.getLine(), e.getColumn()));
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
    d.setRange(PositionConversion.toRangeMax(1, 1));

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

  private static DocumentSymbol getSymbolInfo(final Source source, final Field f) {
    DocumentSymbol sym = new DocumentSymbol();
    sym.setName(f.getName().getString());
    sym.setKind(SymbolKind.Field);
    sym.setRange(PositionConversion.toRange(source, f.getSourceCoordinate()));
    return sym;
  }

  private static DocumentSymbol getSymbolInfo(final Source source, final Variable v) {
    DocumentSymbol sym = new DocumentSymbol();
    sym.setName(v.name.getString());
    sym.setKind(SymbolKind.Variable);
    sym.setRange(PositionConversion.toRange(source, v.coord));
    return sym;
  }

  private static DocumentSymbol getSymbolInfo(final SInvokable d) {
    DocumentSymbol sym = new DocumentSymbol();
    sym.setName(d.getSignature().toString());
    sym.setKind(SymbolKind.Method);
    if (null != d.getSourceSection()) {
      sym.setRange(PositionConversion.toRange(d.getSourceSection()));
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

  private static DocumentSymbol getSymbolInfo(final SClass c) {
    DocumentSymbol sym = new DocumentSymbol();
    sym.setName(c.getName().getString());
    sym.setKind(SymbolKind.Module);
    if (c.getSourceSection() != null) {
      sym.setRange(PositionConversion.toRange(c.getSourceSection()));
    }
    // MixinDefinition outer = c.getOuterMixinDefinition();
    // if (outer != null) {
    // sym.setContainerName(outer.getName().getString());
    // }
    return sym;
  }

  private static void addSymbolInfo(final SClass c, final String query,
      final List<DocumentSymbol> results) {
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

  private static class SomCompiler extends SourcecodeCompiler {

    private boolean sourceIsForPath(final Source s, final String path) {
      if (s.getPath() != null) {
        return s.getPath().equals(path);
      }
      return s.getName().equals(path);
    }

    @Override
    public Parser<?> createParser(final String code, final Source source,
        final StructuralProbe<SSymbol, SClass, SInvokable, Field, Variable> probe) {
      SomStructures p = (SomStructures) probe;

      if (!sourceIsForPath(source, p.getPath())) {
        p = new SomStructures(source, null, "file:" + p.getPath());
      }

      return new SomParser(code, source, p);
    }

    public SClass compileClass(final String text, final Source source,
        final SomStructures probe) throws ProgramDefinitionError {
      Parser<?> parser = createParser(text, source, probe);
      return SourcecodeCompiler.compile(parser, null);
    }
  }
}
