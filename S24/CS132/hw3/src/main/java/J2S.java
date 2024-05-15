import Utils.*;
import Visitors.*;
import minijava.*;
import minijava.syntaxtree.*;

import java.util.Map;

public class J2S {
  public static void main(String[] args) {
    try {
      Node root = new MiniJavaParser(System.in).Goal();
      ClassVisitor cv = new ClassVisitor();
      Context context = new Context();
      root.accept(cv, context);

      root.accept(new IRObjectVisitor(), context);
      root.accept(new IRClassVisitor(), context);

      // System.out.println("\n\nObject Map:");
      // for (Map.Entry<String, SparrowObject> literal : context.ObjectMap().entrySet()) {
      //   System.out.println(literal.getKey() + " = " + literal.getValue().ToString());
      //   System.out.println();
      // }
      root.accept(new TranslatorVisitor(), context);
      for (ClassSymbol currClass : context.ClassTable().values()) {
        for (MethodSymbol method : currClass.Methods().values()) {
          System.out.println(method.SparrowCode().ToString());
          System.out.println();
        }
      }
    } catch (ParseException e) {
      System.out.println(e.toString());
    }
  }
}
