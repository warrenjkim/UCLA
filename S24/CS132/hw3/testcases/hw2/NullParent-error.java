class NullParent {
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

class B extends C {
    public int bar() {
        int x;
        return y;
    }
}
