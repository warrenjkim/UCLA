import minijava.syntaxtree.*;

public class TypeStruct {
    private String type;

    public TypeStruct(String type) {
        this.type = type;
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

    public boolean MatchType(Type type) {
        return this.type.equals(type.f0.choice.getClass().getSimpleName());
    }

    public boolean MatchType(Identifier type) {
        return this.type.equals(type.f0.tokenImage);
    }
}
