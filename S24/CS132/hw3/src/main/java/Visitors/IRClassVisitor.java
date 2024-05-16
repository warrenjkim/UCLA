package Visitors;

import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJVoidDepthFirst;

public class IRClassVisitor extends GJVoidDepthFirst<Context> {
  private IRMethodVisitor irMethodVisitor = new IRMethodVisitor();
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
    context.SetMethod(context.Class().FindMethod(n.f6.tokenImage));
    n.f14.accept(irMethodVisitor, context);
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
    n.f3.accept(this, context);
    n.f4.accept(this, context);
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
    n.f5.accept(this, context);  // fields
    n.f6.accept(this, context);  // method declarations
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public void visit(VarDeclaration n, Context context) {
    TypeStruct type = n.f0.accept(typeVisitor, context);
    String name = n.f1.f0.tokenImage;
    String id = context.Class().Name() + "_" + name;
    if (type.MatchType("ArrayType")) {
      putBaseArray(id, context);
      return;
    }

    SparrowObject base = context.Object(type.Type());
    if (base == null) {
      return;
    }

    SparrowObject fieldObj = new SparrowObject(base);
    fieldObj.SetId(id);
    context.PutObject(id, fieldObj);
  }

  public void putBaseArray(String id, Context context) {
    SparrowObject obj = new SparrowObject(id, 0, new TypeStruct("ArrayType"));
    context.PutObject(id, obj);
  }

  /**
   * f0 -> "public"
   * f1 -> Type()
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( FormalParameterList() )?
   * f5 -> ")"
   * f6 -> "{"
   * f7 -> ( VarDeclaration() )*
   * f8 -> ( Statement() )*
   * f9 -> "return"
   * f10 -> Expression()
   * f11 -> ";"
   * f12 -> "}"
   */
  public void visit(MethodDeclaration n, Context context) {
    context.SetMethod(context.Class().FindMethod(n.f2.f0.tokenImage));
    n.f4.accept(irMethodVisitor, context);
    n.f7.accept(irMethodVisitor, context);
  }
}
