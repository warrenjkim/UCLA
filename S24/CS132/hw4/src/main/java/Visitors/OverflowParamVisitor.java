package Visitors;

import Utils.*;
import IR.syntaxtree.*;
import java.util.Enumeration;
import IR.visitor.GJDepthFirst;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;


public class OverflowParamVisitor extends GJDepthFirst<SparrowVCode, FunctionSymbol> {
  //
  // Auto class visitors--probably don't need to be overridden.
  //
  public SparrowVCode visit(NodeList n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    int _count = 0;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      SparrowVCode res = e.nextElement().accept(this, context);
      if (res != null) {
        stmt.AddBlockStmt(res);
        if (!res.Params().isEmpty()) {
          for (String param : res.Params()) {
            stmt.AddParam(param);
          }
        }
      }
      _count++;
    }
    return stmt;
  }

  public SparrowVCode visit(NodeListOptional n, FunctionSymbol context) {
    if (n.present()) {
      SparrowVCode stmt = new SparrowVCode();
      int _count = 0;
      for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
        SparrowVCode res = e.nextElement().accept(this, context);
        if (res != null) {
          stmt.AddBlockStmt(res);
          if (!res.Params().isEmpty()) {
            for (String param : res.Params()) {
              stmt.AddParam(param);
            }
          }
        }
        _count++;
      }
      return stmt;
    } else return null;
  }

  public SparrowVCode visit(NodeOptional n, FunctionSymbol context) {
    if (n.present()) return n.node.accept(this, context);
    else return null;
  }

  public SparrowVCode visit(NodeSequence n, FunctionSymbol context) {
    SparrowVCode stmt = new SparrowVCode();
    int _count = 0;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      SparrowVCode res = e.nextElement().accept(this, context);
      if (res != null) {
        stmt.AddBlockStmt(res);
        if (!res.Params().isEmpty()) {
          for (String param : res.Params()) {
            stmt.AddParam(param);
          }
        }
      }
      _count++;
    }
    return stmt;
  }

  public SparrowVCode visit(NodeToken n, FunctionSymbol context) {
    return null;
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public SparrowVCode visit(Identifier n, FunctionSymbol context) {
    SparrowVCode param = new SparrowVCode();
    param.AddParam(n.f0.tokenImage);
    return param;
  }
}
