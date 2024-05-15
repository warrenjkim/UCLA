class Main {
	public static void main(String[] a){
    int[] x;
    int j;
    x = new int[5];
    System.out.println(x.length);
    j = 4;
    x = new int[j + (1 - 1)];
    System.out.println(x.length);
    x = new int[(1 + 2)];
    System.out.println(x.length);
    x = new int[(3 - 1)];
    System.out.println(x.length);

    x = new int[(1 * 1)];
    System.out.println(x.length);
	}
}
