package Utils;

public class Pair<T, U> {
  public T first;
  public U second;

  public Pair(T first, U second) {
    this.first = first;
    this.second = second;
  }

  public String ToString() {
    return this.first + ", " + this.second;
  }

  public T first() {
    return first;
  }

  public U second() {
    return second;
  }
}

