package simple.nodes;

import org.antlr.v4.runtime.Token;

import com.oracle.truffle.sl.nodes.SLExpressionNode;

public class SimpleString extends SLExpressionNode {

  public final Token   identifier;
  public final boolean isLiteral;

  public SimpleString(Token identifier, boolean isLiteral) {
    this.identifier = identifier;
    this.isLiteral = isLiteral;
  }

  @Override
  public Token getLastName() {
    return identifier;
  }

  @Override
  public String toString() {
    return "SimpleString(" + identifier.getText() + ", " + isLiteral + ")";
  }
}
