class TestVarInitFunction {
  public static void main(String[] args) {
    t6 x;
    int[] y;
    x = new t6();
    y = new int[5];
    System.out.println((x.init(y)));
    System.out.println(y[4]);
  }
}

class t6 {
  t6 y;

  public t6 vl() {
    return y;
  }

  public int init(int[] x) {
    x = new int[2];
    x[1] = 1;
    return x.length;
  }
}
