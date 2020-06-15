package com.yk.test.stack;

public class JoinThread
{
    public static void main(String[] args) throws InterruptedException
    {
        Join1 join1 = new Join1();
        Join2 join2 = new Join2(join1);
        join2.start();
        join2.join();
        join1.start();//在这才执行join1 start,它不会被加入到join2
        System.out.println("main finished...");
    }

    static class Join1 extends Thread
    {
        public void run()
        {
            System.out.println("Join1 finished...");
        }
    }

    static class Join2 extends Thread
    {
        Join1 join1;

        public Join2(Join1 join1)
        {
            this.join1 = join1;
        }

        public void run()
        {
            try
            {
                join1.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("Join2 finished...");
        }
    }
}
