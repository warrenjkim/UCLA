class Main {
    public static void main(String[] a){
        System.out.println(new Test().Init());
    }
}

class Test {
    public int Init() {
        int i;
        int[] test_array;
        test_array = new int[20];
        test_array[0] = 4;
        test_array[1] = 2;
        i = 0;
        while (i < 2) {
            System.out.println(test_array[i]);
            i = i + 1;
        }
        return 1;
    }
}
