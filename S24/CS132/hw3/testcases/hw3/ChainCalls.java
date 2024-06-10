class Main {
    public static void main(String[] args) {
        A x;

        x = new A();

        System.out.println(((((x.foo()).bar()).baz()).fuck()).fuck());
    }
}

class A {
    public B foo() {
        return new B();
    }
}

class B {
    public C bar() {
        return new C();
    }
}

class C {
    public D baz() {
        return new D();
    }
}

class D {
    public E fuck() {
        return new E();
    }
}

class E {
    public int fuck() {
        return 42;
    }
}
