package Utils;

import java.util.HashMap;
import java.util.LinkedList;
import minijava.syntaxtree.*;

public class ClassSymbol extends Symbol {
  private HashMap<String, MethodSymbol> methods = new HashMap<>();

  public ClassSymbol() {
    super();
    this.methods = new HashMap<>();
    super.EnterScope();
  }

  public ClassSymbol(ClassSymbol other) {
    super(other.name);
    super.type = new TypeStruct(other.type);
    for (HashMap.Entry<String, MethodSymbol> method : other.Methods().entrySet()) {
      this.methods.put(method.getKey(), new MethodSymbol(method.getValue()));
    }

    for (SymbolTable scope : other.scopes) {
      super.scopes.push(new SymbolTable(scope));
    }
  }

  public ClassSymbol(Identifier name) {
    super(name);
    super.type = new TypeStruct(name.f0.tokenImage);
    super.scopes = new LinkedList<>();
    this.methods = new HashMap<>();
    super.EnterScope();
  }

  public ClassSymbol(Identifier name, Identifier parent) {
    super(name);
    super.type = new TypeStruct(name.f0.tokenImage, parent.f0.tokenImage);
    this.methods = new HashMap<>();
    super.EnterScope();
  }

  // boilerplate
  @Override
  public String Type() {
    return super.type.Type();
  }

  public String Parent() {
    return super.type.SuperType();
  }

  public TypeStruct ParentTypeStruct() {
    return super.type.SuperTypeStruct();
  }

  public LinkedList<SymbolTable> Fields() {
    return super.scopes;
  }

  public HashMap<String, MethodSymbol> Methods() {
    return this.methods;
  }

  // method stuff
  public MethodSymbol FindMethod(String method) {
    return this.methods.get(method);
  }

  public MethodSymbol FindMethod(Identifier method) {
    return this.FindMethod(method.f0.tokenImage);
  }

  public TypeStruct AddMethod(MethodSymbol method) {
    if (this.methods.containsKey(method.Name())) {
      return new TypeStruct("Type error");
    }

    this.methods.put(method.Name(), method);
    return null;
  }

  // field stuff
  public TypeStruct FieldTypeStruct(String key) {
    for (SymbolTable scope : super.scopes) {
      TypeStruct field = scope.GetType(key);
      if (field != null) {
        return field;
      }
    }

    return null;
  }

  public TypeStruct FieldTypeStruct(Identifier key) {
    return this.FieldTypeStruct(key.f0.tokenImage);
  }

  public TypeStruct FieldTypeStruct(TypeStruct key) {
    return this.FieldTypeStruct(key.Type());
  }

  public String FieldType(String key) {
    return this.FieldTypeStruct(key).Type();
  }

  public String FieldType(Identifier key) {
    return this.FieldTypeStruct(key).Type();
  }

  public String FieldType(TypeStruct key) {
    return this.FieldTypeStruct(key).Type();
  }

  public TypeStruct AddField(Identifier key, TypeStruct value) {
    if (this.FieldTypeStruct(key) != null) {
      return new TypeStruct("Type error");
    }

    return super.PushSymbol(key, value);
  }
}
