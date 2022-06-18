package bench.generator;

import awfy.Random;


public class FileGenerator {

  public static String generateFile(final int minNumberOfLines, final String[] fragments) {
    return generateFile(minNumberOfLines, fragments, "", "");
  }

  public static String generateFile(final int minNumberOfLines, final String[] fragments,
      final String header, final String footer) {
    int[] length = determineLength(fragments);

    int numLines = 0;
    int fnNum = 0;
    Random r = new Random();

    StringBuilder sb = new StringBuilder(minNumberOfLines * 20);
    sb.append(header);

    while (numLines < minNumberOfLines) {
      int sample = r.next() % fragments.length;
      numLines += length[sample];
      sb.append(fragments[sample].formatted("func" + fnNum));

      fnNum += 1;
    }

    sb.append(footer);
    return sb.toString();
  }

  private static int countLines(final String str) {
    int lines = 1;
    int pos = 0;
    while ((pos = str.indexOf("\n", pos) + 1) != 0) {
      lines += 1;
    }
    return lines;
  }

  private static int[] determineLength(final String[] fragments) {
    int[] length = new int[fragments.length];

    for (int i = 0; i < fragments.length; i += 1) {
      length[i] = countLines(fragments[i]);
    }

    return length;
  }
}
