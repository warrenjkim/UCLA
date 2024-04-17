class DuplicateMethod {
	public static void main(String[] a){
		System.out.println(new A().run());
	}
}

class A {
	public int run() {
		int x;
		return x;
	}
	public int run() {
		int x;
		return x;
	}
}
