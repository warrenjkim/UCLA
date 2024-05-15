package Utils;

import java.util.LinkedList;
import java.util.LinkedHashMap;

class Pair<T, U> {
  public T first;
  public U second;

  public Pair(T first, U second) {
    this.first = first;
    this.second = second;
  }
}

public class Context {
  private ClassSymbol currClass;
  private MethodSymbol currMethod;
  private SparrowObject thisObj;
  private SparrowCode currMethodCode;
  private LinkedHashMap<String, String> methodMap;
  private LinkedHashMap<String, Integer> literalMap;
  private LinkedHashMap<String, SparrowObject> objectMap;
  private LinkedHashMap<String, ClassSymbol> classTable;
  private LinkedList<Pair<String, String>> unresolvedTable;
  private LinkedList<LinkedHashMap<String, TypeStruct>> variableScopes;

  public Context() {
    classTable = new LinkedHashMap<>();
    unresolvedTable = new LinkedList<>();
    variableScopes = new LinkedList<>();
    literalMap = new LinkedHashMap<>();
    methodMap = new LinkedHashMap<>();
    objectMap = new LinkedHashMap<>();
  }

  public void PutMethod(String key, String value) {
    methodMap.put(key, value);
  }

  public LinkedHashMap<String, String> MethodMap() {
    return methodMap;
  }

  public String Method(String key) {
    return methodMap.get(key);
  }

  public LinkedHashMap<String, SparrowObject> ObjectMap() {
    return objectMap;
  }

  public SparrowObject Object(String key) {
    return objectMap.get(key);
  }

  public void PutObject(String key, SparrowObject value) {
    objectMap.put(key, value);
  }

  public LinkedHashMap<String, Integer> LiteralMap() {
    return literalMap;
  }

  public Integer Literal(String key) {
    return literalMap.get(key);
  }

  public void PutLiteral(String key, Integer value) {
    literalMap.put(key, value);
  }

  public void PutLiteral(String key, boolean truth) {
    if (truth) {
      literalMap.put(key, 1);
    } else {
      literalMap.put(key, 0);
    }
  }

  public void SetThis(SparrowObject obj) {
    this.thisObj = obj;
  }

  public SparrowObject This() {
    return thisObj;
  }

  public ClassSymbol Class() {
    return currClass;
  }

  public MethodSymbol Method() {
    return currMethod;
  }

  public SparrowCode MethodCode() {
    return currMethodCode;
  }

  public void EnterScope() {
    variableScopes.push(new LinkedHashMap<>());
  }

  public void ExitScope() {
    variableScopes.pop();
  }

  public void SetClass(String className) {
    this.currClass = this.classTable.get(className);
  }

  public void SetClass(ClassSymbol currClass) {
    this.currClass = currClass;
  }

  public void SetMethod(MethodSymbol currMethod) {
    this.currMethod = currMethod;
  }

  public void SetMethodCode(SparrowCode currMethodCode) {
    this.currMethodCode = currMethodCode;
  }

  public void AddClass(String name, ClassSymbol classSymbol) {
    classTable.put(name, classSymbol);
  }

  public void SetClassTable(LinkedHashMap<String, ClassSymbol> other) {
    this.classTable = other;
  }

  public LinkedHashMap<String, ClassSymbol> ClassTable() {
    return classTable;
  }

  public void DeferResolution(String child, String parent) {
    unresolvedTable.push(new Pair<String, String>(child, parent));
  }

  public void ResolveClassHierarchies() {
    for (Pair<String, String> pair : unresolvedTable) {
      ClassSymbol child = classTable.get(pair.first);
      ClassSymbol parent = classTable.get(pair.second);
      child.SetParentSymbol(parent);
    }
  }
}
