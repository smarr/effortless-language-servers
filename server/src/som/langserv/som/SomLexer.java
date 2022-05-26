package som.langserv.som;

import java.util.List;

import som.langserv.structure.SemanticTokenType;
import trufflesom.compiler.Lexer;


public class SomLexer extends Lexer {
  private final List<int[]> semanticTokens;

  protected SomLexer(final String content, final List<int[]> semanticTokens) {
    super(content);
    this.semanticTokens = semanticTokens;
  }

  @Override
  protected void skipComment() {
    if (currentChar() == '"') {
      int length = 0;
      int col = state.ptr - state.lastLineEnd;
      do {
        length++;
        if (currentChar() == '\n') {
          addCoordsToTokens(state.lineNumber, 1, length);
          state.lineNumber += 1;
          state.lastLineEnd = state.ptr;
          length = 0;
        }
        state.incPtr();
      } while (currentChar() != '"');
      addCoordsToTokens(state.lineNumber, col, length + 1);
      state.incPtr();
    }
  }

  private void addCoordsToTokens(final int line, final int col, final int length) {
    int[] tuple = {line, col, length, // ~
        SemanticTokenType.COMMENT.ordinal(), // ~
        0 /* token modifier */};
    semanticTokens.add(tuple);
  }
}
