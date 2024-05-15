class TwoLevels {
	public static void main(String[] a){
    A x;
    B y;
    x = new D();
		System.out.println(x.run());
    y = new D();
		System.out.println(y.run());
    y = new C();
		System.out.println(y.bar());
    y = new B();
		System.out.println(y.run());
    x = new A();
		System.out.println(x.run());

	}
}

class A {
    int y;
	public int run() {
		int x;
    x = 4;
		return x;
	}
}

class B extends A {
	public int run() {
		int x;
    y = 3;
		return this.bar();
	}

	public int bar() {
		int x;
    x = 3;
		return y;
	}
}

class C extends B {
	public int run() {
		int x;
    x = 2;
		return x;
	}
	public int bar() {
    y = 2;
		return y;
	}
}

class D extends C {
  public int run() {
    y = 1;
    return this.bar();
  }
}
