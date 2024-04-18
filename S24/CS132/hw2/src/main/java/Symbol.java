import java.util.LinkedList;
import minijava.syntaxtree.*;

public class Symbol {
    protected String name;
    protected TypeStruct type;
    protected LinkedList<SymbolTable> scopes;

    public Symbol() {
        this.name = "";
        this.type = null;
        this.scopes = new LinkedList<>();
    }

    public Symbol(Identifier name) {
        this.name = name.f0.tokenImage;
        this.type = null;
        this.scopes = new LinkedList<>();
    }

    public Symbol(String name) {
        this.name = name;
        this.type = null;
        this.scopes = new LinkedList<>();
    }


    public String Name() {
        return this.name;
    }

    public String Type() {
        return this.type.Type();
    }

    public TypeStruct TypeStruct() {
        return this.type;
    }

    public LinkedList<SymbolTable> Scopes() {
        return this.scopes;
    }

    public void PushScope(SymbolTable scope) {
        this.scopes.push(scope);
    }

    public void EnterScope() {
        this.scopes.push(new SymbolTable());
    }

    public SymbolTable ExitScope() {
        return this.scopes.poll();
    }

    public TypeStruct PushSymbol(Identifier key, TypeStruct value) {
        return this.scopes.peekLast().AddSymbol(key, value);
    }
}
