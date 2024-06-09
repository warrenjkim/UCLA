package Visitors;

import IR.token.Identifier;
import Utils.*;
import java.util.HashMap;
import java.util.Map;
import sparrowv.*;
import sparrowv.visitor.ArgVisitor;

public class VariableVisitor implements ArgVisitor<FunctionSymbol> {
  Map<String, FunctionSymbol> functionMap = new HashMap<>();
  LineCounter lineCounter;

  public Map<String, FunctionSymbol> FunctionMap() {
    return functionMap;
  }

  /*   List<FunctionDecl> funDecls; */
  public void visit(Program n, FunctionSymbol context) {
    for (FunctionDecl fd : n.funDecls) {
      fd.accept(this, context);
    }
  }

  /*   Program parent;
   *   FunctionName functionName;
   *   List<Identifier> formalParameters;
   *   Block block; */
  public void visit(FunctionDecl n, FunctionSymbol context) {
    context = new FunctionSymbol(n.functionName);
    lineCounter = new LineCounter();

    for (Identifier formalParam : n.formalParameters) {
      context.AddParam(formalParam);
    }

    n.block.accept(this, context);

    functionMap.put(context.Name(), context);
  }

  /*   FunctionDecl parent;
   *   List<Instruction> instructions;
   *   Identifier return_id; */
  public void visit(Block n, FunctionSymbol context) {
    for (Instruction i : n.instructions) {
      i.accept(this, context);
      lineCounter.IncrementLineNumber();
    }
  }

  /*   Label label; */
  public void visit(LabelInstr n, FunctionSymbol context) {}

  /*   Register lhs;
   *   int rhs; */
  public void visit(Move_Reg_Integer n, FunctionSymbol context) {}

  /*   Register lhs;
   *   FunctionName rhs; */
  public void visit(Move_Reg_FuncName n, FunctionSymbol context) {}

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public void visit(Add n, FunctionSymbol context) {}

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public void visit(Subtract n, FunctionSymbol context) {}

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public void visit(Multiply n, FunctionSymbol context) {}

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public void visit(LessThan n, FunctionSymbol context) {}

  /*   Register lhs;
   *   Register base;
   *   int offset; */
  public void visit(Load n, FunctionSymbol context) {}

  /*   Register base;
   *   int offset;
   *   Register rhs; */
  public void visit(Store n, FunctionSymbol context) {}

  /*   Register lhs;
   *   Register rhs; */
  public void visit(Move_Reg_Reg n, FunctionSymbol context) {}

  /*   Identifier lhs;
   *   Register rhs; */
  public void visit(Move_Id_Reg n, FunctionSymbol context) {
    context.PushVariable(n.lhs);
  }

  /*   Register lhs;
   *   Identifier rhs; */
  public void visit(Move_Reg_Id n, FunctionSymbol context) {}

  /*   Register lhs;
   *   Register size; */
  public void visit(Alloc n, FunctionSymbol context) {}

  /*   Register content; */
  public void visit(Print n, FunctionSymbol context) {}

  /*   String msg; */
  public void visit(ErrorMessage n, FunctionSymbol context) {}

  /*   Label label; */
  public void visit(Goto n, FunctionSymbol context) {}

  /*   Register condition;
   *   Label label; */
  public void visit(IfGoto n, FunctionSymbol context) {}

  /*   Register lhs;
   *   Register callee;
   *   List<Identifier> args; */
  public void visit(Call n, FunctionSymbol context) {}
}
