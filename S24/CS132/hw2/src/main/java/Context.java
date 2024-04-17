import minijava.syntaxtree.*;

public class Context {
    private ClassSymbol currClass;
    private MethodSymbol currMethod;

    public ClassSymbol Class() {
        return this.currClass;
    }

    public MethodSymbol Method() {
        return this.currMethod;
    }

    public void SetClass(Identifier name) {
        this.currClass = new ClassSymbol(name);
    }

    public void SetClass(Identifier name, Identifier parent) {
        this.currClass = new ClassSymbol(name, parent);
    }

    public void SetMethod(Identifier name, Node returnType) {
        this.currMethod = new MethodSymbol(name, returnType);
    }

    public void SetMethod(String name, Node returnType) {
        this.currMethod = new MethodSymbol(name, returnType);
    }

    public void SetMethod(Identifier name, TypeStruct returnType) {
        this.currMethod = new MethodSymbol(name, returnType);
    }

    public void SetMethod(String name, TypeStruct returnType) {
        this.currMethod = new MethodSymbol(name, returnType);
    }
}
