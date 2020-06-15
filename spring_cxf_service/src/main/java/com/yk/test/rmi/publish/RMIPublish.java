package com.yk.test.rmi.publish;

import com.yk.test.rmi.TestRMIServiceProxy;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RMIPublish
{
    public static final int PORT = 61535;

    private static final String HOST = "192.168.31.105";

    private List<AbstractRMIService> list = new ArrayList<>();

    private CurRMIClientSocketFactory csf;
    private CurRMIServerSocketFactory ssf;

    private Registry registry;

    private RMIPublish()
    {
        csf = new CurRMIClientSocketFactory();
        ssf = new CurRMIServerSocketFactory(HOST);
        try
        {
            registry = LocateRegistry.createRegistry(PORT, csf, ssf);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public void publish()
    {
        TestRMIServiceProxy.getInstance().add();
        for (AbstractRMIService rmiService : list)
        {
            try
            {
                rmiService.registry(registry);
            } catch (AlreadyBoundException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static RMIPublish getInstance()
    {
        return RMIPublishInstance.INSTANCE;
    }

    private static class RMIPublishInstance
    {
        public static RMIPublish INSTANCE = new RMIPublish();
    }

    public CurRMIClientSocketFactory getCsf()
    {
        return csf;
    }

    public CurRMIServerSocketFactory getSsf()
    {
        return ssf;
    }

    public void add(AbstractRMIService service)
    {
        synchronized (this)
        {
            this.list.add(service);
        }
    }
}
