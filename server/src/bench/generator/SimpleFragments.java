package bench.generator;

public class SimpleFragments {

  public static String[] basicFunctions = new String[] {
      "function %s(a) { return a; }",
      "function %s(a) { return a * 2; }",
      "function %s(a) { return a + 1; }",
      "function %s(a) { return a - 1; }",
      "function %s(f, v) { return f(v); }",
      """
          function %s() {
            foo = new();
            foo.bar = "3";
            foo.prop = far.bar;
          }
          """,
      """
          function %s(n) {
            obj = new();
            obj.i = 0;
            while (obj.i < n) {
              obj.i = obj.i + 1;
            }
            return obj.i;
          }
          """,
      """
          function %s(num) {
            if (num < 1) {return 0;}
            n1 = 0;
            n2 = 1;
            i = 1;
            while (i < num) {
              next = n2 + n1;
              n1 = n2;
              n2 = next;
              i = i + 1;
            }
            return n2;
          }
          """,
      """
          function loop(n) {
            obj = new();
            obj.i = 0;
            obj.sum = 0;
            while (obj.i <= n) {
              obj.sum = obj.sum + obj.i;
              obj.i = obj.i + 1;
            }
            return obj.sum;
          }
          """,
      """
          function %s() {
            i = 1;
            while (i <= 10) {
              println(i + ": " + fib(i));
              i = i + 1;
            }
          }
          """,
      """
          function %s() {
            context = java(\"org.graalvm.polyglot.Context\").create();
            context.eval("sl", "function createObject() { return new(); }");
            context.eval("sl", "function getPrimitive() { return 42; }");
            innerBindings = context.getBindings("sl");
            println(innerBindings.createObject());
            println(innerBindings.getPrimitive());
            context.close();

            // this is expected fail as
            innerBindings.getPrimitive();
          }
          """
  };
}
