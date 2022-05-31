package som.langserv.simple;

import java.lang.reflect.Field;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;

import simple.SLNodeFactory;
import simple.SimpleLanguageLexer;
import simple.SimpleLanguageParser;


public class SimpleParser extends SimpleLanguageParser {

  private final SimpleStructures struturalProbe;

  public SimpleParser(final SimpleLanguageLexer lexer,
      final SimpleStructures structuralProbe) {
    super(new CommonTokenStream(lexer));
    this.struturalProbe = structuralProbe;
    addParseListener(new SimpleTokenCollector(structuralProbe));
    setFactory(structuralProbe.getFactory());

    lexer.removeErrorListeners();
    removeErrorListeners();

    var errorListener = new CapturingErrorListener();
    lexer.addErrorListener(errorListener);
    addErrorListener(errorListener);
  }

  private void setFactory(final SLNodeFactory factory) {
    Field factoryField;
    try {
      factoryField = SimpleLanguageParser.class.getDeclaredField("factory");
      factoryField.setAccessible(true);
      factoryField.set(this, factory);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
        | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private final class CapturingErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
        final int line, final int charPositionInLine, final String msg,
        final RecognitionException e) {
      Token token = (Token) offendingSymbol;

      Diagnostic diag = new Diagnostic();
      diag.setRange(PositionConversion.getRange(token));
      diag.setMessage(msg);
      diag.setSeverity(DiagnosticSeverity.Error);
      diag.setSource("Simple Language Parser");
      diag.setData(token.getText().equals("."));

      struturalProbe.getSymbols().addDiagnostic(diag);
    }
  }

  public void parse() {
    simplelanguage();
  }
}
