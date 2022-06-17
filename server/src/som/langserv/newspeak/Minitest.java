package som.langserv.newspeak;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;

import com.google.common.collect.Lists;

import som.langserv.LanguageAdapter;
import som.langserv.lens.FileLens;
import som.langserv.structure.DocumentStructures;
import som.langserv.structure.LanguageElement;


public class Minitest implements FileLens {
  private static final String TEST_CONTEXT = "TEST_CONTEXT";
  private static final String TEST_PREFIX  = "test";
  private static final String CLASS        = "class";

  public static final String COMMAND = "minitest";

  private static final String   CLASSPATH = System.getProperty("java.class.path");
  private static final String   JAVA_BIN  = System.getProperty("java.home") + "/bin/java";
  private static final String[] JAVA_ARGS =
      new String[] {"-d64", "-Dbd.settings=som.vm.VmSettings", "som.VM"};

  private static final String[] SOM_ARGS = new String[] {
      "--platform", NewspeakAdapter.CORE_LIB_PATH + "/Platform.ns",
      "--kernel", NewspeakAdapter.CORE_LIB_PATH + "/Kernel.ns",
      NewspeakAdapter.CORE_LIB_PATH + "/TestSuite/TestRunner.ns"};

  @Override
  public List<CodeLens> getCodeLenses(final DocumentStructures symbols) {
    List<CodeLens> result = new ArrayList<>();

    for (var sym : symbols.getRootSymbols()) {
      checkForTestContextAndAddLenses(result, sym, symbols.getUri());
    }

    if (result.isEmpty()) {
      return null;
    } else {
      return result;
    }
  }

  private static void checkForTestContextAndAddLenses(final List<CodeLens> results,
      final LanguageElement elem, final String documentUri) {
    // apply recursive to all classes
    for (var c : elem.getChildren()) {
      if (c.getKind() == SymbolKind.Class && !c.getName().equals(CLASS)) {
        checkForTestContextAndAddLenses(results, (LanguageElement) c, documentUri);
      }
    }

    // now look whether we have the marker method
    LanguageElement clazz = null;
    for (var c : elem.getChildren()) {
      if (c.getKind() == SymbolKind.Class && c.getName().equals(CLASS)) {
        clazz = (LanguageElement) c;
        break;
      }
    }

    if (clazz == null) {
      return;
    }

    for (var method : clazz.getChildren()) {
      LanguageElement m = (LanguageElement) method;
      if (m.getId().getName().equals(TEST_CONTEXT)) {
        createLenses(results, elem, documentUri);
      }
    }
  }

  private static void createLenses(final List<CodeLens> results, final LanguageElement elem,
      final String documentUri) {
    // add a lens for the whole class
    results.add(createTestLensForClass(elem, documentUri));

    // find all methods with the test prefix, and add a lens each
    for (var m : elem.getChildren()) {
      if (m.getKind() == SymbolKind.Method && m.getName().startsWith(TEST_PREFIX)) {
        results.add(createTestLensForMethod(elem, (LanguageElement) m, documentUri));
      }
    }
  }

  private static CodeLens createTestLensForClass(final LanguageElement clazz,
      final String documentUri) {
    return createTestLens(clazz.getName(), "Run tests", clazz.getSelectionRange(),
        documentUri);
  }

  private static CodeLens createTestLensForMethod(final LanguageElement clazz,
      final LanguageElement method, final String documentUri) {
    return createTestLens(
        clazz.getName() + "." + method.getName(),
        "Run test",
        method.getSelectionRange(),
        documentUri);
  }

  private static CodeLens createTestLens(final String testName, final String title,
      final Range select, final String documentUri) {
    CodeLens lens = new CodeLens();
    Command cmd = new Command();
    cmd.setCommand(COMMAND);
    cmd.setTitle(title);

    cmd.setArguments(
        Lists.newArrayList(
            documentUri,
            testName,
            select.getStart().getLine(),
            select.getStart().getCharacter(),
            select.getEnd().getLine(),
            select.getEnd().getCharacter()));

    lens.setCommand(cmd);
    lens.setRange(select);
    return lens;
  }

  private static void addAll(final ArrayList<String> list, final String[] arr) {
    for (String s : arr) {
      list.add(s);
    }
  }

  public static void executeTest(final NewspeakAdapter adapter, final List<Object> arguments) {
    assert arguments.size() == 2 + 4;
    int startLine = (int) (double) arguments.get(2);
    int startChar = (int) (double) arguments.get(3);
    int endLine = (int) (double) arguments.get(4);
    int endChar = (int) (double) arguments.get(5);
    executeTest(adapter, (String) arguments.get(0), (String) arguments.get(1),
        new Range(new Position(startLine, startChar), new Position(endLine, endChar)));
  }

  private static String toString(final InputStream inputStream) throws IOException {
    final int bufferSize = 1024;
    final char[] buffer = new char[bufferSize];
    final StringBuilder out = new StringBuilder();
    Reader in = new InputStreamReader(inputStream, "UTF-8");
    for (;;) {
      int rsz = in.read(buffer, 0, buffer.length);
      if (rsz < 0) {
        break;
      }
      out.append(buffer, 0, rsz);
    }
    return out.toString();
  }

  private static void executeTest(final NewspeakAdapter adapter, final String documentUri,
      final String test, final Range range) {
    ArrayList<String> str = new ArrayList<>();
    str.add(JAVA_BIN);
    str.add("-cp");
    str.add(CLASSPATH);
    addAll(str, JAVA_ARGS);
    addAll(str, SOM_ARGS);

    String path;
    try {
      path = LanguageAdapter.docUriToNormalizedPath(documentUri);
    } catch (URISyntaxException e1) {
      return;
    }
    str.add(path);
    str.add(test);

    Process p;
    try {
      p = Runtime.getRuntime().exec(str.toArray(new String[0]), new String[0]);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    boolean repeat;

    do {
      repeat = false;
      try {
        int r = p.waitFor();

        List<Diagnostic> diagnostics = new ArrayList<>();
        if (r != 0) {
          String stdout = "";
          String error = "";
          try {
            stdout = toString(p.getInputStream());
            error = toString(p.getErrorStream());
          } catch (IOException e) {}

          String s = "";
          for (String sp : str) {
            s += sp + " ";
          }

          diagnostics.add(new Diagnostic(range,
              "Test " + test + " failed. \nError:\n" + error
                  + "\n\nOut:\n" + stdout + "\n\nExec: " + s + "\nExit Code: " + r,
              DiagnosticSeverity.Warning,
              documentUri));
        } else {
          diagnostics.add(new Diagnostic(range,
              "Test " + test + " succeeded", DiagnosticSeverity.Information, documentUri));
        }

        adapter.reportDiagnostics(diagnostics, documentUri);
      } catch (InterruptedException e) {
        repeat = true;
      }
    } while (repeat);
  }
}
