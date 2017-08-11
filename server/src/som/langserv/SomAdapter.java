package som.langserv;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;
import org.eclipse.lsp4j.services.LanguageClient;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.vm.PolyglotEngine;
import com.oracle.truffle.api.vm.PolyglotEngine.Builder;

import som.VM;
import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Parser.ParseError;
import som.compiler.ProgramDefinitionError;
import som.compiler.ProgramDefinitionError.SemanticDefinitionError;
import som.compiler.SourcecodeCompiler;
import som.interpreter.SomLanguage;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.MessageSendNode.AbstractUninitializedMessageSendNode;
import som.interpreter.nodes.dispatch.Dispatchable;
import som.vm.VmOptions;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;
import tools.SourceCoordinate;
import tools.language.StructuralProbe;

public class SomAdapter {

  private final Map<String, SomStructures> structuralProbes = new HashMap<>();
  private final SomCompiler compiler;

  private LanguageClient client;

  public SomAdapter() {
    VM vm = initializePolyglot();
    this.compiler = new SomCompiler(vm.getLanguage());
  }

  public void connect(final LanguageClient client) {
    this.client = client;
  }

  private VM initializePolyglot() {
    String coreLib = System.getProperty("som.langserv.core-lib");
    if (coreLib == null) {
      throw new IllegalArgumentException("The som.langserv.core-lib system property needs to be set. For instance: -Dsom.langserv.core-lib=/SOMns/core-lib");
    }

    String[] args = new String[] {"--kernel", coreLib + "/Kernel.som",
                                  "--platform", coreLib + "/Platform.som"};
    VmOptions vmOptions = new VmOptions(args);
    VM vm = new VM(vmOptions);
    Builder builder = PolyglotEngine.newBuilder();
    builder.config(SomLanguage.MIME_TYPE, SomLanguage.VM_OBJECT, vm);


    PolyglotEngine engine = builder.build();
    engine.getRuntime().getInstruments().values().forEach(i -> i.setEnabled(false));

    // Trigger object system initialization
    engine.getLanguages().get(SomLanguage.MIME_TYPE).getGlobalObject();

    return vm;
  }

  private SomStructures getProbe(final String documentUri) {
    synchronized (structuralProbes) {
      return structuralProbes.get(documentUri);
    }
  }

  public ArrayList<Diagnostic> parse(final String text, final String sourceUri)
      throws URISyntaxException {
    URI uri = new URI(sourceUri);
    Source source = Source.newBuilder(text).
        name(uri.getPath()).
        mimeType(SomLanguage.MIME_TYPE).
        uri(uri).build();

    try {
      // clean out old structural data
      SomStructures newProbe = new SomStructures(source);
      synchronized (structuralProbes) {
        structuralProbes.put(sourceUri, newProbe);
      }
      synchronized (newProbe) {
        compiler.compileModule(source, newProbe);
      }
    } catch (ParseError e) {
      return toDiagnostics(e);
    } catch (SemanticDefinitionError e) {
      return toDiagnostics(e);
    } catch (ProgramDefinitionError e) {
      throw new RuntimeException("Not yet supported error", e);
    }
    return new ArrayList<>();
  }

