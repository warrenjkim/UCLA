import java.util.HashMap;

import minijava.syntaxtree.*;

public class Context {
    private HashMap<Identifier, SymbolTable> scopes;

    public void AddClass(Identifier key) {
        this.scopes = new HashMap<>();
        if (this.scopes.containsKey(key)) {
            // error
        }

        this.scopes.put(key, new SymbolTable());
    }
}
