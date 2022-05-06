package com.oracle.truffle.sl.parser;

import com.oracle.truffle.api.source.Source;

public class SLParseError extends RuntimeException {

  private static final long serialVersionUID = 3446039135206501294L;

  public SLParseError(Source source, int line, int col, int length,
      String format) {
    // TODO Auto-generated constructor stub
  }

}
