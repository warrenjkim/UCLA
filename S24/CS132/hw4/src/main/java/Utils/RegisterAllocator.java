package Utils;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegisterAllocator {
  private static final String[] aRegisterNames = {"a2", "a3", "a4", "a5", "a6", "a7"};
  private static final String[] sRegisterNames = {
    "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9"/*, "s10", "s11" */
  };
  private static final String[] tRegisterNames = {"t0", "t1", "t2", "t3", "t4", "t5"};

  private ArrayDeque<String> aRegisters;
  private ArrayDeque<String> sRegisters;
  private ArrayDeque<String> tRegisters;

  Map<String, String> registerAssignments;
  Map<String, String> argRegisterAssignments;
  LinkedList<Map.Entry<String, SparrowVRange>> active;

  public RegisterAllocator() {
    initialize();
  }

  public void assignArgRegisters(FunctionSymbol func) {
    Set<String> params = func.ParamRanges().LiveRangesMap().keySet();
    for (String param : params) {
      argRegisterAssignments.put(param, nextArgRegister());
    }

    func.SetArgRegisterAssignments(argRegisterAssignments);
  }

  public void AllocateRegisters(FunctionSymbol func) {
    initialize();
    assignArgRegisters(func);

    List<Map.Entry<String, SparrowVRange>> ranges = func.LiveRanges().Sorted();
    for (Map.Entry<String, SparrowVRange> var : ranges) {
      String id = var.getKey();
      Pair<Integer, Integer> range = var.getValue().Range();

      expire(range);

      String freeRegister = nextFreeRegister();
      if (freeRegister == null) {
        spill(var);
      } else {
        registerAssignments.put(id, freeRegister);
        active.offer(var);
      }

      sortActive();
    }

    func.SetRegisterAssignments(registerAssignments);
  }

  private void expire(Pair<Integer, Integer> currRange) {
    Iterator<Map.Entry<String, SparrowVRange>> iterator = active.iterator();

    while (iterator.hasNext()) {
      Map.Entry<String, SparrowVRange> interval = iterator.next();
      String id = interval.getKey();
      Pair<Integer, Integer> range = interval.getValue().Range();
      if (range.second <= currRange.first) {
        iterator.remove();
        freeRegister(registerAssignments.get(id));
      }
    }
  }

  private void spill(Map.Entry<String, SparrowVRange> curr) {
    String currId = curr.getKey();
    Pair<Integer, Integer> currRange = curr.getValue().Range();

    Map.Entry<String, SparrowVRange> spillVar = active.peekLast();
    String spillId = spillVar.getKey();
    Pair<Integer, Integer> spillRange = spillVar.getValue().Range();

    if (spillRange.second > currRange.second) {
      registerAssignments.put(currId, registerAssignments.get(spillId));
      registerAssignments.remove(spillId);
      active.remove(spillVar);
      active.offer(curr);
    }
  }

  private void freeRegister(String reg) {
    if (reg.charAt(0) == 't') {
      tRegisters.offer(reg);
    } else if (reg.charAt(0) == 's') {
      sRegisters.offer(reg);
    }
  }

  private String nextFreeRegister() {
    if (!tRegisters.isEmpty()) {
      return tRegisters.pop();
    }

    if (!sRegisters.isEmpty()) {
      return sRegisters.pop();
    }

    return null;
  }

  private String nextArgRegister() {
    if (!aRegisters.isEmpty()) {
      return aRegisters.pop();
    }

    return null;
  }

  private void sortActive() {
    Collections.sort(
        active,
        new Comparator<Map.Entry<String, SparrowVRange>>() {
          @Override
          public int compare(
              Map.Entry<String, SparrowVRange> lhs, Map.Entry<String, SparrowVRange> rhs) {
            return lhs.getValue().LastUse().compareTo(rhs.getValue().LastUse());
          }
        });
  }

  private void initialize() {
    aRegisters = new ArrayDeque<>();
    sRegisters = new ArrayDeque<>();
    tRegisters = new ArrayDeque<>();

    active = new LinkedList<>();
    registerAssignments = new LinkedHashMap<>();
    argRegisterAssignments = new LinkedHashMap<>();

    for (String registerName : aRegisterNames) {
      aRegisters.offer(registerName);
    }
    for (String registerName : sRegisterNames) {
      sRegisters.offer(registerName);
    }
    for (String registerName : tRegisterNames) {
      tRegisters.offer(registerName);
    }
  }
}
