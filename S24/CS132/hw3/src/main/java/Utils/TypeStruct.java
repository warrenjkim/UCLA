package Utils;

public class TypeStruct {
  private String type;
  private TypeStruct superTypeStruct;

  public TypeStruct(String type) {
    this.type = type;
    this.superTypeStruct = null;
  }

  public TypeStruct(String type, String superTypeStruct) {
    this.type = type;
    this.superTypeStruct = new TypeStruct(superTypeStruct);
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

  public boolean MatchType(String type) {
    return this.type.equals(type);
  }

  public boolean ObjectType() {
    return !MatchType("IntegerType") && !MatchType("BooleanType");
  }
}
