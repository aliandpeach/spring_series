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
import java.security.spec.KeySpec;

/**
 * 对称加密
 *
 * new SecureRandom("固定字符串".getBytes()).nextBytes(new byte[32]); 只要每次执行都 new SecureRandom对象 则得出的new byte[32] 结果都一样
 */
public class SymmetricEncryption
{
    private transient byte[] symmetrickey;
    
    private static volatile SymmetricEncryption INSTANCE;
    
    public SymmetricEncryption(byte[] symmetrickey)
    {
        this.symmetrickey = symmetrickey;
    }
    
    public static SymmetricEncryption getInstance(byte[] symmetrickey)
    {
        if (null == INSTANCE)
        {
            synchronized (SymmetricEncryption.class)
            {
                if (null == INSTANCE)
                {
                    INSTANCE = new SymmetricEncryption(symmetrickey);
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
        
        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[8];
        randomIV.nextBytes(ivBytes);
        
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        DESKeySpec dks = new DESKeySpec(passwd);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptBytes = cipher.doFinal(source);
        byte[] result = new byte[ivBytes.length + encryptBytes.length];
        System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
        System.arraycopy(encryptBytes, 0, result, ivBytes.length, encryptBytes.length);
        randomIV = null;
        secretKey = null;
        dks = null;
        randomDESKey = null;
        cipher = null;
        return ByteBuffer.wrap(result);
    }
    
    /**
     * des解密
     */
    public ByteBuffer desDecrypt(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] passwd = new byte[8];
        randomDESKey.nextBytes(passwd);

        byte[] ivBytes = new byte[8];
        System.arraycopy(source, 0, ivBytes, 0, ivBytes.length);
        
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        DESKeySpec dks = new DESKeySpec(passwd);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] encryptBytes = new byte[source.length - ivBytes.length];
        System.arraycopy(source, ivBytes.length, encryptBytes, 0, encryptBytes.length);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
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
        
        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[8]; // IV length: must be 8 bytes long
        randomIV.nextBytes(ivBytes);
        
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
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
        
        byte[] ivBytes = new byte[8];
        System.arraycopy(source, 0, ivBytes, 0, ivBytes.length);

        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        DESedeKeySpec dks = new DESedeKeySpec(passwd);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] encryptBytes = new byte[source.length - ivBytes.length];
        System.arraycopy(source, ivBytes.length, encryptBytes, 0, encryptBytes.length);

        byte[] decryptBytes = cipher.doFinal(encryptBytes);
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
        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[24];
        randomIV.nextBytes(ivBytes);

        PBEKeySpec pbeKeySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
        SecretKey key = factory.generateSecret(pbeKeySpec);
        // ------加密处理---------
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(ivBytes, 100);
        Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
        cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
        byte[] bytes = cipher.doFinal(source);

        byte[] result = new byte[bytes.length + ivBytes.length];
        System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
        System.arraycopy(bytes, 0, result, ivBytes.length, bytes.length);
        return ByteBuffer.wrap(result);
    }
    
    /**
     * PBE解密
     */
    public ByteBuffer pbeDecrypt(byte[] source) throws Exception
    {
        byte[] ivBytes = new byte[24];
        System.arraycopy(source, 0, ivBytes, 0, ivBytes.length);

        PBEKeySpec pbeKeySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
        SecretKey key = factory.generateSecret(pbeKeySpec);

        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(ivBytes, 100);
        Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
        cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpec);

        byte[] encryptBytes = new byte[source.length - ivBytes.length];
        System.arraycopy(source, ivBytes.length, encryptBytes, 0, encryptBytes.length);

