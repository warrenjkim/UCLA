package Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

public class SparrowVRangeBuilder<T> {
  private Map<String, Integer> firstUse;
  private Map<String, Integer> lastUse;
  private Map<String, List<Integer>> defs;
  private Map<String, List<Integer>> uses;

  public void initialize() {
    this.firstUse = new LinkedHashMap<>();
    this.lastUse = new LinkedHashMap<>();
    this.defs = new LinkedHashMap<>();
    this.uses = new LinkedHashMap<>();
  }

  public SparrowVRangeBuilder() {
    initialize();
  }

  public void AddDef(T id, Integer line) {
    SetFirstUse(id, line);
    SetLastUse(id, line);

    if (Defs(id) == null) {
      defs.put(id.toString(), new LinkedList<>());
    }

    Defs(id).add(line);
  }

  public void AddUse(T id, Integer line) {
    SetFirstUse(id, line);
    SetLastUse(id, line);

    if (Uses(id) == null) {
      uses.put(id.toString(), new LinkedList<>());
    }

    Uses(id).add(line);
  }

  public void SetFirstUse(T id, Integer line) {
    if (FirstUse(id) == null || line < FirstUse(id)) {
      firstUse.put(id.toString(), line);
    }
  }

  public void SetLastUse(T id, Integer line) {
    if (LastUse(id) == null || LastUse(id) < line) {
      lastUse.put(id.toString(), line);
    }
  }

  public List<Integer> Defs(T id) {
    return defs.get(id.toString());
  }

  public List<Integer> Uses(T id) {
    return uses.get(id.toString());
  }

  public Integer FirstUse(T id) {
    return firstUse.get(id.toString());
  }

  public Integer LastUse(T id) {
    return lastUse.get(id.toString());
  }

  public Map<String, SparrowVRange> BuildRangeMap() {
    LinkedHashMap<String, SparrowVRange> rangeMap = new LinkedHashMap<>();
    for (Map.Entry<String, Integer> entry : firstUse.entrySet()) {
      Pair<Integer, Integer> range =
          new Pair<Integer, Integer>(entry.getValue(), lastUse.get(entry.getKey()));
      List<Integer> defList = defs.get(entry.getKey());
      List<Integer> useList = uses.get(entry.getKey());
      if (useList == null) {
        useList = new LinkedList<>();
        useList.add(defList.get(0));
      }

      rangeMap.put(entry.getKey(), new SparrowVRange(range, defList, useList));
    }

    return rangeMap;
  }

  public int Size() {
    return firstUse.size();
  }

  public boolean Contains(T id) {
    return firstUse.containsKey(id.toString());
  }

  public Map<String, SparrowVRange> BuildMergedRangeMap() {
    LinkedList<Map.Entry<String, SparrowVRange>> rangeList = new LinkedList<>();

    for (Map.Entry<String, SparrowVRange> label : BuildRangeMap().entrySet()) {
      SparrowVRange labelRange = label.getValue();
      if (labelRange.FirstUse() == labelRange.LastUse()) {
        continue;
      }

      if (rangeList.isEmpty() || rangeList.peekLast().getValue().LastUse() < labelRange.LastUse()) {
        rangeList.add(label);
      } else {
        rangeList.peekLast().getValue().SetLastUse(Integer.max(rangeList.peekLast().getValue().LastUse(), labelRange.LastUse()));
      }
    }

    Map<String, SparrowVRange> rangeMap = new LinkedHashMap<>();
    for (Map.Entry<String, SparrowVRange> label : rangeList) {
      rangeMap.put(label.getKey(), label.getValue());
    }

    return rangeMap;
  }
}
