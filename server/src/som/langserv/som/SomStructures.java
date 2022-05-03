package som.langserv.som;

import static som.langserv.Matcher.fuzzyMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Location;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bdt.tools.nodes.Invocation;
import bdt.tools.structure.StructuralProbe;
import som.langserv.LanguageAdapter;
import som.langserv.SemanticTokenModifier;
import som.langserv.SemanticTokenType;
import trufflesom.compiler.Field;
import trufflesom.compiler.Variable;
import trufflesom.interpreter.nodes.ExpressionNode;
import trufflesom.vmobjects.SClass;
import trufflesom.vmobjects.SInvokable;
import trufflesom.vmobjects.SSymbol;


public class SomStructures
    extends StructuralProbe<SSymbol, SClass, SInvokable, Field, Variable> {

  protected final Source         source;
  private final ExpressionNode[] map;

  private final List<Diagnostic> diagnostics;

  private final List<Call>    calls;
  private final List<Integer> tokenPosition;

  public static class Call {
    final SSymbol         selector;
    final SourceSection[] sections;

    Call(final SSymbol selector, final SourceSection[] sections) {
      this.selector = selector;
      this.sections = sections;
    }
  }

  public SomStructures(final Source source) {
    this.source = source;
    this.map = new ExpressionNode[source.getLength()];
    this.diagnostics = new ArrayList<>(0);
    this.calls = new ArrayList<>();
    this.tokenPosition = new ArrayList<>();
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

  // TODO: split into getMethodsFor
  public synchronized void getDefinitionsFor(final SSymbol name,
      final ArrayList<Location> results) {
    for (SClass c : classes) {
      if (c.getName() == name) {
        SourceSection ss = c.getSourceSection();
        if (ss != null) {
          results.add(LanguageAdapter.getLocation(ss));
        }
      }
    }

    for (SInvokable m : methods.getValues()) {
      if (m.getSignature() == name) {
        results.add(LanguageAdapter.getLocation(m.getSourceSection()));
      }
    }
  }

  private static boolean fuzzyMatches(final SSymbol symbol, final SSymbol query) {
    if (query == symbol) {
      return true;
    }

    if (query.getNumberOfSignatureArguments() > 1
        && query.getNumberOfSignatureArguments() == symbol.getNumberOfSignatureArguments()) {
      return true;
    }

    return fuzzyMatch(symbol.getString().toLowerCase(), query.getString().toLowerCase());
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
    if (result instanceof Invocation<?>) {
      calls.add(new Call(((Invocation<SSymbol>) result).getInvocationIdentifier(),
          new SourceSection[] {removeLast}));
    }
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

  public void addTokenPosition(final int lineNumber, int startingChar, final int length,
      final SemanticTokenType tokenType, final SemanticTokenModifier... tokenModifiers) {
    if (startingChar <= 0) {
      startingChar = 1;
    }

    tokenPosition.add(lineNumber - 1);
    tokenPosition.add(startingChar - 1);
    tokenPosition.add(length);
    tokenPosition.add(tokenType.ordinal());

    if (tokenModifiers != null && tokenModifiers.length > 0) {
      throw new RuntimeException(
          "Not yet implemented. Need to turn the array into setting bits on a integer. See description after https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#semanticTokensLegend");
    } else {
      tokenPosition.add(0);
    }
  }

  public void addTokenPosition(final List<Integer> list) {
    tokenPosition.addAll(list);
  }

  public List<Integer> getTokenPositions() {
    return tokenPosition;
  }

}
