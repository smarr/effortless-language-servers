package simple;

import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.sl.SLLanguage;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLStatementNode;

import simple.nodes.SimpleString;

public class SLNodeFactory {

  public SLNodeFactory(SLLanguage language, Source source) {}

  public Map<String, RootCallTarget> getAllFunctions() {
    return null;
  }

  public void addFormalParameter(Token identifier) {}

  public SLExpressionNode createBinary(Token op, SLExpressionNode result,
      SLExpressionNode result2) {
    return null;
  }

  public SLExpressionNode createRead(SLExpressionNode assignmentName) {
    return assignmentName;
  }

  public SLExpressionNode createReadProperty(SLExpressionNode receiver,
      SLExpressionNode nestedAssignmentName) {
    return null;
  }

  public SLExpressionNode createAssignment(SLExpressionNode assignmentName,
      SLExpressionNode result) {
    return null;
  }

  public SLStatementNode createBreak(Token b) {
    return null;
  }

  public SLStatementNode createContinue(Token c) {
    return null;
  }

  public SLExpressionNode createCall(SLExpressionNode receiver,
      List<SLExpressionNode> parameters, Token e) {
    return null;
  }

  public SLExpressionNode createWriteProperty(
      SLExpressionNode assignmentReceiver, SLExpressionNode assignmentName,
      SLExpressionNode result) {
    return null;
  }

  public SLStatementNode createDebugger(Token d) {
    return null;
  }

  public SLExpressionNode createStringLiteral(Token identifier, boolean isLiteral) {
    return new SimpleString(identifier, isLiteral);
  }

  public SLStatementNode createIf(Token i, SLExpressionNode result,
      SLStatementNode result2, SLStatementNode elsePart) {
    return null;
  }

  public SLExpressionNode createNumericLiteral(Token numeric_LITERAL) {
    return null;
  }

  public SLExpressionNode createParenExpression(SLExpressionNode result,
      int startIndex, int i) {
    return null;
  }

  public SLStatementNode createReturn(Token r, SLExpressionNode value) {
    return null;
  }

  public SLStatementNode createWhile(Token w, SLExpressionNode result,
      SLStatementNode result2) {
    return null;
  }

  public SLStatementNode finishBlock(List<SLStatementNode> body, int startIndex,
      int length) {
    return null;
  }

  public void finishFunction(SLStatementNode result) {}

  public void startFunction(Token identifier, Token s) {}

  public void startBlock() {}
}
