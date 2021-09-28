package com.crypto.cert;

import cn.hutool.core.io.resource.ClassPathResource;
import com.yk.crypto.KeyUtil;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;

/**
 * ConvertTest
 */

public class ConvertTest
{
    /**
     * openssh生成的 id_rsa 内容
     * ssh-keygen -t rsa
     */
    private static final String id_rsa = "MIIEogIBAAKCAQEApfl8p0vbZF5gXKcJPFVyJkpVloIt7mtmVc91gR6XwmD9OyUpRX19oeAGenuw+HeHUvHFQR0vyM1byZKdM2qp3wf7UhZK9xQ+NRche2wjIVBiB/0iF8+BnkorpjOOBtHuGxuLa4RNxdj/cWt7lA6yZQAMBcDIuYk5pmSk5/RzVH4xQiN0xrsiDS2EUu0wQqZAWe6ypcG59a6gex3zvtPuvJaNIbkEvFKxrzIkMTwJJiGEvg/Kdcq6sDp3EIRwMMMKFLDwaa18WvkuQcsRLGGjAzAC0hqRP9EEmdGyBJQnjh3WVeksd07pzw96Mv85CpWjkmlQ+Dt07wvr31i9UZzNjwIDAQABAoIBAGmGil7DuCXEa3f9K74UhZMax/f9pL5lwpbkZE1H6i1IBTlJk0f/VZVKHlRZuFcBiCSQW13e9Lay+pzafuBl/MM1C/FRAzC8yiBvKHaUdnGD7hAuPTLuV/cQKVhuhuqHJuBNTwN5Bwm3whGWyOeFwJ6+vOYbgIOWX/UPIFnp7jnVS50NERM4EsH3jmhxACLkmpMDSKCoGx+GdrDAJ13kal8q7ORFgBmh12wUYjLNxFF07Qw02DrnRbBltR153RvZdi1OF+E+wdaBdXlFWQSOXewZoHGxIJGV6RDIp1FusCIQb9ohHNOzGM0+nTv4FWJCu26IEGEadeI8qLA3OqorZCECgYEA1tgnP8aAUxqP4rq8+/vmp/crqOH/0GAJfWNGceMpnxujhHd9B14IswqGnk6185K+g9pL30GMvMjrypXyE/dF519z6LvLjgJePvhtJkb4MDMr6wOyILU5wMXDlu0yjaJABDN+acRkbDs7D78wQHF3ktMUyD895yy13/0WZaoWhkUCgYEAxcTJUsJ9Xtj+gSFxyrn6H9jCuItBKgwvp2kxYMq1sBB8/6zl+WR4VkqNzCzKT3rNyr5ZpyHc6oQG9DgHP6hbO+FsR+X4t8gIluW4u8YVEQmGVpmipPfck0m8OHhw5ukvLXb5c1136vWq0cAuHjVXY5UpiL1yGHtfMeu+5X5yW8MCgYB7VuxxeIwDKaShDYOhRiRNGWE/Oz+ZaQkJqwyuJTOn3D4rtk1ZBWsJHmnaSGW8x+oH2DLmoMMjPlXfn+WyAKtpASuR2P4rMMDaddRzvRqO0VHNQfnpPSFCwkZfRx1WomcOAmH4Zn8gg4CMsb4JwRpceEHeFDB9nvVq+ej5xIPZ4QKBgHvGit4CHacUFk1JWkewGAyAhHYtqY4gbF7RpnbWx9m2qBT46EIrEGmpZY/I5KHT0SK3+bJxrisUkKmeu2KrAta6YiOpmf2j80RF6FWVRrAQDwT6SOIIwwNn3aZWMwtCHCXPVZZPhHMIaG24YuTDK7uGqqULNHKAHHdI/YPynK4zAoGAdHpaXlFlTxeS/6sDZAitH2Morgh5aXXoqbPIRHzYK5wuHk/FWYNkREcDBl26CsNJ/djQb8uLOcx+ZeGS9CbJgD14FF0phxMFwZ6ghjp32wU1qVZweCUFVLFtn4qL0D2BcSd1OabxNrv2vGS8lEdJxaBF0Kwu8UgE32eU22Dlsks=";
    // openssh生成的 id_rsa.pub内容
    private static final String original_id_rsa_pub = "AAAAB3NzaC1yc2EAAAADAQABAAABAQCl+XynS9tkXmBcpwk8VXImSlWWgi3ua2ZVz3WBHpfCYP07JSlFfX2h4AZ6e7D4d4dS8cVBHS/IzVvJkp0zaqnfB/tSFkr3FD41FyF7bCMhUGIH/SIXz4GeSiumM44G0e4bG4trhE3F2P9xa3uUDrJlAAwFwMi5iTmmZKTn9HNUfjFCI3TGuyINLYRS7TBCpkBZ7rKlwbn1rqB7HfO+0+68lo0huQS8UrGvMiQxPAkmIYS+D8p1yrqwOncQhHAwwwoUsPBprXxa+S5ByxEsYaMDMALSGpE/0QSZ0bIElCeOHdZV6Sx3TunPD3oy/zkKlaOSaVD4O3TvC+vfWL1RnM2P";

