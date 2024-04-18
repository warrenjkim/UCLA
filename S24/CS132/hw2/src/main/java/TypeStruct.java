import minijava.syntaxtree.*;

public class TypeStruct {
    private String type;
    private TypeStruct superTypeStruct;

    public TypeStruct(TypeStruct other) {
        this.type = other.type;
        if (other.superTypeStruct == null) {
            this.superTypeStruct = null;
        } else {
            this.superTypeStruct = new TypeStruct(other.superTypeStruct);
        }
    }

    public TypeStruct(String type) {
        this.type = type;
        this.superTypeStruct = null;
    }

    public TypeStruct(String type, String superTypeStruct) {
        this.type = type;
        this.superTypeStruct = new TypeStruct(superTypeStruct);
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
        if (this.superTypeStruct == null) {
            return "";
        }

        return this.superTypeStruct.type;
    }

    public TypeStruct SuperTypeStruct() {
        return this.superTypeStruct;
    }

    public void SetSuperTypeStruct(TypeStruct superTypeStruct) {
        this.superTypeStruct = superTypeStruct;
    }

    // fix this (subtype)
    public boolean MatchType(TypeStruct other) {
        return this.MatchSuperType(other);
    }

    public boolean MatchType(String type) {
        return this.type.equals(type);
    }

    public boolean MatchSuperType(TypeStruct other) {
        if (this.superTypeStruct == null) {
            return this.MatchType(other.type);
        }

        return this.MatchType(other.type) || this.superTypeStruct.MatchSuperType(other);
    }
}
