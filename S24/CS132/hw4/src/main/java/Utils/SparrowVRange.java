package Utils;

import java.util.List;
import java.util.LinkedList;
import java.lang.StringBuilder;

public class SparrowVRange {
  Pair<Integer, Integer> range;
  List<Integer> defs;
  List<Integer> uses;

  public SparrowVRange() {
    this.range = new Pair<>(0, 0);
    this.defs = new LinkedList<>();
    this.uses = new LinkedList<>();
  }

  public SparrowVRange(Pair<Integer, Integer> range, List<Integer> defs, List<Integer> uses) {
    this.range = range;
    this.defs = defs;
    this.uses = uses;
  }

  public Pair<Integer, Integer> Range() {
    return range;
  }

  public List<Integer> Defs() {
    return defs;
  }

  public List<Integer> Uses() {
    return uses;
  }

  public void AddDef(Integer line) {
    defs.add(line);
  }

  public void AddUse(Integer line) {
    uses.add(line);
  }

  public Integer FirstUse() {
    return range.first;
  }

  public Integer LastUse() {
    return range.second;
  }

  public void SetFirstUse(Integer firstUse) {
    if (firstUse < range.first) {
      range.first = firstUse;
    }
  }

  public void SetLastUse(Integer lastUse) {
    if (lastUse > range.second) {
      range.second = lastUse;
    }
  }

  public void ExtendRange(Pair<Integer, Integer> labelRange) {
    Integer def = firstDefAfterLabel(labelRange.first);
    Integer use = firstUseAfterLabel(labelRange.second);

    // extend
    if (def != null && use != null && use <= def) {
      SetFirstUse(labelRange.first);
    }

    SetLastUse(labelRange.second);
  }

  private Integer firstDefAfterLabel(Integer labelLine) {
    for (Integer def : defs) {
      if (def > labelLine) {
        return def;
      }
    }

    return null;
  }

  private Integer firstUseAfterLabel(Integer labelLine) {
    for (Integer use : uses) {
      if (use > labelLine) {
        return use;
      }
    }

    return null;
  }

  public String ToString() {
    StringBuilder result = new StringBuilder();

    result.append("[" + range.first + ", " + range.second + ")");

    result.append("\nDefinitions:\n  ");
    defs.forEach((def) -> {
      result.append(def + " ");
    });

    result.append("\nUses:\n  ");
    uses.forEach((use) -> {
      result.append(use + " ");
    });

    return result.toString();
  }

  @Override
  public String toString() {
    return "[" + range.first + ", " + range.second + ")";
  }
}
