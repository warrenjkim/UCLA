import minijava.syntaxtree.*;

public class Context {
    private ClassSymbol currClass;
    private MethodSymbol currMethod;

    public Context() {
        this.currClass = new ClassSymbol();
        this.currMethod = new MethodSymbol();
    }

    public Context(ClassSymbol currClass, MethodSymbol currMethod) {
        this.currClass = new ClassSymbol(currClass);
        this.currMethod = new MethodSymbol(currMethod);
    }

    public ClassSymbol Class() {
        return this.currClass;
    }

    public String ClassName() {
        return this.currClass.Name();
    }

    public MethodSymbol Method() {
        return this.currMethod;
    }

    public String MethodName() {
        return this.currMethod.Name();
    }

    public void SetClass(ClassSymbol currClass) {
        this.currClass = currClass;
    }

    public void SetMethod(MethodSymbol currMethod) {
        this.currMethod = currMethod;
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
