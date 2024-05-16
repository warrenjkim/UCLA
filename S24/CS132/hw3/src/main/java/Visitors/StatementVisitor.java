package Visitors;

import java.util.Enumeration;

import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;

public class StatementVisitor extends GJDepthFirst<SparrowCode, Context> {
  private VarGenerator generator = VarGenerator.GetInstance();
  private ExpressionVisitor expressionVisitor = new ExpressionVisitor();

  //
  // Auto class visitors--probably don't need to be overridden.
  //
  public SparrowCode visit(NodeList n, Context context) {
    SparrowCode stmt = new SparrowCode();
    int _count = 0;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      stmt.AddBlockStmt(e.nextElement().accept(this, context));
      _count++;
    }
    return stmt;
  }

  public SparrowCode visit(NodeListOptional n, Context context) {
    if (n.present()) {
      SparrowCode stmt = new SparrowCode();
      int _count = 0;
      for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
        stmt.AddBlockStmt(e.nextElement().accept(this, context));
        _count++;
      }
      return stmt;
    } else return null;
  }

  public SparrowCode visit(NodeOptional n, Context context) {
    if (n.present()) return n.node.accept(this, context);
    else return null;
  }

  public SparrowCode visit(NodeSequence n, Context context) {
    SparrowCode stmt = new SparrowCode();
    int _count = 0;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      stmt.AddBlockStmt(e.nextElement().accept(this, context));
      _count++;
    }
    return stmt;
  }

  public SparrowCode visit(NodeToken n, Context context) {
    return null;
  }

  //
  // User-generated visitor methods below
  //

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public SparrowCode visit(VarDeclaration n, Context context) {
    String name = n.f1.f0.tokenImage;
    String id = context.Class().Name() + "_" + context.Method().Name() + "_" + name;

    SparrowCode stmt = new SparrowCode();
    SparrowObject obj = context.Object(id);
    if (obj != null) {
      String byteSizeId = "v" + context.Class().Name() + "_Size";
      stmt.AddAssignStmt(byteSizeId, obj.ByteSize());
      stmt.AddAllocStmt(id, byteSizeId);
    }

    return stmt;
  }

  /**
   * f0 -> Block()
   *     | AssignmentStatement()
   *     | ArrayAssignmentStatement()
   *     | IfStatement()
   *     | WhileStatement()
   *     | PrintStatement()
   */
  public SparrowCode visit(Statement n, Context context) {
    return n.f0.accept(this, context);
  }

  /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   */
  public SparrowCode visit(Block n, Context context) {
    return n.f1.accept(this, context);
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   */
  public SparrowCode visit(AssignmentStatement n, Context context) {
    SparrowCode id = n.f0.accept(expressionVisitor, context);
    SparrowCode idStore = n.f0.accept(this, context);
    SparrowCode expr = n.f2.accept(expressionVisitor, context);
    SparrowCode stmt = new SparrowCode();

    // load id
    // expr = [expr]
    // id = expr
    // store id (if possible)
    stmt.AddBlockStmt(id);
    stmt.AddBlockStmt(expr);
    stmt.AddAssignStmt(id.Id(), expr.Id());
    stmt.AddBlockStmt(idStore);

    return stmt;
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
  public SparrowCode visit(ArrayAssignmentStatement n, Context context) {
    String arrId = generator.NextId();

    SparrowCode arrExpr = n.f0.accept(expressionVisitor, context);
    SparrowCode offsetExpr = n.f2.accept(expressionVisitor, context);
    SparrowCode valueExpr = n.f5.accept(expressionVisitor, context);

    String byteSizeId = generator.NextId();
    offsetExpr.MakeByteSize(byteSizeId, offsetExpr.Id());
    offsetExpr.AddOutOfBoundsCheck(arrExpr.Id());

    SparrowCode stmt = new SparrowCode();
    stmt.AddBlockStmt(arrExpr);
    stmt.AddBlockStmt(offsetExpr);
    stmt.AddBlockStmt(valueExpr);
    stmt.AddPlusStmt(arrId, arrExpr.Id(), byteSizeId);
    stmt.AddStoreStmt(valueExpr.Id(), arrId, 0);

    return stmt;
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
  public SparrowCode visit(IfStatement n, Context context) {
    String falseLabel = "else_" + generator.NextLabel();
    String endLabel = "end_if_" + generator.NextLabel();

    SparrowCode condition = n.f2.accept(expressionVisitor, context);
    SparrowCode trueStmt = n.f4.accept(this, context);
    SparrowCode falseStmt = n.f6.accept(this, context);

    //   if0 condition goto falseLabel
    //   trueStmt
    //   goto endLabel
    // falseLabel:
    //   falseStmt
    // endLabel:
    SparrowCode stmt = new SparrowCode();
    stmt.AddBlockStmt(condition);
    stmt.AddIfStmt(condition.Id(), falseLabel);
    stmt.AddBlockStmt(trueStmt);
    stmt.AddGotoStmt(endLabel);
    stmt.AddLabelStmt(falseLabel);
    stmt.AddBlockStmt(falseStmt);
    stmt.AddLabelStmt(endLabel);

    return stmt;
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */
  public SparrowCode visit(WhileStatement n, Context context) {
    String loopLabel = "loop_" + generator.NextLabel();
    String endLabel = "end_loop_" + generator.NextLabel();

    SparrowCode condition = n.f2.accept(expressionVisitor, context);
    SparrowCode trueStmt = n.f4.accept(this, context);

    // loopLabel:
    //   if0 expr goto endLabel
    //   trueStmt
    //   goto loopLabel
    // endLabel:
    SparrowCode stmt = new SparrowCode();
    stmt.AddLabelStmt(loopLabel);
    stmt.AddBlockStmt(condition);
    stmt.AddIfStmt(condition.Id(), endLabel);
    stmt.AddBlockStmt(trueStmt);
    stmt.AddGotoStmt(loopLabel);
    stmt.AddLabelStmt(endLabel);

    return stmt;
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   */
  public SparrowCode visit(PrintStatement n, Context context) {
    SparrowCode expr = n.f2.accept(expressionVisitor, context);
    SparrowCode stmt = new SparrowCode();
    stmt.AddBlockStmt(expr);
    stmt.AddPrintStmt(expr.Id());

    return stmt;
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public SparrowCode visit(Identifier n, Context context) {
    String localPrefix = context.Class().Name() + "_" + context.Method().Name() + "_";
    String id = "";

    SparrowCode stmt = new SparrowCode();
    if (context.Method().VariableTypeStruct(n.f0.tokenImage) != null) {
      id = localPrefix + n.f0.tokenImage;
      stmt.SetId(id);
    } else if (context.Class().FieldTypeStruct(n.f0.tokenImage) != null) {
      ClassSymbol curr = context.Class();
      while (curr.Field(n.f0.tokenImage) == null) {
        curr = curr.ParentSymbol();
      }

      String fieldPrefix = curr.Name() + "_";
      id = fieldPrefix + n.f0.tokenImage;
      SparrowObject classObj = context.Object(curr.Name());
      Integer fieldByteOffset = classObj.FieldByteOffset(id);
      stmt.AddStoreStmt(id, context.This().Id(), fieldByteOffset);
      stmt.SetId(id);
    }

    return stmt;
  }
}
