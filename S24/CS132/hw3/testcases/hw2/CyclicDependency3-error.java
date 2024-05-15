class CyclicDependency {
	public static void main(String[] a){
		System.out.println(new A().run());
	}
}

class A extends B {
	public int run() {
		int x;
		return x;
	}
}

class B extends C {
	public int foo() {
		int x;
		return x;
	}
}

class C extends A {
	public int dol() {
		int x;
		return x;
	}
}
