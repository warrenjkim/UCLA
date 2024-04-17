public class Pair {
    private String name;
    private TypeStruct type;

    public Pair(String name, TypeStruct type) {
        this.name = name;
        this.type = type;
    }

    public String Name() {
        return this.name;
    }

    public TypeStruct Type() {
        return this.type;
    }
}

