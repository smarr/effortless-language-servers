package com.oracle.truffle.sl.nodes;

import org.antlr.v4.runtime.Token;

public class SLRead extends SLExpressionNode {

  public final SLExpressionNode assignmentName;

  public SLRead(SLExpressionNode assignmentName) {
    this.assignmentName = assignmentName;
  }

  @Override
  public Token getLastName() {
    return assignmentName.getLastName();
  }

  @Override
  public boolean isRead() {
    return true;
  }
}
