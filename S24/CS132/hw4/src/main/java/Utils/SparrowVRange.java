package Utils;

import java.util.List;

public class SparrowVRange {
  Pair<Integer, Integer> range;
  List<Integer> defs;
  List<Integer> uses;

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

  public Integer FirstUse() {
    return range.first;
  }

  public Integer LastUse() {
    return range.second;
  }

  public void SetFirstUse(Integer firstUse) {
    range.first = firstUse;
  }

  public void SetLastUse(Integer lastUse) {
    range.second = lastUse;
  }

  public String ToString() {
    return range.first + ", " + range.second;
  }
  public String toString() {
    return "(" + range.first + ", " + range.second + ")";
  }
}
