import minijava.syntaxtree.Identifier;
import minijava.syntaxtree.Type;

public interface Symbol {
    public Identifier GetIdentifier();
    public Type GetType();
}
