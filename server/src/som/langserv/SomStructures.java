package som.langserv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Location;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import som.compiler.MixinDefinition;
import som.compiler.MixinDefinition.SlotDefinition;
import som.compiler.Variable;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.dispatch.Dispatchable;
import som.vm.Symbols;
import som.vmobjects.SInvokable;
import som.vmobjects.SSymbol;
import tools.language.StructuralProbe;


public class SomStructures extends StructuralProbe {

  private final Source           source;
  private final ExpressionNode[] map;

  public SomStructures(final Source source) {
    this.source = source;
    this.map = new ExpressionNode[source.getLength()];
  }

  public String getDocumentUri() {
    return source.getURI().toString();
  }

  public synchronized ExpressionNode getElementAt(final int line, final int character) {
    int idx = source.getLineStartOffset(line) + character;
    return map[idx];
  }

  public synchronized void getDefinitionsFor(final SSymbol name,
      final ArrayList<Location> results) {
    for (MixinDefinition m : classes) {
      if (m.getName() == name) {
        results.add(SomAdapter.getLocation(m.getSourceSection()));
      }
    }

    for (SInvokable m : methods) {
      if (m.getSignature() == name) {
        results.add(SomAdapter.getLocation(m.getSourceSection()));
      }
    }

    for (SlotDefinition s : slots) {
      if (s.getName() == name) {
        results.add(SomAdapter.getLocation(s.getSourceSection()));
      }
    }

    for (Variable v : variables) {
      if (v.name.equals(name.getString())) {
        results.add(SomAdapter.getLocation(v.source));
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

  private static boolean fuzzyMatches(final String string, final String query) {
    // trivial case
    if (query.equals(string)) {
      return true;
    }

    // simple prefix
    if (string.startsWith(query)) {
      return true;
    }

    // TODO: camel case matching etc...
    return false;
  }

  public synchronized void getCompletions(final SSymbol name,
      final Set<CompletionItem> results) {
    for (Variable v : variables) {
      matchAndAdd(name, Symbols.symbolFor(v.name), results, CompletionItemKind.Variable);
    }

    for (SInvokable m : methods) {
      matchAndAdd(name, m.getSignature(), results, CompletionItemKind.Method);
    }

    for (SlotDefinition s : slots) {
      matchAndAdd(name, s.getName(), results, CompletionItemKind.Field);
    }

    for (MixinDefinition c : classes) {
      matchAndAdd(name, c.getName(), results, CompletionItemKind.Class);
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
      for (Dispatchable disp : c.getInstanceDispatchables().values()) {
        if (disp instanceof SInvokable) {
          methods.add((SInvokable) disp);
        }
      }

      for (SInvokable disp : c.getFactoryMethods().values()) {
        methods.add(disp);
      }
    }

    Set<SInvokable> regMethods = new HashSet<>(this.methods);
    regMethods.removeAll(methods);
    assert regMethods.isEmpty();
    return regMethods.isEmpty();
  }

  @Override
  public void recordNewClass(final MixinDefinition clazz) {
    for (Dispatchable disp : clazz.getInstanceDispatchables().values()) {
      if (disp instanceof SInvokable) {
        assert ((SInvokable) disp).getHolder() != null;
      }
    }

    for (SInvokable disp : clazz.getFactoryMethods().values()) {
      assert disp.getHolder() != null;
    }

    super.recordNewClass(clazz);
  }

  public void reportCall(final ExpressionNode send, final SourceSection... section) {
    for (SourceSection s : section) {
      putIntoMap(s, send);
    }
  }

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
