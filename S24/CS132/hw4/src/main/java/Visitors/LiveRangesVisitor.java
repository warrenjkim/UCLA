package Visitors;

import IR.syntaxtree.*;
import IR.visitor.GJVoidDepthFirst;
import Utils.*;
import java.util.Map;
import java.util.LinkedHashMap;

public class LiveRangesVisitor extends GJVoidDepthFirst<LiveRangesBuilder> {
  private LineCounter lineCounter;
  private LiveRangesBuilder labelRanges;
  private LiveRangesBuilder paramRanges;

  private IdVisitor idVisitor;
  private Map<String, FunctionSymbol> functionMap;
  private ParamRangesVisitor paramRangesVisitor;
  private ExtendedLifetimeVisitor extendedLifetimeVisitor;

  private DefinitionVisitor defVisitor;
  private UseVisitor useVisitor;

  public LiveRangesVisitor() {
    this.idVisitor = new IdVisitor();
    this.functionMap = new LinkedHashMap<>();
    this.paramRangesVisitor = new ParamRangesVisitor();

    this.defVisitor = new DefinitionVisitor();
    this.useVisitor = new UseVisitor();
  }

  public Map<String, FunctionSymbol> FunctionMap() {
    return this.functionMap;
  }

  /**
   * f0 -> ( FunctionDeclaration() )*
   * f1 -> <EOF>
   */
  public void visit(Program n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f1.accept(this, ranges);
  }

  /**
   * f0 -> "func"
   * f1 -> FunctionName()
   * f2 -> "("
   * f3 -> ( Identifier() )*
   * f4 -> ")"
   * f5 -> Block()
   */
  public void visit(FunctionDeclaration n, LiveRangesBuilder ranges) {
    String functionName = n.f1.accept(idVisitor);
    lineCounter = new LineCounter();
    ranges = new LiveRangesBuilder();
    paramRanges = new LiveRangesBuilder();
    labelRanges = new LiveRangesBuilder();
    n.f3.accept(paramRangesVisitor, paramRanges);
    n.f5.accept(this, ranges);
    FunctionSymbol func = new FunctionSymbol(functionName,
                                             paramRanges.LiveRanges(),
                                             ranges.LiveRanges(),
                                             labelRanges.LiveRanges());
    functionMap.put(n.f1.f0.tokenImage, func);
    System.out.println("defs; " + ranges.defs.toString());
    System.out.println("uses: " + ranges.uses.toString());
  }

  /**
   * f0 -> ( Instruction() )*
   * f1 -> "return"
   * f2 -> Identifier()
   */
  public void visit(Block n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    lineCounter.IncrementLineNumber();
    n.f2.accept(this, ranges);
  }

  /**
   * f0 -> LabelWithColon()
   *     | SetInteger()
   *     | SetFuncName()
   *     | Add()
   *     | Subtract()
   *     | Multiply()
   *     | LessThan()
   *     | Load()
   *     | Store()
   *     | Move()
   *     | Alloc()
   *     | Print()
   *     | ErrorMessage()
   *     | Goto()
   *     | IfGoto()
   *     | Call()
   */
  public void visit(Instruction n, LiveRangesBuilder ranges) {
    lineCounter.IncrementLineNumber();
    n.f0.accept(this, ranges);
  }

  /**
    * f0 -> Label()
    * f1 -> ":"
    */
  public void visit(LabelWithColon n, LiveRangesBuilder ranges) {
    labelRanges.PutFirstUse(n.f0.accept(idVisitor), lineCounter.LineNumber());
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> IntegerLiteral()
   */
  public void visit(SetInteger n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> "@"
   * f3 -> FunctionName()
   */
  public void visit(SetFuncName n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "+"
   * f4 -> Identifier()
   */
  public void visit(Add n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f2.accept(this, ranges);
    n.f2.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f4.accept(this, ranges);
    n.f4.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "-"
   * f4 -> Identifier()
   */
  public void visit(Subtract n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f2.accept(this, ranges);
    n.f2.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f4.accept(this, ranges);
    n.f4.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "*"
   * f4 -> Identifier()
   */
  public void visit(Multiply n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f2.accept(this, ranges);
    n.f2.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f4.accept(this, ranges);
    n.f4.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   * f3 -> "<"
   * f4 -> Identifier()
   */
  public void visit(LessThan n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f2.accept(this, ranges);
    n.f2.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f4.accept(this, ranges);
    n.f4.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
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
  public void visit(Load n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f3.accept(this, ranges);
    n.f3.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f5.accept(this, ranges);
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
  public void visit(Store n, LiveRangesBuilder ranges) {
    n.f1.accept(this, ranges);
    n.f1.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f3.accept(this, ranges);

    n.f6.accept(this, ranges);
    n.f6.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Identifier()
   */
  public void visit(Move n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f2.accept(this, ranges);
    n.f2.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> "alloc"
   * f3 -> "("
   * f4 -> Identifier()
   * f5 -> ")"
   */
  public void visit(Alloc n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f4.accept(this, ranges);
    n.f4.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
   * f0 -> "print"
   * f1 -> "("
   * f2 -> Identifier()
   * f3 -> ")"
   */
  public void visit(Print n, LiveRangesBuilder ranges) {
    n.f2.accept(this, ranges);
    n.f2.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
    * f0 -> "goto"
    * f1 -> Label()
    */
  public void visit(Goto n, LiveRangesBuilder ranges) {
    String label = n.f1.accept(idVisitor);

    if (labelRanges.LiveRanges().FirstUse(label) != null) {
      labelRanges.PutLastUse(label, lineCounter.LineNumber());
    }
  }

  /**
   * f0 -> "if0"
   * f1 -> Identifier()
   * f2 -> "goto"
   * f3 -> Label()
   */
  public void visit(IfGoto n, LiveRangesBuilder ranges) {
    n.f1.accept(this, ranges);
    n.f1.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
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
  public void visit(Call n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f0.accept(defVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f3.accept(this, ranges);
    n.f3.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));

    n.f5.accept(this, ranges);
    n.f3.accept(useVisitor, new Pair<LiveRangesBuilder, Integer>(ranges, lineCounter.LineNumber()));
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public void visit(Identifier n, LiveRangesBuilder ranges) {
    if (paramRanges.Contains(n.f0.tokenImage)) {
      paramRanges.PutLastUse(n.f0.tokenImage, lineCounter.LineNumber());
      return;
    }

    ranges.PutFirstUse(n.f0.tokenImage, lineCounter.LineNumber());
    ranges.PutLastUse(n.f0.tokenImage, lineCounter.LineNumber());
  }

  @Override
  public void visit(If n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f1.accept(this, ranges);
    n.f2.accept(this, ranges);
    n.f3.accept(this, ranges);
  }

  @Override
  public void visit(LabeledInstruction n, LiveRangesBuilder ranges) {
    n.f0.accept(this, ranges);
    n.f1.accept(this, ranges);
  }
}
