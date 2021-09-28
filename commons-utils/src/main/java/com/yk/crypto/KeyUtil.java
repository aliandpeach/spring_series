package com.yk.crypto;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/09/01 09:27:52
 */
public class KeyUtil
{
    public static KeyPair readKeyStore(InputStream input, String keyAlias, String keystorePasswd, String keyPasswd) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException
    {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(input, keystorePasswd.toCharArray());
        Key key = keyStore.getKey(keyAlias, keyPasswd.toCharArray());
        Certificate certificate = keyStore.getCertificate(keyAlias);
        PublicKey pub = certificate.getPublicKey();
        input.close();
        return new KeyPair(pub, (PrivateKey) key);
    }

    public static KeyPair readKeyStore(String keyPath, String keyAlias, String keystorePasswd, String keyPasswd) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException
    {
        InputStream input = new FileInputStream(keyPath);
        KeyPair keyPair = readKeyStore(input, keyAlias, keystorePasswd, keyPasswd);
        input.close();
        return keyPair;
    }

    public static PublicKey readCertificate(String certificatePath) throws CertificateException, IOException
    {
        InputStream input = new FileInputStream(certificatePath);
        PublicKey pub = readCertificate(input);
        input.close();
        return pub;
    }
    public static PublicKey readCertificate(InputStream certificatePath) throws CertificateException, IOException
    {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        Certificate certificate = certificateFactory.generateCertificate(certificatePath);
        PublicKey pub = certificate.getPublicKey();
        return pub;
    }

    /**
     * keytool -importkeystore -srckeystore rsa.keystore -destkeystore rsa.p12 -srcstoretype JKS -deststoretype PKCS12
     *
     * @param keystorePath   keystore文件路径
     * @param pkcs12Path     pkcs12文件路径
     * @param keystorePasswd keystore文件秘密
     * @param keyPasswd      内部私钥密码
     */
    public static void convertKeystore2PKCS12(String keystorePath, String pkcs12Path, String keystorePasswd, String keyPasswd) throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException
    {
        InputStream inputStream = new FileInputStream(keystorePath);
        OutputStream outputStream = new FileOutputStream(pkcs12Path);
        convertKeystore2PKCS12(inputStream, outputStream, keystorePasswd, keyPasswd);
    }
    public static void convertKeystore2PKCS12(InputStream inputStream, OutputStream outputStream, String keystorePasswd, String keyPasswd) throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException
    {
        KeyStore rsakeystore = KeyStore.getInstance("JKS");
        rsakeystore.load(inputStream, keystorePasswd.toCharArray());

        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);
        Enumeration<String> aliases = rsakeystore.aliases();
        while (aliases.hasMoreElements())
        {
            String alias = aliases.nextElement();
            PrivateKey k = (PrivateKey) rsakeystore.getKey(alias, keyPasswd.toCharArray());
            Certificate[] c = rsakeystore.getCertificateChain(alias);
            pkcs12.setKeyEntry(alias, k, keyPasswd.toCharArray(), c);
        }
        pkcs12.store(outputStream, keystorePasswd.toCharArray());