    /**
     * openssl genrsa -out pkcs1.pem 2048   生成没有加密的私钥  (pkcs1)
     * openssl pkcs8 -topk8 -inform PEM -in privkey.pem -outform pem -nocrypt -out pkcs8.pem  (pkcs8)
     * openssl rsa -in pkcs1.pem -pubout -out pkcs8_pub.pem  (pkcs8 public key)
     */
    private static final String pkcs1_rsa = "com/crypto/cert/pkcs1.pem";
    private static final String pkcs8_rsa = "com/crypto/cert/pkcs8.pem";
    private static final String pkcs8_pub = "com/crypto/cert/pkcs8_pub.pem";

    /**
     * JKS提取私钥 -> 格式为PKCS8
     *
     * 1. jks存储的私钥, 直接通过java 读取
     * 2. jks转换为pkcs12 再提取私钥, 得到的私钥直接通过PKCS8EncodedKeySpec 可以读取
     */
    @Test
    public void readKeystore() throws Exception
    {
        KeyStore rsakeystore = KeyStore.getInstance("JKS");
        rsakeystore.load(new FileInputStream(new ClassPathResource("com/crypto/cert/rsa.key").getFile()), KeyUtilTest.rsa_key_keystore_passwd.toCharArray());
        PrivateKey key = (PrivateKey) rsakeystore.getKey("crazy", KeyUtilTest.rsa_key_passwd.toCharArray());
        byte[] encodedKey = key.getEncoded();
        // 导出pkcs8格式的私钥
        String base64KeyString = Base64.getEncoder().encodeToString(encodedKey);
        System.out.println();
        System.out.println(base64KeyString);
        System.out.println();

        // 1. encodedKey 是rsa.key 中的 PKCS8不加密的格式的私钥
        // 2. 使用keytool 转换rsa.key 为 pkcs12后使用命令提取私钥： openssl pkcs12 -nocerts -nodes -in test.p12 -out test-key.pem
        // 以上两个私钥是相同的

        /**
         * 使用PKCS8去读取 encodedKey
         */
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        byte[] bb = pkcs8EncodedKeySpec.getEncoded();
        Assert.assertTrue(Arrays.equals(encodedKey, bb));//true

        PrivateKey key1 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
        Assert.assertTrue(key1.equals(key));//true


        Certificate certificate = rsakeystore.getCertificate("crazy");
        byte[] encodedCer = certificate.getEncoded();
        String base64CerString = Base64.getEncoder().encodeToString(encodedCer);
        System.out.println(base64CerString);
        System.out.println();

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(rsakeystore, KeyUtilTest.rsa_key_passwd.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        KeyStore truststore = KeyStore.getInstance("JKS");
        truststore.load(null, null);
        truststore.setCertificateEntry("crazy", certificate);
        trustManagerFactory.init(truststore);


        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
    }

    /**
     * 读取openssl生成的私钥 两种方式
     * <p>
     * 1. 生成私钥 - openssl genrsa -out privkey.pem 2048   生成没有加密的私钥
     *    转换为PKCS8 - openssl pkcs8 -topk8 -inform PEM -in privkey.pem -outform pem -nocrypt -out pkcs8.pem , 通过PKCS8EncodedKeySpec 读取
     * <p>
     * 2. 生成私钥 - openssl genrsa -out privkey.pem 2048   生成没有加密的私钥, 通过BouncyCastle读取
     *    java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
     *
     *
     *    openssl rsa : -----BEGIN RSA PRIVATE KEY-----
     *    pkcs8 rsa : -----BEGIN PRIVATE KEY-----
     *
     * 生成公钥 ： openssl rsa -in privkey.pem -pubout -out public.pem ( 使用privkey.pem 或 pkcs8.pem计算的公钥一致)
     */
    @Test
    public void readOpensslRSA() throws Exception
    {
        // java 可以直接读取 openssl 生成的pkcs8
        PemReader reader2 = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream(pkcs8_rsa), StandardCharsets.UTF_8));
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec2 = new PKCS8EncodedKeySpec(reader2.readPemObject().getContent());
        PrivateKey key2 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec2);
