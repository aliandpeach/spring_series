package com.crypto;

import cn.hutool.core.util.HexUtil;
import com.yk.crypto.BinHexSHAUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * BinHexSHAUtilTest
 */

public class BinHexSHAUtilTest
{
    @Test
    public void testString2Bytes()
    {
        String in = "111111";
        byte[] bbs = BinHexSHAUtil.binaryString2bytes(in);
        String hex = HexUtil.encodeHexStr(bbs);
        BigInteger bih = new BigInteger(hex, 16);
        String val1 = bih.toString(10);
        System.out.println(val1);
        
        BigInteger bi = new BigInteger(in, 2);
        String val = bi.toString(10);
        System.out.println(val);
    }
    
    @Test
    public void testDigest()
    {
        // github连接过程中的回显的指纹信息，就是本地的publicKey进行Base64解密后，再执行MessageDigest("SHA-256") 后转16进制字符串
        String path = "F:\\Download\\vcredist_x64.exe";
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            FileInputStream input = new FileInputStream(new File(path));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            byte buff[] = new byte[1024];
            while ((len = input.read(buff)) != -1)
            {
                // 严格注意这里的len, 如果写成buff.length那么当最后一次读取小于1024的时候，就会出错
                // 原因就是byte[1024] 没有填充的位置默认是0，sha-256的计算会包括哪些0
                messageDigest.update(buff, 0, len);
                bos.write(buff, 0, len);
            }
            byte[] clone = new byte[bos.toByteArray().length];
            System.arraycopy(bos.toByteArray(), 0, clone, 0, clone.length);
            bos.flush();
            bos.close();
            input.close();

            /*BigInteger bigIntFile = new BigInteger(1, clone);
            String fileBinaryText = bigIntFile.toString(2);
            System.out.println(fileBinaryText);//文件的byte[]对象 转换为2进制字符串*/
            
            
            byte[] rtn = messageDigest.digest();
            System.out.println("byte2Hex 转换byte[]为16进制 : " + BinHexSHAUtil.byte2Hex(rtn));
            System.out.println("把文件一次性读入后 计算的hash 再转换为16进制 : " + BinHexSHAUtil.byte2Hex(MessageDigest.getInstance("SHA-256").digest(clone)));
            System.out.println("把文件一次性读入后 计算的hash 再转换为16进制 : " + DigestUtils.sha256Hex(clone));
            
            //1代表绝对值, 该方法可以把byte[] 转换为16进制字符串
            BigInteger bigInt = new BigInteger(1, rtn);
            String ahex = bigInt.toString(16);
            System.out.println("BigInteger 转换byte[]为16进制 : " + ahex);//转换为16进制
            /*MessageDigest messageDigest1 = MessageDigest.getInstance("SHA1");
            messageDigest1.update(clone);
            byte[] rtn1 = messageDigest.digest();
            BigInteger bigInt1 = new BigInteger(1, rtn1);//1代表绝对值
            System.out.println(bigInt1.toString(16));//转换为16进制*/
            
            //把16进制字符串转为byte[]
            BigInteger bigInt2 = new BigInteger(ahex, 16);
            byte[] ary2 = bigInt2.toByteArray();
            
            BigInteger bigInt22 = new BigInteger(1, ary2);//1代表绝对值
            String ahexx = bigInt22.toString(16);
            System.out.println("通过每次updat byte[1024] 计算的hex hash 字符串，再通过bigInteger 转成byte[] 再转成hex hash : " + ahexx);
            
            // certutil -hashfile [fille] SHA256
        }
        catch (NoSuchAlgorithmException | IOException e)
        {
            e.printStackTrace();
        }
        
        int _num = 13;
        System.out.println("十进制转二进制：" + Integer.toBinaryString(_num));
        System.out.println("十进制转八进制：" + Integer.toOctalString(_num));
        System.out.println("十进制转十六进制：" + Integer.toHexString(_num));
        System.out.println("十进制转二进制：" + Integer.toString(_num, 2));
        System.out.println("十进制转八进制：" + Integer.toString(_num, 8));
        System.out.println("十进制转十六进制：" + Integer.toString(_num, 16));
        
        
        System.out.println("十六进制转换十进制：" + new BigInteger("fffff", 16).intValue());
        System.out.println("十六进制转换十进制：" + BinHexSHAUtil.change("fffff", 16, 10));
        
        String _onum = "1410";
        System.out.println("八进制转换十进制：" + Integer.parseInt(_onum, 8));
        System.out.println("八进制转换十进制：" + Integer.valueOf(_onum, 8));
        
        String _hnum = "fffff";
        System.out.println("十六进制转换十进制：" + Integer.parseInt(_hnum, 16));
        System.out.println("十六进制转换十进制：" + Integer.valueOf(_hnum, 16));
        
        String _bnum = "100110";
        System.out.println("二进制转换十进制：" + Integer.parseInt(_bnum, 2));
        System.out.println("二进制转换十进制：" + Integer.valueOf(_bnum, 2));
    }
}