  private ArrayList<Diagnostic> toDiagnostics(final ParseError e) {
    ArrayList<Diagnostic> diagnostics = new ArrayList<>();

    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    SourceCoordinate coord = e.getSourceCoordinate();

    Range r = new Range();
    r.setStart(pos(coord.startLine, coord.startColumn));
    r.setEnd(pos(coord.startLine, Integer.MAX_VALUE));
    d.setRange(r);
    d.setMessage(e.getMessage());
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  private ArrayList<Diagnostic> toDiagnostics(final SemanticDefinitionError e) {
    ArrayList<Diagnostic> diagnostics = new ArrayList<>();
    SourceSection source = e.getSourceSection();

    Diagnostic d = new Diagnostic();
    d.setSeverity(DiagnosticSeverity.Error);

    Range r = new Range();
    r.setStart(pos(source.getStartLine(), source.getStartColumn()));
    r.setEnd(pos(source.getEndLine(), source.getEndColumn()));
    d.setRange(r);
    d.setMessage(e.getMessage());
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  private static Position pos(final int startLine, final int startChar) {
    Position pos = new Position();
    pos.setLine(startLine - 1);
    pos.setCharacter(startChar - 1);
    return pos;
  }

  @SuppressWarnings("unused")
  private static boolean in(final SourceSection s, final int line, final int character) {
    if (s.getStartLine() > line || s.getEndLine() < line) {
      return false;
    }

    if (s.getStartLine() == line && s.getStartColumn() > character) {
      return false;
    }
    if (s.getEndLine() == line && s.getEndColumn() < character) {
      return false;
    }

    return true;
  }

  public DocumentHighlight getHighlight(final String documentUri,
      final int line, final int character) {
    // TODO: this is wrong, it should be something entierly different.
    // this feature is about marking the occurrences of a selected element
    // like a variable, where it is used.
    // so, this should actually return multiple results.
    // The spec is currently broken for that.

    // XXX: the code here doesn't make any sense for what it is supposed to do

//    Map<SourceSection, Set<Class<? extends Tags>>> sections = Highlight.
//        getSourceSections();
//    SourceSection[] all = sections.entrySet().stream().map(e -> e.getKey()).toArray(size -> new SourceSection[size]);
//
//    Stream<Entry<SourceSection, Set<Class<? extends Tags>>>> filtered = sections.
//        entrySet().stream().filter(
//            (final Entry<SourceSection, Set<Class<? extends Tags>>> e) -> in(e.getKey(), line, character));
//
//    @SuppressWarnings("rawtypes")
//    Entry[] matching = filtered.toArray(size -> new Entry[size]);
//
//    for (Entry<SourceSection, Set<Class<? extends Tags>>> e : matching) {
//      int kind;
//      if (e.getValue().contains(LiteralTag.class)) {
//        kind = DocumentHighlight.KIND_READ;
//      } else {
//        kind = DocumentHighlight.KIND_TEXT;
//      }
//      DocumentHighlightImpl highlight = new DocumentHighlightImpl();
//      highlight.setKind(kind);
//      highlight.setRange(getRange(e.getKey()));
//      return highlight;
//    }
//
//    DocumentHighlightImpl highlight = new DocumentHighlightImpl();
//    highlight.setKind(DocumentHighlight.KIND_TEXT);
//    RangeImpl range = new RangeImpl();
//    range.setStart(pos(line, character));
//    range.setEnd(pos(line, character + 1));
//    highlight.setRange(range);
//    return highlight;
    return null;
  }

  private static Range getRange(final SourceSection ss) {
    Range range = new Range();
    range.setStart(pos(ss.getStartLine(), ss.getStartColumn()));
    range.setEnd(pos(ss.getEndLine(), ss.getEndColumn() + 1));
    return range;
  }

  public static Location getLocation(final SourceSection ss) {
    Location loc = new Location();
    loc.setUri(ss.getSource().getURI().toString());
    loc.setRange(getRange(ss));
    return loc;
  }

  public List<? extends SymbolInformation> getSymbolInfo(final String documentUri) {
    SomStructures probe = getProbe(documentUri);
    ArrayList<SymbolInformation> results = new ArrayList<>();
    if (probe == null) {
      return results;
    }

    synchronized (probe) {
      Set<MixinDefinition> classes = probe.getClasses();
      for (MixinDefinition m : classes) {
        assert m.getSourceSection().getSource().getURI().toString().equals(documentUri);
        addSymbolInfo(m, results);
      }

      Set<SInvokable> methods = probe.getMethods();
      for (SInvokable m : methods) {
        assert m.getHolder() != null;
        assert m.getSourceSection().getSource().getURI().toString().equals(documentUri);
        results.add(getSymbolInfo(m));
      }
    }
    return results;
  }

  private static SymbolInformation getSymbolInfo(final SInvokable m) {
    SymbolInformation sym = new SymbolInformation();
    sym.setName(m.getSignature().toString());
    sym.setKind(SymbolKind.Method);
    assert null != m.getSourceSection();
    sym.setLocation(getLocation(m.getSourceSection()));
    if (m.getHolder() != null) {
      sym.setContainerName(m.getHolder().getName().getString());
    }
    return sym;
  }

  private static void addSymbolInfo(final MixinDefinition m,
      final ArrayList<SymbolInformation> results) {
    results.add(getSymbolInfo(m));
    for (Dispatchable d : m.getInstanceDispatchables().values()) {
      // needs to be exact test to avoid duplicate info
      if (d.getClass() == SlotDefinition.class) {
        results.add(getSymbolInfo((SlotDefinition) d, m));
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

  public List<? extends Location> getDefinitions(final String docUri,
      final int line, final int character) {
    ArrayList<Location> result = new ArrayList<>();
    SomStructures probe = getProbe(docUri);
    if (probe == null) { return result; }

    ExpressionNode node = probe.getElementAt(line + 1, character); // +1 to get to one based index
    if (node == null) { return result; }

    if (node instanceof AbstractUninitializedMessageSendNode) {
      SSymbol name = ((AbstractUninitializedMessageSendNode) node).getSelector();
      for (SomStructures s : structuralProbes.values()) {
        s.getDefinitionsFor(name, result);
      }
    } else {
      if (ServerLauncher.DEBUG) {
        reportError("GET DEFINITION, unsupported node: " + node.getClass().getSimpleName());
      }
    }
    return result;
  }

  private void reportError(final String msgStr) {
    MessageParams msg = new MessageParams();
    msg.setType(MessageType.Log);
    msg.setMessage(msgStr);

    client.logMessage(msg);

    ServerLauncher.logErr(msgStr);
  }

  public CompletionList getCompletions(final String docUri, final int line, final int character) {
    CompletionList result = new CompletionList();
    result.setIsIncomplete(true);

    SomStructures probe = getProbe(docUri);
    if (probe == null) { return result; }

    // TODO: this expects that this can be parsed without issues...
    ExpressionNode node = probe.getElementAt(line + 1, Math.max(character - 1, 0)); // +1 to get to one based index, - 1 to get back into the element
    if (node == null) { return result; }

    if (node instanceof AbstractUninitializedMessageSendNode) {
      ArrayList<CompletionItem> completion = new ArrayList<>();
      SSymbol name = ((AbstractUninitializedMessageSendNode) node).getSelector();
      for (SomStructures s : structuralProbes.values()) {
        s.getCompletions(name, completion);
      }
      result.setItems(completion);
    } else {
      if (ServerLauncher.DEBUG) {
        reportError("GET COMPLETIONS, unsupported node: " + node.getClass().getSimpleName());
      }
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
        final StructuralProbe structuralProbe) throws ProgramDefinitionError {
      SomParser parser = new SomParser(source.getCode(), source.getLength(),
          source, (SomStructures) structuralProbe, language);
      return compile(parser, source);
    }
  }
}
