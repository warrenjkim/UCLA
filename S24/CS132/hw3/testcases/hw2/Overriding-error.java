class Overriding {
    public static void main(String[] args) {
        A obj;
        obj = new D();
        System.out.println(obj.run(10));
    }
}

class A {
    public int run(int d) {
        return d;
    }
}

class B extends A {
}

class C extends B {
    public B run(int d) {
        return d;
    }
}

class D extends C {
}


