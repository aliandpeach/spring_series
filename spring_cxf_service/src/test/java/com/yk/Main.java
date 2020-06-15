package com.yk;

import java.util.Scanner;

public class Main {
    /**
     * 7
     * 2
     * 3
     * 2
     * 1
     * 2
     * 1
     * 5
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        int number = in.nextInt();
        int[] ary = new int[number];
        for (int i = 0; i < number; i++) {
            int a = in.nextInt();
            ary[i] = a;
        }
        int r = new Main().jump(number, ary);
        System.out.println(r);
    }

    public int jump(int number, int[] arys) {
        int count = 0;
        int start = 0;
        int end = number - 1;
        int temp = 0;
        while (end != start) {
            for (int i = 0; i < end; i++) {
                if (arys[end] - 1 == arys[start]) {
                    count++;
                    end = start;
                    break;
                }
                if (arys[i] + i == end) {
                    temp = i;
                    count++;
                    end = temp;
                    break;
                }
            }
        }
        return count;
    }
}
