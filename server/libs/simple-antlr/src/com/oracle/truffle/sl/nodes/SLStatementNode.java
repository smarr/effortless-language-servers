package com.oracle.truffle.sl.nodes;

import org.antlr.v4.runtime.Token;

public class SLStatementNode {

  public Token getLastName() {
    throw new RuntimeException("subclass responsibility");
  }

  public boolean isRead() {
    return false;
  }
}
