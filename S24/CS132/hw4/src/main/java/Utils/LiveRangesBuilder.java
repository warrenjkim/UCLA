package Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LiveRangesBuilder {
  private Map<String, Integer> firstUse;
  private Map<String, Integer> lastUse;

  public LiveRangesBuilder() {
    this.firstUse = new LinkedHashMap<>();
    this.lastUse = new LinkedHashMap<>();
  }

  public void PutFirstUse(String id, Integer line) {
    firstUse.putIfAbsent(id, line);
  }

  public void PutLastUse(String id, Integer line) {
    lastUse.put(id, line);
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

      rangeMap.put(entry.getKey(), new SparrowVRange(range));
    }

    return rangeMap;
  }
}
