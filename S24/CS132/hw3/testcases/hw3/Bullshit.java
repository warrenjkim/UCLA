class Bullshit {
	public static void main(String[] a){
    int[] x;
    int tmp;
    A ar;

    x = new int[3];
    ar = new A();

		System.out.println((((ar.run(x)).InitX()).GetD()).SetX(2));
    System.out.println(x[0]);
    System.out.println(x[1]);
    tmp = x[1];
    x[2] = tmp + 1;
    System.out.println(x[2]);
    System.out.println((new A().runThis(x)).GetX());
	}
}

class A {
  int x;
  A d;

	public A run(int[] x) {
    int d;
    x[0] = (this.InitX()).GetX();
		d = this.why(x, x[0]);
    return new A();
	}

  public A runThis(int[] x) {
    int d;
    x[0] = (this.InitX()).GetX();
		d = this.why(x, x[0]);
    return this;
  }

  public int why(int[] x, int v) {
    int tmp;
    tmp = x[0];
    x[v] = (this.GetX()) + tmp;
    return x[v];
  }

  public A InitX() {
    x = 1;
    return this;
  }

  public int GetX() {
    return x;
  }

  public A GetD() {
    d = new A();
    return d;
  }

  public int SetX(int y) {
    x = y;
    return x;
  }
}
