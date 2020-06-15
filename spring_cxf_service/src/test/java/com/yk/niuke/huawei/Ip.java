package com.yk.niuke.huawei;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Ip {
    public static void main(String[] args) {
        String ip = "192.168.0.1";
        String[] arys = ip.split("\\.");
        int nums[] = Arrays.stream(arys).mapToInt(Integer::parseInt).toArray();

        List<Integer> list = Arrays.stream(arys).map(Integer::parseInt).collect(Collectors.toList());

        System.out.println();

        for (int i = 0; i < nums.length; i++) {
            String a = new BigInteger(nums[i] + "", 10).toString(2);
            System.out.println(a);
            System.out.println(new BigInteger(a, 2).toString(10));
            System.out.println(Integer.toString(nums[i], 2));
            System.out.println(Integer.toBinaryString(nums[i]));
            String r = new BigInteger(1, "Abdfd".getBytes()).toString(16);
            byte b[] = new BigInteger(r, 16).toByteArray();
        }

        int argsx[] = new int[]{6, 3, 8, 2, 9, 1};
        for (int i = 0; i < argsx.length - 1; i++) {
            for (int j = i + 1; j < argsx.length; i++) {
                if (argsx[i] > argsx[j]) {
                    int temp = argsx[i];
                    argsx[i] = argsx[j];
                    argsx[j] = temp;
                }
            }
        }
        System.out.println(argsx);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String a = scanner.next();
            String b = scanner.next();
            String c = scanner.next();
            int[] arya = Arrays.stream(a.split("\\.")).mapToInt(Integer::parseInt).toArray();
            int[] aryb = Arrays.stream(b.split("\\.")).mapToInt(Integer::parseInt).toArray();
            int[] aryc = Arrays.stream(c.split("\\.")).mapToInt(Integer::parseInt).toArray();
            boolean r = false;
            for (int i = 0; i < arya.length; i++) {
                if (arya[i] > 255 || arya[i] < 0 || aryb[i] > 255 || aryb[i] < 0 || aryc[i] > 255 || aryc[i] < 0) {
                    System.out.println(1);
                    r = false;
                    break;
                } else if ((arya[i] & aryb[i]) == (arya[i] & aryc[i])) {
                    r = true;
                } else {
                    System.out.println(2);
                    r = false;
                    break;
                }
            }
            if (r)
                System.out.println(1);
        }

    }
}
