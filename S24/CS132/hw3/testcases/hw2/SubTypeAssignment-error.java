class SubTypeAssignment {
    public static void main(String[] a) {
        System.out.println(new A().run());
    }
}

class A {
    A grandparent_field;
    int y;

    public int run() {
        int x;
        return x;
    }
}

class B extends A {
}

class C extends B {
    public C bar() {
        int x;
        grandparent_field = new C();
        return grandparent_field;
    }
}
