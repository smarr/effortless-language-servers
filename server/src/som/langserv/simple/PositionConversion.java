package som.langserv.simple;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;


public class PositionConversion {

  private PositionConversion() {}

  public static Position getStart(final Token token) {
    // lines are 1 based, but need to be 0-based
    // but char position is already 0-based
    return new Position(token.getLine() - 1, token.getCharPositionInLine());
  }

  public static Position getEnd(final Token token) {
    return new Position(
        // lines are 1 based, but need to be 0-based
        token.getLine() - 1,
        // but char position is already 0-based
        token.getCharPositionInLine() + token.getText().length());
  }

  public static Range getRange(final Token token) {
    return new Range(getStart(token), getEnd(token));
  }

  public static Range getRange(final int line, final int startChar) {
    Range r = new Range();

    // lines are 1 based, but need to be 0-based
    r.setStart(new Position(line - 1, startChar));
    r.setEnd(new Position(line - 1, Short.MAX_VALUE));

    return r;
  }
}
