public class Pair {
    private String name;
    private TypeStruct type;

    public Pair(Pair other) {
        this.name = other.name;
        this.type = new TypeStruct(other.type);
    }

    public Pair(String name, TypeStruct type) {
        this.name = name;
        this.type = type;
    }

    public String Name() {
        return this.name;
    }

    public String Type() {
        return this.type.Type();
    }

    public TypeStruct TypeStruct() {
        return this.type;
    }
}

