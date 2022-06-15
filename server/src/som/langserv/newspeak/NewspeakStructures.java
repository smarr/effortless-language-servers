package som.langserv.newspeak;

import com.oracle.truffle.api.source.Source;

import bd.tools.structure.StructuralProbe;
import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Variable;
import som.langserv.structure.DocumentStructures;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;


public class NewspeakStructures
    extends StructuralProbe<SSymbol, MixinDefinition, SInvokable, SlotDefinition, Variable> {

  private final Source             source;
  private final DocumentStructures symbols;

  public NewspeakStructures(final Source source, final DocumentStructures structures) {
    this.source = source;
    this.symbols = structures;
  }

  public DocumentStructures getSymbols() {
    return symbols;
  }

  public String getDocumentUri() {
    return source.getURI().toString();
  }
}
