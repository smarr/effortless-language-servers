package som.langserv.simple;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpContext;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.tools.structure.StructuralProbe;
import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Variable;
import som.interpreter.nodes.ExpressionNode;
import som.langserv.LanguageAdapter;
import som.langserv.structure.DocumentData;
import som.langserv.structure.DocumentSymbols;
import som.langserv.structure.SemanticTokenType;
import som.langserv.structure.SemanticTokens;
import som.vmobjects.SSymbol;


public class SimpleStructures
    extends StructuralProbe<String, MixinDefinition, DocumentSymbol, SlotDefinition, Variable>
    implements SemanticTokens, DocumentData {

  private final Source           source;
  private final ExpressionNode[] map;

  private final List<Diagnostic> diagnostics;

  private final List<int[]> semanticTokens;

  private final SimpleNodeFactory nodeFactory;

  private final DocumentSymbols symbols;

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
    this.semanticTokens = new ArrayList<>();
    this.symbols = new DocumentSymbols();

    this.nodeFactory = new SimpleNodeFactory(this);
  }

  @Override
  public List<? extends DocumentSymbol> getRootSymbols() {
    return symbols.getRootSymbols();
  }

  public SimpleNodeFactory getFactory() {
    return nodeFactory;
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
        token.getLine(),
        // char position is 0-based, so, +1 to make it 1-based
        token.getCharPositionInLine() + 1,
        token.getText().length(),
        type);
  }

  public DocumentSymbols getSymbols() {
    return symbols;
  }

  @Override
  public Hover getHover(final Position position) {
    return symbols.getHover(position);
  }

  @Override
  public SignatureHelp getSignatureHelp(final Position position,
      final SignatureHelpContext context) {
    return symbols.getSignatureHelp(position, context);
  }
}
