package lsp;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.PolyglotEngine;
import com.oracle.truffle.api.vm.PolyglotEngine.Builder;

import io.typefox.lsapi.Diagnostic;
import io.typefox.lsapi.DiagnosticImpl;
import io.typefox.lsapi.PositionImpl;
import io.typefox.lsapi.RangeImpl;
import som.VMOptions;
import som.compiler.Lexer.SourceCoordinate;
import som.compiler.MixinBuilder.MixinDefinitionError;
import som.compiler.Parser;
import som.compiler.Parser.ParseError;
import som.interpreter.SomLanguage;

public class SomAdapter {

  public static void initializePolyglot() {
    String[] args = new String[0];
    Builder builder = PolyglotEngine.newBuilder();
    builder.config(SomLanguage.MIME_TYPE, SomLanguage.CMD_ARGS, args);
    VMOptions vmOptions = new VMOptions(args);
    PolyglotEngine engine = builder.build();
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
    r.setStart(pos(coord.startLine - 1, coord.startColumn - 1));
    r.setEnd(pos(coord.startLine - 1, coord.startColumn + 2));
    d.setRange(r);
    d.setMessage(e.getMessage());
    d.setSource("Parser");

    diagnostics.add(d);
    return diagnostics;
  }

  private static PositionImpl pos(final int startLine, final int startChar) {
    PositionImpl pos = new PositionImpl();
    pos.setLine(startLine);
    pos.setCharacter(startChar);
    return pos;
  }

}
