package Visitors;

import java.util.LinkedHashMap;

import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJVoidDepthFirst;

public class ClassVisitor extends GJVoidDepthFirst<Context> {
  private LinkedHashMap<String, ClassSymbol> classTable;
  private MethodVisitor methodVisitor = new MethodVisitor();
  private TypeVisitor typeVisitor = new TypeVisitor();

  public LinkedHashMap<String, ClassSymbol> ClassTable() {
    return this.classTable;
  }

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  public void visit(Goal n, Context context) {
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    context.ResolveClassHierarchies();
    this.classTable = context.ClassTable();
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
    String className = n.f1.f0.tokenImage;
    ClassSymbol mainClass = new ClassSymbol(className);
    MethodSymbol mainMethod = new MethodSymbol(n.f6.tokenImage, new TypeStruct("void"));

    mainClass.AddMethod(mainMethod);
    context.AddClass(className, mainClass);
    context.SetClass(mainClass);
    context.SetMethod(mainMethod);

    n.f14.accept(methodVisitor, context); // method body var declarations
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
    String className = n.f1.f0.tokenImage;
    ClassSymbol classSymbol = new ClassSymbol(className);

    context.AddClass(className, classSymbol);
    context.SetClass(classSymbol);

    n.f3.accept(this, context); // fields
    n.f4.accept(this, context); // method declarations
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
    String className = n.f1.f0.tokenImage;
    String parentName = n.f3.f0.tokenImage;
    ClassSymbol classSymbol = new ClassSymbol(className);

    context.DeferResolution(className, parentName);
    context.AddClass(className, classSymbol);
    context.SetClass(classSymbol);

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

    context.Class().AddField(name, type);
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
    TypeStruct returnType = n.f1.accept(typeVisitor, context);
    String methodName = n.f2.f0.tokenImage;
    MethodSymbol method = new MethodSymbol(methodName, returnType);

    context.Class().AddMethod(method);
    context.SetMethod(method);
    n.f4.accept(this, context);

    context.EnterScope();
    n.f7.accept(methodVisitor, context);  // var declarations
    context.ExitScope();
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

    context.Method().AddFormalParameter(name, type);
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   */
  public void visit(FormalParameterRest n, Context context) {
    n.f1.accept(this, context);
  }
}
