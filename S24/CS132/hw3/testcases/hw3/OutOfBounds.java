class NullPointerMember {
    public static void main(String[] x) {
        int[] x;
        x = new int[5];
        x[(0-1)] = 10;
        System.out.println(x[3]);
    }
}

