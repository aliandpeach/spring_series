package com.crypto.cert;

import com.yk.crypto.SymmetricEncryption;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * 对称加密
 */
public class SymmetricEncryptionTest
{
    private SymmetricEncryption symmetricEncryption;
    
    @Before
    public void before()
    {
        char [] key = new char[]{'t', 'e', 's', 't', '_', 'd', 'b', 's', '_', 'k', 'e', 'y', '_', 'h', 'h', 'g', 'f', 'd', 'o', 'y', 't', 'y', 't', '#', 'd', 's', 'd', '.', '1', '2', '#', '2', 'u', 'i'};
        char [] iv = new char[]{'r', 't', 'e', 'c', '$', 'j', 'b', 's', '_', 'k', 'e', 'y', '_', 'h', 'h', 'g', 'f', 'd', 'o', 'y', 't', 'y', 't', '#', 'd', 's', 'd', '.', '1', '2', '#', '2', 'u', 'i'};
        symmetricEncryption = SymmetricEncryption.getInstance(new String(key).getBytes());
    }
    
    @Test
    public void des() throws Exception
    {
        String testString = "测试字符串。。。￥￥￥！@@@";
        
        ByteBuffer bufferE = symmetricEncryption.desEncrypt(testString.getBytes(StandardCharsets.UTF_8));
        ByteBuffer bufferD = symmetricEncryption.desDecrypt(bufferE.array());
        String ret = new String(bufferD.array(), StandardCharsets.UTF_8);
        Assert.assertEquals(ret, testString);


        // =========desEncrypt 方法的加密 等同于下面的加密=========
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(new SecureRandom("AAA".getBytes()));
        SecretKey key = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        byte[] salt = new byte[8];
        new SecureRandom("salt_bbb".getBytes()).nextBytes(salt);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
        byte[] encryptBytes = cipher.doFinal(testString.getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(Arrays.equals(bufferE.array(), encryptBytes));
    }
    
    @Test
    public void aes256() throws Exception
    {
        String testString = "测试字符串。。。￥￥￥！@@@";
        
        ByteBuffer bufferE = symmetricEncryption.aesEncrypt(testString.getBytes(StandardCharsets.UTF_8));
        ByteBuffer bufferD = symmetricEncryption.aesDecrypt(bufferE.array());
        String ret = new String(bufferD.array(), StandardCharsets.UTF_8);
        Assert.assertEquals(ret, testString);
        
        
        ByteBuffer bufferE2 = symmetricEncryption.aesEncrypt2(testString.getBytes(StandardCharsets.UTF_8));
        ByteBuffer bufferD2 = symmetricEncryption.aesDecrypt2(bufferE2.array());
        String ret2 = new String(bufferD2.array(), StandardCharsets.UTF_8);
        Assert.assertEquals(testString, ret2);

        // 生成的SecretKeySpec和 IvParameterSpec 值如果相同, 这里aesEncrypt和aesEncrypt2的加密结果才会相同
        Assert.assertTrue(Arrays.equals(bufferE.array(), bufferE2.array()));
    }
    
    @Test
    public void des3() throws Exception
    {
        String testString = "测试字符串。。。￥￥￥！@@@";
        
        ByteBuffer bufferE = symmetricEncryption.des3Encrypt(testString.getBytes(StandardCharsets.UTF_8));
        ByteBuffer bufferD = symmetricEncryption.des3Decrypt(bufferE.array());
        String ret = new String(bufferD.array(), StandardCharsets.UTF_8);
        Assert.assertEquals(ret, testString);
    }
    
    @Test
    public void pbe() throws Exception
    {
        String testString = "测试字符串。。。￥￥￥！@@@";
        
        ByteBuffer bufferE = symmetricEncryption.pbeEncrypt(testString.getBytes(StandardCharsets.UTF_8));
        ByteBuffer bufferD = symmetricEncryption.pbeDecrypt(bufferE.array());
        String ret = new String(bufferD.array(), StandardCharsets.UTF_8);
        Assert.assertEquals(ret, testString);
    }

    @Test
    public void aesAes256Salt() throws Exception
    {
        String testString = "测试字符串。。。￥￥￥！@@@";
        ByteBuffer bufferE = symmetricEncryption.aesEncryptWithSalt(testString.getBytes(StandardCharsets.UTF_8));
        ByteBuffer bufferD = symmetricEncryption.aesDecryptWithSalt(bufferE.array());
        String ret = new String(bufferD.array(), StandardCharsets.UTF_8);
        Assert.assertEquals(ret, testString);
    }

    @Test
    public void aesAes256Salt2() throws Exception
    {
        String testString = "测试字符串。。。￥￥￥！@@@";
        ByteBuffer bufferE = symmetricEncryption.aesEncryptWithSalt2(testString.getBytes(StandardCharsets.UTF_8));
        ByteBuffer bufferD = symmetricEncryption.aesDecryptWithSalt2(bufferE.array());
        String ret = new String(bufferD.array(), StandardCharsets.UTF_8);
        Assert.assertEquals(ret, testString);
    }

    /**
     * 秘钥派生函数(就是任意一个长度的字段, 通过PBE算法生成我们想要的固定256 bit的AES秘钥)
     */
    @Test
    public void testPBE() throws InvalidKeySpecException, NoSuchAlgorithmException
    {
        String password = "shared-secret242&(&*^ffkfdfhueowue^%%&*jhojfofjfdfde222222222222222222fffffffffgggggggggggg%^*(%$ll;;;klkjjhhgftt";
        byte[] salt = new byte[8]; // 固定或随机生成的盐值
        int iterations = 10000;
        int keyLength = 256;

        // 生成 AES 密钥
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] aesKey1 = factory.generateSecret(spec).getEncoded();

        KeySpec spec2 = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] aesKey2 = factory2.generateSecret(spec2).getEncoded();
        System.out.println(Arrays.equals(aesKey1, aesKey2));
    }

    @Test
    public void aesKeyTest() throws Exception
    {
        byte[] source = "testtesttesttest".getBytes(StandardCharsets.UTF_8);

        SecureRandom randomAESKey = new SecureRandom();
        byte[] aesKey = new byte[32]; // AESCipher -> AESCrypt.isKeySizeValid 对aesKey进行校验只能是16/24/32字节
        randomAESKey.nextBytes(aesKey);// 生成AES的私钥key 256-bit AES key

        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[16]; // IV length: must be 16 bytes long
        randomIV.nextBytes(ivBytes);

        SecretKey key = new SecretKeySpec(aesKey, 0, aesKey.length, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));

//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // AESCipher -> AESCrypt.isKeySizeValid 对aesKey进行校验只能是16/24/32字节
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding"); // NoPadding不填充 要求被加密的明文必须是16字节的倍数
//        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptBytes = cipher.doFinal(source);

        byte[] result = new byte[ivBytes.length + encryptBytes.length];
        System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
        System.arraycopy(encryptBytes, 0, result, ivBytes.length, encryptBytes.length);
        ByteBuffer r1 = ByteBuffer.wrap(result);

        ByteBuffer r2 = ByteBuffer.allocate(result.length);
        System.out.println(Arrays.toString(r2.array()));

        System.out.println(Arrays.toString(r1.array()));
    }
}
