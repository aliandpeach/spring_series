package com.crypto.cert;

import cn.hutool.core.io.resource.ClassPathResource;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.io.pem.PemObject;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
    private final String id_rsa = "MIIEogIBAAKCAQEApfl8p0vbZF5gXKcJPFVyJkpVloIt7mtmVc91gR6XwmD9OyUpRX19oeAGenuw+HeHUvHFQR0vyM1byZKdM2qp3wf7UhZK9xQ+NRche2wjIVBiB/0iF8+BnkorpjOOBtHuGxuLa4RNxdj/cWt7lA6yZQAMBcDIuYk5pmSk5/RzVH4xQiN0xrsiDS2EUu0wQqZAWe6ypcG59a6gex3zvtPuvJaNIbkEvFKxrzIkMTwJJiGEvg/Kdcq6sDp3EIRwMMMKFLDwaa18WvkuQcsRLGGjAzAC0hqRP9EEmdGyBJQnjh3WVeksd07pzw96Mv85CpWjkmlQ+Dt07wvr31i9UZzNjwIDAQABAoIBAGmGil7DuCXEa3f9K74UhZMax/f9pL5lwpbkZE1H6i1IBTlJk0f/VZVKHlRZuFcBiCSQW13e9Lay+pzafuBl/MM1C/FRAzC8yiBvKHaUdnGD7hAuPTLuV/cQKVhuhuqHJuBNTwN5Bwm3whGWyOeFwJ6+vOYbgIOWX/UPIFnp7jnVS50NERM4EsH3jmhxACLkmpMDSKCoGx+GdrDAJ13kal8q7ORFgBmh12wUYjLNxFF07Qw02DrnRbBltR153RvZdi1OF+E+wdaBdXlFWQSOXewZoHGxIJGV6RDIp1FusCIQb9ohHNOzGM0+nTv4FWJCu26IEGEadeI8qLA3OqorZCECgYEA1tgnP8aAUxqP4rq8+/vmp/crqOH/0GAJfWNGceMpnxujhHd9B14IswqGnk6185K+g9pL30GMvMjrypXyE/dF519z6LvLjgJePvhtJkb4MDMr6wOyILU5wMXDlu0yjaJABDN+acRkbDs7D78wQHF3ktMUyD895yy13/0WZaoWhkUCgYEAxcTJUsJ9Xtj+gSFxyrn6H9jCuItBKgwvp2kxYMq1sBB8/6zl+WR4VkqNzCzKT3rNyr5ZpyHc6oQG9DgHP6hbO+FsR+X4t8gIluW4u8YVEQmGVpmipPfck0m8OHhw5ukvLXb5c1136vWq0cAuHjVXY5UpiL1yGHtfMeu+5X5yW8MCgYB7VuxxeIwDKaShDYOhRiRNGWE/Oz+ZaQkJqwyuJTOn3D4rtk1ZBWsJHmnaSGW8x+oH2DLmoMMjPlXfn+WyAKtpASuR2P4rMMDaddRzvRqO0VHNQfnpPSFCwkZfRx1WomcOAmH4Zn8gg4CMsb4JwRpceEHeFDB9nvVq+ej5xIPZ4QKBgHvGit4CHacUFk1JWkewGAyAhHYtqY4gbF7RpnbWx9m2qBT46EIrEGmpZY/I5KHT0SK3+bJxrisUkKmeu2KrAta6YiOpmf2j80RF6FWVRrAQDwT6SOIIwwNn3aZWMwtCHCXPVZZPhHMIaG24YuTDK7uGqqULNHKAHHdI/YPynK4zAoGAdHpaXlFlTxeS/6sDZAitH2Morgh5aXXoqbPIRHzYK5wuHk/FWYNkREcDBl26CsNJ/djQb8uLOcx+ZeGS9CbJgD14FF0phxMFwZ6ghjp32wU1qVZweCUFVLFtn4qL0D2BcSd1OabxNrv2vGS8lEdJxaBF0Kwu8UgE32eU22Dlsks=";
    // openssh生成的 id_rsa.pub内容
    private final String original_id_rsa_pub = "AAAAB3NzaC1yc2EAAAADAQABAAABAQCl+XynS9tkXmBcpwk8VXImSlWWgi3ua2ZVz3WBHpfCYP07JSlFfX2h4AZ6e7D4d4dS8cVBHS/IzVvJkp0zaqnfB/tSFkr3FD41FyF7bCMhUGIH/SIXz4GeSiumM44G0e4bG4trhE3F2P9xa3uUDrJlAAwFwMi5iTmmZKTn9HNUfjFCI3TGuyINLYRS7TBCpkBZ7rKlwbn1rqB7HfO+0+68lo0huQS8UrGvMiQxPAkmIYS+D8p1yrqwOncQhHAwwwoUsPBprXxa+S5ByxEsYaMDMALSGpE/0QSZ0bIElCeOHdZV6Sx3TunPD3oy/zkKlaOSaVD4O3TvC+vfWL1RnM2P";

    /**
     * openssl genrsa -out privkey.pem 2048   生成没有加密的私钥
     * openssl pkcs8 -topk8 -inform PEM -in privkey.pem -outform pem -nocrypt -out pkcs8.pem
     * openssl rsa -in privkey.pem -pubout -out public.pem ( 使用privkey.pem 或 pkcs8.pem计算的公钥一致)
     */
    private final String openssl_rsa = "MIIEpAIBAAKCAQEAunt0BLLWlB7jy7R6OZzadjXpeTSsIY95l9RVEZPc9PxNnIo/g1V9NVpBRY+ivVyc7kuQ4F00AUOCWKma10wHrWF7xniL69mC92KqrL/8W3aya6splE1yi17HSBCajvOJjBpkAM0yUlreZzdVAjyBGU1Y0NgMwAVu13QKal/PgrvmO9hj7dnyEDw571YQNBEOrOTTkAAKLSsSZ7zWahuq4TJxGTdfDw9wFCzA3sosZt8aUAqk6uxa1K2O/q0CT65Hl2VLekL9iA1Axdl36b/0s0Sl/al7BbsEHVPsGH6P5yrebO7CUtYcql+QBpRbP3WAqpDVBSHCHLsD+3kFpJ4UdwIDAQABAoIBAHM3y5sLlCC4ZS57N/lYvHHFJZfJv4CM0SfJ/TV7Ek4bhShvEobmzxGjzihYcshk7GEfN0gozfTp7SMyx4S2aRfq3zrlSC5UmeKXh7RselNYpyx2+J20rE0IJkCUYappNlbmB1unbKwNkNRdD5zuHSWw8Gs40cfik3VzMOxAfknbGInJiljfEvDgwytgbA/hseP+wNtRpkQiYtc4UXSq0RubekKwWS1SoNBjhoiYXHOVoOuZ6DU2W7L2XqrLe1sp6YO12ma8QmMjXJ+sy4YgiOUrqfwo34XUk25bVEtP56U5m3UvfG+0A9qAqOsgyJyrFuT3/4VCtbFchbMo3BR8g8ECgYEA21vC1j0I3snaF1UsN+sOhHekbRMPPwmlQI+Cj2OD72iB80Wb+tb85unP7/2Wm7+dtW7hTD/caKWdOnqU4DZRcRBWTNHCru0TjOhZB4I69wbrlASYtEqkopV1KQeK+WcNWG7bzAuSVTL1HMLOqw7zf2yS+dgypb0VYJ/wHQqQfVkCgYEA2aHVv6V6b8HFHaUKUCcjDN2FJ9WQALCBJdGu4WiV7PJTVyvfzIZ+oe34bPZZl896HS13NlTWB1EqXhFeufFFiudDADJ1+2iWuSGlfhE1PFqPWHrHngyRWmfwJN3+N0FQoMf2YBExs0uWOqTlc+qv20H+iS5JGHQRBVqvIABX1k8CgYBueWJA33BOM76ArcadSuZ+1Hqc7FJrXwI4543LhUT3F90Kle4egJPR/8NXjJGDUxesPnF9I1Rv56itwPqliPWvSZkNCz9PNdr4xjPAUlg7/OI8I6x8cHNF5pug1Emrnbjc5sgPlWxXOFi16W6IO/lqHVZQOhAX5IiVpzcYiS+H0QKBgQDQ0aLJ35qqTu+28RwDKnpMJuVUAmvI7ZMN2bMGX5azoxRqaRO1KDurXi3nRz1SF+bgxjZDsIUowi130wzkN2f1zs6QCtr/3yv3+RvU6ef9wBsY175fnUl+yIo4pbT+CbK2gweYyAa7NcSxAnWTktUn+Zukvv+t8fg+/fNZLlsZqwKBgQCl/PrdMdwWzFaD33+rq7jE84koPqtYkw2mHMRM2wRQ212zxrUeSzqVZDPMzamc3TY8cVLgRUKgdFA6JAoSTNh+G+ODnMxnDq3Jv77ClbPgMlNBdr1Ev9kwZdGU+3+wRDA3BdFGm6bK8aOjA3d6vHyc+alH4Y2CKli0wjiIwAbO6w==";
    private final String pkcs8_rsa = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC6e3QEstaUHuPLtHo5nNp2Nel5NKwhj3mX1FURk9z0/E2cij+DVX01WkFFj6K9XJzuS5DgXTQBQ4JYqZrXTAetYXvGeIvr2YL3Yqqsv/xbdrJrqymUTXKLXsdIEJqO84mMGmQAzTJSWt5nN1UCPIEZTVjQ2AzABW7XdApqX8+Cu+Y72GPt2fIQPDnvVhA0EQ6s5NOQAAotKxJnvNZqG6rhMnEZN18PD3AULMDeyixm3xpQCqTq7FrUrY7+rQJPrkeXZUt6Qv2IDUDF2Xfpv/SzRKX9qXsFuwQdU+wYfo/nKt5s7sJS1hyqX5AGlFs/dYCqkNUFIcIcuwP7eQWknhR3AgMBAAECggEAczfLmwuUILhlLns3+Vi8ccUll8m/gIzRJ8n9NXsSThuFKG8ShubPEaPOKFhyyGTsYR83SCjN9OntIzLHhLZpF+rfOuVILlSZ4peHtGx6U1inLHb4nbSsTQgmQJRhqmk2VuYHW6dsrA2Q1F0PnO4dJbDwazjRx+KTdXMw7EB+SdsYicmKWN8S8ODDK2BsD+Gx4/7A21GmRCJi1zhRdKrRG5t6QrBZLVKg0GOGiJhcc5Wg65noNTZbsvZeqst7Wynpg7XaZrxCYyNcn6zLhiCI5Sup/CjfhdSTbltUS0/npTmbdS98b7QD2oCo6yDInKsW5Pf/hUK1sVyFsyjcFHyDwQKBgQDbW8LWPQjeydoXVSw36w6Ed6RtEw8/CaVAj4KPY4PvaIHzRZv61vzm6c/v/Zabv521buFMP9xopZ06epTgNlFxEFZM0cKu7ROM6FkHgjr3BuuUBJi0SqSilXUpB4r5Zw1YbtvMC5JVMvUcws6rDvN/bJL52DKlvRVgn/AdCpB9WQKBgQDZodW/pXpvwcUdpQpQJyMM3YUn1ZAAsIEl0a7haJXs8lNXK9/Mhn6h7fhs9lmXz3odLXc2VNYHUSpeEV658UWK50MAMnX7aJa5IaV+ETU8Wo9YeseeDJFaZ/Ak3f43QVCgx/ZgETGzS5Y6pOVz6q/bQf6JLkkYdBEFWq8gAFfWTwKBgG55YkDfcE4zvoCtxp1K5n7UepzsUmtfAjjnjcuFRPcX3QqV7h6Ak9H/w1eMkYNTF6w+cX0jVG/nqK3A+qWI9a9JmQ0LP0812vjGM8BSWDv84jwjrHxwc0Xmm6DUSauduNzmyA+VbFc4WLXpbog7+WodVlA6EBfkiJWnNxiJL4fRAoGBANDRosnfmqpO77bxHAMqekwm5VQCa8jtkw3ZswZflrOjFGppE7UoO6teLedHPVIX5uDGNkOwhSjCLXfTDOQ3Z/XOzpAK2v/fK/f5G9Tp5/3AGxjXvl+dSX7IijiltP4JsraDB5jIBrs1xLECdZOS1Sf5m6S+/63x+D7981kuWxmrAoGBAKX8+t0x3BbMVoPff6uruMTziSg+q1iTDaYcxEzbBFDbXbPGtR5LOpVkM8zNqZzdNjxxUuBFQqB0UDokChJM2H4b44OczGcOrcm/vsKVs+AyU0F2vUS/2TBl0ZT7f7BEMDcF0Uabpsrxo6MDd3q8fJz5qUfhjYIqWLTCOIjABs7r";
    private final String public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAunt0BLLWlB7jy7R6OZzadjXpeTSsIY95l9RVEZPc9PxNnIo/g1V9NVpBRY+ivVyc7kuQ4F00AUOCWKma10wHrWF7xniL69mC92KqrL/8W3aya6splE1yi17HSBCajvOJjBpkAM0yUlreZzdVAjyBGU1Y0NgMwAVu13QKal/PgrvmO9hj7dnyEDw571YQNBEOrOTTkAAKLSsSZ7zWahuq4TJxGTdfDw9wFCzA3sosZt8aUAqk6uxa1K2O/q0CT65Hl2VLekL9iA1Axdl36b/0s0Sl/al7BbsEHVPsGH6P5yrebO7CUtYcql+QBpRbP3WAqpDVBSHCHLsD+3kFpJ4UdwIDAQAB";

    /**
     * JKS提取私钥 -> 格式为PKCS8
     */
    @Test
    public void convertKeystoreToPCKS12() throws Exception
    {
        KeyStore rsakeystore = KeyStore.getInstance("JKS");
        rsakeystore.load(new FileInputStream(new ClassPathResource("rsa.jks").getFile()), "storepasswd".toCharArray());
        PrivateKey key = (PrivateKey) rsakeystore.getKey("crazy", "keypasswd".toCharArray());
        byte[] encodedKey = key.getEncoded();
        // 导出pkcs8格式的私钥
        String base64KeyString = Base64.getEncoder().encodeToString(encodedKey);
        System.out.println();
        System.out.println(base64KeyString);
        System.out.println();

        // 1. encodedKey 是rsa.jks 中的 PKCS8不加密的格式的私钥
        // 2. 使用keytool 转换rsa.jks 为 pkcs12后使用命令提取私钥： openssl pkcs12 -nocerts -nodes -in test.p12 -out test-key.pem
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
        keyManagerFactory.init(rsakeystore, "keypasswd".toCharArray());

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
     *    转换为PKCS8 - openssl pkcs8 -topk8 -inform PEM -in privkey.pem -outform pem -nocrypt -out pkcs8.pem
     * <p>
     * 2. 生成私钥 - openssl genrsa -out privkey.pem 2048   生成没有加密的私钥
     *    java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
     *
     *
     *    openssl rsa : -----BEGIN RSA PRIVATE KEY-----
     *    pkcs8 rsa : -----BEGIN PRIVATE KEY-----
     *
     * 生成公钥 ： openssl rsa -in privkey.pem -pubout -out public.pem ( 使用privkey.pem 或 pkcs8.pem计算的公钥一致)
     */
    @Test
    public void readOpenSSL_RSA() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // pkcs8_rsa 就是根据openssl_rsa 通过命令转换来的 ( openssl pkcs8 -topk8 -inform PEM -in privkey.pem -outform pem -nocrypt -out pkcs8.pem )

        // 用 BouncyCastle读取openssl生成的没有加密的rsa私钥
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(openssl_rsa));
        PrivateKey key1 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
        System.out.println("key1=" + key1);

        // 读取 openssl生成的rsa 然后转换的pkcs8格式私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec2 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pkcs8_rsa));
        PrivateKey key2 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec2);
        System.out.println("key2=" + key2);
        // openssl生成的 rsa私钥 和 在被 openssl转换出来的 pkcs8 私钥, 代入java中, 得到的对象相同
        Assert.assertArrayEquals(key1.getEncoded(), key2.getEncoded());
        Assert.assertTrue(key1.equals(key2));

        // 根据私钥计算公钥 相当于 ： openssl rsa -in privkey.pem -pubout -out public.pem
        RSAPrivateCrtKey privk = (RSAPrivateCrtKey) key2;
        RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        // -----BEGIN PUBLIC KEY-----
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // -----END PUBLIC KEY-----
        System.out.println("publicKeyString=" + publicKeyString);
        // 根据java计算的公钥 和openssl计算的公钥是相同的
        Assert.assertTrue(publicKeyString.equals(public_key));
    }

    /**
     * 读取openssh生成的私钥 - ssh-keygen -t rsa
     */
    @Test
    public void readOpenSSH_rsa() throws NoSuchAlgorithmException, InvalidKeySpecException
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
     */
    @Test
    public void exportToPKCS12() throws Exception
    {
        KeyStore rsakeystore = KeyStore.getInstance("JKS");
        rsakeystore.load(new FileInputStream(new ClassPathResource("rsa.jks").getFile()), "keystore库文件密码".toCharArray());

        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);
        Enumeration<String> aliases = rsakeystore.aliases();
        while (aliases.hasMoreElements())
        {
            String alias = aliases.nextElement();
            PrivateKey k = (PrivateKey) rsakeystore.getKey(alias, "alias私钥密码".toCharArray());
            Certificate[] c = rsakeystore.getCertificateChain(alias);
            pkcs12.setKeyEntry(alias, k, "alias_entry_passwd".toCharArray(), c);
        }
        pkcs12.store(new FileOutputStream("D:\\test.p12"), "P12_store_file_passwd".toCharArray());
    }

    /**
     * 被加密了的私钥:
     *
     * 1. 生成 rsa :                    openssl genrsa -aes256 -out privkey.pem 2048
     * 2. 转换为 pkcs8 encrypted rsa :  openssl pkcs8 -topk8 -in privkey.pem -outform PEM -out pkcs8_privkey.pem
     *
     * 无法使用java的AES256、3DES、DES 解密, 因为openssl 加密过程中的padding 不一样
     *
     *
     * 换一种思路：
     *   1. 生成没有加密的RSA 私钥: openssl genrsa -out privkey.pem 2048
     *   2. 转换为没有加密的PKCS8 : openssl pkcs8 -topk8 -inform PEM -in privkey.pem -outform pem -nocrypt -out pkcs8.pem
     *   3. 使用AES-256 加密
     *   但是这样比较麻烦，且不能使用 java api 直接解密 （EncryptedPrivateKeyInfo）
     *
     * 直接使用 bouncycastle生成加密的pkcs8私钥 - 参考方法: generatPKCS8_Encrypted
     */
    @Test
    public void testEncryptedRSAPrivateKey() throws Exception
    {
        /**
         * 这段代码的私钥格式为 PKCS8 使用 bouncycastle生成的
         */
        String common_client_key = "MIIE9jAoBgoqhkiG9w0BDAEDMBoEFOE9zggeecmUuT9EyHoxk9giOwmyAgIIAASCBMhdhC3oBUSaX4otUDym+vJYdMvHu17NJNim588AfHWZknJHYSvPqaSRfDclYnQkiKWk4GtTVkAcCmNWahVr9DIIpdXV1VjWTc+Hy7M00h5SP6ybV0YiwoF6vgq/lOaqz4rS9blA3uD00/h7Ffup6ocCwvujTQL8UQ5RCg2tu+cLnwonabz6DcB6Lg3wcJQBgIqRX435mqaDj3gdl6GME1k/1xynxF3LytTycS9f2qCeefv5f6iLp1mW6xNKMwWUUcGItCJYyuQefYNwpznoW0wRNKV8Us41IT7Obcq/3ZdUQaG0E/8/4FUcN0mfoR4iQyfzqCS/Kjdn7h79M3naj5FoRst80AIfObc9JBmAvwdROtnodl5gWIPrIfxOcYezf87eoZZupVYsE5ihv3PYvuWoR73Yy/sOxU8PToin7Ah5EhXZNGWKcE6VC2W51bLSguwBxm994+c6GcYP6y0dtVNFWaiqux9OUf+9Xdbmfe7uyQIXMlZZJ4P+WZ3SJjLPNywGkwk18fjBvbOY3/YlBtBbu7dDrz4DLl3b1A1oCTMF+/eiNCoajtZB1UsBEDh9c/hLLnnyKPX1BhWZmo8yzXHf5YonGAS+ie93ulRnyE7ckTR4esjVTprDI2g4TLAeHH8hXm6afrjuxQTIzzLWuOOo9G/rOzxZCtdrhGb+G3MOz+XdIQMHgUlStqXzvNcrq8Tkj/gQyDg1V+no4KKojo69aTL0fk9Vb/z9UE5djTVwoEyirabLi3Krzbwklcl6im8s5zPUgoE6l0rTom1t9p3yWGMw5EM3CTK8Refq6tJC4HmixEumHfGB9vQNZPyLH+RhfGkyWpB1qMr9BmvYZvr0T6/YWJGsn9rtYwLSeaMzvfoJ0RuvX4f8EIS+CCEqxDTJc2n+qPgU3oCC+jRK6BuALzUeb4SODhTv7wrBPcvi0TjjfrG9upfPkfNjESAp+wcfhraopzZe6hJm//nn5nXqeymscfVp0kDEOVkPAuRxizCDmV81HOtNtW8SxINUROogaQlfIW5rK9TnP4PyWhurSGKBiWbNCgLEijiXU0Ku7ed2B/DLvrGPhKMfHafOSsEntWUuG5gvVbqwPf9gs0qavt2x+rrel786zgINNg4BiOhhP+zPAFcpWqWkxFd7A/JjciEGLbjK7Zz4qfZpKBFUJbGlI42IPxCcJuPFwXi8ybx9VgLHxy32MOituzjRpZmWiXvmafwSHauIDOYyEFp74xXCZGWo1dgx5vE9kxUI48OPO0h+g73elRo+g/e82M9twc0/Ju3G7tbDc5zdVlcR8eYzVG7RliBLqMK317O44D3S1dghCH+4ToNA7rT8ZFxqK9pRgS/FpEXx/4KFXLYVE1IzRBHH8lWDeZ279vIRjknJ71KVBCM30DsKTBOWQiF/eL+VzZ8JIBof/BEDxBwrESOjVXcoGaICgVBL7SeeLfBnoUQMKkMxGpr6ZZXy42Mn6MmglC+qte2AGwU+uQfR8OTSoP1czLeQ1M1/BaQaF0e7aCUFMdeez1hssLjjYJTqKONJn4wQuSkL3TT/2MIiY+4hFqRtOQarfEcxGDhaGkGaJVW2MNN2xaAM0nhD0RNFp6Nb01I+42w58jlQ0bXp776HgkmWHI0=";
        byte[] common_client_key_bytes = Base64.getDecoder().decode(common_client_key);

        // ByteBuffer buffer = SymmetricEncryption.getInstance("LmqZy28M3lHBfcJuD9tL".getBytes(), "LmqZy28M3lHBfcJuD9tL".getBytes()).pbeDecrypt(common_client_key_bytes);

        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(common_client_key_bytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
        PBEKeySpec pbeKeySpec = new PBEKeySpec("LmqZy28M3lHBfcJuD9tL".toCharArray()); // PBE
        SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);

        Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
        cipher.init(2, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = encryptedPrivateKeyInfo.getKeySpec(cipher);
        PrivateKey key1 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
        System.out.println(key1);
    }

    /**
     * bouncycastle - format PKCS#1 to PKCS#8
     *
     * 参考 : https://www.cnblogs.com/adylee/p/3625389.html
     *       https://www.cnblogs.com/LiuYanYGZ/p/12519029.html
     */
    @Test
    public void formatPkcs1ToPkcs8() throws Exception
    {
        try (ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.getDecoder().decode(openssl_rsa)))
        {
            // pkcs1格式转换为pkcs8
            ASN1Primitive rsaPrivateKey = asn1InputStream.readObject();
            PrivateKeyInfo privateKeyInfo
                    = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption), rsaPrivateKey);
            byte[] bytes = privateKeyInfo.getPrivateKey().getOctets();

            // 与 使用 openssl转换的pkcs8 比较
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec2 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pkcs8_rsa));
            PrivateKey key2 = KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec2);
            System.out.println(Arrays.toString(key2.getEncoded()));
            // bytes 的去除前26个byte, 就一致了
            Assert.assertArrayEquals(bytes, key2.getEncoded());
        }
    }

    /**
     * 使用 bouncycastle生成加密的 PKCS8格式的RSA私钥
     *
     * https://stackoverflow.com/questions/14580958/exception-when-constructing-encryptedprivatekeyinfo
     *
     * @throws NoSuchAlgorithmException
     * @throws OperatorCreationException
     * @throws IOException
     *
     *
     * 以后可以不再使用keystore存储私钥
     *
     * 1. 使用openssl 生成私钥、证书、签名等, 私钥转换为 pkcs8
     * 2. 再使用 bouncycastle 对私钥进行加密
     */
    @Test
    public void generatPKCS8_Encrypted() throws NoSuchAlgorithmException, OperatorCreationException, IOException
    {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        final PrivateKey privKey = keyPair.getPrivate();
        final PublicKey pubKey = keyPair.getPublic();

        JceOpenSSLPKCS8EncryptorBuilder builder =
                new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.PBE_SHA1_3DES);

        builder.setIterationCount(10000);
        builder.setPasssword("Hello, World!".toCharArray());

        OutputEncryptor outputEncryptor = builder.build();
        PKCS8Generator pkcs8Generator =
                new JcaPKCS8Generator(privKey, outputEncryptor);

        try (PemWriter writer = new PemWriter(new PrintWriter(System.out)))
        {
            writer.writeObject(pkcs8Generator);
        }
    }

    // Write to pem file
    private static String format2PemString(String type, byte[] key) throws Exception
    {
        PemObject pemObject = new PemObject(type, key);
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        String pemString = stringWriter.toString();
        return pemString;
    }


    /**
     * openssl pkcs12 -nocerts -des -in test.p12 -out test-key.pem
     * <p>
     * openssl pkcs12 -nocerts -in test.p12 -out test-key.pem
     * <p>
     * 该命令生成-----BEGIN ENCRYPTED PRIVATE KEY----- 默认是des加密
     * <p>
     * 也可以指定为 -aes256 -des3
     * <p>
     * 由上述命令提取的私钥默认加密
     * <p>
     */
    @Test
    public void testLoadPKCS8Encrypt() throws Exception
    {
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
