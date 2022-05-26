package som.langserv.structure;

import java.util.List;

import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Position;


public interface DocumentData {
  List<? extends DocumentSymbol> getRootSymbols();

  Hover getHover(Position position);
}
