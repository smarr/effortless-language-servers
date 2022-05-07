package som.langserv.simple;

import java.lang.reflect.Field;

import org.antlr.v4.runtime.CommonTokenStream;

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

    // TODO: add error listener to have specific error with the details we need for a
    // diagnostic
    // BailoutErrorListener listener = new BailoutErrorListener(source);
    // lexer.addErrorListener(listener);
    // parser.addErrorListener(listener)
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

  public void parse() {
    simplelanguage();
  }
}
