package som.langserv.newspeak;

import static som.langserv.som.PositionConversion.toRange;
import static som.langserv.som.PositionConversion.toRangeMax;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.graalvm.collections.EconomicMap;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.Value;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

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
import som.interpreter.objectstorage.StorageAccessor;
import som.langserv.LanguageAdapter;
import som.langserv.lint.FileLinter;
import som.langserv.lint.LintEndsWithNewline;
import som.langserv.lint.LintUseNeedsDefine;
import som.langserv.lint.WorkspaceLinter;
import som.langserv.structure.DocumentStructures;
import som.vm.Primitives;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;


/**
 * Provides Newspeak/SOMns specific functionality.
 */
public class NewspeakAdapter extends LanguageAdapter {

  private final static String CORE_LIB_PROP = "som.langserv.somns-core-lib";
  public final static String  CORE_LIB_PATH = System.getProperty(CORE_LIB_PROP);

  private final SomCompiler compiler;

  public NewspeakAdapter() {
    super(
        new FileLinter[] {new LintEndsWithNewline(), new LintFileHasNSEnding()},
        new WorkspaceLinter[] {new LintUseNeedsDefine()});
    VM vm = initializePolyglot();
    this.compiler = new SomCompiler(vm.getLanguage());
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

    DocumentStructures vmStructures =
        new DocumentStructures("internal:vmMirror.ns", "internal:vmMirror.ns");
    NewspeakStructures primProbe =
        new NewspeakStructures(Source.newBuilder(SomLanguage.LANG_ID, "vmMirror", "vmMirror")
                                     .mimeType(SomLanguage.MIME_TYPE).build(),
            vmStructures);
    for (SInvokable i : ps.getValues()) {
      primProbe.recordNewMethod(i.getIdentifier(), i);
    }

    putStructures("internal:vmMirror.ns", vmStructures);
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
  public DocumentStructures parse(final String text, final String sourceUri)
      throws URISyntaxException {
    String path = docUriToNormalizedPath(sourceUri);
    Source source = Source.newBuilder(SomLanguage.LANG_ID, text, path)
                          .mimeType(SomLanguage.MIME_TYPE)
                          .uri(new URI(sourceUri).normalize()).build();

    DocumentStructures structures = new DocumentStructures(sourceUri, "file:" + path);
    NewspeakStructures newProbe = new NewspeakStructures(source, structures);

    try {
      compiler.compileModule(source, newProbe);
    } catch (ParseError e) {
      return toDiagnostics(e, structures);
    } catch (SemanticDefinitionError e) {
      return toDiagnostics(e, structures);
    } catch (Throwable e) {
      return toDiagnostics(e.getMessage(), structures);
    } finally {
      assert structures != null;
      putStructures(path, structures);
    }
    return structures;
  }

  private DocumentStructures toDiagnostics(final ParseError e,
      final DocumentStructures structures) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    d.setRange(toRangeMax(e.getLine(), e.getColumn()));
    d.setMessage(e.getMessage());
    d.setSource("Parser");

    structures.addDiagnostic(d);
    return structures;
  }

  private DocumentStructures toDiagnostics(final SemanticDefinitionError e,
      final DocumentStructures structures) {
    SourceSection source = e.getSourceSection();

    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);
    d.setRange(toRange(source));
    d.setMessage(e.getMessage());
    d.setSource("Parser");

    structures.addDiagnostic(d);
    return structures;
  }

  private DocumentStructures toDiagnostics(final String msg,
      final DocumentStructures structures) {
    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    d.setMessage(msg);
    d.setSource("Parser");

    structures.addDiagnostic(d);
    return structures;
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
}
