package som.langserv;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.Position;


public interface SemanticTokens {

  public List<int[]> getSemanticTokens();

  public default void addSemanticToken(final int lineNumber, final int startingChar,
      final int length, final SemanticTokenType tokenType,
      final SemanticTokenModifier... tokenModifiers) {
    List<int[]> tokenList = getSemanticTokens();
    assert tokenList != null;

    int[] tuple = new int[5];

    tuple[0] = lineNumber;
    tuple[1] = startingChar;
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

  /**
   * Make tokens relative to 1-based line and column indexes, and return a flat list of
   * integers.
   */
  public static List<Integer> makeRelativeTo00(final List<int[]> tokenList) {
    return makeRelative(tokenList, 0, 0);
  }

  /**
   * Make tokens relative to 1-based line and column indexes, and return a flat list of
   * integers.
   */
  public static List<Integer> makeRelativeTo11(final List<int[]> tokenList) {
    return makeRelative(tokenList, 1, 1);
  }

  public static List<Integer> makeRelative(final List<int[]> tokenList, final int baseLine,
      final int baseCol) {
    List<Integer> result = new ArrayList<>(tokenList.size() * 5);

    int prevLine = baseLine;
    int prevCol = baseCol;

    for (int[] token : tokenList) {
      int diffLine = token[0] - prevLine;
      result.add(diffLine);

      if (diffLine != 0) {
        prevLine = token[0];
        prevCol = baseCol;
      }

      int diffCol = token[1] - prevCol;
      result.add(diffCol);
      prevCol = token[1];

      // add the remaining details: length, token type, token modifier
      result.add(token[2]);
      result.add(token[3]);
      result.add(token[4]);
    }

    return result;
  }

  public static List<int[]> sort(final List<int[]> in) {
    in.sort((final int[] a, final int[] b) -> {
      // sort by line
      int diff = a[0] - b[0];
      if (diff != 0) {
        return diff;
      }
      // sort by col
      return a[1] - b[1];
    });

    return in;
  }

  public static List<int[]> combineTokensRemovingErroneousLine(final Position errorStart,
      final List<int[]> prevTokens, final List<int[]> newTokens) {
    int lineWithError = errorStart.getLine();

    if (newTokens.isEmpty()) {
      return filterOutLine(prevTokens, lineWithError);
    }

    List<int[]> newBeforeError =
        filterOutFromError(newTokens, lineWithError, errorStart.getCharacter());

    if (prevTokens != null) {
      List<int[]> oldTokensAfterError =
          filterOutUpToAndIncludingLine(prevTokens, lineWithError);
      newBeforeError.addAll(oldTokensAfterError);
    }
    return newBeforeError;
  }

  private static List<int[]> filterOutLine(final List<int[]> in, final int line) {
    ArrayList<int[]> result = new ArrayList<>(in.size());

    for (int[] tokenTuple : in) {
      if (tokenTuple[0] != line) {
        result.add(tokenTuple);
      }
    }

    return result;
  }

  private static List<int[]> filterOutUpToAndIncludingLine(final List<int[]> in,
      final int line) {
    ArrayList<int[]> result = new ArrayList<>(in.size());

    for (int[] tokenTuple : in) {
      if (tokenTuple[0] > line) {
        result.add(tokenTuple);
      }
    }

    return result;
  }

  private static List<int[]> filterOutFromError(final List<int[]> in, final int line,
      final int col) {
    ArrayList<int[]> result = new ArrayList<>(in.size());

    for (int[] tokenTuple : in) {
      if (tokenTuple[0] < line || (tokenTuple[0] == line && tokenTuple[1] < col)) {
        result.add(tokenTuple);
      }
    }

    return result;
  }
}
