package com.yk.crypto;

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
import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * 对称加密
 */
public class SymmetricEncryption
{
    private transient byte[] symmetrickey;
    
    private transient byte[] symmetricsalt;
    
    private static volatile SymmetricEncryption INSTANCE;
    
    public SymmetricEncryption(byte[] symmetrickey, byte[] symmetricsalt)
    {
        this.symmetrickey = symmetrickey;
        this.symmetricsalt = symmetricsalt;
    }
    
    public static SymmetricEncryption getInstance(byte[] symmetrickey, byte[] symmetricsalt)
    {
        if (null == INSTANCE)
        {
            synchronized (SymmetricEncryption.class)
            {
                if (null == INSTANCE)
                {
                    INSTANCE = new SymmetricEncryption(symmetrickey, symmetricsalt);
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * des加密
     */
    public ByteBuffer desEncrypt(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] passwd = new byte[8];
        randomDESKey.nextBytes(passwd);
        
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[8];
        randomSalt.nextBytes(salt);
        
        IvParameterSpec iv = new IvParameterSpec(salt);
        DESKeySpec dks = new DESKeySpec(passwd);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptBytes = cipher.doFinal(source);
        randomSalt = null;
        secretKey = null;
        dks = null;
        randomDESKey = null;
        cipher = null;
        return ByteBuffer.wrap(encryptBytes);
    }
    
    /**
     * des解密
     */
    public ByteBuffer desDecrypt(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] passwd = new byte[8];
        randomDESKey.nextBytes(passwd);
        
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[8];
        randomSalt.nextBytes(salt);
        
        IvParameterSpec iv = new IvParameterSpec(salt);
        DESKeySpec dks = new DESKeySpec(passwd);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decryptBytes = cipher.doFinal(source);
        randomSalt = null;
        secretKey = null;
        dks = null;
        randomDESKey = null;
        cipher = null;
        return ByteBuffer.wrap(decryptBytes);
    }
    
    /**
     * 3des 加密
     */
    public ByteBuffer des3Encrypt(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] passwd = new byte[24];
        randomDESKey.nextBytes(passwd);
        
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[8]; // IV length: must be 8 bytes long
        randomSalt.nextBytes(salt);
        
        IvParameterSpec iv = new IvParameterSpec(salt);
        DESedeKeySpec dks = new DESedeKeySpec(passwd);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptBytes = cipher.doFinal(source);
        return ByteBuffer.wrap(encryptBytes);
    }
    
    /**
     * 3des解密
     */
    public ByteBuffer des3Decrypt(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] passwd = new byte[24];
        randomDESKey.nextBytes(passwd);
        
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[8]; // IV length: must be 8 bytes long
        randomSalt.nextBytes(salt);
        
        IvParameterSpec iv = new IvParameterSpec(salt);
        DESedeKeySpec dks = new DESedeKeySpec(passwd);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decryptBytes = cipher.doFinal(source);
        return ByteBuffer.wrap(decryptBytes);
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
    public ByteBuffer pbeEncrypt(byte[] source) throws Exception
    {
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[24];
        randomSalt.nextBytes(salt);
        
        PBEKeySpec pbeKeySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
        SecretKey key = factory.generateSecret(pbeKeySpec);
        // ------加密处理---------
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
        cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
        byte[] bytes = cipher.doFinal(source);
        return ByteBuffer.wrap(bytes);
    }
    
    /**
     * PBE解密
     */
    public ByteBuffer pbeDecrypt(byte[] source) throws Exception
    {
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[24];
        randomSalt.nextBytes(salt);
        
        PBEKeySpec pbeKeySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
        SecretKey key = factory.generateSecret(pbeKeySpec);
        
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
        cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpec);
        byte[] bytes = cipher.doFinal(source);
        return ByteBuffer.wrap(bytes);
    }
    
    /**
     * aes256
     */
    public ByteBuffer aesEncrypt(byte[] source) throws Exception
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = new SecureRandom(symmetrickey);
        
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[16]; // IV length: must be 16 bytes long
        randomSalt.nextBytes(salt);
        
        keyGenerator.init(256, sr);
        SecretKey key = keyGenerator.generateKey();
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
        byte[] enbytes = cipher.doFinal(source);
        return ByteBuffer.wrap(enbytes);
    }
    
    /**
     *
     */
    public ByteBuffer aesDecrypt(byte[] source) throws Exception
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = new SecureRandom(symmetrickey);
        
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[16]; // IV length: must be 16 bytes long
        randomSalt.nextBytes(salt);
        
        keyGenerator.init(256, sr);
        SecretKey key = keyGenerator.generateKey();
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(salt));
        
        byte[] debytes = cipher.doFinal(source);
        return ByteBuffer.wrap(debytes);
    }
    
    /**
     * aes256
     */
    public ByteBuffer aesEncrypt2(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] passwd = new byte[32];
        randomDESKey.nextBytes(passwd);
        
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[16]; // IV length: must be 16 bytes long
        randomSalt.nextBytes(salt);
        
        SecretKey key = new SecretKeySpec(passwd, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
        byte[] enbytes = cipher.doFinal(source);
        return ByteBuffer.wrap(enbytes);
    }
    
    /**
     *
     */
    public ByteBuffer aesDecrypt2(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] passwd = new byte[32];
        randomDESKey.nextBytes(passwd);
        
        SecureRandom randomSalt = new SecureRandom(symmetricsalt);
        byte[] salt = new byte[16]; // IV length: must be 16 bytes long
        randomSalt.nextBytes(salt);
        
        SecretKey key = new SecretKeySpec(passwd, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(salt));
        
        byte[] debytes = cipher.doFinal(source);
        return ByteBuffer.wrap(debytes);
    }
}
