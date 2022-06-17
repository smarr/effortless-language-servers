package som.langserv.structure;

public class Pair<T1, T2> {

  public final T1 v1;
  public final T2 v2;

  public Pair(final T1 v1, final T2 v2) {
    this.v1 = v1;
    this.v2 = v2;
  }

  @Override
  public String toString() {
    return "Pair(" + v1.toString() + ", " + v2.toString() + ")";
  }
}
