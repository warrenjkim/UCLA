class ParentReturn {
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
    public int bar() {
        return y;
    }
}

class C extends B {
    public A bar() {
        return new C();
    }
}
