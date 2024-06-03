package Visitors;

import IR.token.Identifier;
import Utils.*;
import java.util.Map;
import sparrow.*;
import sparrow.visitor.ArgVisitor;

public class ControlFlowVisitor implements ArgVisitor<FunctionSymbol> {
  private Map<String, FunctionSymbol> functionMap;
  private String currLabel;

  public ControlFlowVisitor(Map<String, FunctionSymbol> functionMap) {
    this.functionMap = functionMap;
  }


  public Map<String, FunctionSymbol> FunctionMap() {
    return functionMap;
  }

  /*   List<FunctionDecl> funDecls; */
  public void visit(Program n, FunctionSymbol context) {
    for (FunctionDecl fd : n.funDecls) {
      fd.accept(this, null);
    }
  }

  /*   Program parent;
   *   FunctionName functionName;
   *   List<Identifier> formalParameters;
   *   Block block; */
  public void visit(FunctionDecl n, FunctionSymbol context) {
    context = functionMap.get(n.functionName.toString());
    n.block.accept(this, context);
  }

  /*   FunctionDecl parent;
   *   List<Instruction> instructions;
   *   Identifier return_id; */
  public void visit(Block n, FunctionSymbol context) {
    for (Instruction ins : n.instructions) {
      ins.accept(this, context);
    }
  }

  /*   Label label; */
  public void visit(LabelInstr n, FunctionSymbol context) {
    if (context.LabelRanges().get(n.label.toString()) != null) {
      currLabel = n.label.toString();
    }
  }

  /*   Identifier lhs;
   *   int rhs; */
  public void visit(Move_Id_Integer n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
  }

  /*   Identifier lhs;
   *   FunctionName rhs; */
  public void visit(Move_Id_FuncName n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public void visit(Add n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.arg1.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.arg2.toString()).ExtendRange(labelRange);
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public void visit(Subtract n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.arg1.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.arg2.toString()).ExtendRange(labelRange);
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public void visit(Multiply n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.arg1.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.arg2.toString()).ExtendRange(labelRange);
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public void visit(LessThan n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.arg1.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.arg2.toString()).ExtendRange(labelRange);
  }

  /*   Identifier lhs;
   *   Identifier base;
   *   int offset; */
  public void visit(Load n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.base.toString()).ExtendRange(labelRange);
  }

  /*   Identifier base;
   *   int offset;
   *   Identifier rhs; */
  public void visit(Store n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.base.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.rhs.toString()).ExtendRange(labelRange);
  }

  /*   Identifier lhs;
   *   Identifier rhs; */
  public void visit(Move_Id_Id n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.rhs.toString()).ExtendRange(labelRange);
  }

  /*   Identifier lhs;
   *   Identifier size; */
  public void visit(Alloc n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.size.toString()).ExtendRange(labelRange);
  }

  /*   Identifier content; */
  public void visit(Print n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.content.toString()).ExtendRange(labelRange);
  }

  /*   String msg; */
  public void visit(ErrorMessage n, FunctionSymbol context) {
    // nothing
  }

  /*   Label label; */
  public void visit(Goto n, FunctionSymbol context) {
    // nothing
  }

  /*   Identifier condition;
   *   Label label; */
  public void visit(IfGoto n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.condition.toString()).ExtendRange(labelRange);
  }

  /*   Identifier lhs;
   *   Identifier callee;
   *   List<Identifier> args; */
  public void visit(Call n, FunctionSymbol context) {
    if (currLabel == null || context.LabelRanges().get(currLabel) == null) {
      return;
    }

    Pair<Integer, Integer> labelRange = context.LabelRanges().get(currLabel).Range();
    context.VarRanges().get(n.lhs.toString()).ExtendRange(labelRange);
    context.VarRanges().get(n.callee.toString()).ExtendRange(labelRange);
    for (Identifier arg : n.args) {
      context.VarRanges().get(arg.toString()).ExtendRange(labelRange);
    }
  }
}
