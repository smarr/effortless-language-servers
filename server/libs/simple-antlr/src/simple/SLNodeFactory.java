package simple;

import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.sl.SLLanguage;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLStatementNode;

public class SLNodeFactory {

  public SLNodeFactory(SLLanguage language, Source source) {
    // TODO Auto-generated constructor stub
  }

  public Map<String, RootCallTarget> getAllFunctions() {
    // TODO Auto-generated method stub
    return null;
  }

  public void addFormalParameter(Token identifier) {
    // TODO Auto-generated method stub

  }

  public SLExpressionNode createBinary(Token op, SLExpressionNode result,
      SLExpressionNode result2) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLExpressionNode createRead(SLExpressionNode assignmentName) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLExpressionNode createReadProperty(SLExpressionNode receiver,
      SLExpressionNode nestedAssignmentName) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLExpressionNode createAssignment(SLExpressionNode assignmentName,
      SLExpressionNode result) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLStatementNode createBreak(Token b) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLStatementNode createContinue(Token c) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLExpressionNode createCall(SLExpressionNode receiver,
      List<SLExpressionNode> parameters, Token e) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLExpressionNode createWriteProperty(
      SLExpressionNode assignmentReceiver, SLExpressionNode assignmentName,
      SLExpressionNode result) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLStatementNode createDebugger(Token d) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLExpressionNode createStringLiteral(Token identifier, boolean b) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLStatementNode createIf(Token i, SLExpressionNode result,
      SLStatementNode result2, SLStatementNode elsePart) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLExpressionNode createNumericLiteral(Token numeric_LITERAL) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLExpressionNode createParenExpression(SLExpressionNode result,
      int startIndex, int i) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLStatementNode createReturn(Token r, SLExpressionNode value) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLStatementNode createWhile(Token w, SLExpressionNode result,
      SLStatementNode result2) {
    // TODO Auto-generated method stub
    return null;
  }

  public SLStatementNode finishBlock(List<SLStatementNode> body, int startIndex,
      int i) {
    // TODO Auto-generated method stub
    return null;
  }

  public void finishFunction(SLStatementNode result) {
    // TODO Auto-generated method stub

  }

  public void startFunction(Token identifier, Token s) {
    // TODO Auto-generated method stub

  }

  public void startBlock() {
    // TODO Auto-generated method stub

  }

}
