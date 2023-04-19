package com.yk.bitcoin;

import cn.hutool.core.util.HexUtil;
import com.yk.crypto.Base58;
import com.yk.crypto.BinHexSHAUtil;
import com.yk.crypto.Sha256Hash;
import com.yk.crypto.Utils;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * GenKeyTest
 */

public class GenKeyTest
{
    @Before
    public void before()
    {
        System.setProperty("log.home", System.getProperty("user.dir"));
    }
    
    @Test
    public void main() throws InterruptedException
    {
        String[] SECURITY_LEVEL_ARRAY = new String[]{"GONGKAI", "NEIBU", "MIMI", "JIMI", "JUEMI"};
        int index = Arrays.asList(SECURITY_LEVEL_ARRAY).indexOf("JUEMI");
        String[] securityLevelAryMatch = Arrays.copyOfRange(SECURITY_LEVEL_ARRAY, index + 1, SECURITY_LEVEL_ARRAY.length);

        String auth = "Basic ";

        String t = auth.substring(auth.indexOf("Basic ") + 6);

        System.out.println(Base64.getEncoder().encodeToString("secadmin".getBytes(StandardCharsets.UTF_8)));

        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        String id1 = HexUtil.encodeHexStr(bytes).intern();
        System.out.println(id1);
        new SecureRandom().nextBytes(bytes);

        Thread th1 = new Thread(() ->
        {
            synchronized (id1)
            {
                try
                {
                    id1.wait();
                    System.out.println("id1 notified");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        Thread th2 = new Thread(() ->
        {
            String id2 = new String("f3c1a338bcc244e284d3740517a0aaae");
            final String lock = id2.intern();
            synchronized (lock)
            {
                try
                {
                    lock.wait();
                    System.out.println("id2 notified");
                }
                catch (InterruptedException e)
                {
                }
            }
        });
        
        th1.start();
        
        th2.start();
        
        Thread.sleep(2000);

        Thread th3 = new Thread(() ->
        {
            String id2 = new String("f3c1a338bcc244e284d3740517a0aaae");
            final String lock = id2.intern();
            synchronized (lock)
            {
                lock.notifyAll();
                System.out.println("id2 notifyAll");
            }
        });
        th3.start();
        th2.join();

        th1.interrupt();// interrupt()会导致 th1线程抛出异常
        th1.join();
    }

    @Test
    public void randomGenKey()
    {
        KeyGenerator keyGenerator = new KeyGenerator();
        StringBuilder randomBinaryKeyString = new StringBuilder();
        for (int i = 0; i < 256; i++)
        {
            randomBinaryKeyString.append((new Random().nextInt(100) + 1) % 2 == 0 ? "0" : "1");
        }
        System.out.println(randomBinaryKeyString);
        // 二进制字符串转换为byte[]
        byte[] key = BinHexSHAUtil.binaryString2bytes(randomBinaryKeyString.toString());

        // byte[]转换为16进制字符串
        String hexKey = HexUtil.encodeHexStr(key);
        // 16进制字符串或者二进制字符串或者byte[]转换为 BigInteger,  _k == _k2 == _k3;
        BigInteger _k = new BigInteger(hexKey, 16);
        BigInteger _k2 = new BigInteger(randomBinaryKeyString.toString(), 2);
        BigInteger _k3 = new BigInteger(1, key);

        // BigInteger在转换为byte[], temp会有符号问题, 可以用BinHexSHAUtil.to解决或者使用Utils.bigIntegerToBytes
        byte[] temp = _k.toByteArray();
        byte[] ary = new byte[32];
        BinHexSHAUtil.to(temp, ary);
        byte[] ary2 = Utils.bigIntegerToBytes(_k, 32);
        boolean is = Arrays.equals(ary, ary2);
        is = Arrays.equals(ary, key);
        String _hexKey = HexUtil.encodeHexStr(ary2);

        System.out.println(hexKey);
        System.out.println(_hexKey);
        String prk = keyGenerator.keyGen(key, true);
        String pub = keyGenerator.addressGen(key, true);
        System.out.println(prk);
        System.out.println(pub);
    }
    
    @Test
    public void genKey()
    {
        BigInteger zero = new BigInteger("0", 16);
        BigInteger one = new BigInteger("1", 16);
        BigInteger max = new BigInteger("10", 16);

        KeyGenerator keyGenerator = new KeyGenerator();
        for (BigInteger i = zero; i.compareTo(max) < 0; i = i.add(one))
        {
            byte[] barray = i.toByteArray();
            String hex = HexUtil.encodeHexStr(barray);
            byte[] key = new byte[32];
            byte[] f = barray;
            System.arraycopy(f, 0, key, key.length - f.length, f.length);
            System.out.println(BinHexSHAUtil.bytes2BinaryString(key));
            try
            {
                String prk = keyGenerator.keyGen(key, true);
                String puk = keyGenerator.addressGen(key, true);
                System.out.println("key= " + prk + ", address= " + puk);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void genKey2() throws Exception
    {
        KeyGenerator keyGenerator = new KeyGenerator();
        byte[] bytes = new byte[32];
        new SecureRandom(Sha256Hash.hash("0".getBytes(StandardCharsets.UTF_8))).nextBytes(bytes);
        String pri = keyGenerator.keyGen(bytes, false);
        String pri2 = keyGenerator.keyGen(bytes, true);
        // 5JsqvMN5CjwpM36wo8RbR2rM1GommssZubfb5KSJf815uLqG511
        System.out.println(pri);
        System.out.println(pri2);
        System.out.println(keyGenerator.addressGen(bytes, true));
    }
    
    @Test
    public void genKey3()
    {
        byte[] bytes = "0".getBytes();
        byte[] privateKey = Utils.bigIntegerToBytes(new BigInteger(bytes), 32);
        String binaryString = BinHexSHAUtil.bytes2BinaryString(privateKey);
        KeyGenerator keyGenerator = new KeyGenerator();
        String pri = keyGenerator.keyGen(privateKey, true);
        System.out.println(pri);
        String pub = keyGenerator.addressGen(privateKey, true);
        System.out.println(pub);
    }

    @Test
    public void genKey4()
    {
        byte[] keys = Utils.bigIntegerToBytes(new BigInteger("0"), 32);
        KeyGenerator keyGenerator = new KeyGenerator();
        String pub = keyGenerator.addressBy(keys);
        System.out.println(pub);
    }
    
    public static byte[] getBytes(float data)
    {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }
    
    public static byte[] getBytes(int data)
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }
    
    public static byte[] float2byte(float f)
    {
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);
        
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }
        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i)
        {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }
    
    @Test
    public void convertByBase58Key()
    {
        // KykVjGz5fK1yUWZneEfvtwkvPvAywWT7kGfay6bE1GeGbcz6NCG3
        // L17HQpQ3VHmjJ7bkGv1LGp2CyrD3EtBiH7JbujMBhWpKe75Dz1q5
        String keyString = "L2mXzNnQGeZV1eed1N9UkAziUU6RKy27qGG28CSq6uCyLBRVUED6";
        byte[] keyWtihChecksumBytes = Base58.decode(keyString);
        byte[] compressedKey = new byte[keyWtihChecksumBytes.length - 4];
        ByteBuffer byteBuffer = ByteBuffer.allocate(keyWtihChecksumBytes.length);
        byteBuffer.put(keyWtihChecksumBytes);
        byteBuffer.flip();
        byteBuffer.get(compressedKey, 0, compressedKey.length);
        
        byte[] key = new byte[compressedKey.length == 34 ? compressedKey.length - 2 : compressedKey.length - 1];
        System.arraycopy(compressedKey, 1, key, 0, key.length);
        byte[] single0x80 = new byte[]{(byte) 0x80};
        byte[] single0x01 = new byte[]{(byte) 0x01};
        String binaryStringKey = BinHexSHAUtil.bytes2BinaryString(key);
        System.out.println(binaryStringKey);
        String u = new BigInteger(binaryStringKey, 2).toString(16).toUpperCase();
        System.out.println(u);
        u = new BigInteger(1, key).toString(16).toUpperCase();
        System.out.println(u);
    }
    
    @Test
    public void convertByBinaryString() throws Exception
    {
        KeyGenerator generator = new KeyGenerator();
        // 0100101110000011000010111011010101001001011011110110111101110001001010010000100100010111110101000110010101100001111011001011000101010100010000000001101111001101101000100001110111011000000100100101110111000101000101111111001100111001000110101010100100100000
        String binaryString = "0100101110000011000010111011010101001001011011110110111101110001001010010000100100010111110101000110010101100001111011001011000101010100010000000001101111001101101000100001110111011000000100100101110111000101000101111111001100111001000110101010100100100000";
        byte[] key = BinHexSHAUtil.binaryString2bytes(binaryString);
        String compressed = generator.keyGen(key, true);
        String uncompressed = generator.keyGen(key, false);
        System.out.println(compressed);
        System.out.println(uncompressed);
        
        String addr = generator.addressGen(key, true);
        System.out.println(addr);
    }

    @Test
    public void createTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException
    {
        KeyGenerator generator = new KeyGenerator();
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[32];
        random.nextBytes(key);

        String keyStr = generator.keyGen(key, true);
        String keyStr2 = generator.keyGen(key, false);
        System.out.println("compressed : " + keyStr);
        System.out.println("uncompressed : " + keyStr2);

        System.out.println("compressed : " + generator.addressGen(key, true));
        System.out.println("uncompressed : " + generator.addressGen(key, false));

        byte[] _key = generator.convertKeyByBase58Key(keyStr);
        System.out.println();
    }
}
