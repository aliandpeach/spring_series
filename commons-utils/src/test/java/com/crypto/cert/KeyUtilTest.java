package com.crypto.cert;

import com.yk.crypto.KeyUtil;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/09/01 09:37:07
 */
public class KeyUtilTest
{
    public static final String rsa_key_keystore_passwd = "";
    public static final String rsa_key_passwd = "";
    public static final String rsa_encrypted_passwd = "";

    @Test
    public void testLoadKeystore()
    {
        try
        {
            KeyPair keyPair = KeyUtil.readKeyStore(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"),
                    "crazy",
                    rsa_key_keystore_passwd,
                    rsa_key_passwd);
            PrivateKey key = keyPair.getPrivate();
            PublicKey pub = keyPair.getPublic();
            String keyString = Base64.getEncoder().encodeToString(key.getEncoded());
            String pubString = Base64.getEncoder().encodeToString(pub.getEncoded());
            System.out.println(keyString);
            System.out.println();
            System.out.println(pubString);

            System.out.println();
            PublicKey cpub = KeyUtil.readCertificate(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.pub"));
            String cupuString = Base64.getEncoder().encodeToString(cpub.getEncoded());
            System.out.println(cupuString);
            Assert.assertEquals(cupuString, pubString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testConvert()
    {
        try
        {
            KeyUtil.convertKeystore2PKCS12(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"),
                    new FileOutputStream(System.getProperty("user.dir") + File.separator + "rsa_1.p12"),
                    rsa_key_keystore_passwd,
                    rsa_key_passwd);

            KeyPair keyPair = KeyUtil.readPKCS12(System.getProperty("user.dir") + File.separator + "rsa_1.p12",
                    "crazy",
                    rsa_key_keystore_passwd,
                    rsa_key_passwd);

            PrivateKey key = keyPair.getPrivate();
            PublicKey pub = keyPair.getPublic();
            String keyString = Base64.getEncoder().encodeToString(key.getEncoded());
            String pubString = Base64.getEncoder().encodeToString(pub.getEncoded());
            System.out.println(keyString);
            System.out.println();
            System.out.println(pubString);

            System.out.println();
            PublicKey cpub = KeyUtil.readCertificate(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.pub"));
            String cupuString = Base64.getEncoder().encodeToString(cpub.getEncoded());
            System.out.println(cupuString);
            Assert.assertEquals(cupuString, pubString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testWriterEncryptedKey()
    {
        try
        {
            KeyPair keyPair = KeyUtil.readKeyStore(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"),
                    "crazy",
                    rsa_key_keystore_passwd,
                    rsa_key_passwd);
            KeyUtil.writeEncryptedKey(keyPair.getPrivate(),
                    rsa_encrypted_passwd,
                    System.getProperty("user.dir") + File.separator + "rsa-encrypted-1.pem");

            PrivateKey key1 = KeyUtil.readEncryptedKey(
                    System.getProperty("user.dir") + File.separator + "rsa-encrypted-1.pem",
                    rsa_encrypted_passwd);
            String key1String = Base64.getEncoder().encodeToString(key1.getEncoded());
            System.out.println();
            System.out.println(key1String);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testWriterKey2PEM()
    {
        try
        {
            KeyPair keyPair = KeyUtil.readKeyStore(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/rsa.key"),
                    "crazy",
                    rsa_key_keystore_passwd,
                    rsa_key_passwd);
            String key1String = KeyUtil.writerPrivateKey2PEM(keyPair.getPrivate());
            System.out.println();
            System.out.println(key1String);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void comparePkcs1AndPkcs8PrivateKey()
    {
        try
        {
            RSAPrivateKey rsaPrivateKeyPKCS1 = KeyUtil.readPrivateKeySecondApproach(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/pkcs1.pem"));

            RSAPrivateKey rsaPrivateKeyPKCS8 = KeyUtil.readPrivateKey(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/pkcs8.pem"));

            Assert.assertArrayEquals(rsaPrivateKeyPKCS1.getEncoded(), rsaPrivateKeyPKCS8.getEncoded());

            PKCS8EncodedKeySpec pk8 = new PKCS8EncodedKeySpec(rsaPrivateKeyPKCS1.getEncoded());
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(pk8);
            Assert.assertArrayEquals(privateKey.getEncoded(), rsaPrivateKeyPKCS8.getEncoded());

            // 导出为PEM格式, 打印的结果与pkcs8.pem一致
            Assert.assertEquals(KeyUtil.writerPrivateKey2PEM(rsaPrivateKeyPKCS1), KeyUtil.writerPrivateKey2PEM(rsaPrivateKeyPKCS8));
            Assert.assertEquals(KeyUtil.writerPrivateKey2PEM(rsaPrivateKeyPKCS1), KeyUtil.writerPrivateKey2PEM(privateKey));
            System.out.println(KeyUtil.writerPrivateKey2PEM(rsaPrivateKeyPKCS1));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Test
    public void comparePkcs1AndPkcs8PublicKey()
    {
        try
        {
            RSAPublicKey rsaPubKeyPKCS1 = KeyUtil.readPublicKeySecondApproach(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/pkcs1_pub.pem"));

            RSAPublicKey rsaPubKeyPKCS8 = KeyUtil.readPublicKey(KeyUtilTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/pkcs8_pub.pem"));

            Assert.assertArrayEquals(rsaPubKeyPKCS1.getEncoded(), rsaPubKeyPKCS8.getEncoded());

            X509EncodedKeySpec x509 = new X509EncodedKeySpec(rsaPubKeyPKCS1.getEncoded());
            PublicKey pub = KeyFactory.getInstance("RSA").generatePublic(x509);
            Assert.assertArrayEquals(pub.getEncoded(), rsaPubKeyPKCS8.getEncoded());
            // 打印的结果和pkcs8_pub.pem内容一致
            Assert.assertEquals(KeyUtil.writerPublicKey2PEM(rsaPubKeyPKCS1), KeyUtil.writerPublicKey2PEM(rsaPubKeyPKCS8));
            Assert.assertEquals(KeyUtil.writerPublicKey2PEM(rsaPubKeyPKCS1), KeyUtil.writerPublicKey2PEM(pub));
            System.out.println(KeyUtil.writerPublicKey2PEM(rsaPubKeyPKCS1));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testExportPkcs1() throws Exception
    {
        // 打印的结果和pkcs1.pem一致
        PemReader readerPri = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/pkcs1.pem"), StandardCharsets.UTF_8));
        PEMParser pemParserPri = new PEMParser(readerPri);
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(((PEMKeyPair) pemParserPri.readObject()).getPrivateKeyInfo());
        System.out.println(KeyUtil.writerPkcs1Pri2PEM(privateKeyInfo.getPrivateKey().getOctets()));

        // 打印的结果和pkcs1_pub.pem一致
        PemReader readerPub = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/pkcs1_pub.pem"), StandardCharsets.UTF_8));
        PEMParser pemParserPub = new PEMParser(readerPub);
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(((SubjectPublicKeyInfo) pemParserPub.readObject()));
        System.out.println(KeyUtil.writerPkcs1Pub2PEM(publicKeyInfo.getPublicKeyData().getOctets()));
    }

    @Test
    public void test() throws Exception
    {
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        // 测试1和2 两种方式读取pkcs1是否一致, 结果一致
        PemReader reader1 = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/pkcs1.pem"), StandardCharsets.UTF_8));
        PemReader reader2 = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream("com/crypto/cert/pkcs1.pem"), StandardCharsets.UTF_8));
        try (ASN1InputStream asn1InputStream = new ASN1InputStream(reader1.readPemObject().getContent()))
        {
            // 1.
            ASN1Primitive rsaPrivateKey = asn1InputStream.readObject();
            PrivateKeyInfo privateKeyInfo1 = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.pkcs_1), rsaPrivateKey);
            // 这里的转换不能执行
//            RSAPrivateKey key1 = (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo1);
            // 2.
            PEMParser pemParser = new PEMParser(reader2);
            PrivateKeyInfo privateKeyInfo2 = PrivateKeyInfo.getInstance(((PEMKeyPair) pemParser.readObject()).getPrivateKeyInfo());
            RSAPrivateKey key2 = (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo2);

            Assert.assertArrayEquals(privateKeyInfo1.getPrivateKey().getOctets(),privateKeyInfo2.getPrivateKey().getOctets());
            Assert.assertEquals(KeyUtil.writerPkcs1Pri2PEM(privateKeyInfo1.getPrivateKey().getOctets()),
                    KeyUtil.writerPkcs1Pri2PEM(privateKeyInfo2.getPrivateKey().getOctets()));
            System.out.println(KeyUtil.writerPkcs1Pri2PEM(privateKeyInfo1.getPrivateKey().getOctets()));
        }
    }
}
