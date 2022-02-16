package com.yk.base.uitl;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class DESUtils {
    private static SecretKey key;
    private static String KEY_STRING = "MY_KEY"; //只要作为seed的key不变 DES/AES的私钥就不变

    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
//            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            keyGenerator.init(new SecureRandom(KEY_STRING.getBytes()));
            key = keyGenerator.generateKey();
            keyGenerator = null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encryptString(String pwd) {
        try {
            byte[] pwdBytes = pwd.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "DES");

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryteStrBytes = cipher.doFinal(pwdBytes);
            return Base64.getEncoder().encodeToString(encryteStrBytes);
        } catch (Exception e) {
            throw new RuntimeException("decryptString error", e);
        }
    }

    public static String decryptString(String encryptStr) {
        try {
            byte[] strBytes = Base64.getDecoder().decode(encryptStr);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "DES");

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryteStrBytes = cipher.doFinal(strBytes);
            return new String(decryteStrBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("decryptString error", e);
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        String en = DESUtils.encryptString("root");
        System.out.println(en);
        String de = DESUtils.decryptString(en);
        System.out.println(de);

        // 这个类的DES并不安全， 安全算法应该这么写
        String password = "password";
        int count = 1000;
        int keyLength = 256;
        int saltLenght = keyLength / 8;
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[saltLenght];
        secureRandom.nextBytes(salt);
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, count, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] key_bytes = factory.generateSecret(keySpec).getEncoded();
        SecretKey key = new SecretKeySpec(key_bytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");


        byte iv[] = new byte[cipher.getBlockSize()];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] ssss = cipher.doFinal("password".getBytes());
        System.out.println(ssss);
    }
}
