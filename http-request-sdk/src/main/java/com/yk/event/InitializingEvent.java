package com.yk.event;

import com.yk.core.CommonInfo;

import java.util.EventObject;

/**
 * 初始化事件控制
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/25 10:13
 */
public class InitializingEvent extends EventObject
{
    private String type = "";

    private CommonInfo info;

    public InitializingEvent(CommonInfo CommonInfo)
    {
        super(CommonInfo);
        this.info = CommonInfo;
    }

    public InitializingEvent of(String type)
    {
        this.type = type;
        return this;
    }

    public String getType()
    {
        return type;
    }

    public CommonInfo getInfo()
    {
        return info;
    }
}
