package Utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class MethodSymbol {
  String name;
  TypeStruct returnType;
  LinkedHashMap<String, TypeStruct> formalParams;
  LinkedList<LinkedHashMap<String, TypeStruct>> localVariables;
  SparrowCode sparrowCode;

  public MethodSymbol(String name, TypeStruct returnType) {
    this.name = name;
    this.returnType = returnType;
    formalParams = new LinkedHashMap<>();
    localVariables = new LinkedList<>();
    EnterScope();
  }

  public SparrowCode SparrowCode() {
    return sparrowCode;
  }

  public void SetSparrowCode(SparrowCode code) {
    sparrowCode = code;
  }

  public String Name() {
    return name;
  }

  public TypeStruct TypeStruct() {
    return returnType;
  }

  public LinkedHashMap<String, TypeStruct> FormalParameters() {
    return formalParams;
  }

  public LinkedList<LinkedHashMap<String, TypeStruct>> LocalVariables() {
    return localVariables;
  }

  public void AddFormalParameter(String param, TypeStruct paramType) {
    formalParams.put(param, paramType);
  }

  public void EnterScope() {
    localVariables.push(new LinkedHashMap<>());
  }

  public void ExitScope() {
    localVariables.pop();
  }

  public TypeStruct VariableTypeStruct(String name) {
    for (LinkedHashMap<String, TypeStruct> scope : localVariables) {
      TypeStruct varType = scope.get(name);
      if (varType != null) {
        return varType;
      }
    }

    return formalParams.get(name);
  }

  public void AddLocalVariable(String name, TypeStruct varType) {
    localVariables.peek().put(name, varType);
  }
}
