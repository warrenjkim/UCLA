class Main {
  public static void main(String[] a) {
    boolean x;
    int y;

    x = false;
    y = 0;
    while (!x) {
      if (y < 1) {
        System.out.println(y);
      } else if (y < 2) {
        System.out.println(y);
      } else {
        System.out.println(y);
        if (10 < y) {
          x = true;
        } else
          x = false;
      }

      y = y + 1;
    }

    System.out.println(y);
  }
}
