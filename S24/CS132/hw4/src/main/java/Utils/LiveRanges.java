package Utils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Collections;
import java.util.LinkedList;

public class LiveRanges {
  private Map<String, SparrowVRange> liveRanges;

  public LiveRanges() {
    liveRanges = new LinkedHashMap<>();
  }

  public LiveRanges(Map<String, SparrowVRange> liveRanges) {
    this.liveRanges = liveRanges;
  }

  public boolean Contains(String id) {
    return liveRanges.containsKey(id);
  }

  public List<Integer> Defs(String id) {
    if (!Contains(id)) {
      return new LinkedList<>();
    }

    return liveRanges.get(id).Defs();
  }

  public List<Integer> Uses(String id) {
    if (!Contains(id)) {
      return new LinkedList<>();
    }

    return liveRanges.get(id).Uses();
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

  public Map<String, SparrowVRange> LiveRangesMap() {
    return liveRanges;
  }

  public List<Map.Entry<String, SparrowVRange>> Sorted() {
    return liveRanges.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(SparrowVRange::FirstUse)))
        .collect(Collectors.toList());
  }

  public void ExtendRanges(Map<String, SparrowVRange> loops) {
    for (Map.Entry<String, SparrowVRange> varRange : liveRanges.entrySet()) {
      System.out.println("ID def: " + varRange.getKey() + ": " + varRange.getValue().Defs().toString());
      System.out.println("ID use: " + varRange.getKey() + ": " + varRange.getValue().Uses().toString());
    }
  }

  private LinkedList<SparrowVRange> mergeRanges(LinkedList<SparrowVRange> loops) {
    if (loops.isEmpty()) {
      return new LinkedList<>();
    }

    Collections.sort(loops, (lhs, rhs) -> lhs.FirstUse() - rhs.FirstUse());
    LinkedList<SparrowVRange> mergedRanges = new LinkedList<>();
    SparrowVRange curr = loops.peek();
    for (SparrowVRange next : loops) {
      if (curr.LastUse() >= next.FirstUse()) {
        curr.SetLastUse(Math.max(curr.LastUse(), next.LastUse()));
      } else {
        mergedRanges.add(curr);
        curr = next;
      }
    }

    mergedRanges.add(curr);
    return mergedRanges;
  }
}
