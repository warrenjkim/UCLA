package Visitors;

import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;

public class IRTypeVisitor extends GJDepthFirst<TypeStruct, Context> {
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
    return n.f0.accept(this, context);
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */
  public TypeStruct visit(ArrayLookup n, Context context) {
    return new TypeStruct("IntegerType");
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  public TypeStruct visit(ArrayLength n, Context context) {
    return new TypeStruct("IntegerType");
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(AndExpression n, Context context) {
    return new TypeStruct("BooleanType");
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(CompareExpression n, Context context) {
    return new TypeStruct("BooleanType");
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(PlusExpression n, Context context) {
    return new TypeStruct("IntegerType");
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(MinusExpression n, Context context) {
    return new TypeStruct("IntegerType");
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  public TypeStruct visit(TimesExpression n, Context context) {
    return new TypeStruct("IntegerType");
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   */
  public TypeStruct visit(ArrayAllocationExpression n, Context context) {
    return new TypeStruct("ArrayType");
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  public TypeStruct visit(AllocationExpression n, Context context) {
    return new TypeStruct(n.f1.f0.tokenImage);
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   */
  public TypeStruct visit(NotExpression n, Context context) {
    return new TypeStruct("BooleanType");
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  public TypeStruct visit(BracketExpression n, Context context) {
    return n.f1.accept(this, context);
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
    return n.f0.accept(this, context);
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  public TypeStruct visit(IntegerLiteral n, Context context) {
    return new TypeStruct("IntegerType");
  }

  /**
   * f0 -> "true"
   */
  public TypeStruct visit(TrueLiteral n, Context context) {
    return new TypeStruct("BooleanType");
  }

  /**
   * f0 -> "false"
   */
  public TypeStruct visit(FalseLiteral n, Context context) {
    return new TypeStruct("BooleanType");
  }

  /**
   * f0 -> ArrayType()
   *     | BooleanType()
   *     | IntegerType()
   *     | Identifier()
   */
  public TypeStruct visit(Type n, Context context) {
    return n.f0.accept(this, context);
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */
  public TypeStruct visit(ArrayType n, Context context) {
    return new TypeStruct("ArrayType");
  }

  /**
   * f0 -> "boolean"
   */
  public TypeStruct visit(BooleanType n, Context context) {
    return new TypeStruct("BooleanType");
  }

  /**
   * f0 -> "int"
   */
  public TypeStruct visit(IntegerType n, Context context) {
    return new TypeStruct("IntegerType");
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public TypeStruct visit(Identifier n, Context context) {
    TypeStruct localType = context.Method().VariableTypeStruct(n.f0.tokenImage);
    if (localType != null) {
      return localType;
    }

    TypeStruct fieldType = context.Class().FieldTypeStruct(n.f0.tokenImage);
    if (fieldType != null) {
      return fieldType;
    }

    MethodSymbol method = context.Class().FindMethod(n.f0.tokenImage);
    if (method != null) {
      return method.TypeStruct();
    }

    return new TypeStruct(n.f0.tokenImage);
  }

  /**
   * f0 -> "this"
   */
  public TypeStruct visit(ThisExpression n, Context context) {
    return new TypeStruct(context.Class().Name());
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
    Context tmpContext = new Context();
    TypeStruct objType = n.f0.accept(this, context);

    tmpContext.SetClassTable(context.ClassTable());
    tmpContext.SetClass(objType.Type());
    tmpContext.SetMethod(context.Method());
    return n.f2.accept(this, tmpContext);
  }
}
