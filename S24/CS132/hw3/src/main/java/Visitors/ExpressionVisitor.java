package Visitors;

import java.util.Enumeration;

import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionVisitor extends GJDepthFirst<SparrowCode, Context> {
  VarGenerator generator = VarGenerator.GetInstance();
  TypeVisitor typeVisitor = new TypeVisitor();
  IRTypeVisitor irTypeVisitor = new IRTypeVisitor();

  //
  // Auto class visitors--probably don't need to be overridden.
  //
  public SparrowCode visit(NodeList n, Context context) {
    SparrowCode stmt = new SparrowCode();
    int _count = 0;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      SparrowCode res = e.nextElement().accept(this, context);
      if (res != null) {
        stmt.AddBlockStmt(res);
        if (!res.Params().isEmpty()) {
          for (String param : res.Params()) {
            stmt.AddParam(param);
          }
        }
      }
      _count++;
    }
    return stmt;
  }

  public SparrowCode visit(NodeListOptional n, Context context) {
    if (n.present()) {
      SparrowCode stmt = new SparrowCode();
      int _count = 0;
      for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
        SparrowCode res = e.nextElement().accept(this, context);
        if (res != null) {
          stmt.AddBlockStmt(res);
          if (!res.Params().isEmpty()) {
            for (String param : res.Params()) {
              stmt.AddParam(param);
            }
          }
        }
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
      SparrowCode res = e.nextElement().accept(this, context);
      if (res != null) {
        stmt.AddBlockStmt(res);
        if (!res.Params().isEmpty()) {
          for (String param : res.Params()) {
            stmt.AddParam(param);
          }
        }
      }
      _count++;
    }
    return stmt;
  }

  public SparrowCode visit(NodeToken n, Context context) {
    return null;
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
  public SparrowCode visit(Expression n, Context context) {
    return n.f0.accept(this, context);
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   */
  public SparrowCode visit(AndExpression n, Context context) {
    //   lhs = expr
    //   if0 lhs goto falseLabel
    //   rhs = expr
    //   if0 rhs goto falseLabel
    //   id = 1
    //   goto endLabel
    // falseLabel:
    //   id = 0
    // endLabel:
    String id = generator.NextId();
    String falseLabel = "false_and_" + generator.NextLabel();
    String endLabel = "end_and_" + generator.NextLabel();

    SparrowCode lhs = n.f0.accept(this, context);
    SparrowCode rhs = n.f2.accept(this, context);
    SparrowCode stmt = new SparrowCode();
    stmt.AddBlockStmt(lhs);
    stmt.AddIfStmt(lhs.Id(), falseLabel);
    stmt.AddBlockStmt(rhs);
    stmt.AddIfStmt(rhs.Id(), falseLabel);
    stmt.AddAssignStmt(id, 1);
    stmt.AddGotoStmt(endLabel);
    stmt.AddLabelStmt(falseLabel);
    stmt.AddAssignStmt(id, 0);
    stmt.AddLabelStmt(endLabel);
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public SparrowCode visit(CompareExpression n, Context context) {
    String id = generator.NextId();

    SparrowCode lhs = n.f0.accept(this, context);
    SparrowCode rhs = n.f2.accept(this, context);
    SparrowCode stmt = new SparrowCode();

    stmt.AddBlockStmt(lhs);
    stmt.AddBlockStmt(rhs);
    stmt.AddCompareStmt(id, lhs.Id(), rhs.Id());
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  public SparrowCode visit(PlusExpression n, Context context) {
    String id = generator.NextId();

    SparrowCode lhs = n.f0.accept(this, context);
    SparrowCode rhs = n.f2.accept(this, context);
    SparrowCode stmt = new SparrowCode();

    stmt.AddBlockStmt(lhs);
    stmt.AddBlockStmt(rhs);
    stmt.AddPlusStmt(id, lhs.Id(), rhs.Id());
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  public SparrowCode visit(MinusExpression n, Context context) {
    String id = generator.NextId();

    SparrowCode lhs = n.f0.accept(this, context);
    SparrowCode rhs = n.f2.accept(this, context);
    SparrowCode stmt = new SparrowCode();

    stmt.AddBlockStmt(lhs);
    stmt.AddBlockStmt(rhs);
    stmt.AddMinusStmt(id, lhs.Id(), rhs.Id());
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  public SparrowCode visit(TimesExpression n, Context context) {
    String id = generator.NextId();

    SparrowCode lhs = n.f0.accept(this, context);
    SparrowCode rhs = n.f2.accept(this, context);
    SparrowCode stmt = new SparrowCode();

    stmt.AddBlockStmt(lhs);
    stmt.AddBlockStmt(rhs);
    stmt.AddMultiplyStmt(id, lhs.Id(), rhs.Id());
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */
  public SparrowCode visit(ArrayLookup n, Context context) {
    String id = generator.NextId();
    String arrId = generator.NextId();
    String byteSizeId = generator.NextId();

    SparrowCode arrExpr = n.f0.accept(this, context);
    SparrowCode offsetExpr = n.f2.accept(this, context);
    offsetExpr.MakeByteSize(byteSizeId, offsetExpr.Id());
    offsetExpr.AddOutOfBoundsCheck(arrExpr.Id());

    SparrowCode stmt = new SparrowCode();
    stmt.AddBlockStmt(arrExpr);
    stmt.AddBlockStmt(offsetExpr);
    stmt.AddPlusStmt(arrId, arrExpr.Id(), byteSizeId);
    stmt.AddLoadStmt(id, arrId, 0);
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  public SparrowCode visit(ArrayLength n, Context context) {
    String id = generator.NextId();
    SparrowCode var = n.f0.accept(this, context);
    SparrowCode stmt = new SparrowCode();
    stmt.AddBlockStmt(var);
    stmt.AddNullPointerCheck(var.Id());
    stmt.AddLoadStmt(id, var.Id(), 0);
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 > ")"
   */
  public SparrowCode visit(MessageSend n, Context context) {
    TypeStruct type = n.f0.accept(irTypeVisitor, context);
    SparrowCode objExpr = n.f0.accept(this, context);
    SparrowCode params = n.f4.accept(this, context);

    ClassSymbol targetClass = context.ClassTable().get(type.Type());
    while (targetClass != null && targetClass.FindMethod(n.f2.f0.tokenImage) == null) {
      targetClass = targetClass.ParentSymbol();
    }

    if (targetClass == null) {
      SparrowCode shortCircuit = new SparrowCode();
      shortCircuit.AddGotoStmt("null_err_label");
      return shortCircuit;
    }

    String id = generator.NextId();
    String methodId = generator.NextId();
    String vTableId = methodId;
    SparrowObject vTable = context.Object(type.Type() + "_vTable");
    String methodName = targetClass.Name() + "__" + n.f2.f0.tokenImage;
    String prefix = targetClass.Name() + "_" + n.f2.f0.tokenImage + "_";

    SparrowCode stmt = new SparrowCode();
    stmt.AddBlockStmt(objExpr);
    stmt.AddNullPointerCheck(objExpr.Id());
    stmt.AddLoadStmt(vTableId, objExpr.Id(), 0);
    stmt.AddLoadStmt(methodId, vTableId, vTable.FieldByteOffset(methodName));
    if (params != null) {
      LinkedList<String> formalParams = new LinkedList<>();
      for (String formalParam : targetClass.FindMethod(n.f2.f0.tokenImage).FormalParameters().keySet()) {
        formalParams.offer(prefix + formalParam);
      }

      stmt.AddBlockStmt(params);
      stmt.AddCallStmt(id, objExpr.Id(), methodId, params.Params());
    } else {
      stmt.AddCallStmt(id, objExpr.Id(), methodId);
    }

    SparrowObject base = context.Object(type.Type());
    SparrowObject obj = new SparrowObject(base);
    obj.SetId("this");
    context.PutObject("this", obj);
    context.SetThis(obj);

    stmt.SetId(id);
    return stmt;
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )*
   */
  public SparrowCode visit(ExpressionList n, Context context) {
    SparrowCode expr = n.f0.accept(this, context);
    SparrowCode rest = n.f1.accept(this, context);
    expr.AddParam(expr.Id());
    if (rest == null) {
      return expr;
    }

    expr.AddBlockStmt(rest);
    for (String param : rest.Params()) {
      expr.AddParam(param);
    }

    return expr;
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  public SparrowCode visit(ExpressionRest n, Context context) {
    SparrowCode expr =  n.f1.accept(this, context);
    expr.AddParam(expr.Id());

    return expr;
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
  public SparrowCode visit(PrimaryExpression n, Context context) {
    return n.f0.accept(this, context);
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  public SparrowCode visit(IntegerLiteral n, Context context) {
    String id = generator.NextId();
    int value = Integer.parseInt(n.f0.tokenImage);

    SparrowCode stmt = new SparrowCode();
    stmt.AddAssignStmt(id, value);
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> "true"
   */
  public SparrowCode visit(TrueLiteral n, Context context) {
    String id = generator.NextId();

    SparrowCode stmt = new SparrowCode();
    stmt.AddAssignStmt(id, 1);
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> "false"
   */
  public SparrowCode visit(FalseLiteral n, Context context) {
    String id = generator.NextId();

    SparrowCode stmt = new SparrowCode();
    stmt.AddAssignStmt(id, 0);
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public SparrowCode visit(Identifier n, Context context) {
    String id = "";
    String localPrefix = context.Class().Name() + "_" + context.Method().Name() + "_";

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

      stmt.AddLoadStmt(id, context.This().Id(), fieldByteOffset);
      stmt.SetId(id);
    }

    return stmt;
  }

  /**
   * f0 -> "this"
   */
  public SparrowCode visit(ThisExpression n, Context context) {
    SparrowCode stmt = new SparrowCode();
    stmt.SetId("this");

    return stmt;
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   */
  public SparrowCode visit(ArrayAllocationExpression n, Context context) {
    String id = generator.NextId();
    String byteSizeId = generator.NextId();

    SparrowCode size = n.f3.accept(this, context);
    SparrowCode stmt = new SparrowCode();

    stmt.AddBlockStmt(size);
    stmt.MakeByteSize(byteSizeId, size.Id());
    stmt.AddAllocStmt(id, byteSizeId);
    stmt.AddStoreStmt(size.Id(), id, 0);
    stmt.SetId(id);

    return stmt;
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  public SparrowCode visit(AllocationExpression n, Context context) {
    String id = generator.NextId();
    TypeStruct type =  n.f1.accept(typeVisitor, context);

    SparrowObject base = context.Object(type.Type());
    SparrowObject vTable = context.Object(type.Type() + "_vTable");

    SparrowCode stmt = new SparrowCode();

    String classByteSizeId = "v" + base.Id() + "_size";
    stmt.AddAssignStmt(classByteSizeId, base.ByteSize());
    stmt.AddAllocStmt(id, classByteSizeId);

    String vTableByteSizeId = "v" + vTable.Id() + "_size";
    stmt.AddAssignStmt(vTableByteSizeId, vTable.ByteSize());
    stmt.AddAllocStmt(vTable.Id(), vTableByteSizeId);

    SparrowObject obj = new SparrowObject(base);
    obj.SetId(id);
    context.PutObject(id, obj);

    LinkedHashMap<String, Integer> methods = vTable.FieldByteOffsets();
    for (Map.Entry<String, Integer> method : methods.entrySet()) {
      String methodId = generator.NextId();
      stmt.AddFuncAssignStmt(methodId, method.getKey());
      stmt.AddStoreStmt(methodId, vTable.Id(), method.getValue());
    }

    stmt.AddStoreStmt(vTable.Id(), obj.Id(), 0);
    stmt.SetId(obj.Id());
    return stmt;
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   */
  public SparrowCode visit(NotExpression n, Context context) {
    String id = generator.NextId();
    String one = "one_not_" + generator.NextId();
    SparrowCode expr = n.f1.accept(this, context);

    SparrowCode stmt = new SparrowCode();
    stmt.AddBlockStmt(expr);
    stmt.AddAssignStmt(one, 1);
    stmt.AddMinusStmt(id, one, expr.Id());

    stmt.SetId(id);
    return stmt;
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  public SparrowCode visit(BracketExpression n, Context context) {
    return n.f1.accept(this, context);
  }
}
