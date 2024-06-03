package Visitors;

import IR.token.Identifier;
import Utils.*;
import java.util.Map;
import sparrow.*;
import sparrow.visitor.ArgRetVisitor;
import java.util.List;
import java.util.LinkedList;

public class TranslationVisitor implements ArgRetVisitor<FunctionSymbol, SparrowVCode> {
  private Map<String, FunctionSymbol> functionMap;
  private LineCounter lineCounter;

  public TranslationVisitor(Map<String, FunctionSymbol> functionMap) {
    this.functionMap = functionMap;
  }


  public Map<String, FunctionSymbol> FunctionMap() {
    return functionMap;
  }

  /*   List<FunctionDecl> funDecls; */
  public SparrowVCode visit(Program n, FunctionSymbol context) {
    SparrowVCode program = new SparrowVCode();
    for (FunctionDecl fd : n.funDecls) {
      SparrowVCode func = fd.accept(this, null);
      if (func != null) {
        program.AddBlockStmt(func);
      }
    }

    return program;
  }

  /*   Program parent;
   *   FunctionName functionName;
   *   List<Identifier> formalParameters;
   *   Block block; */
  public SparrowVCode visit(FunctionDecl n, FunctionSymbol context) {
    SparrowVCode func = new SparrowVCode();
    lineCounter = new LineCounter();
    context = functionMap.get(n.functionName.toString());
    int i = 0;
    for (Identifier formalParam : n.formalParameters) {
      if (i++ >= 6) {
        func.AddParam(formalParam.toString());
      }
    }

    func.AddFuncLabelStmt(n.functionName, func.Params());
    for (String param : func.Params()) {
      if (context.Register(param) != null) {
        func.AddAssignStmt(context.Register(param), param);
      }
    }

    SparrowVCode funcBody = n.block.accept(this, context);
    if (funcBody != null) {
      func.AddBlockStmt(funcBody);
    }

    return func;
  }

  /*   FunctionDecl parent;
   *   List<Instruction> instructions;
   *   Identifier return_id; */
  public SparrowVCode visit(Block n, FunctionSymbol context) {
    SparrowVCode block = new SparrowVCode();
    for (Instruction ins : n.instructions) {
      lineCounter.IncrementLineNumber();
      block.AddBlockStmt(ins.accept(this, context));
    }

    String retReg = context.Register(n.return_id);
    if (retReg != null) {
      block.AddAssignStmt(n.return_id, retReg);
    }

    lineCounter.IncrementLineNumber();
    block.AddReturnStmt(n.return_id);
    return block;
  }

  /*   Label label; */
  public SparrowVCode visit(LabelInstr n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    stmt.AddLabelStmt(n.label);
    return stmt;
  }

  /*   Identifier lhs;
   *   int rhs; */
  public SparrowVCode visit(Move_Id_Integer n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String reg = context.Register(n.lhs);
    if (reg == null) {
      reg = "t0";
    }

    stmt.AddAssignStmt(reg, n.rhs);
    if (reg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, reg);
    }

