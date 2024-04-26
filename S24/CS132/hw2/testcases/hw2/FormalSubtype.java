class FormalSubtype {
	public static void main(String[] a){
		A f;
        f = new A().run(new C(), new C());
	}
}

class A {
	public A run(A d, B e) {
		int x;
		return d;
	}
}

class B extends A {
}

class C extends B {
}
