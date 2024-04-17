import java.util.HashMap;

import minijava.syntaxtree.*;


/**
 * Symbol table that has the form:
 * <identifier, type>
 */
public class SymbolTable {
    private HashMap<String, TypeStruct> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }

    public TypeStruct GetType(Identifier key) {
        return table.get(key.f0.toString());
    }

    public TypeStruct AddSymbol(Identifier key, TypeStruct value) {
        if (this.table.containsKey(key.f0.toString())) {
            return new TypeStruct("Type error");
        }

        this.table.put(key.f0.toString(), value);

        return null;
    }

    public HashMap<String, TypeStruct> Table() {
        return this.table;
    }
}
