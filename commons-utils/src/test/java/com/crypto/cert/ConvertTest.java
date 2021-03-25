package com.crypto.cert;

import cn.hutool.core.io.resource.ClassPathResource;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;

/**
 * ConvertTest
 */

public class ConvertTest
{
    /**
     * JKS提取私钥 -> 格式为PKCS8
     */
    @Test
    public void convertKeystoreToPCKS12() throws Exception
    {
        KeyStore rsakeystore = KeyStore.getInstance("JKS");
        rsakeystore.load(new FileInputStream(new ClassPathResource("rsa.jks").getFile()), "storepasswd".toCharArray());
        PrivateKey key = (PrivateKey) rsakeystore.getKey("crazy", "keypasswd".toCharArray());
        byte[] encodedKey = key.getEncoded();
        // 导出base64转换后的私钥
        String base64KeyString = Base64.getEncoder().encodeToString(encodedKey);
        System.out.println();
        System.out.println(base64KeyString);
        System.out.println();
    
        // base64KeyString 是PKCS8不加密的格式 相当于转换成pkcs12后使用命令：
        // openssl pkcs12 -nocerts -nodes -in test.p12 -out test-key.pem  //该命令生成-----BEGIN PRIVATE KEY-----
    
        /**
         * 使用PKCS8去读取 encodedKey
         */
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        byte[] bb = pkcs8EncodedKeySpec.getEncoded();
        Assert.assertTrue(Arrays.equals(encodedKey, bb));//true
    
        PrivateKey key1 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
        Assert.assertTrue(key1.equals(key));//true
        
        
        Certificate certificate = rsakeystore.getCertificate("crazy");
        byte[] encodedCer = certificate.getEncoded();
        String base64CerString = Base64.getEncoder().encodeToString(encodedCer);
        System.out.println(base64CerString);
        System.out.println();
        
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(rsakeystore, "keypasswd".toCharArray());
        
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        KeyStore truststore = KeyStore.getInstance("JKS");
        truststore.load(null, null);
        truststore.setCertificateEntry("crazy", certificate);
        trustManagerFactory.init(truststore);
        
        
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
    }
    
