package Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LiveRangesBuilder {
  private Map<String, Integer> firstUse;
  private Map<String, Integer> lastUse;
  private Map<String, Boolean> extendsFunc;

  public LiveRangesBuilder() {
    this.firstUse = new LinkedHashMap<>();
    this.lastUse = new LinkedHashMap<>();
    this.extendsFunc = new LinkedHashMap<>();
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
      Boolean extendFlag = extendsFunc.containsKey(entry.getKey());

      rangeMap.put(entry.getKey(), new SparrowVRange(range, extendFlag));
    }

    return rangeMap;
  }
}