//        System.out.println("key2=" + key2);
        System.out.println();
        System.out.println(KeyUtil.writerPrivateKey2PEM(key2));

        // 用 BouncyCastle 直接读取pkcs1私钥
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        PemReader reader1 = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream(pkcs1_rsa), StandardCharsets.UTF_8));
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(reader1.readPemObject().getContent());
        PrivateKey key1 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
//        System.out.println("key1=" + key1);
        System.out.println();
        System.out.println(KeyUtil.writerPrivateKey2PEM(key1));

        // openssl生成的 rsa私钥 和 该私钥再通过 openssl转换出来的 pkcs8 私钥, 代入java中, 得到的对象相同
        Assert.assertArrayEquals(key1.getEncoded(), key2.getEncoded());
        Assert.assertEquals(key1, key2);
        Assert.assertEquals(KeyUtil.writerPrivateKey2PEM(key1), KeyUtil.writerPrivateKey2PEM(key2));

        // 根据私钥计算公钥 相当于 ： openssl rsa -in privkey.pem -pubout -out public.pem
        RSAPrivateCrtKey privk = (RSAPrivateCrtKey) key2;
        RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PemReader reader3 = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream(pkcs8_pub), StandardCharsets.UTF_8));
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(reader3.readPemObject().getContent());
        PublicKey pub = KeyFactory.getInstance("RSA").generatePublic(x509);
        Assert.assertArrayEquals(publicKey.getEncoded(), pub.getEncoded());
    }

    /**
     * 读取openssh生成的私钥 - ssh-keygen -t rsa
     */
    @Test
    public void readOpensshRSA() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // openssh生成的 id_rsa 内容
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(id_rsa));
        PrivateKey key1 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
        System.out.println(key1);

        // 私钥计算公钥
        RSAPrivateCrtKey privk = (RSAPrivateCrtKey) key1;
        RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // 这里转换生成的base64和 openssh生成的id_rsa.pub内容不一致
        String id_rsa_pub = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println(publicKey.getEncoded().length);
        System.out.println(id_rsa_pub);

        byte[] id_rsa_pub_bytes = Base64.getDecoder().decode(original_id_rsa_pub);
        System.out.println(id_rsa_pub_bytes.length);

        // 对比可知 这两者byte[]中间一部分是一致的,即不同格式的公钥增加了不同的前缀和后缀
        System.out.println(Arrays.toString(publicKey.getEncoded()));
        System.out.println(Arrays.toString(id_rsa_pub_bytes));
    }

    /**
     * JKS导出为PKCS12
     *
     * 转换为pkcs12 :
     *             keytool -importkeystore -srckeystore wlpt.keystore -destkeystore wlpt.keystore.p12 -srcstoretype JKS -deststoretype PKCS12
     *             或者
     *             keytool -importkeystore -srcstoretype JKS -srckeystore ServerCert.jks -srcstorepass 123456 -srcalias server -srckeypass 123456 -deststoretype PKCS12 -destkeystore client.p12 -deststorepass 123456 -destalias client -destkeypass 123456 -noprompt
     *
     * pkcs12 转换keystore :
     *            keytool -importkeystore -srckeystore server.p12 -srcstoretype PKCS12 -srcstorepass ${passwd} -alias server -deststorepass ${passwd} -destkeypass ${passwd} -destkeystore ServerCert.jks
     *
     *
     * 遗留点： openssl 提取pkcs12 私钥  openssl pkcs12 -in server.p12 -nocerts -nodes -out private.key   有 -nodes 和没有 -nodes的区别
     *
     * https://www.openssl.org/docs/man1.0.2/man1/pkcs12.html
     * -nodes: don't encrypt the private keys at all.
     */
    @Test
    public void exportToPKCS12() throws Exception
    {
        KeyStore rsakeystore = KeyStore.getInstance("JKS");
        rsakeystore.load(new FileInputStream(new ClassPathResource("com/crypto/cert/rsa.key").getFile()), KeyUtilTest.rsa_key_keystore_passwd.toCharArray());

        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);
        Enumeration<String> aliases = rsakeystore.aliases();
        while (aliases.hasMoreElements())
        {
            String alias = aliases.nextElement();
            PrivateKey k = (PrivateKey) rsakeystore.getKey(alias, KeyUtilTest.rsa_key_passwd.toCharArray());
            Certificate[] c = rsakeystore.getCertificateChain(alias);
            pkcs12.setKeyEntry(alias, k, KeyUtilTest.rsa_key_passwd.toCharArray(), c);
        }
        pkcs12.store(new FileOutputStream("D:\\test.p12"), KeyUtilTest.rsa_key_keystore_passwd.toCharArray());
    }

    /**
     * bouncycastle - format PKCS#1 to PKCS#8
     *
     * 参考 : https://www.cnblogs.com/adylee/p/3625389.html
     *       https://www.cnblogs.com/LiuYanYGZ/p/12519029.html
     *       https://www.cnblogs.com/ylz8401/p/9004427.html
     *
     *       https://www.cnblogs.com/cocoajin/p/10510574.html
     *
     *       https://stackoverflow.com/questions/54238568/reading-a-pkcs1-or-spki-public-key-in-java-without-libraries
     *
     *       https://www.baeldung.com/java-read-pem-file-keys
     */
    @Test
    public void formatPkcs1ToPkcs8() throws Exception
    {
        PemReader reader = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream(pkcs1_rsa), StandardCharsets.UTF_8));
        try (ASN1InputStream asn1InputStream = new ASN1InputStream(reader.readPemObject().getContent()))
        {
            // 转换 pkcs1 -> pkcs8
            ASN1Primitive rsaPrivateKey = asn1InputStream.readObject();
            PrivateKeyInfo privateKeyInfo
                    = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.pkcs_1), rsaPrivateKey);
            byte[] bytes = privateKeyInfo.getPrivateKey().getEncoded();
            System.out.println(Arrays.toString(bytes));
            PKCS8EncodedKeySpec pk1 = new PKCS8EncodedKeySpec(bytes);