        outputStream.close();
        inputStream.close();
    }

    public static KeyPair readPKCS12(String keyPath, String keyAlias, String keystorePasswd, String keyPasswd) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException
    {
        InputStream input = new FileInputStream(keyPath);
        KeyPair keypair = readPKCS12(input, keyAlias, keystorePasswd, keyPasswd);
        input.close();
        return keypair;
    }

    public static KeyPair readPKCS12(InputStream input, String keyAlias, String keystorePasswd, String keyPasswd) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException
    {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(input, keystorePasswd.toCharArray());
        Key key = keyStore.getKey(keyAlias, keyPasswd.toCharArray());
        Certificate certificate = keyStore.getCertificate(keyAlias);
        PublicKey pub = certificate.getPublicKey();
        return new KeyPair(pub, (PrivateKey) key);
    }

    public static PublicKey exportPublickey(PrivateKey key) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PKCS8EncodedKeySpec pk8 = new PKCS8EncodedKeySpec(key.getEncoded());
        PublicKey pub = KeyFactory.getInstance("RSA").generatePublic(pk8);
        return pub;
    }

    public static PrivateKey readEncryptedKey(InputStream input, String keyPasswd) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException
    {
        try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             PemReader pemReader = new PemReader(reader))
        {
            PemObject pemObject = pemReader.readPemObject();
//          String encryptedKeyString = Base64.getEncoder().encodeToString(pemObject.getContent());

            EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(pemObject.getContent());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
            PBEKeySpec pbeKeySpec = new PBEKeySpec(keyPasswd.toCharArray());
            SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);

            Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
            cipher.init(2, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());

            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = encryptedPrivateKeyInfo.getKeySpec(cipher);
            PrivateKey key = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
            return key;
        }
    }

    public static PrivateKey readEncryptedKey(String encryptedKeyPath, String keyPasswd) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException
    {
        try (FileInputStream input = new FileInputStream(encryptedKeyPath))
        {
            return readEncryptedKey(input, keyPasswd);
        }
    }

    /**
     * 生成加密的私钥, 加密的私钥可以被 readEncryptedKey方法读取
     */
    public static void writeEncryptedKey(PrivateKey key, String outEncryptedPasswd, String out) throws OperatorCreationException, IOException
    {
        PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(key.getEncoded());

        JceOpenSSLPKCS8EncryptorBuilder builder =
                new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.PBE_SHA1_3DES);

        builder.setIterationCount(10000);
        builder.setPasssword(outEncryptedPasswd.toCharArray());

        OutputEncryptor outputEncryptor = builder.build();
        PKCS8Generator pkcs8Generator =
                new PKCS8Generator(PrivateKeyInfo.getInstance(pkcs8.getEncoded()), outputEncryptor);

        try (FileOutputStream fileOut = new FileOutputStream(out);
             OutputStreamWriter outWriter = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8);
             PemWriter writer = new PemWriter(outWriter))
        {
            writer.writeObject(pkcs8Generator);
            writer.flush();
        }
    }

    /**
     * 私钥写入PEM格式文件-不加密. 通过openssl导出pkcs12的私钥和该方法导出结果一致, openssl pkcs12 -nocerts -nodes -in test.p12 -out test-key.pem
     */
    public static String writerPrivateKey2PEM(PrivateKey key) throws IOException
    {
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        PemObjectGenerator pemObject = new PemObject("PRIVATE KEY", key.getEncoded());
        pemWriter.writeObject(pemObject);
        pemWriter.flush();
        pemWriter.close();
        return stringWriter.toString();
    }

    public static String writerPublicKey2PEM(PublicKey key) throws IOException
    {
        StringWriter pemStrWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(pemStrWriter);
        PemObject pemObject = new PemObject("PUBLIC KEY", key.getEncoded());
        pemWriter.writeObject(pemObject);
        pemWriter.flush();
        pemWriter.close();
        return pemStrWriter.toString();
    }

    public static String writerPublicKey2PEM(X509Certificate certificate) throws IOException
    {
        StringWriter pemStrWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(pemStrWriter);
        PemObject pemObject = new PemObject("PUBLIC KEY", certificate.getPublicKey().getEncoded());
        pemWriter.writeObject(pemObject);
        pemWriter.flush();
        pemWriter.close();
        return pemStrWriter.toString();
    }

    /**
     * 读取私钥 pkcs8 PEM格式 ( openssl pkcs8 -topk8 -inform PEM -in pkcs1.pem -outform pem -nocrypt -out pkcs8.pem )
     *
     * @param pem 私钥文件
     */
    public static RSAPrivateKey readPrivateKey(InputStream pem) throws Exception
    {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        try (InputStreamReader reader = new InputStreamReader(pem, StandardCharsets.UTF_8);
             PemReader pemReader = new PemReader(reader))
        {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
            return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
        }
    }

    /**
     * 读取私钥 pkcs1 PEM格式 ( openssl genrsa -out pkcs1.pem 2048 )
     *
     * @param pem 私钥文件
     */
    public static RSAPrivateKey readPrivateKeySecondApproach(InputStream pem) throws IOException
    {
        try (InputStreamReader reader = new InputStreamReader(pem, StandardCharsets.UTF_8))
        {
            PEMParser pemParser = new PEMParser(reader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(((PEMKeyPair) pemParser.readObject()).getPrivateKeyInfo());

            return (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);
        }
    }

    /**
     * 读取公钥 pkcs8 PEM格式 ( openssl rsa -in pkcs8.pem -pubout -out pkcs8_pub.pem )
     *
     * @param pem 私钥文件
     */
    public static RSAPublicKey readPublicKey(InputStream pem) throws Exception
    {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        try (InputStreamReader reader = new InputStreamReader(pem, StandardCharsets.UTF_8);
             PemReader pemReader = new PemReader(reader))
        {

            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
            return (RSAPublicKey) factory.generatePublic(pubKeySpec);
        }
    }

    /**
     * 读取公钥 pkcs1 PEM格式 ( openssl rsa -in pkcs1.pem -RSAPublicKey_out -out pkcs1_pub.pem )
     *
     * @param pem 私钥文件
     */
    public static RSAPublicKey readPublicKeySecondApproach(InputStream pem) throws IOException
    {
        try (InputStreamReader reader = new InputStreamReader(pem, StandardCharsets.UTF_8))
        {
            PEMParser pemParser = new PEMParser(reader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(((SubjectPublicKeyInfo) pemParser.readObject()));
            return (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
        }
    }
}
