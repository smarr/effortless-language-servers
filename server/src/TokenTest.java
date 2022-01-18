public class TokenTest {

  // @Test
  // public void test() throws IOException, URISyntaxException {
  // LanguageAdapter adapter = new NewspeakAdapter();
  // // NewspeakAdapter na = new NewspeakAdapter();
  // // what vs code tels you as the line and col needs to be - 1
  // String testFilePath =
  // "/home/hburchell/vscode/SOMns-vscode/server/libs/SOMns/core-lib/Hello.ns";
  //
  // List<String> lines = Files.readAllLines(Paths.get(testFilePath),
  // Charset.defaultCharset());
  //
  // adapter.parse(
  // "class Hello usingPlatform: platform = Value ()(\n" +
  // " public main: args = (\n" +
  // " 'Hello World!' println.\n" +
  // " args from: 2 to: args size do: [ :arg | arg print. ' ' print ].\n" +
  // " '' println.\n" +
  // " ^ 0\n" +
  // " )\n" +
  // ")\n" +
  // "",
  // testFilePath);
  //
  // List<Integer> tokens = adapter.getTokenPositions(testFilePath);
  //
  // List<Integer> exspectedTokens =
  // Arrays.asList(0, 0, 5, 1, 0,
  // 0, 6, 5, 0, 0,
  // 0, 12, 13, 1, 0,
  // 0, 27, 8, 6, 0,
  // 0, 38, 5, 2, 0,
  // 1, 2, 6, 1, 0,
  // 1, 9, 4, 1, 0,
  // 1, 15, 4, 6, 0,
  // 2, 4, 14, 3, 0,
  // 2, 19, 7, 2, 0,
  // 3, 4, 4, 4, 0,
  // 3, 9, 4, 2, 0,
  // 3, 17, 2, 2, 0,
  // 3, 21, 4, 4, 0,
  // 3, 26, 4, 2, 0,
  // 3, 31, 2, 2, 0,
  // 3, 38, 3, 9, 0,
  // 3, 44, 3, 4, 0,
  // 3, 48, 5, 2, 0,
  // 3, 55, 3, 3, 0,
  // 3, 59, 5, 2, 0,
  // 4, 4, 2, 3, 0,
  // 4, 7, 7, 2, 0);
  // tokens = sortByColNum(sortByLineNum(tokens));
  // List<List<Integer>> differences = new ArrayList<>(chunk(exspectedTokens, 5));
  // differences.removeAll(chunk(tokens, 5));
  // System.out.println(
  // "the following tokens where not found : " + Arrays.toString(differences.toArray()));
  // System.out.println(
  // "what the parser returned : " + Arrays.toString(chunk(tokens, 5).toArray()));
  // assertTrue(differences.size() == 0);
  //
  // }
  //
  // private static List<Integer> sortByLineNum(final List<Integer> in) {
  // List<List<Integer>> list2d = chunk(in, 5);
  // Integer[][] arr;
  //
  // arr = list2d.stream().map(x -> x.toArray(new
  // Integer[x.size()])).toArray(Integer[][]::new);
  //
  // int n = arr.length;
  // for (int i = 0; i < n - 1; i++) {
  // for (int j = 0; j < n - i - 1; j++) {
  // if (arr[j][0].intValue() > arr[j + 1][0].intValue()) {
  //
  // Integer temp[] = arr[j];
  // arr[j] = arr[j + 1];
  // arr[j + 1] = temp;
  // }
  // }
  // }
  // return twoDArrayToList(arr);
  //
  // }
  //
  // private static List<Integer> sortByColNum(final List<Integer> in) {
  // List<List<Integer>> list2d = chunk(in, 5);
  // Integer[][] arr;
  //
  // arr = list2d.stream().map(x -> x.toArray(new
  // Integer[x.size()])).toArray(Integer[][]::new);
  //
  // int n = arr.length;
  // boolean didASwap = true;
  // for (int i = 0; i < n - 1 || didASwap == true; i++) {
  // didASwap = false;
  // for (int j = 0; j < n - i - 1; j++) {
  // if (arr[j][0].intValue() == arr[j + 1][0].intValue()
  // && arr[j][1].intValue() > arr[j + 1][1].intValue()) {
  //
  // Integer temp[] = arr[j];
  // arr[j] = arr[j + 1];
  // arr[j + 1] = temp;
  // didASwap = true;
  // }
  // }
  // }
  // return twoDArrayToList(arr);
  //
  // }
  //
  // public static <T> List<List<T>> chunk(final List<T> input, final int chunkSize) {
  //
  // int inputSize = input.size();
  // int chunkCount = (int) Math.ceil(inputSize / (double) chunkSize);
  //
  // Map<Integer, List<T>> map = new HashMap<>(chunkCount);
  // List<List<T>> chunks = new ArrayList<>(chunkCount);
  //
  // for (int i = 0; i < inputSize; i++) {
  //
  // map.computeIfAbsent(i / chunkSize, (ignore) -> {
  //
  // List<T> chunk = new ArrayList<>();
  // chunks.add(chunk);
  // return chunk;
  //
  // }).add(input.get(i));
  // }
  //
  // return chunks;
  // }
  //
  // private static <T> List<T> twoDArrayToList(final T[][] twoDArray) {
  // List<T> list = new ArrayList<T>();
  // for (T[] array : twoDArray) {
  // list.addAll(Arrays.asList(array));
  // }
  // return list;
  // }

}
// for Hello.ns
//
// keyword class token 1 1 5
// class identifer token 1 7 11
// using keyword token 1 19 13
// identifier token 1 34 8
// varible token 1 45 5
// keyword 2 3 6
// method name 2 10 4
// varible 2 16 4
// literual string 3 5 14
// method 3 20 7
// varible 4 5 4
// keyword 4 10 4
// litural number 4 16 1
// keyword 4 18 2
// varible 4 22 4
// varible 4 27 4
// keyword 4 32 2
// varible 4 39 3
// varible 4 45 3
// method 4 49 5
// litural string 4 56 3
// method 4 60 5
// litural string 5 5 3
// method 5 8 15
