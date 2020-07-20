package com.yk.base.uitl;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class DESUtils {
    private static Key key;
    private static String KEY_STRING = "MY_KEY";

    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
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
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryteStrBytes = cipher.doFinal(pwdBytes);
            return Base64.getEncoder().encodeToString(encryteStrBytes);
        } catch (Exception e) {
            throw new RuntimeException("decryptString error", e);
        }
    }

    public static String decryptString(String encryptStr) {
        try {
            byte[] strBytes = Base64.getDecoder().decode(encryptStr);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
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
