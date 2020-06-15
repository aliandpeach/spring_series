package com.yk.test.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMITest
{
    public static void main(String args[])
    {
        try
        {

            Registry registry = LocateRegistry.getRegistry("192.168.31.152", 65535,
                    new CurRMIClientSocketFactory());

            ITestRMIService testRMIService = (ITestRMIService) registry.lookup("TestRMIService");

            testRMIService.test();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
