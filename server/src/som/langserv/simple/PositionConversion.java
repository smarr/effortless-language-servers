package som.langserv.simple;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;


public class PositionConversion {

  private PositionConversion() {}

  public static Position getStart(final Token token) {
    // lines are 1 based
    // but char position is 0-based, so +1 to make it consistently 1-based
    return new Position(token.getLine(), token.getCharPositionInLine() + 1);
  }

  public static Position getEnd(final Token token) {
    return new Position(
        // lines are 1 based
        token.getLine(),
        // but char position is 0-based, so +1 to make it consistently 1-based
        token.getCharPositionInLine() + token.getText().length() + 1);
  }

  public static Range getRange(final Token token) {
    return new Range(getStart(token), getEnd(token));
  }
}
