class TestVarInitIf {
    public static void main(String[] a) {
        System.out.println((new InitTester().test(5)));
    }
}

class InitTester {
  InitTester x;
    public int test(int n) {
        System.out.println((this.y()).test(n));
        return 1;
    }

  public InitTester y() {
    return x;
  }
}
