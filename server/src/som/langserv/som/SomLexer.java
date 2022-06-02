package som.langserv.som;

import som.langserv.structure.SemanticTokenType;
import som.langserv.structure.SemanticTokens;
import trufflesom.compiler.Lexer;


public class SomLexer extends Lexer {
  private final SemanticTokens semanticTokens;

  protected SomLexer(final String content, final SemanticTokens semanticTokens) {
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
        state.ptr++;
      } while (currentChar() != '"');
      addCoordsToTokens(state.lineNumber, col, length + 1);
      state.ptr++;
    }
  }

  private void addCoordsToTokens(final int line, final int col, final int length) {
    semanticTokens.addSemanticToken(
        line, col, length, SemanticTokenType.COMMENT);
  }
}
