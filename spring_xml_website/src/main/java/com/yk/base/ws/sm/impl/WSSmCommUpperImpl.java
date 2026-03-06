package com.yk.base.ws.sm.impl;

import com.yk.base.ws.sm.WSSmCommUpper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("wsSmCommUpper")
@WebService(targetNamespace = "http://wssmcommupper/")
public class WSSmCommUpperImpl implements WSSmCommUpper
{
    private static final Logger logger = LoggerFactory.getLogger(WSSmCommUpperImpl.class);

    @Override
    public int reportConfig(String deviceId, String oprCode, String configXml)
    {
        System.out.println("reportHealthView: " + deviceId + ", " + oprCode + ", " + configXml);
        return 11;
    }

    @Override
    public int reportPolicy(String deviceId, String oprCode, String policyXml)
    {
        System.out.println("reportHealthView: " + deviceId + ", " + oprCode + ", " + policyXml);
        return 11;
    }

    @Override
    public String registerConfig(String arg0, String arg1, String arg2)
    {
        System.out.println("reportHealthView: " + arg0 + ", " + arg1 + ", " + arg2);
        return "12";
    }

    @Override
    public String queryPolicy(String arg0, String arg1, String arg2)
    {
        System.out.println("reportHealthView: " + arg0 + ", " + arg1 + ", " + arg2);
        return "12";
    }

    @Override
    public int reportHealthView(String deviceId, String oprCode, String viewXml)
    {
        System.out.println("reportHealthView: " + deviceId + ", " + oprCode + ", " + viewXml);
        return 11;
    }

    @Override
    public int reportView(String deviceId, String oprCode, String viewXml)
    {
        System.out.println("reportView: " + deviceId + ", " + oprCode + ", " + viewXml);
        return 11;
    }

    @Override
    public String queryConfig(String arg0, String arg1, String arg2)
    {
        System.out.println("reportHealthView: " + arg0 + ", " + arg1 + ", " + arg2);
        return "12";
    }

    @Override
    public String queryView(String arg0, String arg1, String arg2)
    {
        return "12";
    }

    @Override
    public int reportEvent(String deviceId, String oprCode, String eventXml)
    {
        System.out.println("reportEvent: " + deviceId + ", " + oprCode + ", " + eventXml);
        return 11;
    }

    @Override
    public int reportLog(String deviceId, String oprCode, String logXml)
    {
        System.out.println("reportLog: " + deviceId + ", " + oprCode + ", " + logXml);
        return 11;
    }

    @Override
    public int reportSoftware(String arg0, String arg1, String arg2)
    {
        System.out.println("reportHealthView: " + arg0 + ", " + arg1 + ", " + arg2);
        return 0;
    }
}
