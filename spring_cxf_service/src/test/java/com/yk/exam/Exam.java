package com.yk.exam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Exam {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String str = sc.nextLine();
            List<String> str1 = Arrays.asList(str.split(","));
            List<String> str2 = new ArrayList<String>(str1);
            byte max = 0;
            String out = "";
            int index = 0;
            int length = str2.size();
            for (int i = 0; i < length; i++) {
                max = 0;
                for (int j = 0; j < str2.size(); j++) {

                    byte temp = (byte) str2.get(j).charAt(str2.get(j).length() - 1);
                    if (temp > max) {
                        max = temp;
                        out = str2.get(j);
                        index = j;
                    }

                }
                if (i == length - 1) {
                    System.out.print(out);
                } else {
                    System.out.print(out + ",");
                }
                str2.remove(index);
            }
        }
    }
}
