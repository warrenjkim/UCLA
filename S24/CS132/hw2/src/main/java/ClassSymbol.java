import minijava.syntaxtree.*;
import java.util.HashMap;

public class ClassSymbol {
    private String name;
    private String parent;

    private SymbolTable fields;
    private HashMap<String, MethodSymbol> methods;

    public ClassSymbol(Identifier name) {
        this.name = name.f0.tokenImage;
        this.parent = null;
        this.fields = new SymbolTable();
        this.methods = new HashMap<>();
    }

    public ClassSymbol(Identifier name, Identifier parent) {
        this.name = name.f0.tokenImage;
        this.parent = parent.f0.tokenImage;
        this.fields = new SymbolTable();
        this.methods = new HashMap<>();
    }

    public String ClassName() {
        return this.name;
    }

    public String ParentName() {
        return this.parent;
    }

    public TypeStruct GetFieldType(Identifier fieldName) {
        return this.fields.GetType(fieldName);
    }

    public TypeStruct GetMethodType(Identifier methodName) {
        return this.methods.get(methodName.f0.tokenImage).ReturnType();
    }

    public void AddField(Identifier key, Node value) {
        this.fields.AddSymbol(key, new TypeStruct(value));
    }

    public void AddMethod(MethodSymbol method) {
        this.methods.put(method.MethodName(), method);
    }

    public HashMap<String, MethodSymbol> Methods() {
        return this.methods;
    }

    public SymbolTable Fields() {
        return this.fields;
    }
}
