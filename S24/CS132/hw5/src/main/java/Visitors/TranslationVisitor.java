package Visitors;

import IR.token.Identifier;
import IR.token.Register;
import Utils.*;
import java.util.Map;
import sparrowv.*;
import sparrowv.visitor.ArgRetVisitor;

public class TranslationVisitor implements ArgRetVisitor<FunctionSymbol, RiscVCode> {
  final Register FRAME_POINTER = new Register("fp");
  final Register STACK_POINTER = new Register("sp");
  final Register RETURN_ADDRESS = new Register("ra");
  final Register RETURN_VALUE = new Register("a0");
  final Register ARG_REGISTER = new Register("a1");
  final Register TEMP_REGISTER = new Register("t6");
  final int BYTE_SIZE = 4;

  private LineCounter counter = new LineCounter();

  Map<String, FunctionSymbol> functionMap;

  public TranslationVisitor(Map<String, FunctionSymbol> functionMap) {
    this.functionMap = functionMap;
  }

  /*   List<FunctionDecl> funDecls; */
  public RiscVCode visit(Program n, FunctionSymbol context) {
    RiscVCode program = new RiscVCode();
    program.AddHeader(n.funDecls.get(0).functionName);
    for (FunctionDecl fd : n.funDecls) {
      program.AddBlockStmt(fd.accept(this, context));
      program.AddStmt("");
    }
    program.AddFooter();

    return program;
  }

  /*   Program parent;
   *   FunctionName functionName;
   *   List<Identifier> formalParameters;
   *   Block block; */
  public RiscVCode visit(FunctionDecl n, FunctionSymbol context) {
    context = functionMap.get(n.functionName.toString());
    RiscVCode prologue = new RiscVCode();
    prologue.StoreWord(FRAME_POINTER, -(2 * BYTE_SIZE), STACK_POINTER);
    prologue.Move(FRAME_POINTER, STACK_POINTER);

    int stackSize = BYTE_SIZE * context.StackSize();
    prologue.LoadImmediate(TEMP_REGISTER, stackSize);
    prologue.Subtract(STACK_POINTER, STACK_POINTER, TEMP_REGISTER);
    prologue.StoreWord(RETURN_ADDRESS, -BYTE_SIZE, FRAME_POINTER);

    RiscVCode function = new RiscVCode();
    function.Function(n.functionName);
    function.AddBlockStmt(prologue);
    function.AddBlockStmt(n.block.accept(this, context));

    return function;
  }

  /*   FunctionDecl parent;
   *   List<Instruction> instructions;
   *   Identifier return_id; */
  public RiscVCode visit(Block n, FunctionSymbol context) {
    RiscVCode block = new RiscVCode();
    for (Instruction i : n.instructions) {
      counter.IncrementLineNumber();
      block.AddBlockStmt(i.accept(this, context));
    }

    block.LoadWord(RETURN_VALUE, -(BYTE_SIZE * context.Offset(n.return_id)), FRAME_POINTER);

    RiscVCode epilogue = new RiscVCode();
    int stackSize = BYTE_SIZE * (context.StackSize() + context.ParamSize());
    epilogue.LoadWord(RETURN_ADDRESS, -BYTE_SIZE, FRAME_POINTER);
    epilogue.LoadWord(FRAME_POINTER, -(2 * BYTE_SIZE), FRAME_POINTER);
    epilogue.AddImmediate(STACK_POINTER, STACK_POINTER, stackSize);

    block.AddBlockStmt(epilogue);
    block.Return();

    return block;
  }

  /*   Label label; */
  public RiscVCode visit(LabelInstr n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.Label(context.Name(), n.label);
    return stmt;
  }

  /*   Register lhs;
   *   int rhs; */
  public RiscVCode visit(Move_Reg_Integer n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.LoadImmediate(n.lhs, n.rhs);
    return stmt;
  }

