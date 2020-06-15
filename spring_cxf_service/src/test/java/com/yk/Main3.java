package com.yk;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int all = 10000;
        double d = scanner.nextDouble();

        List<Double> doubleList = new ArrayList<>();
        double m = 1;
        int n = 1;
        for (int i = 1; i <= all; i++) {
            if (n > 10000) {
                break;
            }
            double temp = m / n;
            doubleList.add(temp);
            if (d > temp) {
                m = m + 1;
                continue;
            }
            if (d < temp) {
                n++;
            }
        }
        System.out.println(doubleList);
    }
}
