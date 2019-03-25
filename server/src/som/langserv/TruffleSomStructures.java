package som.langserv;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Location;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import trufflesom.compiler.Variable;
import trufflesom.interpreter.nodes.ExpressionNode;
import trufflesom.tools.Send;
import trufflesom.tools.StructuralProbe;
import trufflesom.vmobjects.SInvokable;
import trufflesom.vmobjects.SSymbol;


public class TruffleSomStructures extends StructuralProbe {

  private final Source           source;
  private final ExpressionNode[] map;

  private final List<Diagnostic> diagnostics;

  private final List<Call> calls;

  public static class Call {
    final SSymbol         selector;
    final SourceSection[] sections;

    Call(final SSymbol selector, final SourceSection[] sections) {
      this.selector = selector;
      this.sections = sections;
    }
  }

  public TruffleSomStructures(final Source source) {
    this.source = source;
    this.map = new ExpressionNode[source.getLength()];
    this.diagnostics = new ArrayList<>(0);
    this.calls = new ArrayList<>();
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
    for (SInvokable m : instanceMethods.getValues()) {
      if (m.getSignature().equals(name)) {
        results.add(LanguageAdapter.getLocation(m.getSourceSection()));
      }
    }

    for (SInvokable m : classMethods.getValues()) {
      if (m.getSignature().equals(name)) {
        results.add(LanguageAdapter.getLocation(m.getInvokable().getSourceSection()));
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

    return fuzzyMatches(symbol.getString().toLowerCase(), query.getString().toLowerCase());
  }

  public static boolean fuzzyMatches(final String string, final String query) {
    if (query == null) {
      return true;
    }

    // simple prefix
    if (string.startsWith(query)) {
      return true;
    }

    // trivial case
    if (query.equals(string)) {
      return true;
    }

    // TODO: camel case matching etc...
    return false;
  }

  public synchronized void getCompletions(final SSymbol name,
      final Set<CompletionItem> results) {
    for (Variable v : variables) {
      matchAndAdd(name, v.name, results, CompletionItemKind.Variable);
    }

    for (SInvokable m : instanceMethods.getValues()) {
      matchAndAdd(name, m.getSignature(), results, CompletionItemKind.Method);
    }

    for (SInvokable m : classMethods.getValues()) {
      matchAndAdd(name, m.getSignature(), results, CompletionItemKind.Method);
    }

    for (Field f : instanceFields) {
      matchAndAdd(name, f.getSymbol(), results, CompletionItemKind.Field);
    }

    for (Field f : classFields) {
      matchAndAdd(name, f.getSymbol(), results, CompletionItemKind.Field);
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

  public void reportCall(final ExpressionNode send, final SourceSection... section) {
    if (send instanceof Send) {
      calls.add(new Call(((Send) send).getSelector(), section));
    }
    for (SourceSection s : section) {
      putIntoMap(s, send);
    }
  }

  public void reportAssignment(final ExpressionNode result,
      final SourceSection removeLast) {
    if (result instanceof Send) {
      calls.add(new Call(((Send) result).getSelector(), new SourceSection[] {removeLast}));
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

    for (SInvokable m : instanceMethods.getValues()) {
      if (m.getSignature() == selector) {
        return true;
      }
    }

    for (SInvokable m : classMethods.getValues()) {
      if (m.getSignature() == selector) {
        return true;
      }
    }

    if (instanceFields.contains(selector)) {
      return true;
    }

    if (classFields.contains(selector)) {
      return true;
    }

    return false;
  }

}
