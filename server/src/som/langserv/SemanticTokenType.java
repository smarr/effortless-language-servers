package som.langserv;

public enum SemanticTokenType {
  TYPE("type"),
  CLASS("class"),
  ENUM("enum"),
  INTERFACE("interface"),
  STRUCT("struct"),
  TYPE_PARAMETER("typeParameter"),
  PARAMETER("parameter"),
  VARIABLE("variable"),
  PROPERTY("property"),
  ENUM_MEMBER("enumMember"),
  EVENT("event"),
  FUNCTION("function"),
  METHOD("method"),
  MACRO("macro"),
  KEYWORD("keyword"),
  MODIFIER("modifier"),
  COMMENT("comment"),
  STRING("string"),
  NUMBER("number"),
  REGEXP("regexp"),
  OPERATOR("operator");

  public final String name;

  private SemanticTokenType(final String name) {
    this.name = name;
  }

  public static SemanticTokenType from(final int ordinal) {
    return values()[ordinal];
  }
}