  /*   Register lhs;
   *   FunctionName rhs; */
  public RiscVCode visit(Move_Reg_FuncName n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.LoadAddress(n.lhs, n.rhs);
    return stmt;
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public RiscVCode visit(Add n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.Add(n.lhs, n.arg1, n.arg2);
    return stmt;
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public RiscVCode visit(Subtract n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.Subtract(n.lhs, n.arg1, n.arg2);
    return stmt;
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public RiscVCode visit(Multiply n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.Multiply(n.lhs, n.arg1, n.arg2);
    return stmt;
  }

  /*   Register lhs;
   *   Register arg1;
   *   Register arg2; */
  public RiscVCode visit(LessThan n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.LessThan(n.lhs, n.arg1, n.arg2);
    return stmt;
  }

  /*   Register lhs;
   *   Register base;
   *   int offset; */
  public RiscVCode visit(Load n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.LoadWord(n.lhs, n.offset, n.base);
    return stmt;
  }

  /*   Register base;
   *   int offset;
   *   Register rhs; */
  public RiscVCode visit(Store n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.StoreWord(n.rhs, n.offset, n.base);
    return stmt;
  }

  /*   Register lhs;
   *   Register rhs; */
  public RiscVCode visit(Move_Reg_Reg n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.Move(n.lhs, n.rhs);
    return stmt;
  }

  /*   Identifier lhs;
   *   Register rhs; */
  public RiscVCode visit(Move_Id_Reg n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.StoreWord(n.rhs, -(BYTE_SIZE * context.Offset(n.lhs)), FRAME_POINTER);
    return stmt;
  }

  /*   Register lhs;
   *   Identifier rhs; */
  public RiscVCode visit(Move_Reg_Id n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.LoadWord(n.lhs, -(BYTE_SIZE * context.Offset(n.rhs)), FRAME_POINTER);
    return stmt;
  }

  /*   Register lhs;
   *   Register size; */
  public RiscVCode visit(Alloc n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.Move(RETURN_VALUE, n.size);
    stmt.Alloc();
    stmt.Move(n.lhs, RETURN_VALUE);
    return stmt;
  }

  /*   Register content; */
  public RiscVCode visit(Print n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.Move(ARG_REGISTER, n.content);
    stmt.Print();
    return stmt;
  }

  /*   String msg; */
  public RiscVCode visit(ErrorMessage n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    if (n.msg.contains("null")) {
      stmt.NullError();
    } else {
      stmt.ArrayIndexError();
    }

    return stmt;
  }

  /*   Label label; */
  public RiscVCode visit(Goto n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.Goto(context.Name(), n.label);
    return stmt;
  }

  /*   Register condition;
   *   Label label; */
  public RiscVCode visit(IfGoto n, FunctionSymbol context) {
    RiscVCode stmt = new RiscVCode();
    stmt.IfGoto(n.condition, context.Name(), n.label, counter.LineNumber());
    return stmt;
  }

  /*   Register lhs;
   *   Register callee;
   *   List<Identifier> args; */
  public RiscVCode visit(Call n, FunctionSymbol context) {
    RiscVCode stackFrame = new RiscVCode();
    if (n.args.size() > 0) {
      stackFrame.LoadImmediate(TEMP_REGISTER, BYTE_SIZE * n.args.size());
      stackFrame.Subtract(STACK_POINTER, STACK_POINTER, TEMP_REGISTER);

      int offset = 0;
      for (Identifier arg : n.args) {
        stackFrame.LoadWord(TEMP_REGISTER, -(BYTE_SIZE * context.Offset(arg)), FRAME_POINTER);
        stackFrame.StoreWord(TEMP_REGISTER, BYTE_SIZE * (offset++), STACK_POINTER);
      }
    }

    RiscVCode stmt = new RiscVCode();
    stmt.AddBlockStmt(stackFrame);
    stmt.Jalr(n.callee);
    stmt.Move(n.lhs, RETURN_VALUE);

    return stmt;
  }
}
