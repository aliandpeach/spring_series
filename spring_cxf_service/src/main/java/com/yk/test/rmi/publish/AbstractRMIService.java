package com.yk.test.rmi.publish;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public abstract class AbstractRMIService
{
    public abstract String getServiceName();

    public abstract Remote getService() throws RemoteException;

    public void registry(Registry registry) throws AlreadyBoundException, RemoteException
    {
        registry.bind(getServiceName(), getService());
    }
}
