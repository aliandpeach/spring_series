package com.yk.test.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITestRMIService extends Remote
{
    void test() throws RemoteException;
}
