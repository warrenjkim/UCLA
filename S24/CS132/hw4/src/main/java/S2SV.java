import Utils.*;
import Visitors.*;
import IR.SparrowParser;
import IR.syntaxtree.Node;
import IR.visitor.SparrowConstructor;
import sparrow.Program;

import java.util.Map;

public class S2SV {
  public static void main(String[] args) {
    try {
      new SparrowParser(System.in);
      Node root = SparrowParser.Program();
      SparrowConstructor constructor = new SparrowConstructor();
      root.accept(constructor);
      Program program = constructor.getProgram();

      LiveRangesVisitor lrv = new LiveRangesVisitor();
      program.accept(lrv);
      Map<String, FunctionSymbol> functionMap = lrv.FunctionMap();

      ControlFlowVisitor cfv = new ControlFlowVisitor(functionMap);
      program.accept(cfv, null);

      RegisterAllocator registerAllocator = new RegisterAllocator();
      for (FunctionSymbol func : functionMap.values()) {
        registerAllocator.AllocateRegisters(func);
      }

      TranslationVisitor tv = new TranslationVisitor(functionMap);
      SparrowVCode code = program.accept(tv, null);
      System.out.println(code.ToString());

    } catch (Exception e) {
      System.out.println("shit: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
