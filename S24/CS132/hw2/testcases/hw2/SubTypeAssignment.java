class SubTypeAssignment {
    public static void main(String[] a) {
        System.out.println(new A().run());
    }
}

class A {
    B grandparent_field;
    int y;

    public int run() {
        int x;
        return x;
    }
}

class B extends A {
}

class C extends A {
    public A bar() {
        int x;
        grandparent_field = new B();
        return grandparent_field;
    }
}

