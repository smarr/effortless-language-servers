package som.langserv.simple;

import java.util.List;

import org.antlr.v4.runtime.TokenStream;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.sl.SLLanguage;
import com.oracle.truffle.sl.parser.SimpleLanguageParser;


public class SimpleParser extends SimpleLanguageParser {

  private SimpleStructures struturalProbe;

  public SimpleParser(final TokenStream input, final SimpleStructures structuralProbe,
      final SLLanguage language,
      final Source source) {
    // this.struturalProbe = structuralProbe;
    super(input);
    this.struturalProbe = structuralProbe;
    super.parseSL(language, source);
    addTokenPositions(getlocalTokenPositions());
    // TODO Auto-generated constructor stub
  }

  protected void addTokenPositions(final List<Integer> tokens) {
    struturalProbe.addAllTokenPosition(tokens);

  }

}
