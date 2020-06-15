package com.yk.test.stack;

public class MyThread extends Thread
{
    @Override
    public void run()
    {
        this.isInterrupted();
        this.interrupt();
        interrupted();
    }
}
