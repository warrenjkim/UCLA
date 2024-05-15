package Visitors;

import java.util.Enumeration;

import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;

public class TEMPLATE extends GJDepthFirst<TypeStruct, Context> {
  //
  // Auto class visitors--probably don't need to be overridden.
  //
  public TypeStruct visit(NodeList n, Context context) {
    TypeStruct _ret = null;
    int _count = 0;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this, context);
      _count++;
    }
    return _ret;
  }

  public TypeStruct visit(NodeListOptional n, Context context) {
    if (n.present()) {
      TypeStruct _ret = null;
      int _count = 0;
      for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
        e.nextElement().accept(this, context);
        _count++;
      }
      return _ret;
    } else return null;
  }

  public TypeStruct visit(NodeOptional n, Context context) {
    if (n.present()) return n.node.accept(this, context);
    else return null;
  }

  public TypeStruct visit(NodeSequence n, Context context) {
    TypeStruct _ret = null;
    int _count = 0;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this, context);
      _count++;
    }
    return _ret;
  }

  public TypeStruct visit(NodeToken n, Context context) {
    return null;
  }

  //
  // User-generated visitor methods below
  //

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  public TypeStruct visit(Goal n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
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
  public TypeStruct visit(MainClass n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    n.f5.accept(this, context);
    n.f6.accept(this, context);
    n.f7.accept(this, context);
    n.f8.accept(this, context);
    n.f9.accept(this, context);
    n.f10.accept(this, context);
    n.f11.accept(this, context);
    n.f12.accept(this, context);
    n.f13.accept(this, context);
    n.f14.accept(this, context);
    n.f15.accept(this, context);
    n.f16.accept(this, context);
    n.f17.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> ClassDeclaration()
   *     | ClassExtendsDeclaration()
   */
  public TypeStruct visit(TypeDeclaration n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  public TypeStruct visit(ClassDeclaration n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    n.f5.accept(this, context);
    return _ret;
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
  public TypeStruct visit(ClassExtendsDeclaration n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    n.f5.accept(this, context);
    n.f6.accept(this, context);
    n.f7.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public TypeStruct visit(VarDeclaration n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
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
  public TypeStruct visit(MethodDeclaration n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    n.f5.accept(this, context);
    n.f6.accept(this, context);
    n.f7.accept(this, context);
    n.f8.accept(this, context);
    n.f9.accept(this, context);
    n.f10.accept(this, context);
    n.f11.accept(this, context);
    n.f12.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   */
  public TypeStruct visit(FormalParameterList n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   */
  public TypeStruct visit(FormalParameter n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   */
  public TypeStruct visit(FormalParameterRest n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> ArrayType()
   *     | BooleanType()
   *     | IntegerType()
   *     | Identifier()
   */
  public TypeStruct visit(Type n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */
  public TypeStruct visit(ArrayType n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "boolean"
   */
  public TypeStruct visit(BooleanType n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "int"
   */
  public TypeStruct visit(IntegerType n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> Block()
   *     | AssignmentStatement()
   *     | ArrayAssignmentStatement()
   *     | IfStatement()
   *     | WhileStatement()
   *     | PrintStatement()
   */
  public TypeStruct visit(Statement n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   */
  public TypeStruct visit(Block n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   */
  public TypeStruct visit(AssignmentStatement n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> Identifier()
   * f1 -> "["
   * f2 -> Expression()
   * f3 -> "]"
   * f4 -> "="
   * f5 -> Expression()
   * f6 -> ";"
   */
  public TypeStruct visit(ArrayAssignmentStatement n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    n.f5.accept(this, context);
    n.f6.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "if"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   * f5 -> "else"
   * f6 -> Statement()
   */
  public TypeStruct visit(IfStatement n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    n.f5.accept(this, context);
    n.f6.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */
  public TypeStruct visit(WhileStatement n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   */
  public TypeStruct visit(PrintStatement n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> AndExpression()
   *     | CompareExpression()
   *     | PlusExpression()
   *     | MinusExpression()
   *     | TimesExpression()
   *     | ArrayLookup()
   *     | ArrayLength()
   *     | MessageSend()
   *     | PrimaryExpression()
   */
  public TypeStruct visit(Expression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(AndExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(CompareExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(PlusExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(MinusExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(TimesExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */
  public TypeStruct visit(ArrayLookup n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  public TypeStruct visit(ArrayLength n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 > ")"
   */
  public TypeStruct visit(MessageSend n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    n.f5.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )*
   */
  public TypeStruct visit(ExpressionList n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  public TypeStruct visit(ExpressionRest n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> IntegerLiteral()
   *     | TrueLiteral()
   *     | FalseLiteral()
   *     | Identifier()
   *     | ThisExpression()
   *     | ArrayAllocationExpression()
   *     | AllocationExpression()
   *     | NotExpression()
   *     | BracketExpression()
   */
  public TypeStruct visit(PrimaryExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  public TypeStruct visit(IntegerLiteral n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "true"
   */
  public TypeStruct visit(TrueLiteral n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "false"
   */
  public TypeStruct visit(FalseLiteral n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public TypeStruct visit(Identifier n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "this"
   */
  public TypeStruct visit(ThisExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   */
  public TypeStruct visit(ArrayAllocationExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    n.f4.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  public TypeStruct visit(AllocationExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    n.f3.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   */
  public TypeStruct visit(NotExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    return _ret;
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  public TypeStruct visit(BracketExpression n, Context context) {
    TypeStruct _ret = null;
    n.f0.accept(this, context);
    n.f1.accept(this, context);
    n.f2.accept(this, context);
    return _ret;
  }
}
