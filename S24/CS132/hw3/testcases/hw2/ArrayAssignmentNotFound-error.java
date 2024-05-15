class ArrayAssignmentNotFound {
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
    public int bar() {
        int x;
        return y;
    }
}

class C extends B {
    public int bar() {
        int[] number;
        number = new int[y];
        a[10] = 10;
        return y;
    }
}

