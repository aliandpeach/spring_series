package com.yk.bitcoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
public class KeyGeneratorRunner implements Runnable
{
    private Logger logger = LoggerFactory.getLogger("generator");

    private Logger record = LoggerFactory.getLogger("record");

    @Autowired
    private KeyGenerator generator;

    @Autowired
    private Cache cache;

    private SecureRandom random = new SecureRandom();

    @Override
    public void run()
    {
        if (!cache.run)
        {
            logger.info("KeyGeneratorRunner stopped!");
            return;
        }
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            logger.error("private key generator sleep error", e);
        }
        while (KeyCache.keyQueue.size() > 0)
        {
            synchronized (KeyCache.lock)
            {
                try
                {
                    KeyCache.lock.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
//      Connection conn = null;
//      PreparedStatement ps = null;
        try
        {
            for (int i = 0; i < 1000; i++)
            {
                byte[] keyBytes = new byte[32];
                random.nextBytes(keyBytes);


                String prikey = generator.keyGen(keyBytes, true);
                String pubkey = generator.addressGen(keyBytes);
                record.info(prikey + ", " + pubkey);
                Map<String, String> keyAddr = new HashMap<>();
                keyAddr.put("privatekey", prikey);
                keyAddr.put("publickey", pubkey);
                KeyCache.keyQueue.offer(keyAddr);
//                conn = dataSource.getConnection();
//                conn.setAutoCommit(false);
//                ps = conn.prepareStatement("INSERT INTO bitcoin_key (`key`, `address`) values(?,?)");
//                ps.setString(1, prikey);
//                ps.setString(2, pubkey);
//                ps.addBatch();
            }
//            Map<String, String> map = new HashMap<>(Collections.singletonMap("privatekey", "L5coqwGu6jUj2ruiATz3idQfxwTQuNJpY3ry1WVqW6ai11CiZT2v"));
//            map.putAll(Collections.singletonMap("publickey", "39grqYvfEUFQSu1qqFRfTfU3CDBoQnPC8z"));
//            KeyCache.keyQueue.offer(map);
//            ps.executeBatch();
//            conn.commit();
        }
        catch (Exception e)
        {
            logger.error("private key generator keyGen error", e);
        }
        finally
        {
//            DruidConnection.close(conn, ps, null);
        }

        synchronized (KeyCache.lock)
        {
            KeyCache.lock.notifyAll();
        }
    }
}
