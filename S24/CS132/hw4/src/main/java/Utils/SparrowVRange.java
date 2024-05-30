package Utils;
import java.util.List;

public class SparrowVRange {
  Pair<Integer, Integer> range;
  private boolean extendsFunc;
  private List<Integer> definitions;
  private List<Integer> uses;


  public SparrowVRange(Pair<Integer, Integer> range, boolean extendsFunc) {
    this.range = range;
    this.extendsFunc = extendsFunc;
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

  public boolean ExtendsFunc() {
    return extendsFunc;
  }

  public void SetFirstUse(Integer firstUse) {
    range.first = firstUse;
  }

  public void SetLastUse(Integer lastUse) {
    range.second = lastUse;
  }

  public String ToString() {
    return range.first + ", " + range.second + ", " + extendsFunc;
  }
}
