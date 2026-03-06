package com.yk.crypto;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;

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
     * 起初, 基于symmetrickey固定值的伪随机数生成器, 再通过KeyGenerator生成加密秘钥, 可以确保只需要传递symmetrickey, 就可以在不同的服务中生成相同的256 bit的AES私钥, 用于加密解密
     *
     * SecretKey key = new SecretKeySpec(aesKey, "AES"); // aesKey只能是16/24/32字节(128 bit/192 bit/256 bit),这个会在AESCrypt.isKeySizeValid做校验
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
     * 随后, 不使用KeyGenerator, 直接使用基于symmetrickey的伪随机数生成器生成256 bit的aes私钥,但其本质上和aesEncrypt方法没有区别
     *      我们的目的是需要基于symmetrickey固定值去安全的生成aes私钥,因此需要再次更换思路(使用秘钥派生函数)
     *
     * 不应该使用固定的 symmetrickey 初始化 SecureRandom，因为这会使得密钥和 IV 的生成过程是可预测的，从而降低安全性。
     * 改进建议：1. 使用系统默认的 SecureRandom 实例生成 AES 密钥和 IV，以确保生成的值是不可预测的。（但是秘钥无法记录， 密文无法去解密）
     *         2. PBKDF2 密钥派生：从 symmetrickey 和盐值中派生出 AES 密钥，采用 1000 次迭代和 256 位密钥长度，这在安全性上是足够的。
     *            因此AES的秘钥都使用new PBEKeySpec派生出来
     *
     */
    public ByteBuffer aesEncrypt2(byte[] source) throws Exception
    {
        SecureRandom randomDESKey = new SecureRandom(symmetrickey);
        byte[] aesKey = new byte[32];
        randomDESKey.nextBytes(aesKey);// 生成AES的私钥key 256-bit AES key

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

    /**
     * 最后, 使用随机数生成器随机生成salt, iv, 由秘钥派生函数通过passwd(这里的passwd不是AES秘钥而是自定义的复杂长字符串) + salt 生成aes的秘钥。
     *      每次需要加密或解密时，使用这些参数重新生成aes密钥
     *
     * 1. Key != Password
     *    SecretKeySpec expects a key, not a password. See below
     * 2. It might be due to a policy restriction that prevents using 32 byte keys. See other answer on that
     *
     * 随机生成 IV：用于 AES CBC 模式，保证每次加密结果不同，即使输入相同。
     * 随机生成盐值：用于 PBKDF2 密钥派生函数，以确保相同的密码生成不同的密钥。
     * 密钥派生：通过 PBKDF2WithHmacSHA1/PBKDF2WithHmacSHA256 从密码和盐值生成一个 AES 密钥。
     * 加密过程：使用生成的 AES 密钥和 IV 进行加密。
     * 结果拼接：将盐值、IV 和加密后的数据拼接在一起，方便解密时使用。
     *
     * 盐值和 IV 的生成：随机生成的盐值和 IV 增加了加密的随机性和安全性。
     * PBKDF2 密钥派生：从 symmetrickey（假设这是用户提供的密钥）和盐值中派生出 AES 密钥，采用 1000 次迭代和 256 位密钥长度，这在安全性上是足够的。
     *        AES/CBC/PKCS5Padding：采用 CBC 模式和 PKCS5Padding 填充，这是一种常见的、合理的选择。
     * 需要注意的点：
     * 密钥管理：symmetrickey 仍然需要安全地存储和管理。虽然 PBKDF2 增强了安全性，但如果 symmetrickey 被泄露，仍然会存在风险。
     * 解密过程：解密时需要从加密结果中提取出盐值和 IV，然后使用相同的 symmetrickey 和 PBKDF2 重新生成 AES 密钥，最后使用该密钥和 IV 进行解密。
     *
     */
    public ByteBuffer aesEncryptWithSalt(byte[] source) throws Exception
    {
        int saltLength = 128;

        // 随机生成iv
        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[16]; // IV length: must be 16 bytes long
        randomIV.nextBytes(ivBytes);

        // 随机生成salt
        SecureRandom randomSalt = new SecureRandom();
        byte[] saltBytes = new byte[saltLength];
        randomSalt.nextBytes(saltBytes);

        // 原来的写法是根据密码生成aesKey(基于symmetrickey的伪随机数生成器, 生成的32字节的数组)再生成PBEKeySpec, 但是key并不等同于passwd, 所以这里直接换成 symmetrickey
        // KeySpec keySpec = new PBEKeySpec(new String(aesKey, 0, aesKey.length).toCharArray(), saltBytes, 1000, 256);
        KeySpec keySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray(), saltBytes, 65536, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
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

        // 从密文中读取salt
        byte[] saltBytes = new byte[saltLength];
        System.arraycopy(source, 0, saltBytes, 0, saltBytes.length);

        // 从密文中读取iv
        byte[] ivBytes = new byte[16];
        System.arraycopy(source, saltBytes.length, ivBytes, 0, ivBytes.length);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // 原来的写法是根据密码生成aesKey再生成PBEKeySpec, key并不等同于passwd, 所以这里直接换成 symmetrickey
        // KeySpec keySpec = new PBEKeySpec(new String(aesKey, 0, aesKey.length).toCharArray(), saltBytes, 1000, 256);
        KeySpec keySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray(), saltBytes, 65536, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();

        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] encryptBytes = new byte[source.length - ivBytes.length - saltBytes.length];
        System.arraycopy(source, ivBytes.length + saltBytes.length, encryptBytes, 0, encryptBytes.length);

        byte[] debytes = cipher.doFinal(encryptBytes);
        return ByteBuffer.wrap(debytes);
    }

    /**
     * jdk支持：
     * list.add("PBEWITHHMACSHA1ANDAES_128");
     * list.add("PBEWITHHMACSHA1ANDAES_256");
     * list.add("PBEWITHHMACSHA224ANDAES_128");
     * list.add("PBEWITHHMACSHA224ANDAES_256");
     * list.add("PBEWITHHMACSHA256ANDAES_128");
     * list.add("PBEWITHHMACSHA256ANDAES_256");
     * list.add("PBEWITHHMACSHA384ANDAES_128");
     * list.add("PBEWITHHMACSHA384ANDAES_256");
     * list.add("PBEWITHHMACSHA512ANDAES_128");
     * list.add("PBEWITHHMACSHA512ANDAES_256");
     * list.add("PBEWITHMD5ANDDES");
     * list.add("PBEWITHMD5ANDTRIPLEDES");
     * list.add("PBEWITHSHA1ANDDESEDE");
     * list.add("PBEWITHSHA1ANDRC2_128");
     * list.add("PBEWITHSHA1ANDRC2_40");
     * list.add("PBEWITHSHA1ANDRC4_128");
     * list.add("PBEWITHSHA1ANDRC4_40");
     */
    public ByteBuffer aesEncryptWithSalt2(byte[] source) throws Exception
    {
        int saltLength = 128;

        SecureRandom randomIV = new SecureRandom();
        byte[] ivBytes = new byte[16]; // IV length: must be 16 bytes long
        randomIV.nextBytes(ivBytes);

        SecureRandom randomSalt = new SecureRandom();
        byte[] saltBytes = new byte[saltLength];
        randomSalt.nextBytes(saltBytes);

        KeySpec keySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray(), saltBytes, 65536, 256);
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

        byte[] saltBytes = new byte[saltLength];
        System.arraycopy(source, 0, saltBytes, 0, saltBytes.length);
        byte[] ivBytes = new byte[16];
        System.arraycopy(source, saltBytes.length, ivBytes, 0, ivBytes.length);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        KeySpec keySpec = new PBEKeySpec(new String(symmetrickey, 0, symmetrickey.length).toCharArray(), saltBytes, 65536, 256);
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

    public String aesEncryptWithJasypt(String plainText)
    {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        char[] passwdNew = new char[symmetrickey.length];
        System.arraycopy(symmetrickey, 0, passwdNew, 0, symmetrickey.length);
        config.setPasswordCharArray(passwdNew);
        standardPBEStringEncryptor.setConfig(config);
        return standardPBEStringEncryptor.encrypt(plainText);
    }

    public String aesDecryptWithJasypt(String encryptedText)
    {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        char[] passwdNew = new char[symmetrickey.length];
        System.arraycopy(symmetrickey, 0, passwdNew, 0, symmetrickey.length);
        config.setPasswordCharArray(passwdNew);
        standardPBEStringEncryptor.setConfig(config);
        return standardPBEStringEncryptor.decrypt(encryptedText);
    }
}
