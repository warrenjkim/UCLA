package Visitors;

import IR.syntaxtree.*;
import IR.visitor.GJVoidDepthFirst;
import Utils.*;
import java.util.Map;

public class ControlFlowVisitor extends GJVoidDepthFirst<FunctionSymbol> {
  private String currLabel;
  private IdVisitor idVisitor;
  private LineCounter lineCounter;
  private Map<String, FunctionSymbol> functionMap;

  public ControlFlowVisitor(Map<String, FunctionSymbol> functionMap) {
    this.currLabel = null;
    this.functionMap = functionMap;
    this.idVisitor = new IdVisitor();
    this.lineCounter = new LineCounter();
  }

  public Map<String, FunctionSymbol> FunctionMap() {
    return this.functionMap;
  }

  /**
    * f0 -> ( FunctionDeclaration() )*
    * f1 -> <EOF>
    */
  public void visit(Program n, FunctionSymbol context) {
    n.f0.accept(this, context);
    n.f1.accept(this, context);
  }

  /**
    * f0 -> "func"
    * f1 -> FunctionName()
    * f2 -> "("
    * f3 -> ( Identifier() )*
    * f4 -> ")"
    * f5 -> Block()
    */
  public void visit(FunctionDeclaration n, FunctionSymbol context) {
    String functionName = n.f1.accept(idVisitor);
    context = functionMap.get(functionName);
    lineCounter = new LineCounter();
    currLabel = null;
    n.f5.accept(this, context);
  }

  /**
    * f0 -> ( Instruction() )*
    * f1 -> "return"
    * f2 -> Identifier()
    */
  public void visit(Block n, FunctionSymbol context) {
    n.f0.accept(this, context);
    lineCounter.IncrementLineNumber();
    n.f2.accept(this, context);
  }

  /**
    * f0 -> LabelWithColon()
    *       | SetInteger()
    *       | SetFuncName()
    *       | Add()
    *       | Subtract()
    *       | Multiply()
    *       | LessThan()
    *       | Load()
    *       | Store()
    *       | Move()
    *       | Alloc()
    *       | Print()
    *       | ErrorMessage()
    *       | Goto()
    *       | IfGoto()
    *       | Call()
    */
  public void visit(Instruction n, FunctionSymbol context) {
    lineCounter.IncrementLineNumber();
    n.f0.accept(this, context);
  }

  /**
    * f0 -> Label()
    * f1 -> ":"
    */
  public void visit(LabelWithColon n, FunctionSymbol context) {
    String label = n.f0.accept(idVisitor);
    if (context.LabelRanges().LastUse(label) != null) {
      currLabel = label;
    }
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> IntegerLiteral()
    */
  public void visit(SetInteger n, FunctionSymbol context) {
    n.f0.accept(this, context);
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "@"
    * f3 -> FunctionName()
    */
  public void visit(SetFuncName n, FunctionSymbol context) {
    n.f0.accept(this, context);
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "+"
    * f4 -> Identifier()
    */
  public void visit(Add n, FunctionSymbol context) {
    n.f0.accept(this, context);
    n.f2.accept(this, context);
    n.f4.accept(this, context);
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "-"
    * f4 -> Identifier()
    */
  public void visit(Subtract n, FunctionSymbol context) {
    n.f0.accept(this, context);
    n.f2.accept(this, context);
    n.f4.accept(this, context);
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "*"
    * f4 -> Identifier()
    */
  public void visit(Multiply n, FunctionSymbol context) {
    n.f0.accept(this, context);
    n.f2.accept(this, context);
    n.f4.accept(this, context);
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    * f3 -> "<"
    * f4 -> Identifier()
    */
  public void visit(LessThan n, FunctionSymbol context) {
    n.f0.accept(this, context);
    n.f2.accept(this, context);
    n.f4.accept(this, context);
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "["
    * f3 -> Identifier()
    * f4 -> "+"
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    */
  public void visit(Load n, FunctionSymbol context) {
    n.f0.accept(this, context);
    n.f3.accept(this, context);
  }

  /**
    * f0 -> "["
    * f1 -> Identifier()
    * f2 -> "+"
    * f3 -> IntegerLiteral()
    * f4 -> "]"
    * f5 -> "="
    * f6 -> Identifier()
    */
  public void visit(Store n, FunctionSymbol context) {
    n.f1.accept(this, context);
    n.f6.accept(this, context);
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Identifier()
    */
  public void visit(Move n, FunctionSymbol context) {
    n.f0.accept(this, context);
    n.f2.accept(this, context);
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "alloc"
    * f3 -> "("
    * f4 -> Identifier()
    * f5 -> ")"
    */
  public void visit(Alloc n, FunctionSymbol context) {
    n.f0.accept(this, context);
    n.f4.accept(this, context);
  }

  /**
    * f0 -> "print"
    * f1 -> "("
    * f2 -> Identifier()
    * f3 -> ")"
    */
  public void visit(Print n, FunctionSymbol context) {
    n.f2.accept(this, context);
  }

  /**
    * f0 -> "goto"
    * f1 -> Label()
    */
  public void visit(Goto n, FunctionSymbol context) {

  }

  /**
    * f0 -> "if0"
    * f1 -> Identifier()
    * f2 -> "goto"
    * f3 -> Label()
    */
  public void visit(IfGoto n, FunctionSymbol context) {
    n.f1.accept(this, context);
  }

  /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> "call"
    * f3 -> Identifier()
    * f4 -> "("
    * f5 -> ( Identifier() )*
    * f6 -> ")"
    */
  public void visit(Call n, FunctionSymbol context) {
    n.f0.accept(this, context);
    n.f3.accept(this, context);
    n.f5.accept(this, context);
  }

  /**
    * f0 -> <IDENTIFIER>
    */
  public void visit(Identifier n, FunctionSymbol context) {
    if (currLabel == null) {
      return;
    }

    Integer labelLine = context.LabelRanges().FirstUse(currLabel);
    Integer gotoLine = context.LabelRanges().LastUse(currLabel);
    String id = n.f0.tokenImage;


    if (gotoLine != null) {
      // context.LiveRanges().OverwriteFirstUse(id, labelLine);
      context.LiveRanges().OverwriteLastUse(id, gotoLine);
    }
  }

  @Override
  public void visit(If n, FunctionSymbol context) {
    n.f0.accept(this,context);
    n.f1.accept(this,context);
    n.f2.accept(this,context);
    n.f3.accept(this,context);
  }

  @Override
  public void visit(LabeledInstruction n, FunctionSymbol context) {
    n.f0.accept(this,context);
    n.f1.accept(this,context);
  }
}
