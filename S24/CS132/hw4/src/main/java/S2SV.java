import IR.*;
import IR.syntaxtree.*;
import Utils.*;
import Visitors.*;

import java.util.Map;

public class S2SV {
  public static void main(String[] args) {
    try {
      Program root = new SparrowParser(System.in).Program();

      LiveRangesVisitor lrv = new LiveRangesVisitor();
      root.accept(lrv, new LiveRangesBuilder());
      Map<String, FunctionSymbol> functionMap = lrv.FunctionMap();

      ControlFlowVisitor crv = new ControlFlowVisitor(functionMap);
      root.accept(crv, null);

      RegisterAllocator registerAllocator = new RegisterAllocator();
      for (FunctionSymbol func : functionMap.values()) {
        registerAllocator.AllocateRegisters(func);
      }

      // System.out.println();
      // System.out.println("Registers allocated:");
      // for (FunctionSymbol func : functionMap.values()) {
      //   System.out.println(func.ToString());
      //   System.out.println();
      // }

      TranslationVisitor tv = new TranslationVisitor(functionMap);
      SparrowVCode code = root.accept(tv, null);
      // System.out.println("Code:");
      System.out.println(code.ToString());

    } catch (Exception e) {
      System.out.println("shit: " + e.getMessage());
    }
  }
}
