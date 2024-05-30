package Visitors;

import IR.syntaxtree.*;
import IR.visitor.GJVoidDepthFirst;
import Utils.*;

public class UseVisitor extends GJVoidDepthFirst<Pair<LiveRangesBuilder, Integer>> {
  /**
   * f0 -> <IDENTIFIER>
   */
  public void visit(Identifier n, Pair<LiveRangesBuilder, Integer> data) {
    data.first.AddUse(n.f0.tokenImage, data.second);
  }
}
