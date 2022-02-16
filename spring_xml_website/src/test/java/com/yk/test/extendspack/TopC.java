package com.yk.test.extendspack;

import java.util.HashMap;
import java.util.Map;

public class TopC {
    private Map<Long, String> topMaps = new HashMap<>();

    {
        topMaps.put(System.currentTimeMillis(), System.currentTimeMillis() + "");
    }
}
