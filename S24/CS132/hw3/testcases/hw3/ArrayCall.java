class Main {
	public static void main(String[] a){
    int[] x;
    int tmp;

    x = new int[3];

		System.out.println(new A().run(x));
    System.out.println(x[0]);
    System.out.println(x[1]);
    tmp = x[1];
    x[2] = tmp + 1;
    System.out.println(x[2]);
	}
}

class A {
  int x;
	public int run(int[] x) {
    x[0] = this.InitX();
		return this.why(x, x[0]);
	}

  public int why(int[] x, int v) {
    int tmp;
    tmp = x[0];
    x[v] = (this.GetX()) + tmp;
    return x[v];
  }

  public int InitX() {
    x = 1;
    return x;
  }

  public int GetX() {
    return x;
  }
}
