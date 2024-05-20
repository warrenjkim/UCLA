class Main {
  public static void main(String[] args) {
    A a;
    B b;
    int x;
    int y;

    a = new A();
    b = new B();

    x = (a.setB(b));
    y = (b.setA(a));

    System.out.println((a.startRecursion(1)));
    System.out.println((b.startRecursion(5)));
  }
}

class A {
  B b;
  int state;

  public int setB(B bInstance) {
    b = bInstance;
    state = 0;
    return 0;  // Since methods can't be void, return 0.
  }

  public int startRecursion(int n) {
    state = n;
    return (this.recursiveMethodA(n));
  }

  public int recursiveMethodA(int n) {
    int result;
    result = 0;
    if (n < 1) {
      result = state;
    } else {
      state = state + 1;
      result = state + (b.recursiveMethodB(n - 1));
    }
    return result;
  }
}

class B {
  A a;
  int state;

  public int setA(A aInstance) {
    a = aInstance;
    state = 0;
    return 0;  // Since methods can't be void, return 0.
  }

  public int startRecursion(int n) {
    state = n;
    return (this.recursiveMethodB(n));
  }

  public int recursiveMethodB(int n) {
    int result;
    result = 0;
    if (n < 1) {
      result = state;
    } else {
      state = state + 2;
      result = state + (a.recursiveMethodA(n - 1));
    }
    return result;
  }
}
