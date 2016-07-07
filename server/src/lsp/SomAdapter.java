package lsp;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import io.typefox.lsapi.PositionImpl;
import io.typefox.lsapi.RangeImpl;
import som.VMOptions;
import som.compiler.Lexer.SourceCoordinate;
import som.compiler.MixinBuilder.MixinDefinitionError;
import som.compiler.Parser;
import som.compiler.Parser.ParseError;
import som.interpreter.SomLanguage;
import tools.highlight.Highlight;
import tools.highlight.Tags;
import tools.highlight.Tags.LiteralTag;

public class SomAdapter {

  public static void initializePolyglot() {
    String[] args = new String[0];
    Builder builder = PolyglotEngine.newBuilder();
    builder.config(SomLanguage.MIME_TYPE, SomLanguage.CMD_ARGS, args);
    VMOptions vmOptions = new VMOptions(args);
    PolyglotEngine engine = builder.build();
    engine.getInstruments().get(Highlight.ID).setEnabled(true);
  }

  public static ArrayList<DiagnosticImpl> parse(final String text, final String sourceUri)
      throws URISyntaxException {
    URI uri = new URI(sourceUri);
    Source source = Source.newBuilder(text).
        name(uri.getPath()).
        mimeType(SomLanguage.MIME_TYPE).
        uri(uri).build();
    StringReader reader = new StringReader(text);
    int fileSize = text.length();

    Parser p = new Parser(reader, fileSize, source);

    try {
      p.moduleDeclaration();
    } catch (ParseError e) {
      return toDiagnostics(e);
    } catch (MixinDefinitionError e) {
      // TODO
    }
    return new ArrayList<>();
  }

  private static ArrayList<DiagnosticImpl> toDiagnostics(final ParseError e) {
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
  public static DocumentHighlight getHighlight(final String documentUri,
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
      RangeImpl range = new RangeImpl();
      range.setStart(pos(line, character));
      range.setEnd(pos(e.getKey().getEndLine(), e.getKey().getEndColumn() + 1));

      highlight.setRange(range);
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
}
