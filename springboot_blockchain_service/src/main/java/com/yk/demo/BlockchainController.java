package com.yk.demo;

import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.Cache;
import com.yk.bitcoin.KeyCache;
import com.yk.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.yk.bitcoin.KeyCache.LOCK;

@Controller
@RequestMapping("/block/chain")
@EnableConfigurationProperties(BlockchainProperties.class)
public class BlockchainController
{
    @Autowired
    private Cache cache;

    @Autowired
    private BlockchainProperties blockchainProperties;

    @RequestMapping(value = "/{status}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, String> opt(@PathVariable("status") String status, @RequestBody Map<String, String> body)
    {
        if (!blockchainProperties.isExecute())
        {
            return new HashMap<>(Collections.singletonMap("status", "un-execute"));
        }
        if (null == status || !status.equalsIgnoreCase("start"))
        {
            cache.setRun(false);
            return new HashMap<>(Collections.singletonMap("status", "stopped"));
        }
        String min = body.get("min");
        String max = body.get("max");
        synchronized (LOCK)
        {
            cache.setMin(new BigInteger(min, 16));
            cache.setMax(new BigInteger(max, 16));
        }
        cache.setRun(status.equalsIgnoreCase("start"));
        return new HashMap<>(Collections.singletonMap("status", "started"));
    }

    @RequestMapping(value = "/the/min", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> theMin()
    {
        Map<String, Object> result = new HashMap<>();
        synchronized (LOCK)
        {
            String min = cache.getMin().toString(16).toUpperCase();
            int size = KeyCache.keyQueue.size();
            result.put("min", min);
            result.put("size", size);
        }
        return result;
    }

    @RequestMapping(value = "/the/range", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> theRange()
    {
        Map<String, Object> result = new HashMap<>();
        synchronized (LOCK)
        {
            BigInteger min = cache.getMin();
            BigInteger max = cache.getMax();

            BigInteger range = max.subtract(min);
            result.put("range", range.toString(16).toUpperCase());
        }
        return result;
    }

    @RequestMapping(value = "/the/calc", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> theCalc(@RequestParam Map<String, String> param)
    {
        Map<String, Object> result = new HashMap<>();
        if (null == param)
        {
            result.put("error", "null info");
            return result;
        }
        if (null == param.get("start") || null == param.get("end"))
        {
            result.put("error", "null info");
            return result;
        }
        if (param.get("start").length() > 64 || param.get("end").length() > 64)
        {
            result.put("error", "length incorrect");
            return result;
        }

        boolean is = ConvertUtil.isHexString(param.get("start"))
                && ConvertUtil.isHexString(param.get("end"));
        if (!is)
        {
            result.put("error", "not hex");
            return result;
        }

        BigInteger min = new BigInteger(param.get("start"), 16);
        BigInteger max = new BigInteger(param.get("end"), 16);

        BigInteger range = max.subtract(min);
        result.put("range", range.longValue());
        return result;
    }
}
