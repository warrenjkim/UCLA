class CyclicDependency {
	public static void main(String[] a){
		System.out.println(new A().run());
	}
}

class A extends C {
	public int run() {
		int x;
		return x;
	}
}

class B extends A {
	public int foo() {
		int x;
		return x;
	}
}

class C extends B {
	public int dol() {
		int x;
		return x;
	}
}
