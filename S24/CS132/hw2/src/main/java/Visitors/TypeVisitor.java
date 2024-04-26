package Visitors;

import Utils.*;
import java.util.Enumeration;
import java.util.HashMap;
import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;

/**
 * Visits the AST in a depth first manner, only visiting necessary nodes. This visitor gets the
 * class signatures from the ClassVisitor and checks to see if the methods typecheck.
 *
 * The class table looks like:
 * (Name, ClassSymbol), where ClassSymbol contains the class'
 *   - field types,
 *   - type hierarchy,
 *   - method signatures,
 */
public class TypeVisitor extends GJDepthFirst<TypeStruct, Context> {
  HashMap<String, ClassSymbol> classTable;

  public TypeVisitor(HashMap<String, ClassSymbol> classTable) {
    this.classTable = classTable;
  }

  //
  // Auto class visitors--probably don't need to be overridden.                                     // these had to be overridden.
  //
  @Override
  public TypeStruct visit(NodeList n, Context context) {
    int _count = 0;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      TypeStruct err = e.nextElement().accept(this, context);                                       // check for errors.
      if (err != null && err.MatchType(new TypeStruct("Type error"))) {
        return err;
      }

      _count++;
    }

    return null;                                                                                    // no error.
  }

  @Override
  public TypeStruct visit(NodeListOptional n, Context context) {
    if (n.present()) {
      int _count = 0;
      for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
        TypeStruct err = e.nextElement().accept(this, context);                                     // check for errors.
        if (err != null && err.MatchType(new TypeStruct("Type error"))) {
          return err;
        }
        _count++;
      }
    }

    return null;                                                                                    // no error.
  }

  @Override
  public TypeStruct visit(NodeSequence n, Context context) {
    int _count = 0;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      TypeStruct err = e.nextElement().accept(this, context);
      if (err != null && err.MatchType(new TypeStruct("Type error"))) {                             // check for errors.
        return err;
      }

      _count++;
    }

    return null;                                                                                    // no error.
  }

  //
  // User-generated visitor methods below
  //

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  @Override
  public TypeStruct visit(Goal n, Context context) {
    TypeStruct err = n.f0.accept(this, context);                                                    // typecheck the main class.
    if (err != null) {
      return new TypeStruct("Type error");
    }

    err = n.f1.accept(this, context);                                                               // typecheck other classes.
    if (err != null) {
      return new TypeStruct("Type error");
    }

    return null;                                                                                    // no error.
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
  @Override
  public TypeStruct visit(MainClass n, Context context) {
    context.SetClass(this.classTable.get(n.f1.f0.tokenImage));                                      // actual reference to the main class.
    context.SetMethod(context.Class().FindMethod(n.f6.tokenImage));                                 // actual reference to the main method.

    TypeStruct err = n.f14.accept(this, context);                                                   // typecheck scope variables.
    if (err != null) {
      return err;
    }

    err = n.f15.accept(this, context);                                                              // typecheck statements.
    if (err != null) {
      return err;
    }

    return null;                                                                                    // no error.
  }

  /**
   * f0 -> ClassDeclaration()
   *     | ClassExtendsDeclaration()
   */
  @Override
  public TypeStruct visit(TypeDeclaration n, Context context) {
    return n.f0.accept(this, context);                                                              // typecheck the current class.
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> (
   * MethodDeclaration() )*
   * f5 -> "}"
   */
  @Override
  public TypeStruct visit(ClassDeclaration n, Context context) {
    context.SetClass(this.classTable.get(n.f1.f0.tokenImage));                                      // actual reference to the current class.
    return n.f4.accept(this, context);                                                              // typecheck the current class methods.
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "extends"
   * f3 -> Identifier()
   * f4 -> "{"
   * f5 -> (
   * VarDeclaration() )*
   * f6 -> ( MethodDeclaration() )*
   * f7 -> "}"
   */
  @Override
  public TypeStruct visit(ClassExtendsDeclaration n, Context context) {
    context.SetClass(this.classTable.get(n.f1.f0.tokenImage));                                      // actual reference to the current class with parent type(s).
    return n.f6.accept(this, context);                                                              // typecheck the current class methods.
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  @Override
  public TypeStruct visit(VarDeclaration n, Context context) {
    return context.Method().AddVariable(n.f1, n.f0.accept(this, context));                          // check for duplicate variables.
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
  @Override
  public TypeStruct visit(MethodDeclaration n, Context context) {
    context.SetMethod(this.classTable.get(context.ClassName()).FindMethod(n.f2));                   // actual reference to the current method.

    TypeStruct err = n.f7.accept(this, context);                                                    // typecheck local variables.
    if (err != null) {
      return err;
    }

    err = n.f8.accept(this, context);                                                               // typecheck statements.
    if (err != null) {
      return err;
    }

    TypeStruct returnType = n.f10.accept(this, context);                                            // typecheck the return type with the expected return type.
    if (returnType == null || !returnType.MatchType(context.Method().TypeStruct())) {
      return new TypeStruct("Type error");
    }

    return null;                                                                                    // no error.
  }

  /**
   * f0 -> ArrayType()
   *     | BooleanType()
   *     | IntegerType()
   *     | Identifier()
   */
  @Override
  public TypeStruct visit(Type n, Context context) {
    return n.f0.accept(this, context);                                                              // get the type.
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */
  @Override
  public TypeStruct visit(ArrayType n, Context context) {
    return new TypeStruct("ArrayType");                                                             // ArrayType.
  }

  /**
   * f0 -> "boolean"
   */
  @Override
  public TypeStruct visit(BooleanType n, Context context) {
    return new TypeStruct("BooleanType");                                                           // BooleanType.
  }

  /**
   * f0 -> "int"
   */
  @Override
  public TypeStruct visit(IntegerType n, Context context) {
    return new TypeStruct("IntegerType");                                                           // IntegerType.
  }

  /**
   *
   * f0 -> Block()
   *     | AssignmentStatement()
   *     | ArrayAssignmentStatement()
   *     | IfStatement()
   *     | WhileStatement()
   *     | PrintStatement()
   */
  @Override
  public TypeStruct visit(Statement n, Context context) {
    return n.f0.accept(this, context);                                                              // type check block.
  }

  /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   */
  @Override
  public TypeStruct visit(Block n, Context context) {
    return n.f1.accept(this, context);                                                              // typecheck statements.
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   */
  @Override
  public TypeStruct visit(AssignmentStatement n, Context context) {
    TypeStruct id = n.f0.accept(this, context);
    if (id.MatchType("Type error")) {
      return new TypeStruct("Type error");
    }

    TypeStruct expr = n.f2.accept(this, context);                                                   // typecheck the expression.
    if (expr == null) {
      return new TypeStruct("Type error");
    }

    if (!expr.MatchType(id)) {                                                                      // id and expression types don't match.
      return new TypeStruct("Type error");
    }

    return null;                                                                                    // no error.
  }

  /**
   *
   * f0 -> Identifier()
   * f1 -> "["
   * f2 -> Expression()
   * f3 -> "]"
   * f4 -> "="
   * f5 -> Expression()
   * f6 -> ";"
   */
  @Override
  public TypeStruct visit(ArrayAssignmentStatement n, Context context) {
    TypeStruct id = n.f0.accept(this, context);                                                     // find id.
    if (!id.MatchType("ArrayType")) {                                                               // id doesn't exist or isnt't an int[].
      return new TypeStruct("Type error");
    }

    TypeStruct index = n.f2.accept(this, context);                                                  // typecheck the index.
    if (index == null || !index.MatchType("IntegerType")) {                                         // index doesn't exist or isn't an integer.
      return new TypeStruct("Type error");
    }

    TypeStruct expr = n.f5.accept(this, context);                                                   // typecheck the expression.
    if (expr == null || !expr.MatchType("IntegerType")) {                                           // id and expression types don't match.
      return new TypeStruct("Type error");
    }

    return null;                                                                                    // no error.
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
  @Override
  public TypeStruct visit(IfStatement n, Context context) {
    TypeStruct expr = n.f2.accept(this, context);                                                   // typecheck the expression.
    if (expr == null || !expr.MatchType("BooleanType")) {                                           // expression isn't a boolean.
      return new TypeStruct("Type error");
    }

    TypeStruct err = n.f4.accept(this, context);                                                    // typecheck the statement inside if.
    if (err != null) {
      return new TypeStruct("Type error");
    }

    err = n.f6.accept(this, context);                                                               // typecheck the statement inside else.
    if (err != null) {
      return new TypeStruct("Type error");
    }

    return null;                                                                                    // no error.
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */
  @Override
  public TypeStruct visit(WhileStatement n, Context context) {
    TypeStruct expr = n.f2.accept(this, context);                                                   // typecheck the expression.
    if (expr == null || !expr.MatchType("BooleanType")) {                                           // expression isnt a boolean.
      return new TypeStruct("Type error");
    }

    TypeStruct err = n.f4.accept(this, context);                                                    // statement inside if
    if (err != null) {
      return new TypeStruct("Type error");
    }

    return null;                                                                                    // no error.
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   */
  @Override
  public TypeStruct visit(PrintStatement n, Context context) {
    TypeStruct expr = n.f2.accept(this, context);                                                   // typecheck the expression.
    if (expr == null || !expr.MatchType("IntegerType")) {                                           // expression isn't an int.
      return new TypeStruct("Type error");
    }

    return null;                                                                                    // no error.
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
  @Override
  public TypeStruct visit(Expression n, Context context) {
    return n.f0.accept(this, context);                                                              // typecheck the expression.
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   */
  @Override
  public TypeStruct visit(AndExpression n, Context context) {
    TypeStruct lhs = n.f0.accept(this, context);                                                    // typecheck lhs.
    if (lhs == null || !lhs.MatchType("BooleanType")) {                                             // lhs isn't a boolean.
      return new TypeStruct("Type error");
    }

    TypeStruct rhs = n.f2.accept(this, context);                                                    // typecheck rhs.
    if (rhs == null || !rhs.MatchType("BooleanType")) {                                             // rhs isn't a boolean.
      return new TypeStruct("Type error");
    }

    return new TypeStruct("BooleanType");                                                           // no error.
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  @Override
  public TypeStruct visit(CompareExpression n, Context context) {
    TypeStruct lhs = n.f0.accept(this, context);                                                    // typecheck lhs.
    if (lhs == null || !lhs.MatchType("IntegerType")) {                                             // lhs isn't an integer.
      return new TypeStruct("Type error");
    }

    TypeStruct rhs = n.f2.accept(this, context);                                                    // typecheck rhs.
    if (rhs == null || !rhs.MatchType("IntegerType")) {                                             // rhs isn't an integer.
      return new TypeStruct("Type error");
    }

    return new TypeStruct("BooleanType");                                                           // no error.
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  @Override
  public TypeStruct visit(PlusExpression n, Context context) {
    TypeStruct lhs = n.f0.accept(this, context);                                                    // typecheck lhs.
    if (lhs == null || !lhs.MatchType("IntegerType")) {                                             // lhs isn't an integer.
      return new TypeStruct("Type error");
    }

    TypeStruct rhs = n.f2.accept(this, context);                                                    // typecheck rhs.
    if (rhs == null || !rhs.MatchType("IntegerType")) {                                             // rhs isn't an integer.
      return new TypeStruct("Type error");
    }

    return new TypeStruct("IntegerType");                                                           // no error.
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  @Override
  public TypeStruct visit(MinusExpression n, Context context) {
    TypeStruct lhs = n.f0.accept(this, context);                                                    // typecheck lhs.
    if (lhs == null || !lhs.MatchType("IntegerType")) {                                             // lhs isn't an integer.
      return new TypeStruct("Type error");
    }

    TypeStruct rhs = n.f2.accept(this, context);                                                    // typecheck rhs.
    if (rhs == null || !rhs.MatchType("IntegerType")) {                                             // rhs isn't an integer.
      return new TypeStruct("Type error");
    }

    return new TypeStruct("IntegerType");                                                           // no error.
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  @Override
  public TypeStruct visit(TimesExpression n, Context context) {
    TypeStruct lhs = n.f0.accept(this, context);                                                    // typecheck lhs.
    if (lhs == null || !lhs.MatchType("IntegerType")) {                                             // lhs isn't an integer.
      return new TypeStruct("Type error");
    }

    TypeStruct rhs = n.f2.accept(this, context);                                                    // typecheck rhs.
    if (rhs == null || !rhs.MatchType("IntegerType")) {                                             // rhs isn't an integer.
      return new TypeStruct("Type error");
    }

    return new TypeStruct("IntegerType");                                                           // no error.
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */
  @Override
  public TypeStruct visit(ArrayLookup n, Context context) {
    TypeStruct id = n.f0.accept(this, context);                                                     // typecheck the identifier.
    if (id == null || !id.MatchType("ArrayType")) {                                                 // id doesn't exist or isn't an int[].
      return new TypeStruct("Type error");
    }

    TypeStruct index = n.f2.accept(this, context);                                                  // typecheck the index.
    if (index == null || !index.MatchType("IntegerType")) {                                         // index isn't an integer.
      return new TypeStruct("Type error");
    }

    return new TypeStruct("IntegerType");                                                           // no error.
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  @Override
  public TypeStruct visit(ArrayLength n, Context context) {
    TypeStruct id = n.f0.accept(this, context);                                                     // typecheck the expression.
    if (!id.MatchType("ArrayType")) {                                                               // id doesn't exist or isn't an int[].
      return new TypeStruct("Type error");
    }

    return new TypeStruct("IntegerType");                                                           // no error.
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )? f5
   * -> ")"
   */
  @Override
  public TypeStruct visit(MessageSend n, Context context) {
    ClassSymbol targetClass = this.classTable.get(n.f0.accept(this, context).Type());               // get the target class.
    if (targetClass == null) {                                                                      // target class doesn't exist.
      return new TypeStruct("Type error");
    }

    MethodSymbol targetMethod = targetClass.FindMethod(n.f2);                                       // get the target method.
    if (targetMethod == null) {                                                                     // target method doesn't exist.
      TypeStruct parentType = targetClass.ParentTypeStruct();
      while (parentType != null) {
        targetClass = this.classTable.get(parentType.Type());                                       // check parent.
        targetMethod = targetClass.FindMethod(n.f2);
        if (targetMethod != null) {                                                                 // stop at the earliest method.
          break;
        }

        parentType = parentType.SuperTypeStruct();                                                  // recurse.
      }

      if (targetMethod == null) {                                                                   // method dne -> error.
        return new TypeStruct("Type error");
      }
    }

    Context targetContext = new Context(context.Class(), new MethodSymbol(targetMethod));           // a deep copy of the target method context.
    for (SymbolTable scope : context.Method().Scopes()) {                                           // push method scope onto the target method context.
      targetContext.Method().PushScope(scope);
    }

    targetContext.Method().PushScope(context.Method().FormalParameters());                          // push formal parameter scope onto the target method context..

    TypeStruct err = n.f4.accept(this, targetContext);                                              // typecheck the expression list using the target context.
    if (err != null) {                                                                              // invalid method call.
      return new TypeStruct("Type error");
    }

    if (!targetContext.Method().FormalParameters().Empty()) {                                       // invalid method call.
      return new TypeStruct("Type error");
    }

    return targetMethod.TypeStruct();                                                               // no error.
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )*
   */
  @Override
  public TypeStruct visit(ExpressionList n, Context context) {
    SymbolTable formalParameters = context.Method().FormalParameters();                             // get the formal parameters.
    if (formalParameters.Empty()) {                                                                 // invalid method call.
      return new TypeStruct("Type error");
    }

    TypeStruct expr = n.f0.accept(this, context);                                                   // typecheck the expression.
    if (expr == null || expr.MatchType("Type error")) {                                             // invalid expression.
      return expr;
    }

    Pair formal = formalParameters.PopParameter();                                                  // get the next formal parameter.
    if (formal == null || !expr.MatchType(formal.TypeStruct())) {                                   // invalid method call or type mismatch.
      return new TypeStruct("Type error");
    }

    return n.f1.accept(this, context);                                                              // typecheck the rest of the expressions in the list.
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  @Override
  public TypeStruct visit(ExpressionRest n, Context context) {
    SymbolTable formalParameters = context.Method().FormalParameters();                             // get the formal parameters.
    if (formalParameters.Empty()) {                                                                 // invalid method call.
      return new TypeStruct("Type error");
    }

    TypeStruct expr = n.f1.accept(this, context);                                                   // typecheck the expression.
    if (expr == null || expr.MatchType("Type error")) {                                             // invalid expression.
      return expr;
    }

    Pair formal = formalParameters.PopParameter();                                                  // get the next formal parameter.
    if (formal == null || !expr.MatchType(formal.TypeStruct())) {                                   // invalid method call or type mismatch.
      return new TypeStruct("Type error");
    }

    return null;                                                                                    // no error.
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
  @Override
  public TypeStruct visit(PrimaryExpression n, Context context) {
    return n.f0.accept(this, context);                                                              // typecheck the primary expression.
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  @Override
  public TypeStruct visit(IntegerLiteral n, Context context) {
    return new TypeStruct("IntegerType");                                                           // IntegerType.
  }

  /**
   * f0 -> "true"
   */
  @Override
  public TypeStruct visit(TrueLiteral n, Context context) {
    return new TypeStruct("BooleanType");                                                           // BooleanType.
  }

  /**
   * f0 -> "false"
   */
  @Override
  public TypeStruct visit(FalseLiteral n, Context context) {
    return new TypeStruct("BooleanType");                                                           // BooleanType.
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  @Override
  public TypeStruct visit(Identifier n, Context context) {
    TypeStruct id = context.Method().FindVariable(n);                                               // get the identifier.
    if (id == null) {                                                                               // if the id isn't in the local scope, recursively check class scope(s).
      id = context.Class().FieldTypeStruct(n);
    }

    if (id == null) {                                                                               // if id still isn't found, check to see that the class exists.
      ClassSymbol targetClass = this.classTable.get(n.f0.tokenImage);
      if (targetClass != null) {                                                                    // id is a class type.
        id = targetClass.TypeStruct();
      }
    }

    if (id == null && context.Class().ParentTypeStruct() != null) {                                 // recurse upwards
      Context targetContext = new Context(this.classTable.get(context.Class().Parent()),
                context.Method());
      id = this.visit(n, targetContext);
      if (id.MatchType("Type error")) {
        return new TypeStruct("Type error");
      }
    }

    if (id == null) {                                                                               // id doesn't exist.
      return new TypeStruct("Type error");
    }

    return id;                                                                                      // no error.
  }

  /**
   * f0 -> "this"
   */
  @Override
  public TypeStruct visit(ThisExpression n, Context context) {
    if (context.Class().ParentTypeStruct() != null && context.Class().Parent().equals("main")) {    // current context is main.
      return new TypeStruct("Type error");
    }

    return context.Class().TypeStruct();                                                            // no error.
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   */
  @Override
  public TypeStruct visit(ArrayAllocationExpression n, Context context) {
    TypeStruct expr = n.f3.accept(this, context);                                                   // typecheck the expression.
    if (expr == null || !expr.MatchType("IntegerType")) {                                           // expression isn't an integer.
      return new TypeStruct("Type error");
    }

    return new TypeStruct("ArrayType");                                                             // no error.
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  @Override
  public TypeStruct visit(AllocationExpression n, Context context) {
    ClassSymbol classType = this.classTable.get(n.f1.f0.tokenImage);                                // get the class name.
    if (classType == null) {                                                                        // class doesn't exist.
      return new TypeStruct("Type error");
    }

    return classType.TypeStruct();                                                                  // no error.
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   */
  @Override
  public TypeStruct visit(NotExpression n, Context context) {
    TypeStruct expr = n.f1.accept(this, context);                                                   // typecheck the expression.
    if (expr == null || !expr.MatchType("BooleanType")) {                                           // expression isn't a boolean.
      return new TypeStruct("Type error");
    }

    return new TypeStruct("BooleanType");                                                           // no error.
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  @Override
  public TypeStruct visit(BracketExpression n, Context context) {
    return n.f1.accept(this, context);                                                              // typecheck the expression.
  }
}
