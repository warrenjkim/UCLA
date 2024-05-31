package Visitors;

import IR.syntaxtree.*;
import IR.visitor.GJVoidDepthFirst;
import Utils.*;

public class DefVisitor extends GJVoidDepthFirst<Pair<LiveRangesBuilder, Integer>> {
  /**
   * f0 -> <IDENTIFIER>
   */
  public void visit(Identifier n, Pair<LiveRangesBuilder, Integer> data) {
    data.first.AddDef(n.f0.tokenImage, data.second);
  }
}
