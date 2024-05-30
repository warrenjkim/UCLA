package Visitors;

import IR.syntaxtree.*;
import IR.visitor.GJVoidDepthFirst;
import Utils.*;
import java.util.Map;

public class OverflowParamVisitor extends GJVoidDepthFirst<FunctionSymbol> {
  /**
    * f0 -> <IDENTIFIER>
    */
  public void visit(Identifier n, FunctionSymbol context) {
    context.AddOverflowParam(
    );
  }
}
