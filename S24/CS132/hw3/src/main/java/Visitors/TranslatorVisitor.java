package Visitors;

import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJVoidDepthFirst;
import java.util.LinkedList;

public class TranslatorVisitor extends GJVoidDepthFirst<Context> {
  private StatementVisitor statementVisitor = new StatementVisitor();
  private ExpressionVisitor expressionVisitor = new ExpressionVisitor();

  //
  // User-generated visitor methods below
  //

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
    context.SetMethodCode(new SparrowCode());

    SparrowCode vars = n.f14.accept(statementVisitor, context);
    SparrowCode block = n.f15.accept(statementVisitor, context);
    SparrowCode body = new SparrowCode();

    LinkedList<String> params = new LinkedList<>();
    params.add(n.f11.f0.tokenImage);
    body.AddMainLabelStmt(context.Class().Name(), context.Method().Name());
    body.AddBlockStmt(vars);
    body.AddBlockStmt(block);
    body.AddGotoStmt("end_" + context.Class().Name() + "__" + context.Method().Name());
    body.AddErrorStmts();
    body.AddLabelStmt("end_" + context.Class().Name() + "__" + context.Method().Name());
    body.AddReturnStmt("v0");
    context.Method().SetSparrowCode(body);
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
    n.f6.accept(this, context);
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
    LinkedList<String> formalParams = new LinkedList<>();
    for (String param : context.Method().FormalParameters().keySet()) {
      formalParams.offer(context.Class().Name() + "_" + context.Method().Name() + "_" + param);
    }

    SparrowCode vars = n.f7.accept(statementVisitor, context);
    SparrowCode block = n.f8.accept(statementVisitor, context);
    SparrowCode returnExpr = n.f10.accept(expressionVisitor, context);

    SparrowCode body = new SparrowCode();
    body.AddFuncLabelStmt(context.Class().Name(), context.Method().Name(), formalParams);
    body.AddBlockStmt(vars);

    if (block != null) {
      body.AddBlockStmt(block);
    }

    body.AddBlockStmt(returnExpr);
    body.AddGotoStmt("end_" + context.Class().Name() + "__" + context.Method().Name());
    body.AddErrorStmts();
    body.AddLabelStmt("end_" + context.Class().Name() + "__" + context.Method().Name());
    body.AddReturnStmt(returnExpr.Id());
    context.Method().SetSparrowCode(body);
  }
}
