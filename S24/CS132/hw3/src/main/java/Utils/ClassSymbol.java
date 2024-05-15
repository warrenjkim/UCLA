package Utils;

import java.util.LinkedHashMap;

public class ClassSymbol {
  String name;
  ClassSymbol parentSymbol;
  LinkedHashMap<String, TypeStruct> fields;
  LinkedHashMap<String, MethodSymbol> methods;

  public ClassSymbol(String name) {
    this.name = name;
    this.fields = new LinkedHashMap<>();
    this.methods = new LinkedHashMap<>();
  }

  public String Name() {
    return name;
  }

  public ClassSymbol ParentSymbol() {
    return parentSymbol;
  }

  public int FieldCount() {
    int size = fields.size();
    if (parentSymbol == null) {
      return size;
    }

    return size + parentSymbol.FieldCount();
  }

  public int MethodCount() {
    int size = methods.size();
    if (parentSymbol == null) {
      return size;
    }

    return size + parentSymbol.MethodCount();
  }

  public LinkedHashMap<String, TypeStruct> Fields() {
    return fields;
  }

  public LinkedHashMap<String, MethodSymbol> Methods() {
    return methods;
  }

  public void SetParentSymbol(ClassSymbol parentSymbol) {
    this.parentSymbol = parentSymbol;
  }

  public MethodSymbol FindMethod(String method) {
    return this.methods.get(method);
  }

  public void AddMethod(MethodSymbol method) {
    this.methods.put(method.Name(), method);
  }

  public TypeStruct FieldTypeStruct(String key) {
    TypeStruct fieldType = fields.get(key);
    if (fieldType != null) {
      return fieldType;
    }

    if (parentSymbol != null) {
      return parentSymbol.FieldTypeStruct(key);
    }

    return null;
  }

  public TypeStruct Field(String field) {
    return fields.get(field);
  }

  public TypeStruct AddField(String name, TypeStruct type) {
    return fields.put(name, type);
  }
}
