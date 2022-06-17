package com.oracle.truffle.sl.parser;

import com.oracle.truffle.api.source.Source;

public class SLParseError extends RuntimeException {

  private static final long serialVersionUID = 3446039135206501294L;

  public final int line;
  public final int col;
  public final int length;
  public final String format;

  public SLParseError(Source source, int line, int col, int length,
      String format) {
    this.line = line;
    this.col = col;
    this.length = length;
    this.format = format;
  }
}
