package som.langserv.som;

import java.util.ArrayList;
import java.util.List;

import trufflesom.compiler.Lexer;


public class SomLexer extends Lexer {
  private List<Integer> commentTokens = new ArrayList<Integer>();

  protected SomLexer(final String content) {
    super(content);
  }

  @Override
  protected void skipComment() {
    if (currentChar() == '"') {
      int length = 0;
      int col = state.ptr - state.lastLineEnd;
      do {
        length++;
        if (currentChar() == '\n') {
          addCoordsToTokens(state.lineNumber - 1, 0, length);
          state.lineNumber += 1;
          state.lastLineEnd = state.ptr;
          length = 0;
        }
        state.incPtr();
      } while (currentChar() != '"');
      addCoordsToTokens(state.lineNumber - 1, col - 1, length + 1);
      state.incPtr();
    }
  }

  private void addCoordsToTokens(final int line, final int col, final int length) {
    commentTokens.add(line);
    commentTokens.add(col);
    commentTokens.add(length);
    commentTokens.add(5);
    commentTokens.add(0);
  }

  public List<Integer> getCommentsPositions() {
    return commentTokens;
  }
}