//            PrivateKey key1 = KeyFactory.getInstance("RSA").generatePrivate(pk1);

            // 与 使用 openssl转换的pkcs8 比较
            PemReader reader8 = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream(pkcs1_rsa), StandardCharsets.UTF_8));
            PKCS8EncodedKeySpec pk2 = new PKCS8EncodedKeySpec(reader8.readPemObject().getContent());
            PrivateKey key2 = KeyFactory.getInstance("RSA").generatePrivate(pk2);
            System.out.println(Arrays.toString(key2.getEncoded()));
            Assert.assertArrayEquals(bytes, key2.getEncoded());

            // 转换 pkcs8 -> pkcs1
//            PrivateKey priv = pair.getPrivate();
//            byte[] privBytes = priv.getEncoded();
//
//            PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(privBytes);
//            ASN1Encodable encodable = pkInfo.parsePrivateKey();
//            ASN1Primitive primitive = encodable.toASN1Primitive();
//            byte[] privateKeyPKCS1 = primitive.getEncoded();
//
//            PrivateKeyInfo pki = PrivateKeyInfo.getInstance(encodeByte);
//            RSAPrivateKey pkcs1Key = RSAPrivateKey.getInstance(pki.getPrivateKey());
//            byte[] pkcs1Bytes = pkcs1Key.getEncoded();//etc.
        }
    }

    /**
     * 使用 bouncycastle生成加密的 PKCS8格式的RSA私钥
     *
     * https://stackoverflow.com/questions/14580958/exception-when-constructing-encryptedprivatekeyinfo
     *
     *
     * 以后可以不再使用keystore存储私钥
     *
     * 1. 使用openssl 生成私钥、证书、签名等, 私钥转换为 pkcs8
     * 2. 再使用 bouncycastle 对私钥进行加密
     */
    @Test
    public void generateEncryptedPKCS8Key() throws Exception
    {
        PemReader reader = new PemReader(new InputStreamReader(ConvertTest.class.getClassLoader().getResourceAsStream(pkcs8_rsa), StandardCharsets.UTF_8));
        byte [] content = reader.readPemObject().getContent();
        PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(content);

        // 源私钥-未加密的
        System.out.println(Base64.getEncoder().encodeToString(content));

        // 先加密
        JceOpenSSLPKCS8EncryptorBuilder builder =
                new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.PBE_SHA1_3DES);

        builder.setIterationCount(10000);
        builder.setPasssword("Hello.World.".toCharArray());

        OutputEncryptor outputEncryptor = builder.build();
        PKCS8Generator pkcs8Generator =
                new PKCS8Generator(PrivateKeyInfo.getInstance(pkcs8.getEncoded()), outputEncryptor);

        String encrypted_pkcs8_key = "";
