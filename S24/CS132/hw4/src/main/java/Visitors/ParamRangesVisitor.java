package Visitors;

import IR.syntaxtree.*;
import IR.visitor.GJVoidDepthFirst;
import Utils.*;

public class ParamRangesVisitor extends GJVoidDepthFirst<LiveRangesBuilder> {
  /**
   * f0 -> <IDENTIFIER>
   */
  public void visit(Identifier n, LiveRangesBuilder ranges) {
    if (ranges.Size() == 6) {
      return;
    }

    ranges.AddDef(n.f0.tokenImage, 0);
    ranges.PutFirstUse(n.f0.tokenImage, 0);
    ranges.PutLastUse(n.f0.tokenImage, 0);
  }
}
