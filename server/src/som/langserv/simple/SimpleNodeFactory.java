package som.langserv.simple;

import java.util.List;

import org.antlr.v4.runtime.Token;

import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLStatementNode;

import simple.SLNodeFactory;
import simple.nodes.SimpleString;
import som.langserv.SemanticTokenType;


public class SimpleNodeFactory extends SLNodeFactory {

  private final SimpleStructures probe;

  public SimpleNodeFactory(final SimpleStructures probe) {
    super(null, null);
    this.probe = probe;
  }

  @Override
  public void startFunction(final Token identifier, final Token s) {
    probe.addSemanticToken(identifier, SemanticTokenType.FUNCTION);
  }

  @Override
  public void addFormalParameter(final Token identifier) {
    probe.addSemanticToken(identifier, SemanticTokenType.PARAMETER);
  }

  @Override
  public SLStatementNode createBreak(final Token b) {
    probe.addSemanticToken(b, SemanticTokenType.KEYWORD);
    return super.createBreak(b);
  }

  @Override
  public SLStatementNode createContinue(final Token c) {
    probe.addSemanticToken(c, SemanticTokenType.KEYWORD);
    return super.createContinue(c);
  }

  @Override
  public SLStatementNode createDebugger(final Token d) {
    probe.addSemanticToken(d, SemanticTokenType.KEYWORD);
    return super.createDebugger(d);
  }

  @Override
  public SLExpressionNode createBinary(final Token op, final SLExpressionNode result,
      final SLExpressionNode result2) {
    probe.addSemanticToken(op, SemanticTokenType.OPERATOR);
    return super.createBinary(op, result, result2);
  }

  @Override
  public SLExpressionNode createStringLiteral(final Token identifier,
      final boolean isLiteral) {
    if (isLiteral) {
      probe.addSemanticToken(identifier, SemanticTokenType.STRING);
    }
    return super.createStringLiteral(identifier, isLiteral);
  }

  @Override
  public SLExpressionNode createNumericLiteral(final Token numeric_LITERAL) {
    probe.addSemanticToken(numeric_LITERAL, SemanticTokenType.NUMBER);
    return super.createNumericLiteral(numeric_LITERAL);
  }

  @Override
  public SLExpressionNode createRead(final SLExpressionNode assignmentName) {
    if (assignmentName instanceof SimpleString) {
      probe.addSemanticToken(
          ((SimpleString) assignmentName).identifier,
          SemanticTokenType.VARIABLE);
    }
    return super.createRead(assignmentName);
  }

  @Override
  public SLExpressionNode createCall(final SLExpressionNode receiver,
      final List<SLExpressionNode> parameters, final Token e) {
    if (receiver instanceof SimpleString) {
      probe.addSemanticToken(
          ((SimpleString) receiver).identifier,
          SemanticTokenType.FUNCTION);
    }
    return super.createCall(receiver, parameters, e);
  }
}
