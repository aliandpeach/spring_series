package com.crypto;

import cn.hutool.core.util.HexUtil;
import com.yk.crypto.AESUtil;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import static com.yk.crypto.AESUtil.ENCRYPTION_KEY_ALIAS;
import static com.yk.crypto.AESUtil.KEYSTORE_PASS;

public class AESUtilTest
{
    @Test
    public void testInitialize()
    {
        AESUtil.initialize();
    }

    /**
     * 执行前 Shorten command line 不能设置为classpath, 只能为none
     */
    @Test
    public void testEncrypt()
    {
        String encryptString = AESUtil.encrypt("Admin@0123");
        System.out.println(encryptString);
        String string = AESUtil.decrypt(encryptString);
        System.out.println(string);
    }

    @Test
    public void createJCEKS()
    {
        try (FileOutputStream fos = new FileOutputStream("D:\\idea_workspace\\spring_series\\commons-utils\\aes256-1.jceks"))
        {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(null, null);

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);

            KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(KEYSTORE_PASS);
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(keyGenerator.generateKey());
            keyStore.setEntry(ENCRYPTION_KEY_ALIAS, secretKeyEntry, passwordProtection);

            keyStore.store(fos, KEYSTORE_PASS);
            System.out.println();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        System.out.println();
    }

    @Test
    public void loadJCEKS()
    {
        byte[] key = AESUtil.getSecretBytes(ENCRYPTION_KEY_ALIAS);
        try (FileInputStream keyStoreInputStream = new FileInputStream("D:\\idea_workspace\\spring_series\\commons-utils\\aes256.jceks"))
        {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(keyStoreInputStream, KEYSTORE_PASS);

            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(ENCRYPTION_KEY_ALIAS, new KeyStore.PasswordProtection(KEYSTORE_PASS));
            SecretKey secretKey = entry.getSecretKey();
            String string = AESUtil.encrypt(secretKey.getEncoded(), "Admin@0123");
            System.out.println(string);
            String _string = AESUtil.decrypt(secretKey.getEncoded(), string);
            System.out.println(_string);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        System.out.println();
    }

    @Test
    public void testMysqlDecrypt() throws Exception
    {
        // mysql aes_decrypt("xxxxx", "keytest")
        SecretKeySpec key = generateMySQLAESKey("keytest", StandardCharsets.UTF_8.name());
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
        byte[] _decryptString = cipher.doFinal(HexUtil.decodeHex("A34707DE9AABD067C46BD11C46D6AA5DF6848870CBB43C2CDF8A010D8E2FEC11"));
        System.out.println(new String(_decryptString));
    }

    public static SecretKeySpec generateMySQLAESKey(final String key, final String encoding)
    {
        try
        {
            final byte[] finalKey = new byte[16];
            int i = 0;
            for (byte b : key.getBytes(encoding))
                finalKey[i++ % 16] ^= b;
            return new SecretKeySpec(finalKey, "AES");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
