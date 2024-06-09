import IR.SparrowParser;
import IR.registers.Registers;
import IR.syntaxtree.Node;
import IR.visitor.SparrowVConstructor;
import Utils.*;
import Visitors.*;
import sparrowv.Program;

public class SV2V {
  public static void main(String[] args) {
    try {
      Registers.SetRiscVregs();
      new SparrowParser(System.in);
      Node root = SparrowParser.Program();
      SparrowVConstructor constructor = new SparrowVConstructor();
      root.accept(constructor);
      Program program = constructor.getProgram();

      VariableVisitor vv = new VariableVisitor();
      program.accept(vv, null);

      TranslationVisitor rv = new TranslationVisitor(vv.FunctionMap());
      RiscVCode code = program.accept(rv, null);
      System.out.println(code.ToString());
    } catch (Exception e) {
      System.out.println("shit");
      e.printStackTrace();
    }
  }
}
