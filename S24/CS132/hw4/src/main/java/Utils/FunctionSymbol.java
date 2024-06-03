package Utils;

import IR.token.Identifier;
import IR.token.FunctionName;
import IR.token.Label;
import java.util.Map;
import java.util.LinkedHashMap;
import java.lang.StringBuilder;

public class FunctionSymbol {
  private FunctionName name;

  private Map<String, SparrowVRange> varRanges;
  private Map<String, SparrowVRange> labelRanges;

  private Map<String, String> tempRegAssignments;
  private Map<String, String> argRegAssignments;

  public FunctionSymbol(FunctionName name) {
    this.name = name;
    this.varRanges = new LinkedHashMap<>();
    this.labelRanges = new LinkedHashMap<>();
  }

  public FunctionSymbol(
      FunctionName name,
      Map<String, SparrowVRange> varRanges,
      Map<String, SparrowVRange> labelRanges) {
    this.name = name;
    this.varRanges = varRanges;
    this.labelRanges = labelRanges;
  }

  public FunctionName Name() {
    return name;
  }

  public String Register(String id) {
    String reg = tempRegAssignments.get(id);
    if (reg == null) {
      reg = argRegAssignments.get(id);
    }

    return reg;
  }

  public String Register(Identifier id) {
    String reg = tempRegAssignments.get(id.toString());
    if (reg == null) {
      reg = argRegAssignments.get(id.toString());
    }

    return reg;
  }

  public void SetTempRegAssignments(Map<String, String> tempRegAssignments) {
    this.tempRegAssignments = tempRegAssignments;
  }

  public void SetArgRegAssignments(Map<String, String> argRegAssignments) {
    this.argRegAssignments = argRegAssignments;
  }

  public Map<String, String> TempRegAssignments() {
    return tempRegAssignments;
  }

  public Map<String, String> ArgRegAssignments() {
    return argRegAssignments;
  }

  public SparrowVRange Range(Identifier id) {
    return varRanges.get(id.toString());
  }

  public SparrowVRange Range(Label id) {
    return labelRanges.get(id.toString());
  }

  public Map<String, SparrowVRange> LabelRanges() {
    return labelRanges;
  }

  public Map<String, SparrowVRange> VarRanges() {
    return varRanges;
  }

  public void SetRegisterAssignments(Map<String, String> argRegAssignments, Map<String, String> tempRegAssignments) {
    this.argRegAssignments = argRegAssignments;
    this.tempRegAssignments = tempRegAssignments;
  }

  public String ToString() {
    StringBuilder builder = new StringBuilder();
    builder.append("func " + name);
    builder.append("\nParameters:\n");
    varRanges.forEach(
    (id, range) -> {
        if (range.FirstUse() == 0) {
          builder.append("  " + id + ": " + range.toString() + "\n");
        }
      });

    builder.append("\nVariables:\n");
    varRanges.forEach(
        (id, range) -> {
          builder.append("  " + id + ": " + range.toString() + "\n");
        });

    builder.append("\nLabels:\n");
    labelRanges.forEach(
        (id, range) -> {
          builder.append("  " + id + ": " + range.toString() + "\n");
        });

    return builder.toString();
  }
}
