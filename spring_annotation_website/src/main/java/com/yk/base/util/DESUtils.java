package com.yk.base.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 和 DES 默认的模式为AES/ECB/PKCS5Padding 和 DES/ECB/PKCS5Padding
 * Cipher.getInstance时可以简写为AES和DES
 *
 * AES/CBC/PKCS5Padding 和 DES/CBC/PKCS5Padding 需要在Cipher.getInstance时需要加入参数new IvParameterSpec(salt)
 *
 * AES :    SecretKey key = new SecretKeySpec(passwd, "AES");
 * DES : 1. SecretKey key = new SecretKeySpec(passwd, "DES");  // passwd 的长度必须是byte[8]
 *
 *       2. DESKeySpec dks = new DESKeySpec(passwd);           // 和上面的DES不同，这里的passwd随意
 *          SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
 *          SecretKey key = keyFactory.generateSecret(dks);
 *
 * KeyGenerator 生成的AES/DES 的 SecretKey 指定的 passwd 来自于new SecureRandom("MY_KEY".getBytes()).nextBytes(passwd)
 *
 * 补充： JAVA 采用  AES/CBC/PKCS7Padding  和 DES/CBC/PKCS5Padding  模式
 *
 * C++和java的两个变数：
 * 1）分块的方式。加密是逐块进行的。分块方法有：CBC、ECB、CFB……
 * 2）padding的方式。当数据的位数不及块的大小时，需要填充。填充方式有：NoPadding、PKCS5Padding……
 *
 * 参考： Java AES算法和openssl配对 https://my.oschina.net/gesuper/blog/174035
 *       Java和C/C++进行DES/AES密文传输 https://blog.csdn.net/weiyuefei/article/details/72741729?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-0&spm=1001.2101.3001.4242
 */
public class DESUtils {
    private static Key key;
    
    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(new SecureRandom("MY_KEY".getBytes()));
            key = keyGenerator.generateKey();
            keyGenerator = null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encryptString(String pwd) {
        try {
            byte[] pwdBytes = pwd.getBytes(StandardCharsets.UTF_8);
    
            byte []passwd = new byte[8];
            new SecureRandom("MY_KEY".getBytes()).nextBytes(passwd);
    
            //IvParameterSpec ivParameterSpec = new IvParameterSpec(salt); // salt不用和passwd相同，指定其他随机数生成器也行
            DESKeySpec desKeySpec = new DESKeySpec(passwd);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            
            
            Cipher cipher = Cipher.getInstance("DES");
            // 这里无论使用secretKey 还key 结果都一样
            cipher.init(Cipher.ENCRYPT_MODE, secretKey); // DES/CBC/PKCS5Padding mode需要用到IvParameterSpec
            byte[] encryteStrBytes = cipher.doFinal(pwdBytes);
            return Base64.getEncoder().encodeToString(encryteStrBytes);
        } catch (Exception e) {
            throw new RuntimeException("decryptString error", e);
        }
    }

    public static String decryptString(String encryptStr) {
        try {
            byte[] strBytes = Base64.getDecoder().decode(encryptStr);
    
            byte []passwd = new byte[8];
            new SecureRandom("MY_KEY".getBytes()).nextBytes(passwd);
    
            //IvParameterSpec ivParameterSpec = new IvParameterSpec(salt); // salt不用和passwd相同，指定其他随机数生成器也行
            DESKeySpec desKeySpec = new DESKeySpec(passwd);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
    
            
            Cipher cipher = Cipher.getInstance("DES");
            // 这里无论使用secretKey 还key 结果都一样
            cipher.init(Cipher.DECRYPT_MODE, secretKey); // DES/CBC/PKCS5Padding mode需要用到IvParameterSpec
            byte[] decryteStrBytes = cipher.doFinal(strBytes);
            return new String(decryteStrBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("decryptString error", e);
        }
    }

    public static void main(String[] args) {
        String en = DESUtils.encryptString("root");
        System.out.println(en);
        String de = DESUtils.decryptString(en);
        System.out.println(de);
    }
}
