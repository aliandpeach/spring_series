package com.yk.index.service.upload.strategy;


import com.yk.base.web.SpringContextHolder;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum UploadContext
{
    INSTANCE;

    private final Map<UploadModeEnum, SliceUploadStrategy> UPLOAD_STRATEGY_MAP = new ConcurrentHashMap<>();

    UploadContext()
    {
        Map<String, SliceUploadStrategy> map = SpringContextHolder.getBeansOfType(SliceUploadStrategy.class);
        if (MapUtils.isEmpty(map))
        {
            return;
        }
        for (Map.Entry<String, SliceUploadStrategy> entry : map.entrySet())
        {
            UploadMode uploadMode = entry.getValue().getClass().getAnnotation(UploadMode.class);
            UPLOAD_STRATEGY_MAP.put(uploadMode.mode(), entry.getValue());
        }
    }

    public SliceUploadStrategy getStrategyByType(UploadModeEnum mode)
    {
        return UPLOAD_STRATEGY_MAP.get(mode);
    }
}
