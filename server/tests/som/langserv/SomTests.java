package som.langserv;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import som.langserv.som.SomAdapter;


public class SomTests {

  @Test
  public void testLoadFile() throws IOException, URISyntaxException {
    var adapter = new SomAdapter();
    var diagnostics =
        adapter.loadFile(new File(SomAdapter.CORE_LIB_PATH + "/Examples/Hello.som"));

    assertEquals(0, diagnostics.size());
  }

  @Test
  public void testLoadingSomWorkspace() {
    var adapter = new SomAdapter();
    var client = new TestLanguageClient();

    adapter.connect(client);
    adapter.loadWorkspaceAndLint(new File(SomAdapter.CORE_LIB_PATH));
    // there are currently two known parse errors in the core lib:
    // - Self.som, where super is assigned to
    // - Examples/Benchmarks/DeltaBlue/SortedCollection.som where we trigger "Currently #dnu
    // with super sent is not yet implemented. "
    assertEquals(2, client.diagnostics.size());
  }
}
