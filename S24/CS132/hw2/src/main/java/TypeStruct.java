import minijava.syntaxtree.*;

public class TypeStruct {
    private String type;
    private String superType;

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

    public String GetType() {
        return this.type;
    }

    public boolean MatchSubType(TypeStruct type) {
        return type.GetType().equals(this.superType);
    }

    // fix this (subtype)
    public boolean MatchType(TypeStruct type) {
        return type.GetType().equals(this.type) || this.MatchSubType(type);
    }

    public boolean MatchType(String type) {
        return type.equals(this.type);
    }
}
