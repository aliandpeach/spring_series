package com.yk.leetcode;

public class Main {
    public static void main(String[] args) {

        Foo foo = new Foo();
        Thread a = new Thread(() -> {
            try {
                foo.first(new MyRunnable("first"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        a.setName("A");
        Thread b = new Thread(() -> {
            try {
                foo.second(new MyRunnable("second"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        b.setName("B");
        Thread c = new Thread(() -> {
            try {
                foo.third(new MyRunnable("third"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        c.setName("C");
        a.start();
        b.start();
        c.start();
    }

    static class MyRunnable implements Runnable {

        String text;

        public MyRunnable(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            System.out.print(text);
        }
    }
}
