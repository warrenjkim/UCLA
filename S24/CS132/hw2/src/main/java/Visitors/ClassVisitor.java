package Visitors;

import Utils.*;
import java.util.Enumeration;
import java.util.HashMap;
import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;

/**
 * Visits the AST in a depth first manner, only visiting necessary nodes. This visitor will only
 * build the class signatures and check for cyclic type hierarchies.
 *
 * The class table looks like:
 * (Name, ClassSymbol), where ClassSymbol contains the class'
 *   - field types,
 *   - type hierarchy,
 *   - method signatures,
 */
public class ClassVisitor extends GJDepthFirst<TypeStruct, Context> {
  private HashMap<String, ClassSymbol> classTable;

  public HashMap<String, ClassSymbol> ClassTable() {
    return this.classTable;
  }

  public ClassVisitor() {
    super();
    this.classTable = new HashMap<>();
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
      TypeStruct err = e.nextElement().accept(this, context);                                       // check for errors.
      if (err != null && err.MatchType(new TypeStruct("Type error"))) {
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
      return err;
    }

    err = n.f1.accept(this, context);                                                               // typecheck other classes.
    if (err != null) {
      return err;
    }

    err = checkAcyclicityOverloading();
    if (err != null) {
        return err;
    }

    buildFieldSubtypes();

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
    context.SetMethod(n.f6.tokenImage, new TypeStruct("void"));                                     // build main method.
    context.Method().AddFormalParameter(n.f11, new TypeStruct("StringArrayType"));                  // String [] isn't a part of MiniJava.
    context.SetClass(n.f1, new Identifier(new NodeToken("main")));                                  // make it easy with ThisExpression, the main class has a "super type" of main.
    context.Class().AddMethod(context.Method());                                                    // add the main method to the main class.

    TypeStruct err = n.f14.accept(this, context);                                                   // typecheck class fields.
    if (err != null) {
      return err;
    }

    this.classTable.put(context.Class().Name(), context.Class());                                   // add class to the global class table.
    return null;                                                                                    // no error.
  }

