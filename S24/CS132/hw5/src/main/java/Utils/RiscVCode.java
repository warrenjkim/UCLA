package Utils;

import java.util.LinkedList;
import java.util.StringJoiner;
import IR.token.Register;
import IR.token.Identifier;
import IR.token.Label;
import IR.token.FunctionName;

public class RiscVCode {
  private LinkedList<String> stmts = new LinkedList<>();

  private final Register RETURN_VALUE = new Register("a0");
  private final Register ARG_REGISTER = new Register("a1");

  public void AddStmt(String stmt) {
    stmts.offer(stmt);
  }

  public void AddBlockStmt(RiscVCode other) {
    if (other == null) {
      return;
    }

    for (String stmt : other.stmts) {
      AddStmt(stmt);
    }
  }

  public void Label(String func, Label label) {
    AddStmt(func + "_" + label.toString() + ":");
  }

  public void Function(FunctionName funcName) {
    AddStmt(funcName.toString() + ":");
  }

  public void LoadImmediate(Register reg, int imm) {
    AddStmt(String.format("li %s, %d", reg, imm));
  }

  public void LoadImmediate(Register reg, Identifier equiv) {
    AddStmt(String.format("li %s, %s", reg, equiv.toString()));
  }

  public void Add(Register res, Register lhs, Register rhs) {
    AddStmt(String.format("add %s, %s, %s", res.toString(), lhs.toString(), rhs.toString()));
  }

  public void Subtract(Register res, Register lhs, Register rhs) {
    AddStmt(String.format("sub %s, %s, %s", res.toString(), lhs.toString(), rhs.toString()));
  }

  public void Multiply(Register res, Register lhs, Register rhs) {
    AddStmt(String.format("mul %s, %s, %s", res.toString(), lhs.toString(), rhs.toString()));
  }

  public void LessThan(Register res, Register lhs, Register rhs) {
    AddStmt(String.format("slt %s, %s, %s", res.toString(), lhs.toString(), rhs.toString()));
  }

  public void LoadWord(Register res, Integer offset, Register base) {
    AddStmt(String.format("lw %s, %d(%s)", res.toString(), offset, base.toString()));
  }

  public void StoreWord(Register val, Integer offset, Register base) {
    AddStmt(String.format("sw %s, %d(%s)", val.toString(), offset, base.toString()));
  }

  public void Move(Register lhs, Register rhs) {
    AddStmt(String.format("mv %s, %s", lhs.toString(), rhs.toString()));
  }

  public void AddImmediate(Register res, Register lhs, Integer rhs) {
    AddStmt(String.format("addi %s, %s, %d", res.toString(), lhs.toString(), rhs));
  }

  public void LoadAddress(Register res, FunctionName addr) {
    AddStmt(String.format("la %s, %s", res.toString(), addr.toString()));
  }

  public void Jalr(Register target) {
    AddStmt(String.format("jalr %s", target.toString()));
  }

  public void Jal(String target) {
      AddStmt(String.format("jal %s", target));
  }

  public void Error() {
    AddStmt("j error");
  }

  public void Alloc() {
    Jal("alloc");
  }

  public void Print() {
    LoadImmediate(RETURN_VALUE, new Identifier("@print_int"));
    Ecall();
    LoadImmediate(ARG_REGISTER, 10);
    LoadImmediate(RETURN_VALUE, 11);
    Ecall();
  }

  public void NullError() {
    AddStmt("la a0 msg_null");
    Error();
  }

  public void ArrayIndexError() {
    AddStmt("la a0 msg_arr_index");
    Error();
  }

  public void Goto(String func, Label label) {
    Jal(func + "_" +  label.toString());
  }

  public void IfGoto(Register condition, String func, Label jumpLabel, int line) {
    AddStmt(String.format("bnez %s %d_nojump_%s_%s", condition.toString(), line, func, jumpLabel.toString()));
    Jal(func + "_" + jumpLabel.toString());
    Label(line + "_nojump_" + func, jumpLabel);
  }

  public void Return() {
    AddStmt("jr ra");
  }

  public void Ecall() {
    AddStmt("ecall");
  }

  public void AddHeader(FunctionName main) {
    AddStmt(".equiv @sbrk, 9");
    AddStmt(".equiv @print_string, 4");
    AddStmt(".equiv @print_char, 11");
    AddStmt(".equiv @print_int, 1");
    AddStmt(".equiv @exit 10");
    AddStmt(".equiv @exit2, 17");
    AddStmt("");

    AddStmt(".text");
    AddStmt("");
    Jal(main.toString());
    LoadImmediate(RETURN_VALUE, new Identifier("@exit"));
    Ecall();
    AddStmt("");
  }

  public void AddFooter() {
    ErrorBlock();
    AddStmt("");

    AllocBlock();
    AddStmt("");

    AddStmt(".data");
    AddStmt("");

    AddStmt("msg_null:");
    AddStmt(".asciiz \"null pointer\"");
    AddStmt(".align 2");
    AddStmt("");

    AddStmt("msg_arr_index:");
    AddStmt(".asciiz \"array index out of bounds\"");
    AddStmt(".align 2");
  }

  public void ErrorBlock() {
    AddStmt("error:");
    Move(ARG_REGISTER, RETURN_VALUE);
    LoadImmediate(RETURN_VALUE, new Identifier("@print_string"));
    Ecall();
    LoadImmediate(ARG_REGISTER, 10);
    LoadImmediate(RETURN_VALUE, new Identifier("@print_char"));
    Ecall();
    LoadImmediate(RETURN_VALUE, new Identifier("@exit"));
    Ecall();
    AddStmt("abort_17:");
    AddStmt("j abort_17");
  }

  public void AllocBlock() {
    AddStmt("alloc:");
    Move(ARG_REGISTER, RETURN_VALUE);
    LoadImmediate(RETURN_VALUE, new Identifier("@sbrk"));
    Ecall();
    Return();
  }

  public String ToString() {
    StringJoiner joiner = new StringJoiner("\n");
    for (String stmt : stmts) {
      if (stmt.contains(":") || (!stmt.isEmpty() && stmt.charAt(0) == '.')) {
        joiner.add(stmt);
      } else {
        joiner.add("  " + stmt);
      }
    }
    return joiner.toString();
  }
}

