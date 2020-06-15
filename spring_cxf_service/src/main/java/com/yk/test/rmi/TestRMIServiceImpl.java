package com.yk.test.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

public class TestRMIServiceImpl extends UnicastRemoteObject implements ITestRMIService
{
    private static final long serialVersionUID = 4551816863636043469L;

    protected TestRMIServiceImpl(int port,
                                 RMIClientSocketFactory csf,
                                 RMIServerSocketFactory ssf) throws RemoteException
    {
        super(port, csf, ssf);
    }

    public void test()
    {
        System.out.println("rmi test running...");
    }
}
