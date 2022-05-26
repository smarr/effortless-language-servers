package som.langserv.som;

import static som.langserv.som.PositionConversion.toRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpContext;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bdt.source.SourceCoordinate;
import bdt.tools.nodes.Invocation;
import bdt.tools.structure.StructuralProbe;
import som.langserv.structure.DocumentData;
import som.langserv.structure.DocumentSymbols;
import som.langserv.structure.SemanticTokenModifier;
import som.langserv.structure.SemanticTokenType;
import som.langserv.structure.SemanticTokens;
import trufflesom.compiler.Field;
import trufflesom.compiler.Variable;
import trufflesom.compiler.Variable.Argument;
import trufflesom.compiler.Variable.Local;
import trufflesom.interpreter.nodes.ExpressionNode;
import trufflesom.vmobjects.SClass;
import trufflesom.vmobjects.SInvokable;
import trufflesom.vmobjects.SSymbol;


public class SomStructures
    extends StructuralProbe<SSymbol, SClass, SInvokable, Field, Variable>
    implements SemanticTokens, DocumentData {

  protected final Source         source;
  private final ExpressionNode[] map;

  private final List<Diagnostic> diagnostics;

  private final List<Call> calls;

  private final List<int[]> semanticTokens;

  private final DocumentSymbols symbols;

  public static class Call {
    final SSymbol         selector;
    final SourceSection[] sections;

    Call(final SSymbol selector, final SourceSection[] sections) {
      this.selector = selector;
      this.sections = sections;
    }
  }

  public SomStructures(final Source source, final String remoteUri,
      final String normalizedUri) {
    this.source = source;
    this.map = new ExpressionNode[source.getLength()];
    this.diagnostics = new ArrayList<>(0);
    this.calls = new ArrayList<>();
    this.semanticTokens = new ArrayList<>();
    this.symbols = new DocumentSymbols(remoteUri, normalizedUri);
  }

  public DocumentSymbols getSymbols() {
    return symbols;
  }

  @Override
  public List<? extends DocumentSymbol> getRootSymbols() {
    return symbols.getRootSymbols();
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

    addSemanticToken(line, col, length, SemanticTokenType.PROPERTY,
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

    addSemanticToken(line, col, length, tokenType,
        (SemanticTokenModifier[]) null);
  }

  public String getDocumentUri() {
    return source.getURI().toString();
  }

  public List<Diagnostic> getDiagnostics() {
    return diagnostics;
  }

  public synchronized ExpressionNode getElementAt(final int line, final int character) {
    int idx = source.getLineStartOffset(line) + character;
    return map[idx];
  }

  // TODO: split into getMethodsFor
  public synchronized void getDefinitionsFor(final SSymbol name,
      final ArrayList<Location> results) {
    for (SClass c : classes) {
      if (c.getName() == name) {
        SourceSection ss = c.getSourceSection();
        if (ss != null) {
          results.add(PositionConversion.getLocation(ss));
        }
      }
    }

    for (SInvokable m : methods.getValues()) {
      if (m.getSignature() == name) {
        results.add(PositionConversion.getLocation(m.getSourceSection()));
      }
    }
  }

  public synchronized void getCompletions(final SSymbol name,
      final Set<CompletionItem> results) {
    for (Variable v : variables) {
      matchAndAdd(name, v.name, results, CompletionItemKind.Variable);
    }

    for (SInvokable m : methods.getValues()) {
      matchAndAdd(name, m.getSignature(), results, CompletionItemKind.Method);
    }

    for (Field f : slots) {
      matchAndAdd(name, f.getName(), results, CompletionItemKind.Field);
    }

    // TODO: for classes
    // for (SClass c : classes) {
    // matchAndAdd(name, c.getName(), results, CompletionItemKind.Class);
    // // matchAndAdd(name, c.getPrimaryFactorySelector(), results,
    // // CompletionItemKind.Constructor);
    // }
  }

  private void matchAndAdd(final SSymbol query, final SSymbol symbol,
      final Set<CompletionItem> results, final CompletionItemKind kind) {
    if (fuzzyMatches(symbol, query)) {
      CompletionItem item = new CompletionItem();
      item.setKind(kind);
      item.setLabel(symbol.getString());
      results.add(item);
    }
  }

  @SuppressWarnings("unchecked")
  public void reportCall(final ExpressionNode send, final SourceSection... section) {
    if (send instanceof Invocation<?>) {
      calls.add(new Call(((Invocation<SSymbol>) send).getInvocationIdentifier(), section));
    }
    for (SourceSection s : section) {
      putIntoMap(s, send);
    }
  }

  @SuppressWarnings("unchecked")
  public void reportAssignment(final ExpressionNode result,
      final SourceSection removeLast) {

    putIntoMap(removeLast, result);
  }

  private synchronized void putIntoMap(final SourceSection section,
      final ExpressionNode result) {
    for (int i = section.getCharIndex(); i < section.getCharEndIndex(); i++) {
      if (map[i] == null) {
        map[i] = result;
      }
    }
  }

  public synchronized boolean defines(final SSymbol selector) {
    // TODO: add classes?

    for (Variable v : variables) {
      if (v.name == selector) {
        return true;
      }
    }

    for (SInvokable m : methods.getValues()) {
      if (m.getSignature() == selector) {
        return true;
      }
    }

    for (Field field : slots) {
      if (field.getName() == selector) {
        return true;
      }
    }

    return false;
  }

  @Override
  public List<int[]> getSemanticTokens() {
    return semanticTokens;
  }

  @Override
  public String toString() {
    return source.getName();
  }

  @Override
  public Hover getHover(final Position position) {
    return symbols.getHover(position);
  }
}
