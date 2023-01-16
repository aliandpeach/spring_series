package com.crypto.util;

import cn.hutool.core.util.HexUtil;
import com.crypto.cert.KeyUtilTest;
import com.yk.crypto.BinHexSHAUtil;
import com.yk.crypto.EnDecryptUtil;
import com.yk.crypto.KeyUtil;
import com.yk.crypto.RSA2048Util;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

/**
 * RSA2048UtilTest
 */

public class RSA2048UtilTest
{
    private RSA2048Util rsa;
    
    
    @Before
    public void before()
    {
        this.rsa = RSA2048Util.getInstance(KeyUtilTest.rsa_key_keystore_passwd.toCharArray(), KeyUtilTest.rsa_key_passwd.toCharArray(), null, null);
    }
    
    @Test
    public void main() throws IOException, CertificateException, NoSuchAlgorithmException,
            UnrecoverableKeyException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException,
            BadPaddingException, KeyStoreException, IllegalBlockSizeException
    {
        String keyE = "GGGGGGGGGGBBBBBBBBBBBBBB";
        String saltE = "gggghhhhhhereFFFFF55555";
        
        byte[] keybytes = rsa.encrypt(keyE.getBytes()).array();
        byte[] saltbytes = rsa.encrypt(saltE.getBytes()).array();
        
        String key1 = HexUtil.encodeHexStr(keybytes);
        String salt1 = HexUtil.encodeHexStr(saltbytes);
        System.out.println(key1);
        System.out.println(salt1);
        
        String key2 = DatatypeConverter.printHexBinary(keybytes);
        String salt2 = DatatypeConverter.printHexBinary(saltbytes);
        
        String key3 = Hex.toHexString(keybytes);
        String salt3 = Hex.toHexString(saltbytes);
        
        String key4 = new BigInteger(1, keybytes).toString(16);
        String salt4 = new BigInteger(1, saltbytes).toString(16);
        
        
        String key5 = BinHexSHAUtil.byte2Hex(keybytes);
        String salt5 = BinHexSHAUtil.byte2Hex(saltbytes);
        
        String key6 = BinHexSHAUtil.byteArrayToHex(keybytes);
        String salt6 = BinHexSHAUtil.byteArrayToHex(saltbytes);
        
        String key7 = BinHexSHAUtil.byteArrayToHex2(keybytes);
        String salt7 = BinHexSHAUtil.byteArrayToHex2(saltbytes);
        
        Assert.assertTrue(key1.equalsIgnoreCase(key2));
        Assert.assertTrue(key2.equalsIgnoreCase(key3));
        Assert.assertTrue(key3.equalsIgnoreCase(getString(key4)));
        Assert.assertTrue(getString(key4).equalsIgnoreCase(key5));
        Assert.assertTrue(key5.equalsIgnoreCase(key6));
        Assert.assertTrue(key6.equalsIgnoreCase(key7));
        
        Assert.assertTrue(salt1.equalsIgnoreCase(salt2));
        Assert.assertTrue(salt2.equalsIgnoreCase(salt3));
        Assert.assertTrue(salt3.equalsIgnoreCase(getString(salt4)));
        Assert.assertTrue(getString(salt4).equalsIgnoreCase(salt5));
        Assert.assertTrue(salt5.equalsIgnoreCase(salt6));
        Assert.assertTrue(salt6.equalsIgnoreCase(salt7));
        
        // decrypt
        
        byte[] byteskey = new BigInteger(key4, 16).toByteArray();
        byte[] bytessalt = new BigInteger(salt4, 16).toByteArray();
        
        byte[] byteskey_temp = new byte[256];
        BinHexSHAUtil.to(byteskey, byteskey_temp);
        
        byte[] bytessalt_temp = new byte[256];
        BinHexSHAUtil.to(bytessalt, bytessalt_temp);
        
        String keyD = new String(rsa.decrypt(byteskey_temp).array());
        String saltD = new String(rsa.decrypt(bytessalt_temp).array());
        
        Assert.assertTrue(keyE.equalsIgnoreCase(keyD));
        Assert.assertTrue(saltE.equalsIgnoreCase(saltD));
    }
    
    private String getString(String key)
    {
        int len = key.length();
        for (int i = 0; i < Math.abs(512 - len); i++)
        {
            if (len > 512)
                key = key.substring(1);
            if (len < 512)
                key = "0" + key;
        }
        return key;
    }
    
    @Test
    public void test() throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException,
            InvalidKeyException, NoSuchPaddingException, BadPaddingException, KeyStoreException,
            InvalidKeySpecException, IllegalBlockSizeException
    {
        for (int i = 0; i < 100000; i++)
        {
            this.main();
        }
    }

    @Test
    public void encryptTest() throws Exception
    {
        RSAPublicKey rsaPubKeyPKCS1 = KeyUtil.readPublicKeySecondApproach(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/gitee_login.pub"));
        RSAPublicKey rsaPubKeyPKCS2 = KeyUtil.readPublicKey(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/gitee_login.pub"));
        boolean is = Arrays.equals(rsaPubKeyPKCS2.getEncoded(), rsaPubKeyPKCS2.getEncoded());
        byte[] _r = EnDecryptUtil.encrypt("abc.1234".getBytes(StandardCharsets.UTF_8), rsaPubKeyPKCS1.getEncoded());
        System.out.println(Base64.getEncoder().encodeToString(_r));
    }

    @Test
    public void giteeTest() throws Exception
    {
        // gitee导出的公钥, 分别为der(二进制)和crt(pem)格式
        PublicKey publicKey1 = KeyUtil.readCertificate(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/gitee.der"));
        PublicKey publicKey2 = KeyUtil.readCertificate(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/gitee.crt"));
        boolean is = Arrays.equals(publicKey1.getEncoded(), publicKey2.getEncoded());
        byte[] _r = EnDecryptUtil.encrypt("abc.1234".getBytes(StandardCharsets.UTF_8), publicKey1.getEncoded());
        System.out.println(Base64.getEncoder().encodeToString(_r));
    }

    @Test
    public void x509ToPem() throws Exception
    {
        CertificateFactory ft = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) ft.generateCertificate(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/gitee.der"));
        PublicKey publicKey = certificate.getPublicKey();

        String strKey = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getEncoder().encodeToString(publicKey.getEncoded())
                + "\n-----END PUBLIC KEY-----";
        System.out.println(strKey);
    }
}
