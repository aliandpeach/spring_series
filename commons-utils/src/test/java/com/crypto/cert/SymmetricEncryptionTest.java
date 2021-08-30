package com.crypto.cert;

import com.yk.crypto.SymmetricEncryption;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
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
        symmetricEncryption = SymmetricEncryption.getInstance(new String(key).getBytes(), new String(iv).getBytes());
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
}
