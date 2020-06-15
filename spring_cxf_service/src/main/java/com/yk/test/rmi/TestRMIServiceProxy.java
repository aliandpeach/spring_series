package com.yk.test.rmi;

import com.yk.test.rmi.publish.AbstractRMIService;
import com.yk.test.rmi.publish.RMIPublish;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class TestRMIServiceProxy
{
    private static TestRMIServiceProxy testRMIServiceProxy = new TestRMIServiceProxy();

    public static TestRMIServiceProxy getInstance()
    {
        return testRMIServiceProxy;
    }

    public void add()
    {
        try
        {
            AbstractRMIService service = new TestRMIServiceProxyInner();
            RMIPublish.getInstance().add(service);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class TestRMIServiceProxyInner extends AbstractRMIService
    {
        @Override
        public String getServiceName()
        {
            return "TestRMIService";
        }

        @Override
        public Remote getService() throws RemoteException
        {
            return new TestRMIServiceImpl(RMIPublish.PORT, RMIPublish.getInstance().getCsf(), RMIPublish.getInstance().getSsf());
        }
    }
}
