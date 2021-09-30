package com.crypto.util;

import com.yk.crypto.EnDecryptUtil;
import com.yk.io.FileUtil;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public class EnDecryptUtilTest
{
    
    private char[] storepasswd;
    
    private char[] keypasswd;
    
    @Before
    public void init()
    {
        storepasswd = "".toCharArray();
        keypasswd = "".toCharArray();;
    }
    
    @Test
    public void fileEncryptToHex() throws Exception
    {
        String srcf = "C:\\Users\\yangkai\\Desktop\\test\\record__.txt";
        String tof = "C:\\Users\\yangkai\\Desktop\\test\\record___encrypt.txt";
        String toHexf = "C:\\Users\\yangkai\\Desktop\\test\\record___encrypt_hex.txt";

        new EnDecryptUtil(storepasswd, keypasswd, EnDecryptUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"), "crazy")
                .fileEncryptToHex(srcf, tof, toHexf, 245);
    }
    
    @Test
    public void hexDecryptToFile() throws IOException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException
    {
        String srcHexf = "C:\\Users\\xxxx\\Desktop\\test\\record__hex.txt";
        String tof = "C:\\Users\\xxxx\\Desktop\\test\\record__hex_src.txt";
        new EnDecryptUtil(storepasswd, keypasswd, EnDecryptUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"), "crazy")
                .hexDecryptToFile(srcHexf, tof, 256);
    }
    
    
    @Test
    public void fileEncryptToHex1() throws Exception
    {
        String srcf = "C:\\Users\\xxxx\\Desktop\\test\\lombok.zip";
        String toHexf = "C:\\Users\\xxxx\\Desktop\\test\\lombok_encrypt_hex.zip";
        
        new EnDecryptUtil(storepasswd, keypasswd, EnDecryptUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"), "crazy")
                .fileEncryptToHex(srcf, toHexf, 245);
    }
    
    @Test
    public void hexDecryptToFile1() throws Exception
    {
        String srcHexf = "C:\\Users\\xxxx\\Desktop\\test\\lombok_encrypt_hex.zip";
        String tof = "C:\\Users\\xxxx\\Desktop\\test\\lombok_decrypt.zip";
        new EnDecryptUtil(storepasswd, keypasswd, EnDecryptUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"), "crazy")
                .hexDecryptToFile(srcHexf, tof);
    }
    
    @Test
    public void decrypt() throws IOException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, InvalidKeySpecException, BadPaddingException,
            NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException
    {
        new EnDecryptUtil(storepasswd, keypasswd, EnDecryptUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"), "crazy")
                .decrypt("D:\\idea_workspace\\development_code\\doc\\test");
    }
    
    @Test
    public void encrypt() throws IOException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, InvalidKeySpecException, BadPaddingException,
            NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException
    {
        new EnDecryptUtil(storepasswd, keypasswd, EnDecryptUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"), "crazy")
                .encrypt("D:\\idea_workspace\\development_code\\doc\\test", true);
    }
    
    @Test
    public void copy() throws IOException
    {
        new FileUtil().copy("C:\\Users\\xxxx\\Desktop\\test", "C:\\Users\\xxxx\\Desktop\\test");
    }
    
    @Test
    public void copyByChannel() throws IOException
    {
        new FileUtil().copyByChannel("C:\\Users\\xxxx\\Desktop\\test", "C:\\Users\\xxxx\\Desktop\\test");
    }
    
    @Test
    public void copyCommon() throws IOException
    {
        new FileUtil().copyCommon("C:\\Users\\xxxx\\Desktop\\test", "C:\\Users\\xxxx\\Desktop\\test");
    }
}
