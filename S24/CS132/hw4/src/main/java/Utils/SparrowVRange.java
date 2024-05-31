package Utils;

public class SparrowVRange {
  Pair<Integer, Integer> range;


  public SparrowVRange(Pair<Integer, Integer> range) {
    this.range = range;
  }

  public Pair<Integer, Integer> Range() {
    return range;
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
    return range.first + ", " + range.second;
  }
}
