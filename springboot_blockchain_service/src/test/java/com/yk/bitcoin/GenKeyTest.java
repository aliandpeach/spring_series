package com.yk.bitcoin;

import cn.hutool.core.util.HexUtil;
import com.yk.crypto.Base58;
import com.yk.crypto.BinHexSHAUtil;
import com.yk.crypto.Utils;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
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
    public void genKey()
    {
        KeyGenerator keyGenerator = new KeyGenerator();
        StringBuilder max256BinaryString = new StringBuilder();
        for (int i = 0; i < 256; i++)
        {
            max256BinaryString.append("1");
        }
        byte[] max32bytes = BinHexSHAUtil.binaryString2bytes(max256BinaryString.toString());
        // ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
        String maxHex = HexUtil.encodeHexStr(max32bytes);

        BigInteger zero = new BigInteger("0", 16);
        BigInteger one = new BigInteger("1", 16);
        BigInteger max = new BigInteger(maxHex, 16);
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
                String puk = keyGenerator.addressGen(key);
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
        new Random().nextBytes(bytes);
        String pri = keyGenerator.keyGen(bytes, false);
        String pri2 = keyGenerator.keyGen(bytes, true);
        // 5JsqvMN5CjwpM36wo8RbR2rM1GommssZubfb5KSJf815uLqG511
        System.out.println(pri);
        System.out.println(pri2);
    }

    @Test
    public void genKey3() throws Exception
    {
        Random random = new Random();
        for (int i = 0; i < 1; i++)
        {
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            String binaryString = BinHexSHAUtil.bytes2BinaryString(bytes);
            String hex = HexUtil.encodeHexStr(bytes);
            System.out.println(hex);
        }

        byte[] bytes = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF".getBytes();
        String hex_ = BinHexSHAUtil.byteArrayToHex(bytes);
        hex_ = "0";
        byte[] privateKey = Utils.bigIntegerToBytes(new BigInteger(hex_, 16), 32);
        String binaryString = BinHexSHAUtil.bytes2BinaryString(privateKey);
        // System.out.println(binaryString); 1FYMZEHnszCHKTBdFZ2DLrUuk3dGwYKQxh|1BgGZ9tcN4rm9KBzDn7KprQz87SZ26SAMH
        KeyGenerator keyGenerator = new KeyGenerator();
        String pri = keyGenerator.keyGen(privateKey, true);
        System.out.println(pri);
        String pub = keyGenerator.addressGen(privateKey);
        System.out.println(pub);
    }

    @Test
    public void convertByBase58Key()
    {
        // KykVjGz5fK1yUWZneEfvtwkvPvAywWT7kGfay6bE1GeGbcz6NCG3
        String keyString = "KykVjGz5fK1yUWZneEfvtwkvPvAywWT7kGfay6bE1GeGbcz6NCG3";
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

        String addr = generator.addressGen(key);
        System.out.println(addr);
    }
}
