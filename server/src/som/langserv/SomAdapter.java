package som.langserv;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.vm.PolyglotEngine;
import com.oracle.truffle.api.vm.PolyglotEngine.Builder;

import io.typefox.lsapi.Diagnostic;
import io.typefox.lsapi.DiagnosticImpl;
import io.typefox.lsapi.DocumentHighlight;
import io.typefox.lsapi.DocumentHighlightImpl;
import io.typefox.lsapi.Location;
import io.typefox.lsapi.LocationImpl;
import io.typefox.lsapi.PositionImpl;
import io.typefox.lsapi.RangeImpl;
import io.typefox.lsapi.SymbolInformation;
import io.typefox.lsapi.SymbolInformationImpl;
import som.VM;
import som.VMOptions;
import som.compiler.Lexer.SourceCoordinate;
import som.compiler.MixinBuilder.MixinDefinitionError;
import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Parser.ParseError;
import som.compiler.SourcecodeCompiler;
import som.interpreter.SomLanguage;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.MessageSendNode.AbstractUninitializedMessageSendNode;
import som.interpreter.nodes.dispatch.Dispatchable;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;
import tools.highlight.Highlight;
import tools.highlight.Tags;
import tools.highlight.Tags.LiteralTag;
import tools.language.StructuralProbe;

public class SomAdapter {

  private final Map<String, SomStructures> structuralProbes = new HashMap<>();
  private final SomCompiler compiler = new SomCompiler();

  public SomAdapter() {
    initializePolyglot();
  }

  private void initializePolyglot() {
    String coreLib = System.getProperty("som.langserv.core-lib");
    if (coreLib == null) {
      throw new IllegalArgumentException("The som.langserv.core-lib system property needs to be set. For instance: -Dsom.langserv.core-lib=/SOMns/core-lib");
    }

    String[] args = new String[] {"--kernel", coreLib + "/Kernel.som",
                                  "--platform", coreLib + "/Platform.som"};
    Builder builder = PolyglotEngine.newBuilder();
    builder.config(SomLanguage.MIME_TYPE, SomLanguage.CMD_ARGS, args);
    VMOptions vmOptions = new VMOptions(args);
    PolyglotEngine engine = builder.build();
    VM.setEngine(engine);
    engine.getInstruments().values().forEach(i -> i.setEnabled(false));

    // Trigger object system initialization
    engine.getLanguages().get(SomLanguage.MIME_TYPE).getGlobalObject();
  }

  private SomStructures getProbe(final String documentUri) {
    synchronized (structuralProbes) {
      return structuralProbes.get(documentUri);
    }
  }