//        try (PemWriter writer1 = new PemWriter(new PrintWriter(System.out)))
//        {
//            writer1.writeObject(pkcs8Generator);
//
//            PemObject pemObject = pkcs8Generator.generate();
//            System.out.println();
//            System.out.println((encrypted_pkcs8_key = Base64.getEncoder().encodeToString(pemObject.getContent())));
//        }
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             PemWriter writer = new PemWriter(new OutputStreamWriter(bout, StandardCharsets.UTF_8)))
        {
            writer.writeObject(pkcs8Generator);
            writer.flush();
            System.out.println();
            System.out.println((encrypted_pkcs8_key = (bout.toString())));
        }

        // 再解密
        PemReader pemReader = new PemReader(new StringReader(encrypted_pkcs8_key));
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(pemReader.readPemObject().getContent());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
        PBEKeySpec pbeKeySpec = new PBEKeySpec("Hello.World.".toCharArray()); // PBE
        SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);

        Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
        cipher.init(2, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = encryptedPrivateKeyInfo.getKeySpec(cipher);
        PrivateKey key1 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
        System.out.println();
        System.out.println(Base64.getEncoder().encodeToString(key1.getEncoded()));
    }

    /**
     * des加密
     *
     * @throws Exception
     */
    @Test
    public void testDESEncrypt() throws Exception
    {
        //初始化盐 "MY_KEY" 作为种子，生成的盐不变
        SecureRandom random = new SecureRandom("MY_KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        IvParameterSpec iv = new IvParameterSpec(salt);
        // 根据源码 这里的字符串转换的byte[]无论多长， 只会取byte[8]
        DESKeySpec dks = new DESKeySpec("11111111".getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] tgtBytes = cipher.doFinal("测试测试测试".getBytes(StandardCharsets.UTF_8));
        System.out.println("encrypt: " + Base64.getEncoder().encodeToString(tgtBytes));
    }

    /**
     * des解密
     *
     * @throws Exception
     */
    @Test
    public void testDESDecrypt() throws Exception
    {
        byte[] src = Base64.getDecoder().decode("PfJTkKyelH4p+q9mth599g8vi5MJF0fJ");

        //初始化盐 "MY_KEY" 作为种子，生成的盐不变
        SecureRandom random = new SecureRandom("MY_KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        IvParameterSpec iv = new IvParameterSpec(salt);
        // 根据源码 这里的字符串转换的byte[]无论多长， 只会取byte[8]
        DESKeySpec dks = new DESKeySpec("11111111".getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] debytes = cipher.doFinal(src);
        System.out.println("decrypt: " + new String(debytes, StandardCharsets.UTF_8));
    }

    /**
     * 3des 加密
     */
    @Test
    public void test3DESEncrypt() throws Exception
    {
        //初始化盐 "MY_KEY" 作为种子，生成的盐不变
        SecureRandom random = new SecureRandom("MY_KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        IvParameterSpec iv = new IvParameterSpec(salt);
        // 根据源码 这里的字符串转换的byte[]无论多长， 只会取byte[24]
        DESedeKeySpec dks = new DESedeKeySpec("111111111111111111111111".getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] tgtBytes = cipher.doFinal("测试测试测试".getBytes(StandardCharsets.UTF_8));
        System.out.println("encrypt: " + Base64.getEncoder().encodeToString(tgtBytes));
    }

    /**
     * 3des解密
     */
    @Test
    public void test3DESDecrypt() throws Exception
    {
        byte[] src = Base64.getDecoder().decode("V8Ev798jmHjogR9Gy0W1gVZKv1yICaog");

        //初始化盐 "MY_KEY" 作为种子，生成的盐不变
        SecureRandom random = new SecureRandom("MY_KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        IvParameterSpec iv = new IvParameterSpec(salt);
        // 根据源码 这里的字符串转换的byte[]无论多长， 只会取byte[24]
        DESedeKeySpec dks = new DESedeKeySpec("111111111111111111111111".getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] debytes = cipher.doFinal(src);
        System.out.println("decrypt: " + new String(debytes, StandardCharsets.UTF_8));
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
    @Test
    public void testPBEEncrypt() throws Exception
    {
        String src = "http://www.google.com";
        //初始化盐
        SecureRandom random = new SecureRandom("KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        //-----------口令及秘钥------------
        String password = "Admin@123890";
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
        SecretKey key = factory.generateSecret(pbeKeySpec);
        //------加密处理---------
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 1024);
        Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
        cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
        byte[] bytes = cipher.doFinal(src.getBytes());
        System.out.println("jdk pbe encrypt: " + Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void testPBEDecrypt() throws Exception
    {
        byte[] enbytes = Base64.getDecoder().decode("asZRqkddtPiFXU7fn/I26FY2LZePerDH");
        //初始化盐
        SecureRandom random = new SecureRandom("KEY".getBytes());
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        String password = "Admin@123890";
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
        SecretKey key = factory.generateSecret(pbeKeySpec);

        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 1024);
        Cipher cipher = Cipher.getInstance("PBEWithSHA1AndDESede");
        cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpec);
        enbytes = cipher.doFinal(enbytes);
        System.out.println("jdk pbe decrypt: " + new String(enbytes));

    }

    @Test
    public void testAESEncrypt() throws Exception
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = new SecureRandom("MY_KEY".getBytes());

        // 这里生成盐值的随机数种子和生成 AES SecretKey的相同，也可指定其他种子去生成盐值
        byte[] salt = new byte[16];
        sr.nextBytes(salt);

        keyGenerator.init(256, sr);
        SecretKey key = keyGenerator.generateKey();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
        String src = "http://www.google.com";
        byte[] enbytes = cipher.doFinal(src.getBytes());
        System.out.println("jdk aes-256 encrypt: " + Base64.getEncoder().encodeToString(enbytes));
    }

    @Test
    public void testAESDecrypt() throws Exception
    {
        String src = "3AlXCDWfbf03e1CbOtwv4GHr3dpvXJ5fch8zoSfFLBg=";

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = new SecureRandom("MY_KEY".getBytes());

        // 这里生成盐值的随机数种子和生成 AES SecretKey的相同，也可指定其他种子去生成盐值
        byte[] salt = new byte[16];
        sr.nextBytes(salt);

        keyGenerator.init(256, sr);
        SecretKey key = keyGenerator.generateKey();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(salt));

        byte[] debytes = cipher.doFinal(Base64.getDecoder().decode(src));
        System.out.println("jdk aes-256 decrypt: " + new String(debytes));
    }

    /**
     * 根据源码 不同的方式生成AES key
     * <p>
     * 结果表明使用KeyGenerator 256 加上固定字符串产生的16位盐值生成的key 等同于下面的 该固定字符串产生的16位盐值，加上产生的32位密码 生成的key
     * <p>
     * 不加盐值的情况下，则要简单的多， 相当于无论有无盐值，都是根据相同字符串作为种子，随机生成的32位byte密码 (32取决于init参数中的 256/8)
     */
    @Test
    public void testAESEncrypt2() throws Exception
    {
        // 这里生成盐值的随机数种子和生成 AES SecretKey的相同，也可指定其他种子去生成盐值
        byte[] salt = new byte[16];
        SecureRandom sr = new SecureRandom("MY_KEY".getBytes());
        sr.nextBytes(salt);

        byte[] passwd = new byte[32];
        sr.nextBytes(passwd);
        SecretKey key = new SecretKeySpec(passwd, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
        String src = "http://www.google.com";
        byte[] enbytes = cipher.doFinal(src.getBytes());
        System.out.println("jdk aes-256 encrypt: " + Base64.getEncoder().encodeToString(enbytes));
    }

    @Test
    public void testAESDecrypt2() throws Exception
    {
        String src = "3AlXCDWfbf03e1CbOtwv4GHr3dpvXJ5fch8zoSfFLBg=";

        // 这里生成盐值的随机数种子和生成 AES SecretKey的相同，也可指定其他种子去生成盐值
        byte[] salt = new byte[16];
        SecureRandom sr = new SecureRandom("MY_KEY".getBytes());
        sr.nextBytes(salt);

        byte[] passwd = new byte[32];
        sr.nextBytes(passwd);
        SecretKey key = new SecretKeySpec(passwd, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(salt));

        byte[] debytes = cipher.doFinal(Base64.getDecoder().decode(src));
        System.out.println("jdk aes-256 decrypt: " + new String(debytes));
    }
}
