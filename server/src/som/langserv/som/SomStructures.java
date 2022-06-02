package som.langserv.som;

import static som.langserv.som.PositionConversion.toRange;

import org.eclipse.lsp4j.SymbolKind;

import com.oracle.truffle.api.source.Source;

import bdt.source.SourceCoordinate;
import bdt.tools.structure.StructuralProbe;
import som.langserv.structure.DocumentStructures;
import som.langserv.structure.SemanticTokenModifier;
import som.langserv.structure.SemanticTokenType;
import trufflesom.compiler.Field;
import trufflesom.compiler.Variable;
import trufflesom.compiler.Variable.Argument;
import trufflesom.compiler.Variable.Local;
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
  public synchronized void recordNewSlot(final Field field) {
    super.recordNewSlot(field);

    int line = SourceCoordinate.getLine(source, field.getSourceCoordinate());
    int col = SourceCoordinate.getColumn(source, field.getSourceCoordinate());
    int length = SourceCoordinate.getLength(field.getSourceCoordinate());

    symbols.recordDefinition(field.getName().getString(), new FieldId(field),
        SymbolKind.Field, toRange(source, field.getSourceCoordinate(), length));
    assert field.getName().getString().length() == length;

    symbols.getSemanticTokens().addSemanticToken(line, col, length, SemanticTokenType.PROPERTY,
        (SemanticTokenModifier[]) null);
  }

  @Override
  public synchronized void recordNewVariable(final Variable var) {
    super.recordNewVariable(var);

    if (var instanceof Argument) {
      if (((Argument) var).isSelf()) {
        // we ignore self, since it's implicit
        return;
      }
    }

    int line = SourceCoordinate.getLine(source, var.coord);
    int col = SourceCoordinate.getColumn(source, var.coord);
    int length = SourceCoordinate.getLength(var.coord);

    symbols.recordDefinition(var.getName().getString(), new VariableId(var),
        SymbolKind.Variable, toRange(line, col, length));
    assert var.getName().getString().length() == length;

    SemanticTokenType tokenType;
    if (var instanceof Local) {
      tokenType = SemanticTokenType.VARIABLE;
    } else {
      assert var instanceof Argument;
      tokenType = SemanticTokenType.PARAMETER;
    }

    symbols.getSemanticTokens().addSemanticToken(line, col, length, tokenType,
        (SemanticTokenModifier[]) null);
  }

  public String getDocumentUri() {
    return source.getURI().toString();
  }

  @Override
  public String toString() {
    return source.getName();
  }
}
