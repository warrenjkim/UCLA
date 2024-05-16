import Utils.*;
import Visitors.*;
import minijava.*;
import minijava.syntaxtree.*;

public class J2S {
  public static void main(String[] args) {
    try {
      Node root = new MiniJavaParser(System.in).Goal();
      ClassVisitor cv = new ClassVisitor();
      Context context = new Context();
      root.accept(cv, context);

      root.accept(new IRObjectVisitor(), context);
      root.accept(new IRClassVisitor(), context);

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
