import minijava.syntaxtree.*;

public class TypeStruct {
    private String type;
    private String superType;

    public TypeStruct(TypeStruct other) {
        this.type = other.type;
        this.superType = other.superType;
    }

    public TypeStruct(String type) {
        this.type = type;
        this.superType = null;
    }

    public TypeStruct(String type, String superType) {
        this.type = type;
        this.superType = superType;
    }

    public TypeStruct(Node type) {
        if (type instanceof Type) {
            this.type = ((Type) type).f0.choice.getClass().getSimpleName();
        } else if (type instanceof Identifier) {
            this.type = ((Identifier) type).f0.tokenImage;
        }
    }

    public String Type() {
        return this.type;
    }

    public String SuperType() {
        return this.superType;
    }

    // fix this (subtype)
    public boolean MatchType(TypeStruct type) {
        return this.MatchType(type.Type());
    }

    public boolean MatchType(String type) {
        return type.equals(this.type) || (!type.equals("main") && type.equals(this.superType));
    }
}
