class TwoLevels {
	public static void main(String[] a){
		System.out.println(new A().run());
	}
}

class A {
    int y;
	public int run() {
		int x;
		return x;
	}
}

class B extends A {
	public int bar() {
		int x;
		return y;
	}
}

class C extends B {
	public int bar() {
		int x;
		return y;
	}
}
