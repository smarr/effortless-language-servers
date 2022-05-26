package com.oracle.truffle.sl.nodes;

import java.util.List;

public class SLBlock extends SLStatementNode {
  public final List<SLStatementNode> body;
  public final int startIndex;
  public final int length;

  public SLBlock(List<SLStatementNode> body, int startIndex, int length) {
    this.body = body;
    this.startIndex = startIndex;
    this.length = length;
  }
}
