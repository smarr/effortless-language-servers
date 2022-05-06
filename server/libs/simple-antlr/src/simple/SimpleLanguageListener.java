// Generated from SimpleLanguage.g4 by ANTLR 4.9.2
package simple;

// DO NOT MODIFY - generated from SimpleLanguage.g4 using "mx create-sl-parser"

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.sl.SLLanguage;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLRootNode;
import com.oracle.truffle.sl.nodes.SLStatementNode;
import com.oracle.truffle.sl.parser.SLParseError;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SimpleLanguageParser}.
 */
public interface SimpleLanguageListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#simplelanguage}.
	 * @param ctx the parse tree
	 */
	void enterSimplelanguage(SimpleLanguageParser.SimplelanguageContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#simplelanguage}.
	 * @param ctx the parse tree
	 */
	void exitSimplelanguage(SimpleLanguageParser.SimplelanguageContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(SimpleLanguageParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(SimpleLanguageParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(SimpleLanguageParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(SimpleLanguageParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(SimpleLanguageParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(SimpleLanguageParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void enterWhile_statement(SimpleLanguageParser.While_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void exitWhile_statement(SimpleLanguageParser.While_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_statement(SimpleLanguageParser.If_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_statement(SimpleLanguageParser.If_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#return_statement}.
	 * @param ctx the parse tree
	 */
	void enterReturn_statement(SimpleLanguageParser.Return_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#return_statement}.
	 * @param ctx the parse tree
	 */
	void exitReturn_statement(SimpleLanguageParser.Return_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(SimpleLanguageParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(SimpleLanguageParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#logic_term}.
	 * @param ctx the parse tree
	 */
	void enterLogic_term(SimpleLanguageParser.Logic_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#logic_term}.
	 * @param ctx the parse tree
	 */
	void exitLogic_term(SimpleLanguageParser.Logic_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#logic_factor}.
	 * @param ctx the parse tree
	 */
	void enterLogic_factor(SimpleLanguageParser.Logic_factorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#logic_factor}.
	 * @param ctx the parse tree
	 */
	void exitLogic_factor(SimpleLanguageParser.Logic_factorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#arithmetic}.
	 * @param ctx the parse tree
	 */
	void enterArithmetic(SimpleLanguageParser.ArithmeticContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#arithmetic}.
	 * @param ctx the parse tree
	 */
	void exitArithmetic(SimpleLanguageParser.ArithmeticContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(SimpleLanguageParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(SimpleLanguageParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(SimpleLanguageParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(SimpleLanguageParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleLanguageParser#member_expression}.
	 * @param ctx the parse tree
	 */
	void enterMember_expression(SimpleLanguageParser.Member_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleLanguageParser#member_expression}.
	 * @param ctx the parse tree
	 */
	void exitMember_expression(SimpleLanguageParser.Member_expressionContext ctx);
}