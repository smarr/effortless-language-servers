package som.langserv.simple;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Location;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.tools.structure.StructuralProbe;
import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Variable;
import som.interpreter.nodes.ExpressionNode;
import som.langserv.LanguageAdapter;
import som.langserv.SemanticTokenType;
import som.langserv.SemanticTokens;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;


public class SimpleStructures
    extends StructuralProbe<SSymbol, MixinDefinition, SInvokable, SlotDefinition, Variable>
    implements SemanticTokens {

  private final Source           source;
  private final ExpressionNode[] map;

  private final List<Diagnostic> diagnostics;

  private final List<Call> calls;

  private final List<int[]> semanticTokens;

  private final SimpleNodeFactory nodeFactory;

  public static class Call {
    final SSymbol         selector;
    final SourceSection[] sections;

    Call(final SSymbol selector, final SourceSection[] sections) {
      this.selector = selector;
      this.sections = sections;
    }
  }

  public SimpleStructures(final int length) {
    this.source = null;

    this.map = new ExpressionNode[length];
    this.diagnostics = new ArrayList<>(0);
    this.calls = new ArrayList<>();
    this.semanticTokens = new ArrayList<>();
    this.nodeFactory = new SimpleNodeFactory(this);
  }

  public SimpleNodeFactory getFactory() {
    return nodeFactory;
  }

  public List<Call> getCalls() {
    return calls;
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

  public synchronized void getDefinitionsFor(final SSymbol name,
      final ArrayList<Location> results) {
    for (MixinDefinition m : classes) {
      if (m.getName() == name) {
        results.add(LanguageAdapter.getLocation(m.getSourceSection()));
      }
    }

    for (SInvokable m : methods.getValues()) {
      if (m.getSignature() == name) {
        results.add(LanguageAdapter.getLocation(m.getSourceSection()));
      }
    }

    for (SlotDefinition s : slots) {
      if (s.getName() == name) {
        results.add(LanguageAdapter.getLocation(s.getSourceSection()));
      }
    }

    for (Variable v : variables) {
      if (v.name == name) {
        results.add(LanguageAdapter.getLocation(v.source));
      }
    }
  }

  @Override
  public List<int[]> getSemanticTokens() {
    return semanticTokens;
  }

  public void addSemanticToken(final Token token, final SemanticTokenType type) {
    addSemanticToken(
        token.getLine(), token.getCharPositionInLine(), token.getText().length(),
        type);
  }
}
