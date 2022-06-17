package som.langserv;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.Range;

import som.langserv.structure.SemanticTokenType;


public class Helpers {
  public static List<int[]> splitIntoTokenDetails(final List<Integer> tokenDetails) {
    assertTrue("It's expected that the token details come as 5-tuples, but length is: "
        + tokenDetails.size(), tokenDetails.size() % 5 == 0);

    List<int[]> result = new ArrayList<>();
    for (int i = 0; i < tokenDetails.size(); i += 5) {
      int[] tuple = new int[5];
      tuple[0] = tokenDetails.get(i);
      tuple[1] = tokenDetails.get(i + 1);
      tuple[2] = tokenDetails.get(i + 2);
      tuple[3] = tokenDetails.get(i + 3);
      tuple[4] = tokenDetails.get(i + 4);
      result.add(tuple);
    }

    return result;
  }

  public static void printAllToken(final List<int[]> tokens) {
    int i = 0;
    for (int[] t : tokens) {
      System.out.print(i + ": ");
      printToken(t);
      i += 1;
    }
  }

  public static void printToken(final int[] tuple) {
    System.out.println("line: " + tuple[0] + " col: " + tuple[1] + " len: " + tuple[2]
        + " type: " + SemanticTokenType.from(tuple[3]).name + " mod: unsupported "
        + tuple[4]);
    // TODO: to support the modifiers, I need to parse the bitmap
    // SemanticTokenModifier.values()[tuple[4]].name
  }

  public static void assertToken(final int expectedLine, final int expectedCol,
      final String token, final SemanticTokenType expectedType, final int[] actualTuple) {
    String error = "";

    if (expectedLine != actualTuple[0]) {
      error += "line expected " + expectedLine + ", but was " + actualTuple[0] + "\n";
    }

    if (expectedCol != actualTuple[1]) {
      error += "col expected " + expectedCol + ", but was " + actualTuple[1] + "\n";
    }

    if (token.length() != actualTuple[2]) {
      error += "expected " + token + " with " + token.length() + "chars, but got "
          + actualTuple[2] + "\n";
    }

    if (expectedType.ordinal() != actualTuple[3]) {
      error += "expected " + expectedType.name + ", but got "
          + SemanticTokenType.from(actualTuple[3]) + "\n";
    }

    if (!error.equals("")) {
      throw new AssertionError(error);
    }
  }

  public static void assertStart(final int startLine, final int startCol, final Range range) {
    String error = "";
    boolean failed = false;
    if (startLine != range.getStart().getLine()) {
      error += "expected start line: " + startLine + " but got: " + range.getStart().getLine();
      failed = true;
    }

    if (startCol != range.getStart().getCharacter()) {
      if (failed) {
        error += "\n";
      }
      error +=
          "expected start col: " + startCol + " but got: " + range.getStart().getCharacter();
      failed = true;
    }

    if (failed) {
      throw new AssertionError(error);
    }
  }

  public static void assertRange(final int startLine, final int startCol, final int endLine,
      final int endCol, final Range range) {
    String error = "";
    boolean failed = false;
    if (startLine != range.getStart().getLine()) {
      error += "expected start line: " + startLine + " but got: " + range.getStart().getLine();
      failed = true;
    }

    if (startCol != range.getStart().getCharacter()) {
      if (failed) {
        error += "\n";
      }
      error +=
          "expected start col: " + startCol + " but got: " + range.getStart().getCharacter();
      failed = true;
    }

    if (endLine != range.getEnd().getLine()) {
      if (failed) {
        error += "\n";
      }
      error +=
          "expected end line: " + endLine + " but got: " + range.getEnd().getLine();
      failed = true;
    }

    if (endCol != range.getEnd().getCharacter()) {
      if (failed) {
        error += "\n";
      }
      error +=
          "expected end col: " + endCol + " but got: " + range.getEnd().getCharacter();
      failed = true;
    }

    if (failed) {
      throw new AssertionError(error);
    }
  }
}
