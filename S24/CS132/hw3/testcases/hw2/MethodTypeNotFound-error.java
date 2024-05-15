class MethodTypeNotFound {
    public static void main(String[] a) {
        System.out.println(new A().run());
    }
}

class A {
    int y;
    int z;

    public int run() {
        int x;
        return x;
    }
}

class B extends A {
    public D bar() {
        int x;
        D d;
        d = new D();
        return d;
    }
}

class C extends B {
    public int bar() {
        int[] number;
        number = new int[y];
        return y;
    }
}