        byte[] bytes = cipher.doFinal(encryptBytes);
        return ByteBuffer.wrap(bytes);
    }
    
    /**
     * aes256
     */
    public ByteBuffer aesEncrypt(byte[] source) throws Exception
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = new SecureRandom(symmetrickey);

        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[16]; // IV length: must be 16 bytes long
        randomIV.nextBytes(ivBytes);

        keyGenerator.init(256, sr);// 生成AES的私钥key
        SecretKey key = keyGenerator.generateKey();
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes)); // CBC模式必须有 IvParameterSpec
        byte[] encryptBytes = cipher.doFinal(source);

        byte[] result = new byte[ivBytes.length + encryptBytes.length];
        System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
        System.arraycopy(encryptBytes, 0, result, ivBytes.length, encryptBytes.length);
        return ByteBuffer.wrap(result);
    }
    
    /**
     *
     */
    public ByteBuffer aesDecrypt(byte[] source) throws Exception
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = new SecureRandom(symmetrickey);

        byte[] ivBytes = new byte[16];
        System.arraycopy(source, 0, ivBytes, 0, ivBytes.length);

        keyGenerator.init(256, sr);
        SecretKey key = keyGenerator.generateKey();
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));

        byte[] encryptBytes = new byte[source.length - ivBytes.length];
        System.arraycopy(source, ivBytes.length, encryptBytes, 0, encryptBytes.length);
        
        byte[] debytes = cipher.doFinal(encryptBytes);
        return ByteBuffer.wrap(debytes);
    }
    
    /**
     * aes256
     */
    public ByteBuffer aesEncrypt2(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] aesKey = new byte[32];
        randomDESKey.nextBytes(aesKey);// 生成AES的私钥key

        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[16]; // IV length: must be 16 bytes long
        randomIV.nextBytes(ivBytes);

        SecretKey key = new SecretKeySpec(aesKey, 0, aesKey.length, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));
        byte[] encryptBytes = cipher.doFinal(source);

        byte[] result = new byte[ivBytes.length + encryptBytes.length];
        System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
        System.arraycopy(encryptBytes, 0, result, ivBytes.length, encryptBytes.length);
        return ByteBuffer.wrap(result);
    }
    
    /**
     * 通过SecureRandom生成的 AES256的32字节密码，win和linux不同 需要指定为 SecureRandom r = SecureRandom.getInstance("SHA1PRNG","SUN"); r.setSeed(symmetrickey);
     */
    public ByteBuffer aesDecrypt2(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] aesKey = new byte[32];
        randomDESKey.nextBytes(aesKey);// 生成AES的私钥key

        byte[] ivBytes = new byte[16];
        System.arraycopy(source, 0, ivBytes, 0, ivBytes.length);
        
        SecretKey key = new SecretKeySpec(aesKey, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));

        byte[] encryptBytes = new byte[source.length - ivBytes.length];
        System.arraycopy(source, ivBytes.length, encryptBytes, 0, encryptBytes.length);

        byte[] debytes = cipher.doFinal(encryptBytes);
        return ByteBuffer.wrap(debytes);
    }

    public ByteBuffer aesEncryptWithSalt(byte[] source) throws Exception
    {
        int saltLength = 128;

        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] aesKey = new byte[32];
        randomDESKey.nextBytes(aesKey);// 生成AES的私钥key

        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[16]; // IV length: must be 16 bytes long
        randomIV.nextBytes(ivBytes);

        SecureRandom randomSalt = new SecureRandom();
        byte[] saltBytes = new byte[saltLength];
        randomSalt.nextBytes(saltBytes);

        KeySpec keySpec = new PBEKeySpec(new String(aesKey, 0, aesKey.length).toCharArray(), saltBytes, 1000, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();

        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptBytes = cipher.doFinal(source);

        byte[] result = new byte[saltBytes.length + ivBytes.length + encryptBytes.length];
        System.arraycopy(saltBytes, 0, result, 0, saltBytes.length);
        System.arraycopy(ivBytes, 0, result, saltBytes.length, ivBytes.length);
        System.arraycopy(encryptBytes, 0, result, ivBytes.length + saltBytes.length, encryptBytes.length);
        return ByteBuffer.wrap(result);
    }

    public ByteBuffer aesDecryptWithSalt(byte[] source) throws Exception
    {
        int saltLength = 128;

        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] aesKey = new byte[32];
        randomDESKey.nextBytes(aesKey);// 生成AES的私钥key

        byte[] saltBytes = new byte[saltLength];
        System.arraycopy(source, 0, saltBytes, 0, saltBytes.length);
        byte[] ivBytes = new byte[16];
        System.arraycopy(source, saltBytes.length, ivBytes, 0, ivBytes.length);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        KeySpec keySpec = new PBEKeySpec(new String(aesKey, 0, aesKey.length).toCharArray(), saltBytes, 1000, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();

        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] encryptBytes = new byte[source.length - ivBytes.length - saltBytes.length];
        System.arraycopy(source, ivBytes.length + saltBytes.length, encryptBytes, 0, encryptBytes.length);

        byte[] debytes = cipher.doFinal(encryptBytes);
        return ByteBuffer.wrap(debytes);
    }

    public ByteBuffer aesEncryptWithSalt2(byte[] source) throws Exception
    {
        int saltLength = 128;

//        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
//        byte[] aesKey = new byte[32];
//        randomDESKey.nextBytes(aesKey);

        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[16]; // IV length: must be 16 bytes long
        randomIV.nextBytes(ivBytes);

        SecureRandom randomSalt = new SecureRandom();
        byte[] saltBytes = new byte[saltLength];
        randomSalt.nextBytes(saltBytes);

        KeySpec keySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWITHHMACSHA512ANDAES_256");
        SecretKey key = keyFactory.generateSecret(keySpec);

        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        PBEParameterSpec parameterSpec = new PBEParameterSpec(saltBytes, 1000, iv);

        Cipher cipher = Cipher.getInstance("PBEWITHHMACSHA512ANDAES_256");
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        byte[] encryptBytes = cipher.doFinal(source);

        byte[] result = new byte[saltBytes.length + ivBytes.length + encryptBytes.length];
        System.arraycopy(saltBytes, 0, result, 0, saltBytes.length);
        System.arraycopy(ivBytes, 0, result, saltBytes.length, ivBytes.length);
        System.arraycopy(encryptBytes, 0, result, ivBytes.length + saltBytes.length, encryptBytes.length);
        return ByteBuffer.wrap(result);
    }

    public ByteBuffer aesDecryptWithSalt2(byte[] source) throws Exception
    {
        int saltLength = 128;

//        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
//        byte[] aesKey = new byte[32];
//        randomDESKey.nextBytes(aesKey);

        byte[] saltBytes = new byte[saltLength];
        System.arraycopy(source, 0, saltBytes, 0, saltBytes.length);
        byte[] ivBytes = new byte[16];
        System.arraycopy(source, saltBytes.length, ivBytes, 0, ivBytes.length);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        KeySpec keySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWITHHMACSHA512ANDAES_256");
        SecretKey key = keyFactory.generateSecret(keySpec);

        PBEParameterSpec parameterSpec = new PBEParameterSpec(saltBytes, 1000, iv);

        Cipher cipher = Cipher.getInstance("PBEWITHHMACSHA512ANDAES_256");
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] encryptBytes = new byte[source.length - ivBytes.length - saltBytes.length];
        System.arraycopy(source, ivBytes.length + saltBytes.length, encryptBytes, 0, encryptBytes.length);

        byte[] debytes = cipher.doFinal(encryptBytes);
        return ByteBuffer.wrap(debytes);
    }
}
