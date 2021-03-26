package com.yk.bitcoin;

import cn.hutool.core.util.HexUtil;
import com.yk.crypto.BinHexSHAUtil;
import org.junit.Test;

import java.math.BigInteger;

/**
 * GenKeyTest
 */

public class GenKeyTest
{
    @Test
    public void genKey()
    {
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
            KeyGenerator keyGenerator = new KeyGenerator();
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
}
