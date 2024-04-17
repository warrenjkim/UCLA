import java.util.HashMap;
import java.util.ArrayList;
import minijava.syntaxtree.*;


/**
 * Symbol table that has the form:
 * <identifier, type>
 */
public class SymbolTable {
    private ArrayList<Pair> table;

    public SymbolTable() {
        this.table = new ArrayList<>();
    }

    public TypeStruct GetType(String key) {
        for (Pair symbol : this.table) {
            if (symbol.Name().equals(key)) {
                return symbol.Type();
            }
        }

        return null;
    }

    public TypeStruct GetType(Identifier key) {
        return this.GetType(key.f0.tokenImage);
    }


    public TypeStruct AddSymbol(Identifier key, TypeStruct value) {
        if (this.GetType(key) != null) {
            return new TypeStruct("Type error");
        }

        this.table.add(new Pair(key.f0.tokenImage, value));

        return null;
    }

    public ArrayList<Pair> Table() {
        return this.table;
    }
}
