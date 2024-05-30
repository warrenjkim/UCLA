package Utils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LiveRanges {
  private Map<String, SparrowVRange> liveRanges;

  public LiveRanges() {
    liveRanges = new LinkedHashMap<>();
  }

  public LiveRanges(Map<String, SparrowVRange> liveRanges) {
    this.liveRanges = liveRanges;
  }

  public Integer FirstUse(String id) {
    if (liveRanges.get(id) == null) {
      return null;
    }

    return liveRanges.get(id).FirstUse();
  }

  public Integer LastUse(String id) {
    if (liveRanges.get(id) == null) {
      return null;
    }

    return liveRanges.get(id).LastUse();
  }

  public void OverwriteFirstUse(String id, Integer line) {
    if (liveRanges.containsKey(id) && line < liveRanges.get(id).FirstUse()) {
      liveRanges.get(id).SetFirstUse(line);
    }
  }

  public void OverwriteLastUse(String id, Integer line) {
    if (liveRanges.containsKey(id) && line > liveRanges.get(id).LastUse()) {
      liveRanges.get(id).SetLastUse(line);
    }
  }

  public List<Integer> Defs(String id) {
    return liveRanges.get(id).Defs();
  }

  public List<Integer> Uses(String id) {
    return liveRanges.get(id).Uses();
  }

  public Map<String, SparrowVRange> LiveRangesMap() {
    return liveRanges;
  }

  public List<Map.Entry<String, SparrowVRange>> Sorted() {
    return liveRanges.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(SparrowVRange::FirstUse)))
        .collect(Collectors.toList());
  }
}
