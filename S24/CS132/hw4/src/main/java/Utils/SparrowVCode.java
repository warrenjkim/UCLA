package Utils;

import java.util.LinkedList;
import java.util.StringJoiner;
import IR.token.*;

public class SparrowVCode {
  private String id;
  private LinkedList<String> stmts;
  private LinkedList<String> params;

  public SparrowVCode() {
    stmts = new LinkedList<>();
    params = new LinkedList<>();
  }

  public LinkedList<String> Params() {
    return params;
  }

  public void AddParam(String paramId) {
    params.offer(paramId);
  }

  public String NextParam() {
    return params.poll();
  }

  public LinkedList<String> Stmts() {
    return stmts;
  }

  public String Id() {
    return id;
  }

  public void SetId(String id) {
    this.id = id;
  }

  public void AddStmt(String stmt) {
    stmts.offer(stmt);
  }

  public void AddBlockStmt(SparrowVCode other) {
    if (other == null) {
      return;
    }

    for (String stmt : other.stmts) {
      AddStmt(stmt);
    }
  }

  public void AppendStmts(SparrowVCode other) {
    for (String stmt : other.stmts) {
      AddStmt(stmt);
    }
  }

  public void AddReturnStmt(Identifier id) {
    AddStmt("return " + id);
  }

  public void AddLabelStmt(Label label) {
    AddStmt(label.toString() + ":");
  }

  public void AddAssignStmt(String reg, int value) {
    AddStmt(reg + " = " + value);
  }

  public void AddAssignStmt(String reg, FunctionName functionName) {
    AddStmt(reg + " = @" + functionName.toString());
  }

  public void AddAssignStmt(Identifier lhs, Identifier rhs) {
    AddStmt(lhs.toString() + " = " + rhs.toString());
  }

  public void AddAssignStmt(Identifier lhs, String reg) {
    AddStmt(lhs.toString() + " = " + reg);
  }

  public void AddAssignStmt(String reg, Identifier rhs) {
    AddStmt(reg + " = " + rhs.toString());
  }

  public void AddAssignStmt(String lhsReg, String rhsReg) {
    AddStmt(lhsReg + " = " + rhsReg);
  }

  public void AddPlusStmt(String resReg, String lhsReg, String rhsReg) {
    AddStmt(resReg + " = " + lhsReg + " + " + rhsReg);
  }

  public void AddMinusStmt(String resReg, String lhsReg, String rhsReg) {
    AddStmt(resReg + " = " + lhsReg + " - " + rhsReg);
  }
  public void AddMultiplyStmt(String resReg, String lhsReg, String rhsReg) {
    AddStmt(resReg + " = " + lhsReg + " * " + rhsReg);
  }

  public void AddCompareStmt(String resReg, String lhsReg, String rhsReg) {
    AddStmt(resReg + " = " + lhsReg + " < " + rhsReg);
  }

  public void AddLoadStmt(String resReg, String heapReg, int offset) {
    AddStmt(resReg + " = [" + heapReg + " + " + offset + "]");
  }

  public void AddStoreStmt(String heapReg, int offset, String valReg) {
    AddStmt("[" + heapReg + " + " + offset + "] = " + valReg);
  }

  public void AddAllocStmt(String resReg, String sizeReg) {
    AddStmt(resReg + " = alloc(" + sizeReg + ")");
  }

  public void AddPrintStmt(String reg) {
    AddStmt("print(" + reg + ")");
  }

  public void AddErrorStmt(String msg) {
    AddStmt("error(" + msg + ")");
  }

  public void AddGotoStmt(Label jumpLabel) {
    AddStmt("goto " + jumpLabel.toString());
  }

  public void AddIfStmt(String conditionReg, Label jumpLabel) {
    AddStmt("if0 " + conditionReg + " goto " + jumpLabel.toString());
  }

  public void AddFuncLabelStmt(FunctionName funcName, LinkedList<String> params) {
    StringJoiner args = new StringJoiner(" ");
    for (String param : params) {
      args.add(param);
    }

    AddStmt("func " + funcName.toString() + "(" + args.toString() + ")");
  }

  public void AddCallStmt(String resReg, String funcReg, LinkedList<String> params) {
    StringJoiner args = new StringJoiner(" ");
    for (String param : params) {
      args.add(param);
    }

    AddStmt(resReg + " = " + "call " + funcReg + "(" + args.toString() + ")");
  }

  public String ToString() {
    String tab;
    StringBuilder output = new StringBuilder();

    for (String s : stmts) {
      if (s.startsWith("func")) {
        tab = "";
      } else if (s.contains(":")) {
        tab = "  ";
      } else if (s.startsWith("return")) {
        tab = "      ";
      } else {
        tab = "    ";
      }

      output.append(tab).append(s).append("\n");
    }

    return output.toString();
  }
}