    /**
     * JKS导出为PKCS12
     */
    public void exportToPKCS12() throws Exception
    {
        KeyStore rsakeystore = KeyStore.getInstance("JKS");
        rsakeystore.load(new FileInputStream(new ClassPathResource("rsa.jks").getFile()), "keystore库文件密码".toCharArray());
        
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);
        Enumeration<String> aliases = rsakeystore.aliases();
        while (aliases.hasMoreElements())
        {
            String alias = aliases.nextElement();
            PrivateKey k = (PrivateKey) rsakeystore.getKey(alias, "alias私钥密码".toCharArray());
            Certificate[] c = rsakeystore.getCertificateChain(alias);
            pkcs12.setKeyEntry(alias, k, "alias_entry_passwd".toCharArray(), c);
        }
        pkcs12.store(new FileOutputStream("D:\\test.p12"), "P12_store_file_passwd".toCharArray());
    }
    
    /**
     * openssl pkcs12 -nocerts -des -in test.p12 -out test-key.pem
     *
     * openssl pkcs12 -nocerts -in test.p12 -out test-key.pem
     *
     * 该命令生成-----BEGIN ENCRYPTED PRIVATE KEY----- 默认是des加密
     *
     * 也可以指定为 -aes256 -des3
     *
     * 由上述命令提取的私钥默认加密
     * <p>
     */
    @Test
    public void testLoadPKCS8Encrypt() throws Exception
    {
    }
    
    /**
     * des加密
     *
     * @throws Exception
     */
    @Test
    public void testDESEncrypt() throws Exception
    {
        //初始化盐 "MY_KEY" 作为种子，生成的盐不变
        SecureRandom random = new SecureRandom("MY_KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        
        IvParameterSpec iv = new IvParameterSpec(salt);
        // 根据源码 这里的字符串转换的byte[]无论多长， 只会取byte[8]
        DESKeySpec dks = new DESKeySpec("11111111".getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] tgtBytes = cipher.doFinal("测试测试测试".getBytes(StandardCharsets.UTF_8));
        System.out.println("encrypt: " + Base64.getEncoder().encodeToString(tgtBytes));
    }
    
    /**
     * des解密
     *
     * @throws Exception
     */
    @Test
    public void testDESDecrypt() throws Exception
    {
        byte[] src = Base64.getDecoder().decode("PfJTkKyelH4p+q9mth599g8vi5MJF0fJ");
        
        //初始化盐 "MY_KEY" 作为种子，生成的盐不变
        SecureRandom random = new SecureRandom("MY_KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        
        IvParameterSpec iv = new IvParameterSpec(salt);
        // 根据源码 这里的字符串转换的byte[]无论多长， 只会取byte[8]
        DESKeySpec dks = new DESKeySpec("11111111".getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] debytes = cipher.doFinal(src);
        System.out.println("decrypt: " + new String(debytes, StandardCharsets.UTF_8));
    }
    
    /**
     * 3des 加密
     */
    @Test
    public void test3DESEncrypt() throws Exception
    {
        //初始化盐 "MY_KEY" 作为种子，生成的盐不变
        SecureRandom random = new SecureRandom("MY_KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        
        IvParameterSpec iv = new IvParameterSpec(salt);
        // 根据源码 这里的字符串转换的byte[]无论多长， 只会取byte[24]
        DESedeKeySpec dks = new DESedeKeySpec("111111111111111111111111".getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] tgtBytes = cipher.doFinal("测试测试测试".getBytes(StandardCharsets.UTF_8));
        System.out.println("encrypt: " + Base64.getEncoder().encodeToString(tgtBytes));
    }
    
    /**
     * 3des解密
     */
    @Test
    public void test3DESDecrypt() throws Exception
    {
        byte[] src = Base64.getDecoder().decode("V8Ev798jmHjogR9Gy0W1gVZKv1yICaog");
        
        //初始化盐 "MY_KEY" 作为种子，生成的盐不变
        SecureRandom random = new SecureRandom("MY_KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        
        IvParameterSpec iv = new IvParameterSpec(salt);
        // 根据源码 这里的字符串转换的byte[]无论多长， 只会取byte[24]
        DESedeKeySpec dks = new DESedeKeySpec("111111111111111111111111".getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] debytes = cipher.doFinal(src);
        System.out.println("decrypt: " + new String(debytes, StandardCharsets.UTF_8));
    }
    
    /**
     *  
     * java6和bouncycastle支持的算法列表
     * 算法    密钥长度    密钥长度默认值    工作模式    填充方式    备注
     * PBEWithMD5AndDES    56    56    CBC    PKCS5Padding    java6实现
     * PBEWithMD5AndTripeDES    112、168    168    CBC    PKCS6Padding    java7实现
     * PBEWithSHA1AndDESede    112、168    168    CBC    PKCS7Padding    java8实现
     * PBEWithSHA1AndRC2_40    40至1024    128    CBC    PKCS8Padding    java9实现
     * <p>
     * PBEWithMD5AndDES    64    64    CBC    PKCS5Padding/PKCS7Padding/ISO10126Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithMD5AndRC2    128    128    CBC    PKCS5Padding/PKCS7Padding/ISO10127Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHA1AndDES    64    64    CBC    PKCS5Padding/PKCS7Padding/ISO10128Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHA1AndRC2    128    128    CBC    PKCS5Padding/PKCS7Padding/ISO10129Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHAAndIDEA-CBC    128    128    CBC    PKCS5Padding/PKCS7Padding/ISO10130Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHAAnd2-KeyTripleDES-CBC    128    128    CBC    PKCS5Padding/PKCS7Padding/ISO10131Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHAAnd3-KeyTripleDES-CBC    192    192    CBC    PKCS5Padding/PKCS7Padding/ISO10132Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHAAnd128BitRC2-CBC    128    128    CBC    PKCS5Padding/PKCS7Padding/ISO10133Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHAAnd40BitRC2-CBC    40    40    CBC    PKCS5Padding/PKCS7Padding/ISO10134Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHAAnd128BitRC4    128    128    CBC    PKCS5Padding/PKCS7Padding/ISO10135Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHAAnd40BitRC4    40    40    CBC    PKCS5Padding/PKCS7Padding/ISO10136Padding/ZeroBytePadding    BouncyCastle实现
     * PBEWithSHAAndTwofish-CBC    256    256    CBC    PKCS5Padding/PKCS7Padding/ISO10137Padding/ZeroBytePadding    BouncyCastle实现
     *       
     */
    @Test
    public void testPBEEncrypt() throws Exception
    {
        String src = "http://www.google.com";
        //初始化盐
        SecureRandom random = new SecureRandom("KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        
        //-----------口令及秘钥------------
        String password = "Admin@123890";
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
        SecretKey key = factory.generateSecret(pbeKeySpec);
        //------加密处理---------
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 1024);
        Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
        cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
        byte[] bytes = cipher.doFinal(src.getBytes());
        System.out.println("jdk pbe encrypt: " + Base64.getEncoder().encodeToString(bytes));
    }
    @Test
    public void testPBEDecrypt() throws Exception
    {
        byte[] enbytes = Base64.getDecoder().decode("asZRqkddtPiFXU7fn/I26FY2LZePerDH");
        //初始化盐
        SecureRandom random = new SecureRandom("KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        
        String password = "Admin@123890";
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
        SecretKey key = factory.generateSecret(pbeKeySpec);
        
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 1024);
        Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
        cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpec);
        enbytes = cipher.doFinal(enbytes);
        System.out.println("jdk pbe decrypt: " + new String(enbytes));
        
    }
    
    @Test
    public void testAESEncrypt() throws Exception
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = new SecureRandom("MY_KEY".getBytes());
        
        // 这里生成盐值的随机数种子和生成 AES SecretKey的相同，也可指定其他种子去生成盐值
        byte []salt = new byte[16];
        sr.nextBytes(salt);
        
        keyGenerator.init(256, sr);
        SecretKey key = keyGenerator.generateKey();
    
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
        String src = "http://www.google.com";
        byte []enbytes = cipher.doFinal(src.getBytes());
        System.out.println("jdk aes-256 encrypt: " + Base64.getEncoder().encodeToString(enbytes));
    }
    @Test
    public void testAESDecrypt() throws Exception
    {
        String src = "3AlXCDWfbf03e1CbOtwv4GHr3dpvXJ5fch8zoSfFLBg=";
        
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = new SecureRandom("MY_KEY".getBytes());
    
        // 这里生成盐值的随机数种子和生成 AES SecretKey的相同，也可指定其他种子去生成盐值
        byte []salt = new byte[16];
        sr.nextBytes(salt);
        
        keyGenerator.init(256, sr);
        SecretKey key = keyGenerator.generateKey();
    
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(salt));
        
        byte []debytes = cipher.doFinal(Base64.getDecoder().decode(src));
        System.out.println("jdk aes-256 decrypt: " + new String(debytes));
    }
    
    /**
     * 根据源码 不同的方式生成AES key
     *
     * 结果表明使用KeyGenerator 256 加上固定字符串产生的16位盐值生成的key 等同于下面的 该固定字符串产生的16位盐值，加上产生的32位密码 生成的key
     *
     * 不加盐值的情况下，则要简单的多， 相当于无论有无盐值，都是根据相同字符串作为种子，随机生成的32位byte密码 (32取决于init参数中的 256/8)
     *
     */
    @Test
    public void testAESEncrypt2() throws Exception
    {
        // 这里生成盐值的随机数种子和生成 AES SecretKey的相同，也可指定其他种子去生成盐值
        byte [] salt = new byte[16];
        SecureRandom sr = new SecureRandom("MY_KEY".getBytes());
        sr.nextBytes(salt);
        
        byte [] passwd = new byte[32];
        sr.nextBytes(passwd);
        SecretKey key = new SecretKeySpec(passwd, "AES");
    
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
        String src = "http://www.google.com";
        byte []enbytes = cipher.doFinal(src.getBytes());
        System.out.println("jdk aes-256 encrypt: " + Base64.getEncoder().encodeToString(enbytes));
    }
    
    @Test
    public void testAESDecrypt2() throws Exception
    {
        String src = "3AlXCDWfbf03e1CbOtwv4GHr3dpvXJ5fch8zoSfFLBg=";
    
        // 这里生成盐值的随机数种子和生成 AES SecretKey的相同，也可指定其他种子去生成盐值
        byte [] salt = new byte[16];
        SecureRandom sr = new SecureRandom("MY_KEY".getBytes());
        sr.nextBytes(salt);
    
        byte [] passwd = new byte[32];
        sr.nextBytes(passwd);
        SecretKey key = new SecretKeySpec(passwd, "AES");
    
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(salt));
        
        byte []debytes = cipher.doFinal(Base64.getDecoder().decode(src));
        System.out.println("jdk aes-256 decrypt: " + new String(debytes));
    }
}
