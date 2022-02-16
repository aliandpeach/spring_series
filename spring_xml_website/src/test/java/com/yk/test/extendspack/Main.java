package com.yk.test.extendspack;

import java.util.Optional;

public class Main {

    public static void main(String args[]) {
        DownC topC1 = new DownC();
        LowC topC2 = new LowC();
        System.out.println();

        int type = 1;
        try {
            int t = Optional.of(type).filter(e -> e <= 2 && e >= 0).orElseThrow(() -> new RuntimeException("type is not correct 0 or 1 or 2 is required"));
            type = -1;
        } catch (Exception e) {
            type = 3;
        }
    }
}
