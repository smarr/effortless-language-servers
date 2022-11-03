package som.langserv.newspeak;

import static util.PositionConversion.toRange;
import static util.PositionConversion.toRangeMax;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.graalvm.collections.EconomicMap;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.Value;

import com.oracle.truffle.api.source.Source;

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
import som.langserv.lens.FileLens;
import som.langserv.lens.Minitest;
import som.langserv.lint.FileLinter;
import som.langserv.lint.LintEndsWithNewline;
import som.langserv.lint.LintFileHasNSEnding;
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
  private final VM          vm;

  public NewspeakAdapter() {
    super(
        new FileLinter[] {new LintEndsWithNewline(), new LintFileHasNSEnding()},
        new WorkspaceLinter[] {new LintUseNeedsDefine()},
        new FileLens[] {new Minitest()});
    this.compiler = new SomCompiler();
    VM.setCompiler(compiler);
    this.vm = initializePolyglot(this.compiler);
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

  private VM initializePolyglot(final SomCompiler compiler) {
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
      compiler.compileModule(source, vm.getLanguage(), newProbe);
      return structures;
    } catch (ParseError e) {
      return toErrorDiagnostics(e, toRangeMax(e.getLine(), e.getColumn()), structures);
    } catch (SemanticDefinitionError e) {
      return toErrorDiagnostics(e, toRange(e.getSourceSection()), structures);
    } catch (Throwable e) {
      return toErrorDiagnostics(e,
          new Range(new Position(0, 0), new Position(1, Short.MAX_VALUE)), structures);
    }
  }

  private DocumentStructures toErrorDiagnostics(final Throwable e, final Range range,
      final DocumentStructures structures) {
    Diagnostic d = new Diagnostic(range, e.getMessage(), DiagnosticSeverity.Error, "Parser");
    return updateDiagnostics(d, structures);
  }

  private final class SomCompiler extends SourcecodeCompiler {

    @Override
    public MixinDefinition compileModule(final Source source, final SomLanguage language,
        final StructuralProbe<SSymbol, MixinDefinition, SInvokable, SlotDefinition, Variable> structuralProbe)
        throws bd.basic.ProgramDefinitionError {
      NewspeakStructures probe = (NewspeakStructures) structuralProbe;
      if (probe == null) {
        String path = source.getPath();
        String sourceUri = source.getURI().toString();
        if (path == null) {
          path = source.getName();
        }

        DocumentStructures structures = new DocumentStructures(sourceUri, "file:" + path);
        probe = new NewspeakStructures(source, structures);
      }

      NewspeakParser parser =
          new NewspeakParser(source.getCharacters().toString(), source,
              probe, language, NewspeakAdapter.this);
      return compile(parser, source, language);
    }
  }
}
