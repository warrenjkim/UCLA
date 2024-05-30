package Visitors;

import IR.syntaxtree.*;
import IR.visitor.GJVoidDepthFirst;
import Utils.*;

public class ParamRangesVisitor extends GJVoidDepthFirst<LiveRangesBuilder> {
  /**
   * f0 -> "func"
   * f1 -> FunctionName()
   * f2 -> "("
   * f3 -> ( Identifier() )*
   * f4 -> ")"
   * f5 -> Block()
   */
  public void visit(FunctionDeclaration n, LiveRangesBuilder ranges) {
    n.f3.accept(this, ranges);
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public void visit(Identifier n, LiveRangesBuilder ranges) {
    if (ranges.Size() == 6) {
      return;
    }

    ranges.PutFirstUse(n.f0.tokenImage, 0);
    ranges.PutLastUse(n.f0.tokenImage, 0);
  }
}
