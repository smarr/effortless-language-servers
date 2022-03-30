import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.Diagnostic;

import com.google.common.collect.Lists;

import som.langserv.LanguageAdapter;
import som.langserv.som.SomAdapter;


public class SOMBenchmarking extends Benchmark {
  LanguageAdapter adapter;

  @Override
  public Object benchmark() {
    adapter = new SomAdapter();
    try {
      return loadWorkspace(
          "file:///home/hburchell/vscode/SOMns-vscode/server/libs/TruffleSOM/core-lib/Examples/Benchmarks");
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public int loadWorkspace(final String uri) throws URISyntaxException {
    if (uri == null) {
      return 1;
    }

    URI workspaceUri = new URI(uri);
    File workspace = new File(workspaceUri);
    assert workspace.isDirectory();
    loadFolder(workspace);
    return 0;

  }

  private void loadFolder(final File folder) {
    for (File f : folder.listFiles()) {
      if (f.isDirectory()) {
        loadFolder(f);
      } else if (f.getName().endsWith(".som")) {
        try {
          byte[] content = Files.readAllBytes(f.toPath());
          String str = new String(content, StandardCharsets.UTF_8);
          String uri = f.toURI().toString();
          parse(str, uri);
        } catch (IOException e) {
          // if loading fails, we don't do anything, just move on to the next file
        }
      }
    }
  }

  private void parse(final String str, final String uri) {
    try {
      adapter.parse(str,
          uri);
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public Object withoutTokens() {
    LanguageAdapter adapter = new SomAdapter();
    String testFilePath =
        "/home/hburchell/vscode/SOMns-vscode/server/tokenTests/Som/BenchmarkFile1000.som";

    try {
      Path path = Paths.get(testFilePath);
      byte[] content = Files.readAllBytes(path);
      String str = new String(content, StandardCharsets.UTF_8);
      adapter.parse(str,
          testFilePath);
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return 0;

  }

  public Object withTokens() {
    LanguageAdapter adapter = new SomAdapter();
    String testFilePath =
        "/home/hburchell/vscode/SOMns-vscode/server/tokenTests/Som/BenchmarkFile1000.som";

    try {
      Path path = Paths.get(testFilePath);
      byte[] content = Files.readAllBytes(path);
      StringBuilder str = new StringBuilder();
      str = str.append(new String(content, StandardCharsets.UTF_8));
      List<String> lines = Files.readAllLines(Paths.get(testFilePath),
          Charset.defaultCharset());

      adapter.parse(
          str.toString(),
          testFilePath);

      List<Integer> tokens = adapter.getTokenPositions(testFilePath);

      tokens = sort(tokens);
      if (!tokens.isEmpty()) {
        return 0;
      }
      return 1;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public boolean verifyResult(final Object result) {
    // TODO Auto-generated method stub
    return result.equals(0);
  }

  private static List<Integer> sort(final List<Integer> in) {
    List<List<Integer>> list2d = Lists.partition(in, 5);
    Integer[][] arr;

    arr = list2d.stream().map(x -> x.toArray(new Integer[x.size()])).toArray(Integer[][]::new);
    // sort by line
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n - i - 1; j++) {
        if (arr[j][0].intValue() > arr[j + 1][0].intValue()) {

          Integer temp[] = arr[j];
          arr[j] = arr[j + 1];
          arr[j + 1] = temp;
        }
      }
    }
    // sort by col
    boolean didASwap = true;
    Integer temp[];
    for (int i = 0; i < n - 1 || didASwap == true; i++) {
      didASwap = false;
      for (int j = 0; j < n - i - 1; j++) {
        if (arr[j][0].intValue() == arr[j + 1][0].intValue()
            && arr[j][1].intValue() > arr[j + 1][1].intValue()) {
          temp = arr[j];
          arr[j] = arr[j + 1];
          arr[j + 1] = temp;
          didASwap = true;
        }
      }
    }
    List<Integer> list = new ArrayList<Integer>();
    for (Integer[] array : arr) {
      list.addAll(Arrays.asList(array));
    }
    return list;

  }

  private static List<Integer> excludingSort(final Diagnostic diag,
      final List<Integer> orginal,
      final List<Integer> newtokens) {
    int brokenTokenLineNUmber;
    if (newtokens.isEmpty()) {

      brokenTokenLineNUmber = diag.getRange().getStart().getLine();
      return removeByLine(orginal, brokenTokenLineNUmber);

    }
    List<List<Integer>> differences = new ArrayList<>(chunk(orginal, 5));
    differences.removeAll(chunk(newtokens, 5));
    brokenTokenLineNUmber = differences.remove(0).get(0);

    List<Integer> list = new ArrayList<Integer>();
    Integer[][] arr = differences.stream().map(x -> x.toArray(new Integer[x.size()]))
                                 .toArray(Integer[][]::new);
    for (Integer[] array : arr) {
      list.addAll(Arrays.asList(array));
    }
    newtokens.addAll(removeByLine(list, brokenTokenLineNUmber));
    return newtokens;

  }

  private static List<Integer> removeByLine(final List<Integer> in, final int Line) {
    List<List<Integer>> list2d = Lists.partition(in, 5);
    Integer[][] arr;

    arr = list2d.stream().map(x -> x.toArray(new Integer[x.size()])).toArray(Integer[][]::new);
    // sort by line
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n - i - 1; j++) {
        if (arr[j][0].intValue() == Line) {
          for (int j2 = 0; j2 < 5; j2++) {
            arr[j][j2] = 99999;

          }
        }
      }
    }

    List<Integer> list = new ArrayList<Integer>();
    for (Integer[] array : arr) {
      list.addAll(Arrays.asList(array));
    }
    return list;

  }

  public static <T> List<List<T>> chunk(final List<T> input, final int chunkSize) {

    int inputSize = input.size();
    int chunkCount = (int) Math.ceil(inputSize / (double) chunkSize);

    Map<Integer, List<T>> map = new HashMap<>(chunkCount);
    List<List<T>> chunks = new ArrayList<>(chunkCount);

    for (int i = 0; i < inputSize; i++) {

      map.computeIfAbsent(i / chunkSize, (ignore) -> {

        List<T> chunk = new ArrayList<>();
        chunks.add(chunk);
        return chunk;

      }).add(input.get(i));
    }

    return chunks;
  }
}
