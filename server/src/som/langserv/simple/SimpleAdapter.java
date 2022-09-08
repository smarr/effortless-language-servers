package som.langserv.simple;

import java.net.URISyntaxException;

import org.antlr.v4.runtime.CharStreams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import com.oracle.truffle.sl.parser.SLParseError;

import simple.SimpleLanguageLexer;
import som.langserv.LanguageAdapter;
import som.langserv.lint.FileLinter;
import som.langserv.lint.LintEndsWithNewline;
import som.langserv.lint.LintUseNeedsDefine;
import som.langserv.lint.WorkspaceLinter;
import som.langserv.structure.DocumentStructures;


public class SimpleAdapter extends LanguageAdapter {

  public SimpleAdapter() {
    super(
        new FileLinter[] {new LintEndsWithNewline()},
        new WorkspaceLinter[] {new LintUseNeedsDefine()});
  }

  @Override
  public String getFileEnding() {
    return ".sl";
  }

  @Override
  public DocumentStructures parse(final String text, final String sourceUri)
      throws URISyntaxException {
    String path = docUriToNormalizedPath(sourceUri);

    DocumentStructures structures = new DocumentStructures(sourceUri, "file:" + path);
    assert structures != null;

    try {
      parse(text, structures);
      putStructures(path, structures);
      return structures;
    } catch (SLParseError e) {
      return toDiagnostics(e, structures);
    } catch (Throwable e) {
      return toDiagnostics(e, structures);
    }
  }

  private DocumentStructures toDiagnostics(final SLParseError e,
      final DocumentStructures structures) {
    String[] msgParts = e.format.split(":");
    String msg = msgParts[2].trim();

    Diagnostic d = new Diagnostic();
    d.setRange(util.PositionConversion.toRange(e.line, e.col, e.length));

    d.setSeverity(DiagnosticSeverity.Error);

    d.setMessage(msg);
    d.setSource("Parser");

    return updateDiagnostics(d, structures);
  }

  private DocumentStructures toDiagnostics(final Throwable e,
      final DocumentStructures structures) {
    Diagnostic d = new Diagnostic();
    d.setRange(new Range(new Position(0, 0), new Position(0, Short.MAX_VALUE)));
    d.setSeverity(DiagnosticSeverity.Error);

    d.setMessage(e.getMessage());

    d.setSource("Parser");

    return updateDiagnostics(d, structures);
  }

  public void parse(final String source, final DocumentStructures structures) {
    SimpleLanguageLexer lexer =
        new SimpleLanguageLexer(CharStreams.fromString(source));
    SimpleParser parser = new SimpleParser(lexer, structures);

    parser.parse();
  }
}
