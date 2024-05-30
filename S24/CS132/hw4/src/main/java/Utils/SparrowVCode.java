package Utils;

import java.util.LinkedList;
import java.util.StringJoiner;

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

  public void AddReturnStmt(String expr) {
    AddStmt("return " + expr);
  }

  public void AddLabelStmt(String label) {
    AddStmt(label + ":");
  }

  public void AddAssignStmt(String id, int value) {
    AddStmt(id + " = " + value);
  }

  public void AddFieldAssignStmt(String id, String expr) {
    AddStmt(id + " = " + expr);
  }

  public void AddAssignStmt(String id, String expr) {
    AddStmt(id + " = " + expr);
  }

  public void AddFuncAssignStmt(String id, String funcName) {
    AddStmt(id + " = @" + funcName);
  }

  public void AddPlusStmt(String id, String lhs, String rhs) {
    AddStmt(id + " = " + lhs + " + " + rhs);
  }

  public void AddMinusStmt(String id, String lhs, String rhs) {
    AddStmt(id + " = " + lhs + " - " + rhs);
  }

  public void AddMultiplyStmt(String id, String lhs, String rhs) {
    AddStmt(id + " = " + lhs + " * " + rhs);
  }

  public void AddCompareStmt(String id, String lhs, String rhs) {
    AddStmt(id + " = " + lhs + " < " + rhs);
  }

  public void AddLoadStmt(String id, String arr, int byteOffset) {
    AddStmt(id + " = [" + arr + " + " + byteOffset + "]");
  }

  public void AddStoreStmt(String arr, int byteOffset, String id) {
    AddStmt("[" + arr + " + " + byteOffset + "] = " + id);
  }

  public void AddAllocStmt(String id, String byteSize) {
    AddStmt(id + " = alloc(" + byteSize + ")");
  }

  public void AddPrintStmt(String expr) {
    AddStmt("print(" + expr + ")");
  }

  public void AddErrorStmt(String msg) {
    AddStmt("error(" + msg + ")");
  }

  public void AddGotoStmt(String jumpLabel) {
    AddStmt("goto " + jumpLabel);
  }

  public void AddIfStmt(String condition, String jumpLabel) {
    AddStmt("if0 " + condition + " goto " + jumpLabel);
  }

  public void AddMainLabelStmt(String className, String funcName) {
    AddStmt("func " + className + "__" + funcName + "()");
  }

  public void AddFuncLabelStmt(String funcName) {
    AddStmt("func " + funcName + "()");
  }

  public void AddFuncLabelStmt(String funcName, LinkedList<String> params) {
    StringJoiner args = new StringJoiner(" ");
    for (String param : params) {
      args.add(param);
    }

    AddStmt("func " + funcName + "(" + args.toString() + ")");
  }

  public void AddCallStmt(String id, String label, LinkedList<String> params) {
    StringJoiner args = new StringJoiner(" ");
    for (String param : params) {
      args.add(param);
    }

    AddStmt(id + " = " + "call " + label + "(" + args.toString() + ")");
  }

  public void AddErrorStmts() {
    AddLabelStmt("arr_err_label");
    AddErrorStmt("\"array index out of bounds\"");

    AddLabelStmt("null_err_label");
    AddErrorStmt("\"null pointer\"");
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

