package com.yk.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.KeyCache;
import com.yk.bitcoin.KeyGenerator;
import com.yk.bitcoin.KeyGeneratorWatchedService;
import com.yk.bitcoin.model.Task;
import com.yk.bitcoin.model.TaskForm;
import com.yk.bitcoin.produce.AbstractKeyGenerator;
import com.yk.crypto.Sha256Hash;
import com.yk.demo.model.BlockchainModel;
import com.yk.demo.model.GroupInterface;
import com.yk.exception.BlockchainException;
import com.yk.httprequest.HttpClientUtil;
import com.yk.util.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/block/chain")
@EnableConfigurationProperties(BlockchainProperties.class)
public class BlockchainController
{
    private static final Logger logger = LoggerFactory.getLogger(BlockchainController.class);

    @Autowired
    private BlockchainProperties blockchainProperties;

    @Autowired
    private KeyGenerator keyGenerator;

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Autowired
    private KeyGeneratorWatchedService keyGeneratorWatchedService;

    @RequestMapping(value = "/{status}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, String> opt(@PathVariable("status") String status, @RequestBody @Validated TaskForm body)
    {
        boolean started = KeyCache.TASK_INFO.computeIfAbsent(AbstractKeyGenerator.getKeyGeneratorName(body.getType()), Task::new).getState() == 1;
        if (started)
        {
            throw new BlockchainException(0, "已经启动");
        }
        BigInteger min = new BigInteger(body.getMin(), 16);
        BigInteger max = new BigInteger(body.getMin(), 16);
        keyGeneratorWatchedService.main(new Task(AbstractKeyGenerator.getKeyGeneratorName(body.getType()), min, max));
        return new HashMap<>(Collections.singletonMap("status", "started"));
    }

    @RequestMapping(value = "/the/min", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> theMin()
    {
        Map<String, Object> result = new HashMap<>();
        return result;
    }

    @RequestMapping(value = "/the/range", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> theRange()
    {
        Map<String, Object> result = new HashMap<>();

//        BigInteger range = max.subtract(min);
//        result.put("range", range.toString(16).toUpperCase());
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
        result.put("range", range.toString(10));
        return result;
    }

    @RequestMapping(value = "/the/insert", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> insert(@RequestBody Map<String, String> param)
    {
        Map<String, Object> result = new HashMap<>();
        String key = param.get("key");
        if (null == key || key.trim().length() == 0)
        {
            result.put("error", "key is null");
            return result;
        }

        try
        {
            byte[] biKey = keyGenerator.convertKeyByBase58Key(key);
            BigInteger zero = new BigInteger("0", 16);
            BigInteger max = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140", 16);
            if (null == biKey
                    || new BigInteger(1, biKey).compareTo(zero) < 0
                    || new BigInteger(1, biKey).compareTo(max) > 0)
            {
                result.put("error", "key is incorrect");
                return result;
            }
            String addr = keyGenerator.addressGen(biKey);
            String keystring = keyGenerator.keyGen(biKey, true);
            Map<String, String> key2Addr = new HashMap<>();
            key2Addr.put("privatekey", keystring);
            key2Addr.put("publickey", addr);
//            KeyCache.keyQueue.offer(key2Addr);
            result.put("success", "success");
            return result;
        }
        catch (IllegalArgumentException e)
        {
            logger.error("the-insert error", e);
            result.put("error", "key is null");
            return result;
        }
    }

    @RequestMapping(value = "/the/query", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Map<String, Long>> query(@RequestParam("addr") String addr)
    {
        Map<String, Map<String, Long>> result = new HashMap<>();
        if (null == addr || addr.trim().length() == 0)
        {
            return result;
        }


        Map<String, String> params = new HashMap<>();
        params.put("active", addr);

        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "keep-alive");
        try
        {
            result = httpClientUtil.get(blockchainProperties.getApiHost()
                    , headers, params, new TypeReference<Map<String, Map<String, Long>>>()
                    {
                    }, 3);
        }
        catch (Exception e)
        {
            logger.error("the-query error", e);
        }
        return result;
    }

    /**
     * 脑钱包
     */
    @RequestMapping(value = "/the/brain", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> brain(@RequestBody @Validated(GroupInterface.ITheBrain.class) BlockchainModel blockchainModel)
    {
        Map<String, String> result = new HashMap<>();
        byte[] bytes = blockchainModel.getPhrase().getBytes();
        byte[] privateKey = Sha256Hash.hash(bytes);

        String pri = keyGenerator.keyGen(privateKey, true);
        String pub = keyGenerator.addressGen(privateKey);
        result.put("privateKey", pri);
        result.put("publicKey", pub);
        return result;
    }

    /**
     * 私钥详情
     */
    @RequestMapping(value = "/the/detail", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> detail(@RequestBody @Validated(GroupInterface.ITheDetail.class) BlockchainModel blockchainModel)
    {
        String key = blockchainModel.getKey();
        logger.info(key);
        Map<String, String> result = new HashMap<>();
        byte[] privateKey = keyGenerator.convertKeyByBase58Key(key);
        if (null == privateKey)
        {
            return result;
        }
        String pub = keyGenerator.addressGen(privateKey);
        result.put("publicKey", pub);
        return result;
    }
}