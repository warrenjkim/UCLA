package Visitors;

import java.util.LinkedList;
import Utils.*;
import minijava.syntaxtree.*;
import minijava.visitor.GJVoidDepthFirst;

public class MethodVisitor extends GJVoidDepthFirst<Context> {
  private TypeVisitor typeVisitor = new TypeVisitor();

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public void visit(VarDeclaration n, Context context) {
    TypeStruct type = n.f0.accept(typeVisitor, context);
    String name = n.f1.f0.tokenImage;

    context.Method().AddLocalVariable(name, type);
  }
}
