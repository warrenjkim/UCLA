package Visitors;

import IR.syntaxtree.*;
import IR.visitor.GJVoidDepthFirst;
import Utils.*;

public class ExtendedLifetimeVisitor extends GJVoidDepthFirst<LiveRangesBuilder> {
  /**
   * f0 -> <IDENTIFIER>
   */
  public void visit(Identifier n, LiveRangesBuilder ranges) {
    ranges.ExtendsFunc(n.f0.tokenImage);
  }
}
