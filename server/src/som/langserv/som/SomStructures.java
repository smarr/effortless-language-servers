package som.langserv.som;

import com.oracle.truffle.api.source.Source;

import bdt.tools.structure.StructuralProbe;
import som.langserv.structure.DocumentStructures;
import trufflesom.compiler.Field;
import trufflesom.compiler.Variable;
import trufflesom.vmobjects.SClass;
import trufflesom.vmobjects.SInvokable;
import trufflesom.vmobjects.SSymbol;


public class SomStructures
    extends StructuralProbe<SSymbol, SClass, SInvokable, Field, Variable> {

  protected final Source             source;
  protected final DocumentStructures symbols;

  public SomStructures(final Source source, final String remoteUri,
      final String normalizedUri) {
    this.source = source;
    this.symbols = new DocumentStructures(remoteUri, normalizedUri);
  }

  public DocumentStructures getSymbols() {
    return symbols;
  }

  public DocumentStructures getDocumentStructures() {
    return symbols;
  }

  /** Return the source path represented by the probe. */
  public String getPath() {
    String result = source.getPath();
    if (result == null) {
      result = source.getName();
    }
    return result;
  }

  @Override
  public String toString() {
    return source.getName();
  }
}