  /**
   * f0 -> ClassDeclaration()
   *     | ClassExtendsDeclaration()
   */
  @Override
  public TypeStruct visit(TypeDeclaration n, Context context) {
    return n.f0.accept(this, context);                                                              // typecheck classes.
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  @Override
  public TypeStruct visit(ClassDeclaration n, Context context) {
    context.SetClass(n.f1);                                                                         // build the current class.
    if (this.classTable.containsKey(context.Class().Name())) {                                      // duplicate class name.
      return new TypeStruct("Type error");
    }

    TypeStruct err = n.f3.accept(this, context);                                                    // typecheck current class fields.
    if (err != null) {
      return err;
    }

    err = n.f4.accept(this, context);                                                               // typecheck current class methods.
    if (err != null) {
      return err;
    }

    this.classTable.put(context.Class().Name(), context.Class());                                   // add class to the global class table.
    return null;                                                                                    // no error.
  }

  /**
   *
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
    context.SetClass(n.f1, n.f3);                                                                   // build the current class (with parent type).
    if (this.classTable.containsKey(context.Class().Name())) {                                      // duplicate class name.
      return new TypeStruct("Type error");
    }

    TypeStruct err = n.f5.accept(this, context);                                                    // typecheck current class fields.
    if (err != null) {
      return err;
    }

    err = n.f6.accept(this, context);                                                               // typecheck current class methods.
    if (err != null) {
      return err;
    }

    this.classTable.put(context.Class().Name(), context.Class());                                   // add class to the global class table.
    return null;                                                                                    // no error
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  @Override
  public TypeStruct visit(VarDeclaration n, Context context) {
    return context.Class().AddField(n.f1, n.f0.accept(this, context));                              // check for duplicate class fields.
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
   * f10 ->
   * Expression()
   * f11 -> ";"
   * f12 -> "}"
   */
  @Override
  public TypeStruct visit(MethodDeclaration n, Context context) {
    context.SetMethod(n.f2, n.f1.accept(this, context));                                            // build the current method.
    if (context.Class().Methods().containsKey(context.Method().Name())) {                           // duplicate method name.
      return new TypeStruct("Type error");
    }

    TypeStruct err = n.f4.accept(this, context);                                                    // typecheck formal parameters
    if (err != null) {
      return err;
    }

    context.Class().AddMethod(context.Method());                                                    // add the method to the current class.
    return null;                                                                                    // no error.
  }

  /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   */
  @Override
  public TypeStruct visit(FormalParameterList n, Context context) {
    TypeStruct err = n.f0.accept(this, context);                                                    // typecheck the formal parameter.
    if (err != null) {
      return err;
    }

    return n.f1.accept(this, context);                                                              // typecheck formal parameters.
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   */
  @Override
  public TypeStruct visit(FormalParameter n, Context context) {
    return context.Method().AddFormalParameter(n.f1, n.f0.accept(this, context));                   // check for duplicate formal parameters.
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   */
  @Override
  public TypeStruct visit(FormalParameterRest n, Context context) {
    return n.f1.accept(this, context);                                                              // typecheck formal parameter.
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
   * f0 -> <IDENTIFIER>
   */
  @Override
  public TypeStruct visit(Identifier n, Context context) {
    return new TypeStruct(n);                                                                       // Name of the identifier.
  }

  private TypeStruct checkFormalParameters(SymbolTable curr, SymbolTable parent) {
    if (curr.Table().size() != parent.Table().size()) {                                             // overloading -> error.
      return new TypeStruct("Type error");
    }

    while(!curr.Table().isEmpty()) {                                                                // while there are params to match.
      Pair currParam = curr.Table().pollFirst();                                                    // pop the first element.
      Pair parentParam = parent.Table().pollFirst();                                                // pop the first element.

      if (!currParam.Type().equals(parentParam.Type())) {                                           // types don't match -> overloading -> error.
        return new TypeStruct("Type error");
      }
    }

    return null;                                                                                    // no error.
  }

  private TypeStruct checkOverloading(HashMap<String, MethodSymbol> curr,
                                      HashMap<String, MethodSymbol> parent) {
    for (HashMap.Entry<String, MethodSymbol> method : parent.entrySet()) {                          // for each method in parent class.
      if (curr.containsKey(method.getKey())) {                                                      // child redefines method -> check for overloading.
        if (!curr.get(method.getKey()).Type().equals(method.getValue().Type())) {                   // check exact type signatures.
          return new TypeStruct("Type error");
        }

        TypeStruct err = checkFormalParameters(
            new SymbolTable(curr.get(method.getKey()).FormalParameters()),
            new SymbolTable(method.getValue().FormalParameters()));                                 // match deep copy of params.
        if (err != null) {                                                                          // params don't match -> overloading -> error.
          return new TypeStruct("Type error");
        }
      }
    }

    return null;                                                                                    // no error.
  }

  private TypeStruct checkAcyclicityOverloading() {
    for (HashMap.Entry<String, ClassSymbol> currClass : this.classTable.entrySet()) {               // cyclic dependency check.
      ClassSymbol currSymbol = currClass.getValue();                                                // actual reference to the current class symbol.
      TypeStruct currType = currSymbol.TypeStruct();                                                // actual reference to the current class type.

      if (currSymbol.ParentTypeStruct() != null) {                                                  // check if we need to add a super type.
        ClassSymbol parentSymbol = this.classTable.get(currSymbol.Parent());                        // actual reference to the parent class.
        if (parentSymbol == null && !currSymbol.Parent().equals("main")) {                          // parent does not exist -> error.
              return new TypeStruct("Type error");
        }

        while (parentSymbol != null) {                                                              // traverse up the type hierarchy.
          HashMap<String, MethodSymbol> currMethods =
                                        new HashMap<String, MethodSymbol>(currSymbol.Methods());    // deep copy of current methods.
          HashMap<String, MethodSymbol> parentMethods =
                                        new HashMap<String, MethodSymbol>(parentSymbol.Methods());  // deep copy of parent methods.
          TypeStruct err = checkOverloading(currMethods, parentMethods);                            // check for overloaded methods.
          if (err != null) {                                                                        // overloaded -> error.
            return new TypeStruct("Type error");
          }

          TypeStruct parentType = parentSymbol.TypeStruct();                                        // actual reference to the parent class type.
          currType.SetSuperTypeStruct(parentType);                                                  // add the parent class type to the current class type hierarchy.
          parentSymbol = this.classTable.get(parentSymbol.Parent());                                // actual reference to the parent class.
          currType = parentType;                                                                    // set the current class type to the parent class type.
          if (currType.MatchType(currSymbol.Type())) {                                              // there's a cycle -> error.
            return new TypeStruct("Type error");
          }
        }
      }
    }

    return null;                                                                                    // no error.
  }

  private void buildFieldSubtypes() {
    for (HashMap.Entry<String, ClassSymbol> currClass : this.classTable.entrySet()) {
      for (SymbolTable scope : currClass.getValue().Fields()) {
        for (Pair field : scope.Table()) {
          ClassSymbol classType = this.classTable.get(field.Type());
          if (classType != null) {
            field.SetTypeStruct(classType.TypeStruct());
          }
        }
      }
    }
  }
}
