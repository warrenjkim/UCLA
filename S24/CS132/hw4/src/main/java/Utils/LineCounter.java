package Utils;

public class LineCounter {
  private int lineNumber;

  public LineCounter() {
    lineNumber = 0;
  }

  public int LineNumber() {
    return lineNumber;
  }

  public void IncrementLineNumber() {
    this.lineNumber++;
  }

  public void Reset() {
    lineNumber = 0;
  }
}
