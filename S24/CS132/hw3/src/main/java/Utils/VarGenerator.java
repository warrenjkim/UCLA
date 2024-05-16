package Utils;

public class VarGenerator {
  private static VarGenerator instance;

  private String id;
  private int idCount;

  private String label;
  private int labelCount;

  private VarGenerator() {
    id = "v";
    label = "l";
    idCount = 0;
    labelCount = 0;
  }

  public static VarGenerator GetInstance() {
    if (instance == null) {
      instance = new VarGenerator();
    }
    return instance;
  }

  public synchronized String NextId() {
    return id + idCount++;
  }

  public synchronized String NextLabel() {
    return label + labelCount++;
  }
}
