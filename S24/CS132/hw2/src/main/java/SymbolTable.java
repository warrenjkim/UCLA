import java.util.ArrayDeque;
import minijava.syntaxtree.*;


public class SymbolTable {
    private ArrayDeque<Pair> table;

    public SymbolTable() {
        this.table = new ArrayDeque<>();
    }

    public SymbolTable(SymbolTable other) {
        this.table = new ArrayDeque<Pair>(other.table);
    }

    public TypeStruct GetType(String key) {
        for (Pair symbol : this.table) {
            if (symbol.Name().equals(key)) {
                return symbol.TypeStruct();
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

    public ArrayDeque<Pair> Table() {
        return this.table;
    }

    public void SetTable(ArrayDeque<Pair> table) {
        this.table = table;
    }

    // pops param from front
    public Pair PopParameter() {
        return this.table.poll();
    }

    public boolean Empty() {
        return this.table.isEmpty();
    }
}
