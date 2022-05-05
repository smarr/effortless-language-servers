package som.langserv;

import java.util.List;


public interface SemanticTokens {

  public List<int[]> getSemanticTokens();

  public default void addSemanticToken(final int lineNumber, int startingChar,
      final int length, final SemanticTokenType tokenType,
      final SemanticTokenModifier... tokenModifiers) {
    List<int[]> tokenList = getSemanticTokens();
    assert tokenList != null;

    if (startingChar <= 0) {
      startingChar = 1;
    }

    int[] tuple = new int[5];

    tuple[0] = lineNumber;
    tuple[1] = startingChar - 1;
    tuple[2] = length;
    tuple[3] = tokenType.ordinal();

    if (tokenModifiers != null && tokenModifiers.length > 0) {
      throw new RuntimeException(
          "Not yet implemented. Need to turn the array into setting bits on a integer. "
              + "See description after https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#semanticTokensLegend");
    } else {
      tuple[4] = 0;
    }

    tokenList.add(tuple);
  }

}
