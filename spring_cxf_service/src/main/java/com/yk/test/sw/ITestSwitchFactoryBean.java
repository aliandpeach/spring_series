package com.yk.test.sw;

import org.springframework.beans.factory.FactoryBean;

public class ITestSwitchFactoryBean implements FactoryBean<ITestSwitchService>
{
    private String key;

    public void setKey(String key)
    {
        this.key = key;
    }

    @Override
    public ITestSwitchService getObject() throws Exception
    {
        if (null == key)
        {
            return new TestSwitchService1();
        }
        if ("ONE".equals(key))
        {
            return new TestSwitchService1();
        }
        if ("TWO".equals(key))
        {
            return new TestSwitchService1();
        }
        return new TestSwitchService1();
    }

    @Override
    public Class<?> getObjectType()
    {
        return ITestSwitchService.class;
    }
}