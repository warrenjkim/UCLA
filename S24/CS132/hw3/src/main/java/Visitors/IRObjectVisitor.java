package Visitors;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJVoidDepthFirst;

public class IRObjectVisitor extends GJVoidDepthFirst<Context> {
  private TypeVisitor typeVisitor = new TypeVisitor();

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  public void visit(Goal n, Context context) {
    n.f0.accept(this, context);
    n.f1.accept(this, context);
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> "public"
   * f4 -> "static"
   * f5 -> "void"
   * f6 -> "main"
   * f7 -> "("
   * f8 -> "String"
   * f9 -> "["
   * f10 -> "]"
   * f11 -> Identifier()
   * f12 -> ")"
   * f13 -> "{"
   * f14 -> ( VarDeclaration() )*
   * f15 -> ( Statement() )*
   * f16 -> "}"
   * f17 -> "}"
   */
  public void visit(MainClass n, Context context) {
    context.SetClass(n.f1.f0.tokenImage);
    TypeStruct type = n.f1.accept(typeVisitor, context);
    putBaseObject(n.f1.f0.tokenImage, type, context);
  }

  /**
   * f0 -> ClassDeclaration()
   *     | ClassExtendsDeclaration()
   */
  public void visit(TypeDeclaration n, Context context) {
    n.f0.accept(this, context);
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  public void visit(ClassDeclaration n, Context context) {
    context.SetClass(n.f1.f0.tokenImage);
    TypeStruct type = n.f1.accept(typeVisitor, context);
    putBaseObject(n.f1.f0.tokenImage, type, context);
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "extends"
   * f3 -> Identifier()
   * f4 -> "{"
   * f5 -> ( VarDeclaration() )*
   * f6 -> ( MethodDeclaration() )*
   * f7 -> "}"
   */
  public void visit(ClassExtendsDeclaration n, Context context) {
    context.SetClass(n.f1.f0.tokenImage);
    TypeStruct type = n.f1.accept(typeVisitor, context);
    putBaseObject(n.f1.f0.tokenImage, type, context);
  }

  public void putBaseObject(String className, TypeStruct type, Context context) {
    String id = className;
    String vTableId = className + "_vTable";
    int fieldByteSize = 4 * (context.Class().FieldCount() + 1);
    int methodByteSize = 4 * context.Class().MethodCount();

    SparrowObject obj = new SparrowObject(id, fieldByteSize, type);
    SparrowObject vTable = new SparrowObject(vTableId, methodByteSize, new TypeStruct("vTable"));

    LinkedList<LinkedList<String>> fieldStack = new LinkedList<>();
    LinkedList<LinkedHashMap<String, String>> methodStack = new LinkedList<>();
    ClassSymbol curr = context.Class();
    while (curr != null) {
      fieldStack.push(new LinkedList<>());
      for (String field : curr.Fields().keySet()) {
        fieldStack.peek().offer(curr.Name() + "_" + field);
      }

      methodStack.push(new LinkedHashMap<>());
      for (String method : curr.Methods().keySet()) {
        methodStack.peek().put(method, curr.Name() + "__" + method);
      }

      curr = curr.ParentSymbol();
    }

    int fieldByteOffset = 4;
    int methodByteOffset = 0;
    LinkedHashMap<String, String> methodMap = new LinkedHashMap<>();
    for (LinkedHashMap<String, String> classScope : methodStack) {
      for (Map.Entry<String, String> method : classScope.entrySet()) {
        if (methodMap.containsKey(method.getKey())) {
          vTable.ReplaceFieldByteOffset(methodMap.get(method.getKey()), method.getValue());
          methodMap.put(method.getKey(), method.getValue());
        } else {
          vTable.PutFieldByteOffset(method.getValue(), methodByteOffset);
          methodMap.put(method.getKey(), method.getValue());
          methodByteOffset += 4;
        }
      }
    }

    for (LinkedList<String> classScope : fieldStack) {
      for (String fieldName : classScope) {
        obj.PutFieldByteOffset(fieldName, fieldByteOffset);
        fieldByteOffset += 4;
      }
    }

    obj.PutFieldByteOffset(vTable.Id(), 0);
    context.PutObject(id, obj);
    context.PutObject(vTableId, vTable);
  }
}
