package som.langserv.structure;

public enum ParseContextKind {
  /** In primary position, i.e., not after a navigation. */
  Primary,

  /** After a navigation indicator. */
  Navigation
}
