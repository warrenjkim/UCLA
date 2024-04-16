import minijava.syntaxtree.Identifier;
import minijava.syntaxtree.Type;

public class ClassSymbol implements Symbol {
    private Identifier name;
    private Type type;
    private Type parent;
    private SymbolTable fields;
    private SymbolTable methods;

    public ClassSymbol(Identifier name, Type type) {
        this.name = name;
        this.type = type;
        this.parent = null;
        this.fields = new SymbolTable();
        this.methods = new SymbolTable();
    }

    public ClassSymbol(Identifier name, Type type, Type parent) {
        this.name = name;
        this.type = type;
        this.parent = parent;
        this.fields = new SymbolTable();
        this.methods = new SymbolTable();
    }

    @Override
    public Type GetType() {
        return this.type;
    }

    @Override
    public Identifier GetIdentifier() {
        return this.name;
    }

    public Type GetParent() {
        return this.parent;
    }

    public void AddField(Identifier key, Type value) {
        this.fields.AddSymbol(key, value);
    }

    public void AddMethod(MethodSymbol method) {
        this.methods.AddSymbol(method.GetIdentifier(), method.GetType());
    }
}