    return stmt;
  }

  /*   Identifier lhs;
   *   FunctionName rhs; */
  public SparrowVCode visit(Move_Id_FuncName n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String reg = context.Register(n.lhs);
    if (reg == null) {
      reg = "t0";
    }

    stmt.AddAssignStmt(reg, n.rhs);
    if (reg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, reg);
    }

    return stmt;
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public SparrowVCode visit(Add n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String resReg = context.Register(n.lhs);
    String lhsReg = context.Register(n.arg1);
    String rhsReg = context.Register(n.arg2);
    if (lhsReg == null) {
      lhsReg = "t0";
      stmt.AddAssignStmt(lhsReg, n.arg1);
    }

    if (rhsReg == null) {
      rhsReg = "t1";
      stmt.AddAssignStmt(rhsReg, n.arg2);
    }

    if (resReg == null) {
      resReg = "t0";
    }

    stmt.AddPlusStmt(resReg, lhsReg, rhsReg);
    if (resReg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, resReg);
    }

    return stmt;
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public SparrowVCode visit(Subtract n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String resReg = context.Register(n.lhs);
    String lhsReg = context.Register(n.arg1);
    String rhsReg = context.Register(n.arg2);
    if (lhsReg == null) {
      lhsReg = "t0";
      stmt.AddAssignStmt(lhsReg, n.arg1);
    }

    if (rhsReg == null) {
      rhsReg = "t1";
      stmt.AddAssignStmt(rhsReg, n.arg2);
    }

    if (resReg == null) {
      resReg = "t0";
    }

    stmt.AddMinusStmt(resReg, lhsReg, rhsReg);
    if (resReg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, resReg);
    }

    return stmt;
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public SparrowVCode visit(Multiply n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String resReg = context.Register(n.lhs);
    String lhsReg = context.Register(n.arg1);
    String rhsReg = context.Register(n.arg2);
    if (lhsReg == null) {
      lhsReg = "t0";
      stmt.AddAssignStmt(lhsReg, n.arg1);
    }

    if (rhsReg == null) {
      rhsReg = "t1";
      stmt.AddAssignStmt(rhsReg, n.arg2);
    }

    if (resReg == null) {
      resReg = "t0";
    }

    stmt.AddMultiplyStmt(resReg, lhsReg, rhsReg);
    if (resReg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, resReg);
    }

    return stmt;
  }

  /*   Identifier lhs;
   *   Identifier arg1;
   *   Identifier arg2; */
  public SparrowVCode visit(LessThan n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String resReg = context.Register(n.lhs);
    String lhsReg = context.Register(n.arg1);
    String rhsReg = context.Register(n.arg2);
    if (lhsReg == null) {
      lhsReg = "t0";
      stmt.AddAssignStmt(lhsReg, n.arg1);
    }

    if (rhsReg == null) {
      rhsReg = "t1";
      stmt.AddAssignStmt(rhsReg, n.arg2);
    }

    if (resReg == null) {
      resReg = "t0";
    }

    stmt.AddCompareStmt(resReg, lhsReg, rhsReg);
    if (resReg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, resReg);
    }

    return stmt;
  }

  /*   Identifier lhs;
   *   Identifier base;
   *   int offset; */
  public SparrowVCode visit(Load n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String resReg = context.Register(n.lhs);
    String heapReg = context.Register(n.base);

    if (heapReg == null) {
      heapReg = "t0";
      stmt.AddAssignStmt(heapReg, n.base);
    }

    if (resReg == null) {
      resReg = "t0";
    }

    stmt.AddLoadStmt(resReg, heapReg, n.offset);
    if (resReg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, resReg);
    }

    return stmt;
  }

  /*   Identifier base;
   *   int offset;
   *   Identifier rhs; */
  public SparrowVCode visit(Store n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String heapReg = context.Register(n.base);
    String valReg = context.Register(n.rhs);

    if (heapReg == null) {
      heapReg = "t0";
      stmt.AddAssignStmt(heapReg, n.base);
    }

    if (valReg == null) {
      valReg = "t1";
      stmt.AddAssignStmt(valReg, n.rhs);
    }

    stmt.AddStoreStmt(heapReg, n.offset, valReg);
    return stmt;
  }

  /*   Identifier lhs;
   *   Identifier rhs; */
  public SparrowVCode visit(Move_Id_Id n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String resReg = context.Register(n.lhs);
    String valReg = context.Register(n.rhs);
    if (valReg == null) {
      valReg = "t0";
      stmt.AddAssignStmt(valReg, n.rhs);
    }

    if (resReg == null) {
      resReg = "t0";
    }

    stmt.AddAssignStmt(resReg, valReg);
    if (resReg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, resReg);
    }

    return stmt;
  }

  /*   Identifier lhs;
   *   Identifier size; */
  public SparrowVCode visit(Alloc n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String resReg = context.Register(n.lhs);
    String sizeReg = context.Register(n.size);
    if (sizeReg == null) {
      sizeReg = "t0";
      stmt.AddAssignStmt(sizeReg, n.size);
    }

    if (resReg == null) {
      resReg = "t0";
    }

    stmt.AddAllocStmt(resReg, sizeReg);
    if (resReg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, resReg);
    }

    return stmt;
  }

  /*   Identifier content; */
  public SparrowVCode visit(Print n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String reg = context.Register(n.content);
    if (reg == null) {
      reg = "t0";
      stmt.AddAssignStmt(reg, n.content);
    }

    stmt.AddPrintStmt(reg);

    return stmt;
  }

  /*   String msg; */
  public SparrowVCode visit(ErrorMessage n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    stmt.AddErrorStmt(n.msg);

    return stmt;
  }

  /*   Label label; */
  public SparrowVCode visit(Goto n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    stmt.AddGotoStmt(n.label);

    return stmt;
  }

  /*   Identifier condition;
   *   Label label; */
  public SparrowVCode visit(IfGoto n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String conditionReg = context.Register(n.condition);
    if (conditionReg == null) {
      conditionReg = "t0";
      stmt.AddAssignStmt(conditionReg, n.condition);
    }

    stmt.AddIfStmt(conditionReg, n.label);

    return stmt;
  }

  /*   Identifier lhs;
   *   Identifier callee;
   *   List<Identifier> args; */
  public SparrowVCode visit(Call n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    String resReg = context.Register(n.lhs);
    String funcReg = context.Register(n.callee);
    LinkedList<String> params = new LinkedList<>();
    for (Identifier arg : n.args) {
      params.add(arg.toString());
    }

    LinkedList<String> overflowParams = setOverflowParams(stmt, params, context);
    LinkedList<String> stackSaves = new LinkedList<>();
    saveRegisters(stmt, stackSaves, context, n.lhs.toString(), n.callee.toString());
    assignFunctionArguments(stmt, params, context);

    if (resReg == null) {
      resReg = "t0";
      stmt.AddAssignStmt(resReg, n.lhs);
    }

    if (funcReg == null) {
      funcReg = "t1";
      stmt.AddAssignStmt(funcReg, n.callee);
    }

    stmt.AddCallStmt(resReg, funcReg, overflowParams);
    if (resReg.equals("t0")) {
      stmt.AddAssignStmt(n.lhs, resReg);
    }

    restoreRegisters(stmt, stackSaves, context);

    return stmt;
  }

  private LinkedList<String> setOverflowParams(SparrowVCode stmt, LinkedList<String> params, FunctionSymbol context) {
    LinkedList<String> paramList = new LinkedList<>(params);
    LinkedList<String> overflowParams = new LinkedList<>();
    int aCount = 0;
    while (!paramList.isEmpty() && aCount++ < 6) {
      paramList.pop();
    }

    for (String arg : paramList) {
      overflowParams.add(arg);
    }

    for (String overflowParam : overflowParams) {
      String paramReg = context.Register(overflowParam);
      if (paramReg == null) {
        continue;
      }

      stmt.AddAssignStmt(overflowParam, paramReg);
    }

    return overflowParams;
  }

  private void saveRegisters(SparrowVCode stmt, List<String> stackSaves, FunctionSymbol context, String resId, String funcId) {
    for (Map.Entry<String, String> arg : context.ArgRegAssignments().entrySet()) {
      String argId = arg.getKey();
      String argReg = arg.getValue();

      Integer lastUse = context.VarRanges().get(argId).LastUse();
      if (lastUse >= lineCounter.LineNumber() && !argId.equals(resId) && !argId.equals(funcId)) {
        stackSaves.add(argId);
        stmt.AddAssignStmt(argReg + "_stack", argReg);
      }
    }

    for (Map.Entry<String, String> temp : context.TempRegAssignments().entrySet()) {
      String tempId = temp.getKey();
      String tempReg = temp.getValue();

      Integer firstUse = context.VarRanges().get(tempId).FirstUse();
      Integer lastUse = context.VarRanges().get(tempId).LastUse();
      if (firstUse < lineCounter.LineNumber() && lastUse > lineCounter.LineNumber() && !tempId.equals(resId) && !tempId.equals(funcId)) {
        stackSaves.add(tempId);
        stmt.AddAssignStmt(tempId, tempReg);
      }
    }
  }

  private void assignFunctionArguments(SparrowVCode stmt, LinkedList<String> params, FunctionSymbol context) {
    int aCount = 2;
    while (!params.isEmpty() && aCount < 8) {
      String param = params.pop();
      String paramReg = context.Register(param);
      if (paramReg == null) {
        paramReg = "t0";
      }

      if (paramReg.equals("a" + aCount)) {
        aCount++;
        continue;
      } else if (context.ArgRegAssignments().containsKey(param)) {
        stmt.AddAssignStmt("a" + aCount, paramReg + "_stack");
      } else if (paramReg.equals("t0")) {
        stmt.AddAssignStmt(paramReg, param);
        stmt.AddAssignStmt("a" + aCount, paramReg);
      } else {
        stmt.AddAssignStmt("a" + aCount, paramReg);
      }

      aCount++;
    }
  }

  private void restoreRegisters(SparrowVCode stmt, LinkedList<String> stackSaves, FunctionSymbol context) {
    for (String id : stackSaves) {
      String reg = context.Register(id);
      if (context.ArgRegAssignments().containsKey(id)) {
        stmt.AddAssignStmt(reg, reg + "_stack");
      } else {
        stmt.AddAssignStmt(reg, id);
      }
    }
  }
}
