class Main {
    public static void main(String[] a) {
        Main x;
    }
}

class B extends Main {
    public B foo() {
        return this;
    }
}

class C extends B {
    public Main bar() {
        return this;
    }
}
