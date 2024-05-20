class Main {
  public static void main(String[] args) {
    MutualRecursion e;

    e = new MutualRecursion();
    System.out.println(e.methodA(10));
  }
}

class MutualRecursion {
  public int methodA(int n) {
    int res;
    if (n < 1) {
      res = 0;
    } else {
      res = 1 + (this.methodB(n - 1));
    }

    return res;
  }

  public int methodB(int n) {
    int res;
    if (n < 1) {
      res = 0;
    } else {
      res = 1 + (this.methodA(n - 1));
    }

    return res;
  }
}
