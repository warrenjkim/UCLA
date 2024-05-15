package Visitors;

import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJVoidDepthFirst;

public class IRMethodVisitor extends GJVoidDepthFirst<Context> {
  private TypeVisitor typeVisitor = new TypeVisitor();

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public void visit(VarDeclaration n, Context context) {
    TypeStruct type = n.f0.accept(typeVisitor, context);
    String name = n.f1.f0.tokenImage;
    String id = context.Class().Name() + "_" + context.Method().Name() + "_" + name;

    if (type.MatchType("ArrayType")) {
      putBaseArray(id, context);
      return;
    }

    SparrowObject base = context.Object(type.Type());
    if (base == null) {
      return;
    }

    SparrowObject paramObj = new SparrowObject(base);
    paramObj.SetId(id);
    context.PutObject(id, paramObj);
  }

  /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   */
  public void visit(FormalParameterList n, Context context) {
    n.f0.accept(this, context);
    n.f1.accept(this, context);
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   */
  public void visit(FormalParameter n, Context context) {
    TypeStruct type = n.f0.accept(typeVisitor, context);
    String name = n.f1.f0.tokenImage;
    String id = context.Class().Name() + "_" + context.Method().Name() + "_" + name;
    if (type.MatchType("ArrayType")) {
      putBaseArray(id, context);
      return;
    }

    SparrowObject base = context.Object(type.Type());
    if (base == null) {
      return;
    }

    SparrowObject paramObj = new SparrowObject(base);
    paramObj.SetId(id);
    context.PutObject(id, paramObj);
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   */
  public void visit(FormalParameterRest n, Context context) {
    n.f1.accept(this, context);
  }

  public void putBaseArray(String id, Context context) {
    SparrowObject obj = new SparrowObject(id, 0, new TypeStruct("ArrayType"));
    context.PutObject(id, obj);
  }
}
