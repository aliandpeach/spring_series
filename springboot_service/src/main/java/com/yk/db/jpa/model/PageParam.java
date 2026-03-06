package com.yk.db.jpa.model;

import lombok.Data;

@Data
public class PageParam
{
    private int page;

    private int pageSize;

    // 获取起始位置
    public int getStart()
    {
        return (page - 1) * pageSize;
    }

    public int getCurrent()
    {
        return page <= 0 ? 0 : page - 1;
    }
}
