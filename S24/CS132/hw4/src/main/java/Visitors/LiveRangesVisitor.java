package Visitors;

import IR.token.FunctionName;
import IR.token.Identifier;
import IR.token.Label;
import Utils.*;
import java.util.Map;
import sparrow.*;
import sparrow.visitor.DepthFirst;
import java.util.LinkedHashMap;

public class LiveRangesVisitor extends DepthFirst {
  private SparrowVRangeBuilder<Identifier> varRanges;
  private SparrowVRangeBuilder<Label> labelRanges;

  private LineCounter lineCounter;
  private Map<String, FunctionSymbol> functionMap = new LinkedHashMap<>();

  public Map<String, FunctionSymbol> FunctionMap() {
    return functionMap;
  }

  /*   List<FunctionDecl> funDecls; */
  public void visit(Program n) {
    for (FunctionDecl fd : n.funDecls) {
      fd.accept(this);
    }
  }

  /*   Program parent;
   *   FunctionName functionName;
   *   List<Identifier> formalParameters;
   *   Block block; */
  public void visit(FunctionDecl n) {
    lineCounter = new LineCounter();
    varRanges = new SparrowVRangeBuilder<Identifier>();
    labelRanges = new SparrowVRangeBuilder<Label>();

    for (Identifier formalParam : n.formalParameters) {
      if (varRanges.Size() == 6) {
        break;
      }

      varRanges.AddDef(formalParam, 0);
      varRanges.AddUse(formalParam, 0);
    }

    n.block.accept(this);
    functionMap.put(
        n.functionName.toString(),
        new FunctionSymbol(
            n.functionName, varRanges.BuildRangeMap(), labelRanges.BuildMergedRangeMap()));
  }

  /*   FunctionDecl parent;
   *   List<Instruction> instructions;
   *   Identifier return_id; */
  public void visit(Block n) {
    for (Instruction ins : n.instructions) {
      lineCounter.IncrementLineNumber();
      ins.accept(this);
    }

    lineCounter.IncrementLineNumber();
    varRanges.AddUse(n.return_id, lineCounter.LineNumber());
  }

  /*   Label label; */
  public void visit(LabelInstr n) {
    labelRanges.AddDef(n.label, lineCounter.LineNumber());
  }

  /*   Identifier lhs;
   *   int rhs; */
  public void visit(Move_Id_Integer n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
  }

  /*   Identifier lhs;
   *   FunctionName rhs; */
  public void visit(Move_Id_FuncName n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public void visit(Add n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
    varRanges.AddUse(n.arg1, lineCounter.LineNumber());
    varRanges.AddUse(n.arg2, lineCounter.LineNumber());
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public void visit(Subtract n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
    varRanges.AddUse(n.arg1, lineCounter.LineNumber());
    varRanges.AddUse(n.arg2, lineCounter.LineNumber());
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public void visit(Multiply n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
    varRanges.AddUse(n.arg1, lineCounter.LineNumber());
    varRanges.AddUse(n.arg2, lineCounter.LineNumber());
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public void visit(LessThan n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
    varRanges.AddUse(n.arg1, lineCounter.LineNumber());
    varRanges.AddUse(n.arg2, lineCounter.LineNumber());
  }

  /*   Identifier lhs;
   *   Identifier base;
   *   int offset; */
  public void visit(Load n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
    varRanges.AddUse(n.base, lineCounter.LineNumber());
  }

  /*   Identifier base;
   *   int offset;
   *   Identifier rhs; */
  public void visit(Store n) {
    varRanges.AddUse(n.base, lineCounter.LineNumber());
    varRanges.AddUse(n.rhs, lineCounter.LineNumber());
  }

  /*   Identifier lhs;
   *   Identifier rhs; */
  public void visit(Move_Id_Id n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
    varRanges.AddUse(n.rhs, lineCounter.LineNumber());
  }

  /*   Identifier lhs;
   *   Identifier size; */
  public void visit(Alloc n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
    varRanges.AddUse(n.size, lineCounter.LineNumber());
  }

  /*   Identifier content; */
  public void visit(Print n) {
    varRanges.AddUse(n.content, lineCounter.LineNumber());
  }

  /*   String msg; */
  public void visit(ErrorMessage n) {
    // nothing
  }

  /*   Label label; */
  public void visit(Goto n) {
    if (labelRanges.Contains(n.label)) {
      labelRanges.AddUse(n.label, lineCounter.LineNumber());
    }
  }

  /*   Identifier condition;
   *   Label label; */
  public void visit(IfGoto n) {
    varRanges.AddUse(n.condition, lineCounter.LineNumber());
    if (labelRanges.Contains(n.label)) {
      labelRanges.AddUse(n.label, lineCounter.LineNumber());
    }
  }

  /*   Identifier lhs;
   *   Identifier callee;
   *   List<Identifier> args; */
  public void visit(Call n) {
    varRanges.AddDef(n.lhs, lineCounter.LineNumber());
    varRanges.AddUse(n.callee, lineCounter.LineNumber());
    for (Identifier arg : n.args) {
      varRanges.AddUse(arg, lineCounter.LineNumber());
    }
  }
}
