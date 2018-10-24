package som.langserv;

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

import com.google.common.collect.Lists;

import som.compiler.MixinDefinition;
import som.interpreter.nodes.dispatch.Dispatchable;
import som.vm.Symbols;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;


public class SomMinitest {
  private static final SSymbol TEST_CONTEXT = Symbols.symbolFor("TEST_CONTEXT");
  private static final String  TEST_PREFIX  = "test";

  public static final String COMMAND = "minitest";

  private static final String   CLASSPATH = System.getProperty("java.class.path");
  private static final String   JAVA_BIN  = System.getProperty("java.home") + "/bin/java";
  private static final String[] JAVA_ARGS =
      new String[] {"-d64", "-Dbd.settings=som.vm.VmSettings", "som.VM"};

  private static final String[] SOM_ARGS = new String[] {
      "--platform", SomAdapter.CORE_LIB_PATH + "/Platform.ns",
      "--kernel", SomAdapter.CORE_LIB_PATH + "/Kernel.ns",
      SomAdapter.CORE_LIB_PATH + "/TestSuite/TestRunner.ns"};

  public static void checkForTests(final MixinDefinition def,
      final List<CodeLens> codeLenses, final String documentUri) {
    for (SSymbol s : def.getFactoryMethods().getKeys()) {
      if (s == TEST_CONTEXT) {
        CodeLens lens = createTestLense(def, documentUri);

        codeLenses.add(lens);

        addTestMethods(def, codeLenses, documentUri);
        return;
      }
    }
  }

  private static CodeLens createTestLense(final MixinDefinition def,
      final String documentUri) {
    CodeLens lens = new CodeLens();
    Command cmd = new Command();
    cmd.setCommand(COMMAND);
    cmd.setTitle("Run tests");
    Range r = SomAdapter.toRange(def.getNameSourceSection());
    cmd.setArguments(Lists.newArrayList(documentUri, def.getName().getString(),
        r.getStart().getLine(), r.getStart().getCharacter(), r.getEnd().getLine(),
        r.getEnd().getCharacter()));

    lens.setCommand(cmd);
    lens.setRange(r);
    return lens;
  }

  private static void addTestMethods(final MixinDefinition def,
      final List<CodeLens> codeLenses, final String documentUri) {
    for (Dispatchable d : def.getInstanceDispatchables().getValues()) {
      if (d instanceof SInvokable) {
        SInvokable i = (SInvokable) d;
        if (i.getSignature().getString().startsWith(TEST_PREFIX)) {
          CodeLens lens = addTestMethod(def, documentUri, i);
          codeLenses.add(lens);
        }
      }
    }
  }

  private static CodeLens addTestMethod(final MixinDefinition def, final String documentUri,
      final SInvokable i) {
    CodeLens lens = new CodeLens();
    Command cmd = new Command();
    cmd.setCommand(COMMAND);
    cmd.setTitle("Run test");
    Range r = SomAdapter.toRange(i.getSourceSection());
    cmd.setArguments(Lists.newArrayList(documentUri,
        def.getName().getString() + "." + i.getSignature().getString(),
        r.getStart().getLine(), r.getStart().getCharacter(), r.getEnd().getLine(),
        r.getEnd().getCharacter()));

    lens.setCommand(cmd);
    lens.setRange(r);
    return lens;
  }

  private static void addAll(final ArrayList<String> list, final String[] arr) {
    for (String s : arr) {
      list.add(s);
    }
  }

  public static void executeTest(final SomAdapter adapter, final List<Object> arguments) {
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

  private static void executeTest(final SomAdapter adapter, final String documentUri,
      final String test, final Range range) {
    ArrayList<String> str = new ArrayList<>();
    str.add(JAVA_BIN);
    str.add("-cp");
    str.add(CLASSPATH);
    addAll(str, JAVA_ARGS);
    addAll(str, SOM_ARGS);

    String path;
    try {
      path = SomAdapter.docUriToNormalizedPath(documentUri);
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
