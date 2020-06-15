package com.yk.test;

public class Main
{
    public static void main(String arg[])
    {
        B b = new B();

        System.out.println(A.a);
        System.out.println(A.b);

        System.out.println(B.a);
        System.out.println(B.b);

        b.methodA();
        b.methodB();
    }
}
