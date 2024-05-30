package Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

public class LiveRangesBuilder {
  private Map<String, Integer> firstUse;
  private Map<String, Integer> lastUse;
  private Map<String, Boolean> extendsFunc;

  public Map<String, List<Integer>> defs;
  public Map<String, List<Integer>> uses;

  public LiveRangesBuilder() {
    this.firstUse = new LinkedHashMap<>();
    this.lastUse = new LinkedHashMap<>();
    this.extendsFunc = new LinkedHashMap<>();

    this.defs = new LinkedHashMap<>();
    this.uses = new LinkedHashMap<>();
  }

  public void AddDef(String id, Integer line) {
    if (defs.get(id) == null) {
      defs.put(id, new LinkedList<>());
    }
    defs.get(id).add(line);
  }

  public void AddUse(String id, Integer line) {
    if (uses.get(id) == null) {
      uses.put(id, new LinkedList<>());
    }
    uses.get(id).add(line);
  }

  public void PutFirstUse(String id, Integer line) {
    firstUse.putIfAbsent(id, line);
  }

  public void PutLastUse(String id, Integer line) {
    lastUse.put(id, line);
  }

  public void ExtendsFunc(String id) {
    extendsFunc.put(id, true);
  }

  public LiveRanges LiveRanges() {
    return new LiveRanges(liveRangesBuilderMap());
  }

  public int Size() {
    return firstUse.size();
  }

  public boolean Contains(String id) {
    return firstUse.containsKey(id);
  }

  private LinkedHashMap<String, SparrowVRange> liveRangesBuilderMap() {
    LinkedHashMap<String, SparrowVRange> rangeMap = new LinkedHashMap<>();
    for (Map.Entry<String, Integer> entry : firstUse.entrySet()) {
      Pair<Integer, Integer> range =
          new Pair<Integer, Integer>(entry.getValue(), lastUse.get(entry.getKey()));
      List<Integer> defList = defs.get(entry.getKey());
      List<Integer> useList = uses.get(entry.getKey());

      rangeMap.put(entry.getKey(), new SparrowVRange(range, defList, useList));
    }

    return rangeMap;
  }
}
