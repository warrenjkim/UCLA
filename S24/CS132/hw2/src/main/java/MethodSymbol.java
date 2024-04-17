import minijava.syntaxtree.Identifier;
import minijava.syntaxtree.Node;
import java.util.LinkedList;

public class MethodSymbol {
    private String name;
    private TypeStruct returnType;
    private LinkedList<SymbolTable> scopes;

    public MethodSymbol(Identifier name, Node returnType) {
        this.name = name.f0.tokenImage;
        this.returnType = new TypeStruct(returnType);
        this.scopes = new LinkedList<>();
        this.EnterScope();
    }

    public MethodSymbol(String name, Node returnType) {
        this.name = name;
        this.returnType = new TypeStruct(returnType);
        this.scopes = new LinkedList<>();
        this.EnterScope();
    }

    public MethodSymbol(Identifier name, TypeStruct returnType) {
        this.name = name.f0.tokenImage;
        this.returnType = returnType;
        this.scopes = new LinkedList<>();
        this.EnterScope();
    }

    public MethodSymbol(String name, TypeStruct returnType) {
        this.name = name;
        this.returnType = returnType;
        this.scopes = new LinkedList<>();
        this.EnterScope();
    }

    public String MethodName() {
        return this.name;
    }

    public TypeStruct ReturnType() {
        return this.returnType;
    }

    public void EnterScope() {
        this.scopes.push(new SymbolTable());
    }

    public void ExitScope() {
        if (!this.scopes.isEmpty()) {
            this.scopes.pop();
        }
    }

    public TypeStruct AddVariable(Identifier key, Node value) {
        return this.scopes.peek().AddSymbol(key, new TypeStruct(value));
    }

    public TypeStruct AddVariable(Identifier key, TypeStruct value) {
        return this.scopes.peek().AddSymbol(key, value);
    }

    public LinkedList<SymbolTable> VariableScopes() {
        return this.scopes;
    }

    public TypeStruct FindVariable(Identifier key) {
        for (SymbolTable scope : this.scopes) {
            TypeStruct var = scope.GetType(key);
            if (var != null) {
                return var;
            }
        }

        return null;
    }
}
