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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;

import com.oracle.truffle.api.source.Source;

import bdt.basic.ProgramDefinitionError;
import bdt.source.SourceCoordinate;
import bdt.tools.structure.StructuralProbe;
import som.langserv.LanguageAdapter;
import som.langserv.lint.FileLinter;
import som.langserv.lint.LintEndsWithNewline;
import som.langserv.lint.LintFileHasNSEnding;
import som.langserv.lint.LintUseNeedsDefine;
import som.langserv.lint.WorkspaceLinter;
import som.langserv.structure.DocumentStructures;
import trufflesom.compiler.Field;
import trufflesom.compiler.Parser;
import trufflesom.compiler.Parser.ParseError;
import trufflesom.compiler.SourcecodeCompiler;
import trufflesom.compiler.Variable;
import trufflesom.interpreter.SomLanguage;
import trufflesom.vm.Classes;
import trufflesom.vm.Globals;
import trufflesom.vm.Universe;
import trufflesom.vmobjects.SClass;
import trufflesom.vmobjects.SInvokable;
import trufflesom.vmobjects.SSymbol;
import util.PositionConversion;


public class SomAdapter extends LanguageAdapter {
  private final static String CORE_LIB_PROP = "som.langserv.som-core-lib";
  public final static String  CORE_LIB_PATH = System.getProperty(CORE_LIB_PROP);

  private Context context;

  private final ForkJoinPool pool;

  private final SomCompiler somCompiler;

  private final SSymbol[] systemClassNames = new SSymbol[16];

  public SomAdapter() {
    super(
        new FileLinter[] {new LintEndsWithNewline(), new LintFileHasNSEnding()},
        new WorkspaceLinter[] {new LintUseNeedsDefine()});
    this.pool = new ForkJoinPool(1);
    this.somCompiler = new SomCompiler();

    ForkJoinTask<?> task =
        this.pool.submit(() -> context = initializePolyglot(somCompiler, systemClassNames));
    task.join();
  }

  @Override
  public String getFileEnding() {
    return ".som";
  }

  public static Context initializePolyglot(final SourcecodeCompiler somCompiler,
      final SSymbol[] systemClassNames) {
    if (CORE_LIB_PATH == null) {
      throw new IllegalArgumentException(
          "The " + CORE_LIB_PROP + " system property needs to be set. For instance: -D"
              + CORE_LIB_PROP + "=/TruffleSOM/core-lib");
    }
    String[] args = new String[] {"-cp", CORE_LIB_PATH + "/Smalltalk"};

    Universe.setSourceCompiler(somCompiler);
    Builder builder = Universe.createContextBuilder();
    builder.arguments(SomLanguage.LANG_ID, args);
    Context context = builder.build();

    context.eval(SomLanguage.INIT);

    Universe.selfSource = SomLanguage.getSyntheticSource("self", "self");
    Universe.selfCoord = SourceCoordinate.createEmpty();

    context.enter();

    Universe.setupClassPath(constructClassPath(CORE_LIB_PATH));

    SomStructures systemClassProbe = new SomStructures(
        Source.newBuilder(SomLanguage.LANG_ID, "systemClasses", null).internal(true).build(),
        null, null);
    Universe.setStructuralProbe(systemClassProbe);
    Universe.initializeObjectSystem();
    systemClassNames[0] = Classes.objectClass.getName();
    systemClassNames[1] = Classes.classClass.getName();
    systemClassNames[2] = Classes.classClass.getName();
    systemClassNames[3] = Classes.metaclassClass.getName();
    systemClassNames[4] = Classes.nilClass.getName();
    systemClassNames[5] = Classes.integerClass.getName();
    systemClassNames[6] = Classes.arrayClass.getName();
    systemClassNames[7] = Classes.methodClass.getName();
    systemClassNames[8] = Classes.symbolClass.getName();
    systemClassNames[9] = Classes.primitiveClass.getName();
    systemClassNames[10] = Classes.stringClass.getName();
    systemClassNames[11] = Classes.doubleClass.getName();
    systemClassNames[12] = Classes.booleanClass.getName();
    systemClassNames[13] = Classes.trueClass.getName();
    systemClassNames[14] = Classes.falseClass.getName();
    systemClassNames[15] = Classes.blockClasses[0].getName();

    context.leave();

    return context;
  }

  private static String constructClassPath(final String rootPath) {
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

  private static void findClassPathFolders(final File root, final List<File> allFolders) {
    for (File f : root.listFiles()) {
      if (f.isDirectory()) {
        allFolders.add(f);
        findClassPathFolders(f, allFolders);
      }
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

  private boolean isSystemClass(final SSymbol name) {
    for (var n : systemClassNames) {
      if (n == name) {
        return true;
      }
    }

    return false;
  }

  public DocumentStructures parseSync(final String text, final String sourceUri)
      throws URISyntaxException {
    String path = docUriToNormalizedPath(sourceUri);
    Source source = createSource(text, sourceUri, path);

    SomStructures newProbe = new SomStructures(source, sourceUri, "file:" + path);
    DocumentStructures structures = newProbe.getDocumentStructures();

    try {
      try {
        SClass def = somCompiler.compileClass(text, source, newProbe);
        if (!isSystemClass(def.getName())) {
          Globals.setGlobal(def.getName(), def);
        }
        // SomLint.checkModuleName(path, def, diagnostics);
      } catch (ParseError e) {
        return toDiagnostics(e, structures);
      } catch (Throwable e) {
        String msg = e.getMessage();
        if (msg == null) {
          msg = getStackTrace(e);
        }
        return toDiagnostics(msg, structures);
      }
    } finally {
      putStructures(path, structures);
    }

    return structures;
  }

  public static Source createSource(final String text, final String sourceUri,
      final String path)
      throws URISyntaxException {
    Source source =
        Source.newBuilder(SomLanguage.LANG_ID, text, path).name(path)
              .mimeType(SomLanguage.MIME_TYPE)
              .uri(new URI(sourceUri).normalize()).build();
    return source;
  }

  private String getStackTrace(final Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }

  private DocumentStructures toDiagnostics(final ParseError e,
      final DocumentStructures structures) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);
    d.setRange(PositionConversion.toRangeMax(e.getLine(), e.getColumn()));
    d.setMessage(e.toString());
    d.setSource("Parser");

    structures.addDiagnostic(d);
    return structures;
  }

  private DocumentStructures toDiagnostics(final String msg,
      final DocumentStructures structures) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    d.setMessage(msg == null ? "" : msg);
    d.setSource("Parser");
    d.setRange(PositionConversion.toRangeMax(1, 1));

    structures.addDiagnostic(d);
    return structures;
  }

  public static class SomCompiler extends SourcecodeCompiler {

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
