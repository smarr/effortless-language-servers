package som.langserv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Location;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bd.tools.nodes.Invocation;
import bd.tools.structure.StructuralProbe;
import som.compiler.MixinBuilder;
import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Variable;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.dispatch.Dispatchable;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;


public class SomStructures
    extends StructuralProbe<SSymbol, MixinDefinition, SInvokable, SlotDefinition, Variable> {

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

  public SomStructures(final Source source) {
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
      if (v.name.equals(name.getString())) {
        results.add(LanguageAdapter.getLocation(v.source));
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

    for (SInvokable m : methods.getValues()) {
      matchAndAdd(name, m.getSignature(), results, CompletionItemKind.Method);
    }

    for (SlotDefinition s : slots) {
      matchAndAdd(name, s.getName(), results, CompletionItemKind.Field);
    }

    for (MixinDefinition c : classes) {
      matchAndAdd(name, c.getName(), results, CompletionItemKind.Class);
      matchAndAdd(name, c.getPrimaryFactorySelector(), results,
          CompletionItemKind.Constructor);
    }
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

  public synchronized boolean classesAndMethodsConsistent() {
    Set<SInvokable> methods = new HashSet<>();

    for (MixinDefinition c : classes) {
      for (Dispatchable disp : c.getInstanceDispatchables().getValues()) {
        if (disp instanceof SInvokable) {
          methods.add((SInvokable) disp);
        }
      }

      for (SInvokable disp : c.getFactoryMethods().getValues()) {
        methods.add(disp);
      }
    }

    Set<SInvokable> regMethods = new HashSet<>(methods);
    regMethods.removeAll(methods);
    assert regMethods.isEmpty();
    return regMethods.isEmpty();
  }

  @Override
  public void recordNewClass(final MixinDefinition clazz) {
    for (Dispatchable disp : clazz.getInstanceDispatchables().getValues()) {
      if (disp instanceof SInvokable) {
        assert ((SInvokable) disp).getHolder() != null;
      }
    }

    for (SInvokable disp : clazz.getFactoryMethods().getValues()) {
      assert disp.getHolder() != null;
    }

    super.recordNewClass(clazz);
  }

  @SuppressWarnings("unchecked")
  public void reportCall(final ExpressionNode send, final SourceSection... section) {
    if (send instanceof Invocation<?>) {
      calls.add(new Call(((Invocation<SSymbol>) send).getInvocationIdentifier(), section));
    } else {
      // ...
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
    } else {
      // ...
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

    for (SlotDefinition s : slots) {
      if (s.getName() == selector) {
        return true;
      }

      if (!s.isImmutable() && MixinBuilder.getSetterName(s.getName()) == selector) {
        return true;
      }
    }

    for (MixinDefinition c : classes) {
      if (c.getName() == selector) {
        return true;
      }
      if (c.getPrimaryFactorySelector() == selector) {
        return true;
      }
    }

    return false;
  }

  // REM:
  // how to continue with the support for the language server protocol?
  // - subclass the parser, and actively report more info to the structural probe subclass
  // - main idea is that we do not want to reconstruct imprecise info
  // - instead, we have direct access to the info in the parser,
  // - however, we also need to link up the lexical info with the runtime
  // structures, and need to resolve them in a later step
  // - how to do later resolution:
  // - potentially with a future
  // - or another data structure, like a stack, to associate for instance
  // the class name with a class/mixin data structure
  // - should take 'private' semantics into account and bind definition and usage tightly
  // together
}
