package com.yk;

import java.util.*;

public class Main2 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String line = scanner.next();

            if (!line.contains("@")) {
                continue;
            }

            String[] twoLine = line.split("@");

            if (twoLine.length == 1) {
                System.out.println(line);
                continue;
            }

            if (twoLine.length != 2) {
                continue;
            }

            String[] alls = twoLine[0].split(",");
            String[] places = twoLine[1].split(",");

            boolean is = false;

            for (int i = 0; i < alls.length; i++) {
                String[] temp = alls[i].split(":");
                if (temp.length != 2) {
                    break;
                }
                if (alls[i].contains(":") && temp.length == 2) {
                    is = true;
                }
                char ch = temp[0].charAt(0);
                int num = Integer.parseInt(temp[1]);
                if (num > 0 && num <= 100) {
                    is = true;
                } else {
                    break;
                }
                if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                    is = true;
                }
            }

            if (!is) {
                continue;
            }

            is = false;
            for (int i = 0; i < places.length; i++) {
                String[] temp = places[i].split(":");
                if (temp.length != 2) {
                    break;
                }
                if (places[i].contains(":") && temp.length == 2) {
                    is = true;
                }
                char ch = temp[0].charAt(0);
                int num = Integer.parseInt(temp[1]);
                if (num > 0 && num <= 100) {
                    is = true;
                } else {
                    break;
                }
                if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                    is = true;
                }
            }

            if (!is) {
                continue;
            }


            Map<Character, Integer> allsMap = new LinkedHashMap<>();
            Map<Character, Integer> placesMap = new LinkedHashMap<>();

            for (int i = 0; i < alls.length; i++) {
                String[] temp = alls[i].split(":");
                char ch = temp[0].charAt(0);
                int num = Integer.parseInt(temp[1]);
                allsMap.put(ch, num);
            }

            for (int i = 0; i < places.length; i++) {
                String[] temp = places[i].split(":");
                char ch = temp[0].charAt(0);
                int num = Integer.parseInt(temp[1]);
                if (allsMap.containsKey(ch)) {
                    allsMap.put(ch, allsMap.get(ch) - num);
                } else {
                    is = false;
                }
            }

            if (!is) {
                continue;
            }

            String ret = "";

            for (Map.Entry<Character, Integer> entry : allsMap.entrySet()) {
                if (entry.getValue() <= 0) {
                    continue;
                }
                ret += entry.getKey() + ":" + entry.getValue() + ",";
            }

            System.out.println(ret.endsWith(",") ? ret.substring(0, ret.length() - 1) : ret);
        }
    }

}
