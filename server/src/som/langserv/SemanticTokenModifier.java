package som.langserv;

public enum SemanticTokenModifier {
  DECLARATION("declaration"),
  DEFINITION("definition"),
  READ_ONLY("readonly"),
  STATIC("static"),
  DEPRECATED("deprecated"),
  ABSTRACT("abstract"),
  ASYNC("async"),
  MODIFICATION("modification"),
  DOCUMENTATION("documentation"),
  DEFAULT_LIBRARY("defaultLibrary");

  public final String name;

  private SemanticTokenModifier(final String name) {
    this.name = name;
  }
}
