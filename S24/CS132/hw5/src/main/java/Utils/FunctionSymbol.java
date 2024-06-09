package Utils;

import IR.token.FunctionName;
import IR.token.Identifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class FunctionSymbol {
  private String name = "";
  private int varCounter = 3;
  private int paramCounter = 0;

  private Map<String, Integer> offsetMap = new LinkedHashMap<>();

  public FunctionSymbol(FunctionName name) {
    this.name = name.toString();
  }

  public String Name() {
    return name;
  }

  public void PushVariable(Identifier id) {
    if (offsetMap.containsKey(id.toString())) {
      return;
    }

    offsetMap.put(id.toString(), varCounter++);
  }

  public void AddParam(Identifier id) {
    offsetMap.put(id.toString(), paramCounter--);
  }

  public Integer Offset(Identifier id) {
    return offsetMap.get(id.toString());
  }

  public int StackSize() {
    return varCounter;
  }

  public int ParamSize() {
    return -paramCounter;
  }

  public String ToString() {
    StringBuilder builder = new StringBuilder();
    builder.append("func " + name);
    builder.append("\nOffset Map:\n");
    offsetMap.forEach(
        (id, offset) -> {
          builder.append("  (" + id + ", " + offset + ")\n");
        });

    return builder.toString();
  }
}
