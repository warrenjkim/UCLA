import minijava.syntaxtree.*;
import java.util.HashMap;

public class ClassSymbol {
    private String name;
    private String parent;

    private SymbolTable fields;
    private HashMap<String, MethodSymbol> methods;

    public ClassSymbol(ClassSymbol other) {
        this.name = other.name;
        this.parent = other.parent;
        this.fields = other.fields;
        this.methods = other.methods;
    }

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

    public TypeStruct FieldType(Identifier fieldName) {
        return this.fields.GetType(fieldName.f0.tokenImage);
    }

    public TypeStruct FieldType(TypeStruct fieldName) {
        return this.fields.GetType(fieldName.GetType());
    }

    public TypeStruct MethodType(String methodName) {
        return this.methods.get(methodName).ReturnType();
    }

    public void AddField(Identifier key, Node value) {
        this.fields.AddSymbol(key, new TypeStruct(value));
    }

    public void AddMethod(MethodSymbol method) {
        this.methods.put(method.MethodName(), method);
    }

    public SymbolTable Fields() {
        return this.fields;
    }

    public HashMap<String, MethodSymbol> Methods() {
        return this.methods;
    }

    public TypeStruct ClassType() {
        return new TypeStruct(this.name);
    }

    public MethodSymbol FindMethod(Identifier methodName) {
        return this.methods.get(methodName.f0.tokenImage);
    }

    public MethodSymbol FindMethod(String methodName) {
        return this.methods.get(methodName);
    }
}

