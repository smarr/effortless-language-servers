package com.oracle.truffle.sl.nodes;


public class SLExpressionNode extends SLStatementNode {
  protected final SLStatementNode[] children;

  public SLExpressionNode(SLStatementNode... children) {
    this.children = children;
  }
}
