package som.langserv.structure;

import java.util.List;

import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpContext;
import org.eclipse.lsp4j.SymbolInformation;


public interface DocumentData {
  List<? extends DocumentSymbol> getRootSymbols();

  Hover getHover(Position position);

  SignatureHelp getSignatureHelp(Position position, SignatureHelpContext context);

  void symbols(List<SymbolInformation> results, String query);

  Pair<LanguageElementId, Range> getElement(Position pos);

  void lookupDefinitions(Pair<LanguageElementId, Range> element,
      List<LocationLink> definitions);

  void lookupDefinitionsLocation(Pair<LanguageElementId, Range> element,
      List<Location> definitions);

  void lookupReferences(Pair<LanguageElementId, Range> element,
      List<Location> references);

  List<DocumentHighlight> getHighlight(Position position);
}
