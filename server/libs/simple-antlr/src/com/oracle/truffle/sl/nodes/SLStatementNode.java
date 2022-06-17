package com.oracle.truffle.sl.nodes;

import org.antlr.v4.runtime.Token;

public abstract class SLStatementNode {

  public Token getLastName() {
    throw new RuntimeException("subclass responsibility");
  }

  public boolean isRead() {
    return false;
  }
}
