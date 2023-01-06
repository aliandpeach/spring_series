package com.base;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/09/09 14:18:26
 */
public class InfoBase extends AbstractBase
{
    public void insert(Info _info)
    {
        cache.put(_info.getId(), _info);
    }
}
