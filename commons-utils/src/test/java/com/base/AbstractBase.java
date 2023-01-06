package com.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/09/09 14:16:02
 */
public abstract class AbstractBase
{
    protected static Map<String, Info> cache = new ConcurrentHashMap<>();
}
