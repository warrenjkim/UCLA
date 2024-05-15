class Main {
  public static void main(String[] a) {
    System.out.println(new A().foo(3));
  }
}

class A {
  int[] y;

  public int foo(int x) {
    int dd;
    dd = this.run(x);
    if (0 < dd) {
      System.out.println(dd);
      dd = 99;
    } else {
      y[1] = 42;
      System.out.println(y[1]);
    }
    return dd;
  }

  public int run(int x) {
    if (0 < x) {
      x = 1;
    } else {
      y = new int[x];
      y[(x - 1)] = 2;
      System.out.println(y[(x - 1)]);
    }
    return x;
  }
}
