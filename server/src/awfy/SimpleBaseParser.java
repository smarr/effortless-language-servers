package awfy;

import java.lang.reflect.Field;

import org.antlr.v4.runtime.CommonTokenStream;

import simple.SLNodeFactory;
import simple.SimpleLanguageLexer;
import simple.SimpleLanguageParser;


public class SimpleBaseParser extends SimpleLanguageParser {

  public SimpleBaseParser(final SimpleLanguageLexer lexer) {
    super(new CommonTokenStream(lexer));

    setFactory(new SLNodeFactory(null, null));

    lexer.removeErrorListeners();
    removeErrorListeners();
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
