package com.yk.crypto;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;

public class AESUtil
{
    private static final Logger log = LoggerFactory.getLogger(AESUtil.class);

    public static final String CRYPT_ALGORITHM = "AES";

    public static final String HASH_ALGORITHM = "SHA-256";

    private static final String keyStoreFile = System.getProperty("user.dir") + File.separator + "aes256.jceks";

    public static final char[] KEYSTORE_PASS = new char[]{
            'G', '~', 'r', 'x', 'Z', 'E', 'w', 'f', 'a', '[', '!', 'f', 'Z', 'd', '*', 'L', '8', 'm', 'h', 'u', '#',
            'j', '9', ':', '~', ';', 'U', '>', 'O', 'i', '8', 'r', 'C', '}', 'f', 't', '%', '[', 'H', 'h', 'M', '&',
            'K', ':', 'l', '5', 'c', 'H', '6', 'r', 'A', 'E', '.', 'F', 'Y', 'W', '}', '{', '*', '8', 'd', 'E', 'C',
            'A', '6', 'F', 'm', 'j', 'u', 'A', 'Q', '%', '{', '/', '@', 'm', '&', '5', 'S', 'q', '4', 'Q', '+', 'Y',
            '|', 'X', 'W', 'z', '8', '<', 'j', 'd', 'a', '}', '`', '0', 'N', 'B', '3', 'i', 'v', '5', 'U', ' ', '2',
            'd', 'd', '(', '&', 'J', '_', '9', 'o', '(', '2', 'I', '`', ';', '>', '#', '$', 'X', 'j', '&', '&', '%',
            '>', '#', '7', 'q', '>', ')', 'L', 'A', 'v', 'h', 'j', 'i', '8', '~', ')', 'a', '~', 'W', '/', 'l', 'H',
            'L', 'R', '+', '\\', 'i', 'R', '_', '+', 'y', 's', '0', 'n', '\'', '=', '{', 'B', ':', 'l', '1', '%', '^',
            'd', 'n', 'H', 'X', 'B', '$', 'f', '"', '#', ')', '{', 'L', '/', 'q', '\'', 'O', '%', 's', 'M', 'Q', ']',
            'D', 'v', ';', 'L', 'C', 'd', '?', 'D', 'l', 'h', 'd', 'i', 'N', '4', 'R', '>', 'O', ';', '$', '(', '4',
            '-', '0', '^', 'Y', ')', '5', 'V', 'M', '7', 'S', 'a', 'c', 'D', 'C', 'w', 'A', 'o', 'n', 's', 'r', '*',
            'G', '[', 'l', 'h', '$', 'U', 's', '_', 'D', 'f', 'X', '~', '.', '7', 'B', 'A', 'E', '(', '#', ']', ':',
            '`', ',', 'k', 'y'};

    public static final int KEY_LENGTH = 256;
    public static final String ENCRYPTION_KEY_ALIAS = "AES-ENCRYPTION_KEY";

    private AESUtil()
    {
    }

    public static void initialize()
    {
        File _key = new File(keyStoreFile);
        if (!_key.exists() || !_key.isFile())
        {
            initializeKeyStore();
        }
    }

    public static String encrypt(String str)
    {
        initialize();
        return encrypt(getSecretBytes(ENCRYPTION_KEY_ALIAS), str);
    }

    public static String decrypt(String str)
    {
        initialize();
        return decrypt(getSecretBytes(ENCRYPTION_KEY_ALIAS), str);
    }

    public static String hash(String str, String salt)
    {
        String hash = null;
        try
        {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            if (StringUtils.isNotEmpty(salt))
            {
                md.update(Base64.decodeBase64(salt.getBytes()));
            }
            md.update(str.getBytes(StandardCharsets.UTF_8));
            hash = new String(Base64.encodeBase64(md.digest()));
        }
        catch (Exception e)
        {
            log.error(e.toString(), e);
        }
        return hash;
    }

    public static String hash(String str)
    {
        return hash(str, null);
    }

    public static String encrypt(byte[] key, String str)
    {
        String retVal = null;
        if (StringUtils.isBlank(str))
        {
            return null;
        }
        try
        {
            Cipher c = Cipher.getInstance(CRYPT_ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, CRYPT_ALGORITHM));
            byte[] encVal = c.doFinal(str.getBytes());
            retVal = new String(Base64.encodeBase64(encVal));
        }
        catch (Exception ex)
        {
            log.error(ex.toString(), ex);
        }
        return retVal;
    }

    public static String decrypt(byte[] key, String str)
    {
        String retVal = null;
        if (StringUtils.isBlank(str))
        {
            return null;
        }
        try
        {
            Cipher c = Cipher.getInstance(CRYPT_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, CRYPT_ALGORITHM));
            byte[] decodedVal = Base64.decodeBase64(str.getBytes());
            retVal = new String(c.doFinal(decodedVal));
        }
        catch (Exception ex)
        {
            log.error(ex.toString(), ex);
        }
        return retVal;
    }

    public static void resetKeyStore()
    {
        File file = new File(keyStoreFile);
        try
        {
            if (file.exists())
            {
                FileUtils.forceDelete(file);
            }
        }
        catch (Exception ex)
        {
            log.error(ex.toString(), ex);
        }
        initializeKeyStore();
    }

    /**
     * 自动生成AES私钥 保存到 keystore文件中
     */
    private static void initializeKeyStore()
    {
        try (FileOutputStream fos = new FileOutputStream(keyStoreFile))
        {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(null, null);

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(KEY_LENGTH);
            setSecret(ENCRYPTION_KEY_ALIAS, keyGenerator.generateKey(), keyStore);

            keyStore.store(fos, KEYSTORE_PASS);
        }
        catch (Exception ex)
        {
            log.error(ex.toString(), ex);
        }
    }

    /**
     * keystore 设置 AES私钥 别名为 alias
     */
    public static void setSecret(String alias, SecretKey secretKey, KeyStore keyStore)
    {
        try
        {
            KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(KEYSTORE_PASS);
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            keyStore.setEntry(alias, secretKeyEntry, passwordProtection);
            log.debug("set secret entry {}", alias);
        }
        catch (Exception ex)
        {
            log.error(ex.toString(), ex);
        }
    }

    public static byte[] getSecretBytes(String alias)
    {
        byte[] value = null;
        try (FileInputStream keyStoreInputStream = new FileInputStream(keyStoreFile))
        {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(keyStoreInputStream, KEYSTORE_PASS);

            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(KEYSTORE_PASS));
            value = entry.getSecretKey().getEncoded();
        }
        catch (Exception ex)
        {
            log.error(ex.toString(), ex);
        }
        return value;
    }
}
