import java.util.LinkedList;

import minijava.syntaxtree.Identifier;
import minijava.syntaxtree.Type;

public class MethodSymbol implements Symbol {
    private Identifier name;
    private Type returnType;
    private LinkedList<SymbolTable> scopes;

    public MethodSymbol(Identifier name, Type returnType) {
        this.name = name;
        this.returnType = returnType;
        this.scopes = new LinkedList<>();
        this.EnterScope();
    }

    @Override
    public Identifier GetIdentifier() {
        return this.name;
    }

    @Override
    public Type GetType() {
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

    public Type GetType(Identifier key) {
        for (SymbolTable scope : this.scopes) {
            Type value = scope.GetType(key);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    public void AddVariable(Identifier key, Type value) {
        this.scopes.peek().AddSymbol(key, value);
    }
}
