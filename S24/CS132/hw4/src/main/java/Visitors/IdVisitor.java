package Visitors;

import IR.syntaxtree.*;
import IR.visitor.GJNoArguDepthFirst;

public class IdVisitor extends GJNoArguDepthFirst<String> {
  /**
    * f0 -> <IDENTIFIER>
    */
  public String visit(FunctionName n) {
    return n.f0.tokenImage;
  }

  /**
    * f0 -> <IDENTIFIER>
    */
  public String visit(Label n) {
    return n.f0.tokenImage;
  }

  /**
    * f0 -> <IDENTIFIER>
    */
  public String visit(Identifier n) {
    return n.f0.tokenImage;
  }

  /**
    * f0 -> <INTEGER_LITERAL>
    */
  public String visit(IntegerLiteral n) {
    return n.f0.tokenImage;
  }

  /**
    * f0 -> <STRINGCONSTANT>
    */
  public String visit(StringLiteral n) {
    return n.f0.tokenImage;
  }
}
