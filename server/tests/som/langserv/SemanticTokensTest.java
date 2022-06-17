package som.langserv;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.lsp4j.Position;
import org.junit.Test;

import som.langserv.structure.SemanticTokens;


public class SemanticTokensTest {

  @Test
  public void testMakeRelativeWithExampleFromLSPSpec() {
    // example from
    // https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#semanticTokensLegend

    List<int[]> absoluteTokens = new ArrayList<>();
    absoluteTokens.add(new int[] {2, 5, 3, 0, 3});
    absoluteTokens.add(new int[] {2, 10, 4, 1, 0});
    absoluteTokens.add(new int[] {5, 2, 7, 2, 0});

    List<Integer> expectedResult = Arrays.asList(
        // 1st token
        2, 5, 3, 0, 3,
        // 2nd token
        0, 5, 4, 1, 0,
        // 3rd token
        3, 2, 7, 2, 0);

    List<Integer> actualResult = SemanticTokens.makeRelativeTo00(absoluteTokens);
    assertEquals(expectedResult, actualResult);
  }

  @Test
  public void testMakeRelativeFor1BasedIdx() {
    // example from
    // https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#semanticTokensLegend

    List<int[]> absoluteTokens = new ArrayList<>();
    absoluteTokens.add(new int[] {2, 5, 3, 0, 3});
    absoluteTokens.add(new int[] {2, 10, 4, 1, 0});
    absoluteTokens.add(new int[] {5, 2, 7, 2, 0});

    List<Integer> expectedResult = Arrays.asList(
        // 1st token
        1, 4, 3, 0, 3,
        // 2nd token
        0, 5, 4, 1, 0,
        // 3rd token
        3, 1, 7, 2, 0);

    List<Integer> actualResult = SemanticTokens.makeRelativeTo11(absoluteTokens);
    assertEquals(expectedResult, actualResult);
  }

  @Test
  public void testSortTokens() {
    List<int[]> absoluteTokens = new ArrayList<>();
    absoluteTokens.add(new int[] {2, 5, 3, 0, 3});
    absoluteTokens.add(new int[] {2, 10, 4, 1, 0});
    absoluteTokens.add(new int[] {5, 2, 7, 2, 0});

    List<int[]> expected = new ArrayList<>(absoluteTokens);

    assertListsEqual(expected, SemanticTokens.sort(absoluteTokens));

    absoluteTokens = new ArrayList<>();
    absoluteTokens.add(new int[] {5, 2, 7, 2, 0});
    absoluteTokens.add(new int[] {2, 5, 3, 0, 3});
    absoluteTokens.add(new int[] {2, 10, 4, 1, 0});

    assertListsEqual(expected, SemanticTokens.sort(absoluteTokens));

    absoluteTokens = new ArrayList<>();
    absoluteTokens.add(new int[] {5, 2, 7, 2, 0});
    absoluteTokens.add(new int[] {2, 10, 4, 1, 0});
    absoluteTokens.add(new int[] {2, 5, 3, 0, 3});

    assertListsEqual(expected, SemanticTokens.sort(absoluteTokens));

    absoluteTokens = new ArrayList<>();
    absoluteTokens.add(new int[] {2, 10, 4, 1, 0});
    absoluteTokens.add(new int[] {5, 2, 7, 2, 0});
    absoluteTokens.add(new int[] {2, 5, 3, 0, 3});

    assertListsEqual(expected, SemanticTokens.sort(absoluteTokens));
  }

  @Test
  public void testTokenRemovalAfterErrorWithoutCachedTokens() {
    List<int[]> tokens = new ArrayList<>();
    tokens.add(new int[] {1, 1, 1, 0, 0});
    tokens.add(new int[] {2, 1, 1, 1, 0});
    tokens.add(new int[] {3, 1, 1, 2, 0});

    List<int[]> filtered =
        SemanticTokens.combineTokensRemovingErroneousLine(new Position(2, 0), null, tokens);

    List<int[]> expected = new ArrayList<>();
    expected.add(new int[] {1, 1, 1, 0, 0});

    assertListsEqual(expected, filtered);
  }

  @Test
  public void testTokenRemovalAfterErrorWithoutCachedTokensCheckingKeepingAllInSameLineBeforeError() {
    List<int[]> tokens = new ArrayList<>();
    tokens.add(new int[] {1, 1, 1, 0, 0});
    tokens.add(new int[] {2, 1, 1, 1, 0});
    tokens.add(new int[] {2, 10, 1, 1, 0});
    tokens.add(new int[] {2, 20, 1, 1, 0});
    tokens.add(new int[] {3, 1, 1, 2, 0});

    List<int[]> filtered =
        SemanticTokens.combineTokensRemovingErroneousLine(new Position(2, 15), null, tokens);

    List<int[]> expected = new ArrayList<>();
    expected.add(new int[] {1, 1, 1, 0, 0});
    expected.add(new int[] {2, 1, 1, 1, 0});
    expected.add(new int[] {2, 10, 1, 1, 0});

    assertListsEqual(expected, filtered);
  }

  @Test
  public void testAllErroneousRemovedButRestRemaining() {
    List<int[]> newTokens = new ArrayList<>();
    newTokens.add(new int[] {1, 1, 10, 2, 0});
    newTokens.add(new int[] {2, 1, 5, 2, 0});
    newTokens.add(new int[] {2, 10, 1, 2, 0});
    newTokens.add(new int[] {2, 20, 1, 2, 0});

    List<int[]> oldTokens = new ArrayList<>();
    oldTokens.add(new int[] {1, 1, 1, 0, 0});
    oldTokens.add(new int[] {2, 1, 1, 1, 0});
    oldTokens.add(new int[] {2, 10, 1, 1, 0});
    oldTokens.add(new int[] {2, 20, 1, 1, 0});
    oldTokens.add(new int[] {3, 1, 1, 0, 0});

    List<int[]> filtered =
        SemanticTokens.combineTokensRemovingErroneousLine(
            new Position(2, 9), oldTokens, newTokens);

    List<int[]> expected = new ArrayList<>();
    expected.add(new int[] {1, 1, 10, 2, 0});
    expected.add(new int[] {2, 1, 5, 2, 0});
    expected.add(new int[] {3, 1, 1, 0, 0});

    assertListsEqual(expected, filtered);
  }

  void assertListsEqual(final List<int[]> expected, final List<int[]> actual) {
    assertEquals(expected.size(), actual.size());

    for (int i = 0; i < expected.size(); i += 1) {
      assertEquals(Arrays.toString(expected.get(i)), Arrays.toString(actual.get(i)));
    }
  }
}
