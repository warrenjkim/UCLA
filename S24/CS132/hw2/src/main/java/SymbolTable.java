import java.util.HashMap;

import minijava.syntaxtree.*;

public class SymbolTable {
    private HashMap<String, Type> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }

    public Type GetType(Identifier key) {
        return table.get(key.f0.toString());
    }

    public void AddSymbol(Identifier key, Type value) {
        this.table.put(key.f0.toString(), value);
    }
}
