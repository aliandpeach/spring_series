package com.yk.bitcoin;

import java.util.HashMap;
import java.util.Map;

public class CacheTest
{
    private final Map<String, ParseParams> RULES = new HashMap<>();

    public synchronized ParseParams get(String roleId)
    {
        if (null == RULES.get(roleId))
        {
            ParseParams parseParams = Utils.getRule();
            RULES.put(roleId, parseParams);
            return parseParams;
        }
        return RULES.get(roleId);
    }

    public synchronized ParseParams cover(String ruleId)
    {
        ParseParams parseParams = get(ruleId);
        RULES.remove(ruleId);
        RULES.put(ruleId, parseParams);
        return parseParams;
    }
}
