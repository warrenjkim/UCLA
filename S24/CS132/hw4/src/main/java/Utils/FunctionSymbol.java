package Utils;

import java.util.Map;

public class FunctionSymbol {
  private String name;

  private LiveRanges paramRanges;
  private LiveRanges liveRanges;
  private LiveRanges labelRanges;

  private Map<String, String> registerAssignments;
  private Map<String, String> argRegisterAssignments;

  public FunctionSymbol(String name, LiveRanges paramRanges, LiveRanges liveRanges, LiveRanges labelRanges) {
    this.name = name;
    this.liveRanges = liveRanges;
    this.labelRanges = labelRanges;
    this.paramRanges = paramRanges;
  }

  public void SetParamRanges(LiveRanges paramRanges) {
    this.paramRanges = paramRanges;
  }

  public void SetRegisterAssignments(Map<String, String> registerAssignments) {
    this.registerAssignments = registerAssignments;
  }

  public void SetArgRegisterAssignments(Map<String, String> argRegisterAssignments) {
    this.argRegisterAssignments = argRegisterAssignments;
  }

  public String Register(String id) {
    String reg = registerAssignments.get(id);
    if (reg == null) {
      reg = argRegisterAssignments.get(id);
    }

    return reg;
  }

  public Map<String, String> ArgRegisterAssignments() {
    return argRegisterAssignments;
  }

  public Map<String, String> RegisterAssignments() {
    return registerAssignments;
  }

  public String Name() {
    return name;
  }

  public LiveRanges ParamRanges() {
    return paramRanges;
  }

  public LiveRanges LiveRanges() {
    return liveRanges;
  }

  public LiveRanges LabelRanges() {
    return labelRanges;
  }

  public Integer FirstUse(String id) {
    Integer firstUse = liveRanges.FirstUse(id);
    if (firstUse == null) {
      firstUse = paramRanges.FirstUse(id);
    }

    return firstUse;
  }

  public Integer LastUse(String id) {
    Integer lastUse = liveRanges.LastUse(id);
    if (lastUse == null) {
      lastUse = paramRanges.LastUse(id);
    }

    return lastUse;
  }

  public String ToString() {
    String function = name + ":\n  Params:\n";
    if (paramRanges != null) {
      for (Map.Entry<String, SparrowVRange> param : paramRanges.LiveRangesMap().entrySet()) {
        function += "    " + param.getKey() + ": [" + param.getValue().ToString() + ")\n";
        function += "      Definitions: " + param.getValue().Defs().toString() + "\n";
        function += "      Uses       : " + param.getValue().Uses().toString() + "\n";
      }
    }

    function += "\n  Live Ranges:\n";

    for (Map.Entry<String, SparrowVRange> var : liveRanges.LiveRangesMap().entrySet()) {
      function += "    " + var.getKey() + ": [" + var.getValue().ToString() + ")\n";
      function += "      Definitions: " + var.getValue().Defs().toString() + "\n";
      function += "      Uses       : " + var.getValue().Uses().toString() + "\n";
    }

    function += "\n  Label Ranges:\n";
    for (Map.Entry<String, SparrowVRange> var : labelRanges.LiveRangesMap().entrySet()) {
      function += "    " + var.getKey() + ": [" + var.getValue().ToString() + ")\n";
    }

    if (registerAssignments != null) {
      function += "\n  Register Assignments:\n";
      for (Map.Entry<String, String> var : registerAssignments.entrySet()) {
        function += "    " + var.getKey() + ": " + var.getValue() + "\n";
      }
    }

    return function;
  }
}
