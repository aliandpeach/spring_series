package com.yk.base.ws.sm.impl;

import com.yk.base.ws.sm.WSSmCommLower;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("wsSmCommLower")
@WebService(targetNamespace = "http://wssmcommlower/")
public class WSSmCommLowerImpl implements WSSmCommLower
{

    private static Logger logger = LoggerFactory.getLogger(WSSmCommLowerImpl.class);

    @Override
    public int fillConfig(String oprCode, String configXml)
    {
        return 12;
    }

    @Override
    public int fillPolicy(String oprCode, String policyXml)
    {
        return 12;
    }

    @Override
    public int fillCommand(String oprCode, String commandXml)
    {
        return 12;
    }

    @Override
    public String queryPolicy(String oprCode)
    {
        return "12";
    }
}
