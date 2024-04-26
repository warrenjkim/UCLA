package Utils;

import minijava.syntaxtree.Identifier;
import minijava.syntaxtree.Node;

public class MethodSymbol extends Symbol {
  private SymbolTable formalParameters;

  public MethodSymbol() {
    super();
    this.formalParameters = new SymbolTable();
    super.EnterScope();
  }

  public MethodSymbol(MethodSymbol other) {
    super(other.name);
    super.type = new TypeStruct(other.type);
    this.formalParameters = new SymbolTable(other.formalParameters);

    for (SymbolTable scope : other.scopes) {
      super.scopes.push(new SymbolTable(scope));
    }
  }

  public MethodSymbol(Identifier name, Node type) {
    super(name);
    super.type = new TypeStruct(type);
    this.formalParameters = new SymbolTable();
    super.EnterScope();
  }

  public MethodSymbol(String name, Node type) {
    super(name);
    super.type = new TypeStruct(type);
    this.formalParameters = new SymbolTable();
    super.EnterScope();
  }

  public MethodSymbol(Identifier name, TypeStruct type) {
    super(name);
    super.type = new TypeStruct(type);
    this.formalParameters = new SymbolTable();
    super.EnterScope();
  }

  public MethodSymbol(String name, TypeStruct type) {
    super(name);
    super.type = new TypeStruct(type);
    this.formalParameters = new SymbolTable();
    super.EnterScope();
  }

  // boilerplate
  public SymbolTable FormalParameters() {
    return this.formalParameters;
  }

  // find
  public TypeStruct FindVariable(String key) {
    for (SymbolTable scope : super.scopes) {
      TypeStruct variable = scope.GetType(key);
      if (variable != null) {
        return variable;
      }
    }

    return this.formalParameters.GetType(key);
  }

  public TypeStruct FindVariable(Identifier key) {
    return this.FindVariable(key.f0.tokenImage);
  }

  public TypeStruct FindVariable(TypeStruct key) {
    return this.FindVariable(key.Type());
  }

  // set
  public TypeStruct AddFormalParameter(Identifier key, TypeStruct value) {
    return this.formalParameters.AddSymbol(key, value);
  }

  public TypeStruct AddVariable(Identifier key, TypeStruct value) {
    if (this.FindVariable(key) != null) {
      return new TypeStruct("Type error");
    }

    return super.PushSymbol(key, value);
  }
}
