package som.langserv;

import static org.junit.Assert.assertEquals;
import static som.langserv.SimpleLanguageTests.getRootForSimpleLanguageExamples;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.junit.Test;

import som.langserv.simple.SimpleAdapter;


/** These tests check the completeness of capturing details in the "parser". */
public class SimpleCompletenessTests {

  @Test
  public void testVarWriteAndRead() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function foo() {\n"
            + "  myVar = 1;\n"
            + "  return myVar;\n"
            + "}",
        path);

    List<Location> refs = adapter.getReferences(path, new Position(2, 11), true);

    assertEquals(2, refs.size());
    assertEquals(1, refs.get(0).getRange().getStart().getLine());
    assertEquals(2, refs.get(1).getRange().getStart().getLine());
  }

  @Test
  public void testReferenceFunctionAsVar() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function add(a, b) {\n"
            + "  return a + b;\n"
            + "}\n"
            + "function foo(f) {\n"
            + "  println(f(40, 2));\n"
            + "}\n"
            + "function main() {\n"
            + "  foo(add);\n"
            + "}  \n"
            + "",
        path);

    List<Location> refs = adapter.getReferences(path, new Position(7, 7), true);

    assertEquals(2, refs.size());
    assertEquals(0, refs.get(0).getRange().getStart().getLine());
    assertEquals(7, refs.get(1).getRange().getStart().getLine());
  }

  @Test
  public void testObjectProp() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function main() {  \n"
            + "  obj1 = new();\n"
            + "  obj1.x = 42;\n"
            + "  obj1.o = obj1;\n"
            + "  obj1.o.y = \"why\";\n"
            + "  println(obj1.x);\n"
            + "}\n"
            + "function x() {}",
        path);

    List<Location> refs = adapter.getReferences(path, new Position(5, 15), true);

    assertEquals(2, refs.size());
    assertEquals(2, refs.get(0).getRange().getStart().getLine());
    assertEquals(5, refs.get(1).getRange().getStart().getLine());
  }

  @Test
  public void testObjectPropVars() throws URISyntaxException {
    var adapter = new SimpleAdapter();
    String path = "file:" + getRootForSimpleLanguageExamples() + File.separator + "Test.sl";
    adapter.parse(
        "function loop(name) {\n"
            + "  obj[name] = 0;\n"
            + "}",
        path);

    List<? extends LocationLink> refs = adapter.getDefinitions(path, new Position(1, 7));

    assertEquals(1, refs.size());
    assertEquals(0, refs.get(0).getTargetSelectionRange().getStart().getLine());
    assertEquals(1, refs.get(0).getOriginSelectionRange().getStart().getLine());
  }
}
