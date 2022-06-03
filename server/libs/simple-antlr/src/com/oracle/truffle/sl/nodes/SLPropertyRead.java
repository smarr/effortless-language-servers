package com.oracle.truffle.sl.nodes;

import org.antlr.v4.runtime.Token;

public class SLPropertyRead extends SLExpressionNode {

  public SLPropertyRead(SLExpressionNode rcvr, SLExpressionNode nestedAssignmentName) {
    super(rcvr, nestedAssignmentName);
  }

  @Override
  public Token getLastName() {
    return children[1].getLastName();
  }
}
