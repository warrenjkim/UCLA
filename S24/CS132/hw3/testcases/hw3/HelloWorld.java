class HelloWorld {
  public static void main(String[] a) {
    int[] x;
    int i;
    x = new int[13];
    x[0] = 72;
    x[1] = 101;
    x[2] = 108;
    x[3] = 108;
    x[4] = 11;
    x[5] = 44;
    x[6] = 32;
    x[7] = 87;
    x[8] = 111;
    x[9] = 114;
    x[10] = 108;
    x[11] = 100;
    x[12] = 33;

    i = 0;
    while (i < (x.length)) {
      System.out.println(x[i]);
      i = i + 1;
    }
  }
}
