/**
 *
 */
package som.langserv.simple;

import org.antlr.v4.runtime.Token;

import simple.AbstractSimpleListener;
import simple.SimpleLanguageParser.FunctionContext;
import simple.SimpleLanguageParser.If_statementContext;
import simple.SimpleLanguageParser.Return_statementContext;
import simple.SimpleLanguageParser.While_statementContext;
import som.langserv.structure.SemanticTokenType;


public class SimpleTokenCollector extends AbstractSimpleListener {

  private final SimpleNodeFactory factory;

  public SimpleTokenCollector(final SimpleNodeFactory factory) {
    this.factory = factory;
  }

  @Override
  public void enterFunction(final FunctionContext ctx) {
    Token fn = ctx.getStart();
    factory.addSemanticToken(fn, SemanticTokenType.KEYWORD);
  }

  @Override
  public void exitFunction(final FunctionContext ctx) {
    factory.finishFunction(ctx.getStop());
  }

  @Override
  public void enterWhile_statement(final While_statementContext ctx) {
    Token whileT = ctx.getStart();
    factory.addSemanticToken(whileT, SemanticTokenType.KEYWORD);
  }

  @Override
  public void exitWhile_statement(final While_statementContext ctx) {}

  @Override
  public void enterIf_statement(final If_statementContext ctx) {
    Token ifT = ctx.getStart();
    factory.addSemanticToken(ifT, SemanticTokenType.KEYWORD);
  }

  @Override
  public void exitIf_statement(final If_statementContext ctx) {
    if (ctx.getChildCount() > 5) {
      Token elseT = (Token) ctx.getChild(5).getPayload();
      factory.addSemanticToken(elseT, SemanticTokenType.KEYWORD);
    }
  }

  @Override
  public void enterReturn_statement(final Return_statementContext ctx) {}

  @Override
  public void exitReturn_statement(final Return_statementContext ctx) {
    Token returnT = ctx.getStart();
    factory.addSemanticToken(returnT, SemanticTokenType.KEYWORD);
  }
}
