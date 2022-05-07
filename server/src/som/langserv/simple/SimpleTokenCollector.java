/**
 *
 */
package som.langserv.simple;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import simple.SimpleLanguageListener;
import simple.SimpleLanguageParser.ArithmeticContext;
import simple.SimpleLanguageParser.BlockContext;
import simple.SimpleLanguageParser.ExpressionContext;
import simple.SimpleLanguageParser.FactorContext;
import simple.SimpleLanguageParser.FunctionContext;
import simple.SimpleLanguageParser.If_statementContext;
import simple.SimpleLanguageParser.Logic_factorContext;
import simple.SimpleLanguageParser.Logic_termContext;
import simple.SimpleLanguageParser.Member_expressionContext;
import simple.SimpleLanguageParser.Return_statementContext;
import simple.SimpleLanguageParser.SimplelanguageContext;
import simple.SimpleLanguageParser.StatementContext;
import simple.SimpleLanguageParser.TermContext;
import simple.SimpleLanguageParser.While_statementContext;
import som.langserv.SemanticTokenType;


public class SimpleTokenCollector implements SimpleLanguageListener {

  private final SimpleStructures probe;

  public SimpleTokenCollector(final SimpleStructures probe) {
    this.probe = probe;
  }

  @Override
  public void enterEveryRule(final ParserRuleContext arg0) {}

  @Override
  public void exitEveryRule(final ParserRuleContext arg0) {}

  @Override
  public void visitErrorNode(final ErrorNode arg0) {}

  @Override
  public void visitTerminal(final TerminalNode arg0) {}

  @Override
  public void enterSimplelanguage(final SimplelanguageContext ctx) {}

  @Override
  public void exitSimplelanguage(final SimplelanguageContext ctx) {}

  @Override
  public void enterFunction(final FunctionContext ctx) {
    Token fn = ctx.getStart();
    probe.addSemanticToken(fn, SemanticTokenType.KEYWORD);
  }

  @Override
  public void exitFunction(final FunctionContext ctx) {}

  @Override
  public void enterBlock(final BlockContext ctx) {}

  @Override
  public void exitBlock(final BlockContext ctx) {}

  @Override
  public void enterStatement(final StatementContext ctx) {}

  @Override
  public void exitStatement(final StatementContext ctx) {}

  @Override
  public void enterWhile_statement(final While_statementContext ctx) {
    Token whileT = ctx.getStart();
    probe.addSemanticToken(whileT, SemanticTokenType.KEYWORD);
  }

  @Override
  public void exitWhile_statement(final While_statementContext ctx) {}

  @Override
  public void enterIf_statement(final If_statementContext ctx) {
    Token ifT = ctx.getStart();
    probe.addSemanticToken(ifT, SemanticTokenType.KEYWORD);
  }

  @Override
  public void exitIf_statement(final If_statementContext ctx) {
    if (ctx.getChildCount() > 5) {
      Token elseT = (Token) ctx.getChild(5).getPayload();
      probe.addSemanticToken(elseT, SemanticTokenType.KEYWORD);
    }
  }

  @Override
  public void enterReturn_statement(final Return_statementContext ctx) {}

  @Override
  public void exitReturn_statement(final Return_statementContext ctx) {
    Token returnT = ctx.getStart();
    probe.addSemanticToken(returnT, SemanticTokenType.KEYWORD);
  }

  @Override
  public void enterExpression(final ExpressionContext ctx) {}

  @Override
  public void exitExpression(final ExpressionContext ctx) {}

  @Override
  public void enterLogic_term(final Logic_termContext ctx) {}

  @Override
  public void exitLogic_term(final Logic_termContext ctx) {}

  @Override
  public void enterLogic_factor(final Logic_factorContext ctx) {}

  @Override
  public void exitLogic_factor(final Logic_factorContext ctx) {}

  @Override
  public void enterArithmetic(final ArithmeticContext ctx) {}

  @Override
  public void exitArithmetic(final ArithmeticContext ctx) {}

  @Override
  public void enterTerm(final TermContext ctx) {}

  @Override
  public void exitTerm(final TermContext ctx) {}

  @Override
  public void enterFactor(final FactorContext ctx) {}

  @Override
  public void exitFactor(final FactorContext ctx) {}

  @Override
  public void enterMember_expression(final Member_expressionContext ctx) {
    // TODO Auto-generated method stub

  }

  @Override
  public void exitMember_expression(final Member_expressionContext ctx) {
    // TODO Auto-generated method stub

  }

}
