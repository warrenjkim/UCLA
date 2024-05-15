class Main {
	public static void main(String[] a){
		System.out.println(new A().run());
	}
}

class A {
	public int run() {
		int[] a;
		a = new int[20];
    a[0] = 2;
    System.out.println(a.length);
    a[19] = 4;
		System.out.println(a[0]);
		System.out.println(a[19]);
    return 0;
	}
}
