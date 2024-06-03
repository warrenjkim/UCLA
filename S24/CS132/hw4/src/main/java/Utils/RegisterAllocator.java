package Utils;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegisterAllocator {
  private static final String[] aRegisterNames = {"a2", "a3", "a4", "a5", "a6", "a7"};
  private static final String[] sRegisterNames = {
    "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11"
  };
  private static final String[] tRegisterNames = {
    /*"t0", "t1",*/
    "t2", "t3", "t4", "t5"
  };

  private ArrayDeque<String> aRegisters;
  private ArrayDeque<String> sRegisters;
  private ArrayDeque<String> tRegisters;

  Map<String, String> argRegAssignments;
  Map<String, String> tempRegAssignments;
  LinkedList<Map.Entry<String, SparrowVRange>> active;

  public RegisterAllocator() {
    initialize();
  }

  public void AllocateRegisters(FunctionSymbol func) {
    initialize();
    setArgRegAssignments(func.VarRanges());
    setTempRegAssignments(func.VarRanges());
    func.SetRegisterAssignments(argRegAssignments, tempRegAssignments);
  }

  public void setArgRegAssignments(Map<String, SparrowVRange> vars) {
    List<String> params = getParams(vars);
    for (String param : params) {
      argRegAssignments.put(param, nextArgRegister());
    }
  }

  public List<String> getParams(Map<String, SparrowVRange> vars) {
    List<String> params = new LinkedList<>();
    for (Map.Entry<String, SparrowVRange> var : vars.entrySet()) {
      if (var.getValue().FirstUse() == 0) {
        params.add(var.getKey());
      }
    }

    return params;
  }

  public String nextArgRegister() {
    if (!aRegisters.isEmpty()) {
      return aRegisters.pop();
    }

    return null;
  }

  public void setTempRegAssignments(Map<String, SparrowVRange> vars) {
    List<Map.Entry<String, SparrowVRange>> sortedVars = sorted(vars);
    for (Map.Entry<String, SparrowVRange> var : sortedVars) {
      if (var.getValue().FirstUse() == 0
          || var.getValue().Uses().size() == 0) {
        continue;
      }

      String id = var.getKey();
      SparrowVRange range = var.getValue();
      expire(range.Range());
      String freeRegister = nextFreeRegister();
      if (freeRegister == null) {
        spill(var);
      } else {
        tempRegAssignments.put(id, freeRegister);
        active.offer(var);
      }

      sortActive();
    }
  }

  private void expire(Pair<Integer, Integer> currRange) {
    Iterator<Map.Entry<String, SparrowVRange>> iterator = active.iterator();

    while (iterator.hasNext()) {
      Map.Entry<String, SparrowVRange> interval = iterator.next();
      String id = interval.getKey();
      Pair<Integer, Integer> range = interval.getValue().Range();
      if (range.second <= currRange.first) {
        iterator.remove();
        freeRegister(tempRegAssignments.get(id));
      }
    }
  }

  private void spill(Map.Entry<String, SparrowVRange> curr) {
    String currId = curr.getKey();
    Pair<Integer, Integer> currRange = curr.getValue().Range();
    Integer currUses = curr.getValue().Uses().size();

    Map.Entry<String, SparrowVRange> spillVar = active.peekLast();
    String spillId = spillVar.getKey();
    Pair<Integer, Integer> spillRange = spillVar.getValue().Range();
    Integer spillUses = spillVar.getValue().Uses().size();

    if (spillRange.second > currRange.second && spillUses <= currUses) {
      tempRegAssignments.put(currId, tempRegAssignments.get(spillId));
      tempRegAssignments.remove(spillId);
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

  private List<Map.Entry<String, SparrowVRange>> sorted(Map<String, SparrowVRange> vars) {
    return vars.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(SparrowVRange::FirstUse)))
        .collect(Collectors.toList());
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
    tempRegAssignments = new LinkedHashMap<>();
    argRegAssignments = new LinkedHashMap<>();

    for (String tempRegName : aRegisterNames) {
      aRegisters.offer(tempRegName);
    }
    for (String tempRegName : sRegisterNames) {
      sRegisters.offer(tempRegName);
    }
    for (String tempRegName : tRegisterNames) {
      tRegisters.offer(tempRegName);
    }
  }
}