  public ArrayList<DiagnosticImpl> parse(final String text, final String sourceUri)
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
    } catch (MixinDefinitionError e) {
      // TODO
    }
    return new ArrayList<>();
  }

  private ArrayList<DiagnosticImpl> toDiagnostics(final ParseError e) {
    ArrayList<DiagnosticImpl> diagnostics = new ArrayList<>();

    DiagnosticImpl d = new DiagnosticImpl();
    d.setSeverity(Diagnostic.SEVERITY_ERROR);

    SourceCoordinate coord = e.getSourceCoordinate();

    RangeImpl r = new RangeImpl();
    r.setStart(pos(coord.startLine, coord.startColumn));
    r.setEnd(pos(coord.startLine, coord.startColumn + 2)); // TODO: better upper limit /IntMax??
    d.setRange(r);
    d.setMessage(e.getMessage());
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  private static PositionImpl pos(final int startLine, final int startChar) {
    PositionImpl pos = new PositionImpl();
    pos.setLine(startLine - 1);
    pos.setCharacter(startChar - 1);
    return pos;
  }

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

  @SuppressWarnings("unchecked")
  public DocumentHighlight getHighlight(final String documentUri,
      final int line, final int character) {
    // TODO: this is wrong, it should be something entierly different.
    // this feature is about marking the occurrences of a selected element
    // like a variable, where it is used.
    // so, this should actually return multiple results.
    // The spec is currently broken for that.

    // XXX: the code here doesn't make any sense for what it is supposed to do

    Map<SourceSection, Set<Class<? extends Tags>>> sections = Highlight.
        getSourceSections();
    SourceSection[] all = sections.entrySet().stream().map(e -> e.getKey()).toArray(size -> new SourceSection[size]);

    Stream<Entry<SourceSection, Set<Class<? extends Tags>>>> filtered = sections.
        entrySet().stream().filter(
            (final Entry<SourceSection, Set<Class<? extends Tags>>> e) -> in(e.getKey(), line, character));

    @SuppressWarnings("rawtypes")
    Entry[] matching = filtered.toArray(size -> new Entry[size]);

    for (Entry<SourceSection, Set<Class<? extends Tags>>> e : matching) {
      int kind;
      if (e.getValue().contains(LiteralTag.class)) {
        kind = DocumentHighlight.KIND_READ;
      } else {
        kind = DocumentHighlight.KIND_TEXT;
      }
      DocumentHighlightImpl highlight = new DocumentHighlightImpl();
      highlight.setKind(kind);
      highlight.setRange(getRange(e.getKey()));
      return highlight;
    }

    DocumentHighlightImpl highlight = new DocumentHighlightImpl();
    highlight.setKind(DocumentHighlight.KIND_TEXT);
    RangeImpl range = new RangeImpl();
    range.setStart(pos(line, character));
    range.setEnd(pos(line, character + 1));
    highlight.setRange(range);
    return highlight;
  }

  private static RangeImpl getRange(final SourceSection ss) {
    RangeImpl range = new RangeImpl();
    range.setStart(pos(ss.getStartLine(), ss.getStartColumn()));
    range.setEnd(pos(ss.getEndLine(), ss.getEndColumn() + 1));
    return range;
  }

  public static LocationImpl getLocation(final SourceSection ss) {
    LocationImpl loc = new LocationImpl();
    loc.setUri(ss.getSource().getURI().toString());
    loc.setRange(getRange(ss));
    return loc;
  }

  public List<? extends SymbolInformation> getSymbolInfo(final String documentUri) {
    SomStructures probe = getProbe(documentUri);
    ArrayList<SymbolInformationImpl> results = new ArrayList<>();
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

  private static SymbolInformationImpl getSymbolInfo(final SInvokable m) {
    SymbolInformationImpl sym = new SymbolInformationImpl();
    sym.setName(m.getSignature().toString());
    sym.setKind(SymbolInformation.KIND_METHOD);
    assert null != m.getSourceSection();
    sym.setLocation(getLocation(m.getSourceSection()));
    sym.setContainer(m.getHolder().getName().getString());
    return sym;
  }

  private static void addSymbolInfo(final MixinDefinition m,
      final ArrayList<SymbolInformationImpl> results) {
    results.add(getSymbolInfo(m));
    for (Dispatchable d : m.getInstanceDispatchables().values()) {
      // needs to be exact test to avoid duplicate info
      if (d.getClass() == SlotDefinition.class) {
        results.add(getSymbolInfo((SlotDefinition) d, m));
      }
    }
  }

  private static SymbolInformationImpl getSymbolInfo(final SlotDefinition d,
      final MixinDefinition m) {
    SymbolInformationImpl sym = new SymbolInformationImpl();
    sym.setName(d.getName().getString());
    int kind = m.isModule() ? SymbolInformation.KIND_CONSTANT
                            : SymbolInformation.KIND_PROPERTY;
    sym.setKind(kind);
    sym.setLocation(getLocation(d.getSourceSection()));
    sym.setContainer(m.getName().getString());
    return sym;
  }

  private static SymbolInformationImpl getSymbolInfo(final MixinDefinition m) {
    SymbolInformationImpl sym = new SymbolInformationImpl();
    sym.setName(m.getName().getString());
    int kind = m.isModule() ? SymbolInformation.KIND_MODULE
                            : SymbolInformation.KIND_CLASS;
    sym.setKind(kind);
    sym.setLocation(getLocation(m.getSourceSection()));

    MixinDefinition outer = m.getOuter();
    if (outer != null) {
      sym.setContainer(outer.getName().getString());
    }
    return sym;
  }

  public List<? extends Location> getDefinitions(final String docUri,
      final int line, final int character) {
    ArrayList<LocationImpl> result = new ArrayList<>();
    SomStructures probe = getProbe(docUri);
    if (probe == null) { return result; }

    ExpressionNode node = probe.getElementAt(line + 1, character); // +1 to get to one based index
    if (node == null) { return result; }

    if (node instanceof AbstractUninitializedMessageSendNode) {
      SSymbol name = ((AbstractUninitializedMessageSendNode) node).getSelector();
      for (SomStructures s : structuralProbes.values()) {
        s.getDefinitionsFor(name, result);
      }
    }
    return result;
  }

  private static final class SomCompiler extends SourcecodeCompiler {
    @Override
    public MixinDefinition compileModule(final Source source,
        final StructuralProbe structuralProbe) throws ParseError, MixinDefinitionError {
      SomParser parser = new SomParser(source.getReader(), source.getLength(),
          source, (SomStructures) structuralProbe);
      return compile(parser, source);
    }
  }
}
