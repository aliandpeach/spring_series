package com.yk.crypto;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HexUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利用RSA 对文件加密
 */
public class EnDecryptUtil
{
    
    private static Map<Integer, byte[]> keyMap = new HashMap<Integer, byte[]>();  //用于封装随机产生的公钥与私钥
    
    private RSA2048Util rsa;
    
    public EnDecryptUtil(char[] storepasswd, char[] keypasswd)
    {
        rsa = RSA2048Util.getInstance(storepasswd, keypasswd);
    }
    
    /**
     * 源文件转加密为RSA2048字节和HEX字符串 ( 分割加密转换后的hex末尾以换行符标记)
     *
     * @param srcf       源文件
     * @param tof        按encryptLen长度分割加密后合并的文件
     * @param toHexf     按encryptLen长度分割加密后的字节, 转换为Hex后合并的字符串
     * @param encryptLen 要加密的字节长度(由于RSA的特殊限制，2048加密的字节范围应该是 1-245)
     * @throws Exception
     */
    public void fileEncryptToHex(String srcf, String tof, String toHexf, int encryptLen) throws Exception
    {
        if (encryptLen < 1 || encryptLen > 245)
        {
            throw new RuntimeException("encrypt bytes array length incorrect");
        }
        
        //生成公钥私钥
        // genKeyPair();
        loadKeyPair();
        // 加密文件（RSA2048每次只能加密245字节，加密后（无论被加密的字节数是多少）的结果是256字节）
        try (InputStream inputStream = new FileInputStream(new File(srcf));
             OutputStream outputStream = new FileOutputStream(new File(tof));
             FileWriter writer = new FileWriter(toHexf))
        {
            int len = -1;
            byte[] buf = new byte[encryptLen];
            while ((len = inputStream.read(buf)) != -1)
            {
                if (len < encryptLen)
                {
                    // 源文件最后读取的部分可能小于245字节
                    byte[] temp = new byte[len];
                    System.arraycopy(buf, 0, temp, 0, len);
                    byte[] encrypt = encrypt(temp, keyMap.get(0));
                    String hex = new BigInteger(1, encrypt).toString(16);
                    outputStream.write(encrypt);
                    writer.write(hex);
                    break;
                }
                byte[] encrypt = encrypt(buf, keyMap.get(0));
                String hex = new BigInteger(1, encrypt).toString(16);
                writer.write(hex + "\n");
                outputStream.write(encrypt);
            }
        }
    }
    
    /**
     * RSA2048解密HEX内容的文件 ( 按照换行符逐行读取)
     *
     * @param srcHexf    16进制内容的源文件 (按行读取)
     * @param tof        解密后的文件
     * @param decryptLen 分割解密的长度 （根据RSA2048的特性，任何1-245的字节都会被加密为256长度的字节）
     * @throws IOException
     * @throws UnrecoverableKeyException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public void hexDecryptToFile(String srcHexf, String tof, int decryptLen) throws IOException,
            UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException
    {
        if (decryptLen != 256)
        {
            throw new RuntimeException("decrypt bytes length incorrect");
        }
        
        loadKeyPair();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(srcHexf)), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader);
             OutputStream outputStream = new FileOutputStream(new File(tof));)
        {
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
            {
                line = line.replace("\n", "");
                byte array[] = new BigInteger(line, 16).toByteArray();
                byte[] temp = new byte[decryptLen];
                // 经过BigInteger计算的byte 可能会有符号的题
    
                BinHexSHAUtil.to(array, temp);
                
                //只能按照每次取出256个字节来解密（密文文件的大小 % 256 必须等于0）
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(temp))
                {
                    int len = -1;
                    byte[] buf = new byte[decryptLen];
                    while ((len = inputStream.read(buf)) != -1)
                    {
                        try
                        {
                            byte[] decrypt = decrypt(buf, keyMap.get(1));
                            outputStream.write(decrypt);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 源文件转加密为RSA2048 并转换HEX字符串 (  分割加密转换后的HEX字符串不使用换行符标记)
     *
     * @param srcf       源文件
     * @param toHexf     按encryptLen长度分割加密后的字节, 转换为Hex后合并的字符串
     * @param encryptLen 要加密的字节长度(由于RSA的特殊限制，2048加密的字节范围应该是 1-245)
     * @throws Exception
     */
    public void fileEncryptToHex(String srcf, String toHexf, int encryptLen) throws Exception
    {
        if (encryptLen < 1 || encryptLen > 245)
        {
            throw new RuntimeException("encrypt bytes array length incorrect");
        }
        
        //生成公钥私钥
        // genKeyPair();
        loadKeyPair();
        // 加密文件（RSA2048每次只能加密245字节，加密后（无论被加密的字节数是多少）的结果是256字节）
        try (InputStream inputStream = new FileInputStream(new File(srcf));
             FileWriter writer = new FileWriter(toHexf))
        {
            int len = -1;
            byte[] buf = new byte[encryptLen];
            while ((len = inputStream.read(buf)) != -1)
            {
                if (len < encryptLen)
                {
                    // 源文件最后读取的部分可能小于245字节
                    byte[] temp = new byte[len];
                    System.arraycopy(buf, 0, temp, 0, len);
                    byte[] encrypt = encrypt(temp, keyMap.get(0));
                    String hex = HexUtil.encodeHexStr(encrypt);// length = 512
                    writer.write(hex);
                    break;
                }
                byte[] encrypt = encrypt(buf, keyMap.get(0));
                String hex = HexUtil.encodeHexStr(encrypt);
                // 末尾不加分隔符 "\n" 那么在读取的时候就只能按照每次读取512长度去做转换
                // BigInteger会忽略符号位，有时会被转换为510或者514所以这里不用BigInteger做转化了
                writer.write(hex);// length = 512
            }
        }
    }
    
    /**
     * RSA2048解密HEX内容的文件( 每次按512长度读取)
     *
     * @param srcHexf 16进制内容的源文件 (按行读取)
     * @param tof     解密后的文件
     * @throws IOException
     * @throws UnrecoverableKeyException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public void hexDecryptToFile(String srcHexf, String tof) throws Exception
    {
        loadKeyPair();
        try (FileInputStream fis = new FileInputStream(new File(srcHexf));
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr);
             OutputStream out = new FileOutputStream(new File(tof)))
        {
            int len = -1;
            char[] buffer = new char[512];
            while ((len = reader.read(buffer)) != -1)
            {
                if (len != 512)
                {
                   /* char[] temp = new char[len];
                    System.arraycopy(buffer, 0, temp, 0, len);
                    byte[] bytes = HexUtil.decodeHex(temp);
                    out.write(bytes);
                    break;*/
                    
                    // 不可能进来，源文件内的HEX字符串长度一定是 length % 512 == 0
                    // RSA2048加密后的byte[256] 转换为HEX就是char[512]
                    // 所以每次读取都应该是byte[512]或者char[512], 再转为byte[256]的加密字节数组进行解密
                    throw new RuntimeException("src hex file incorrect");
                }
                byte[] decrypt = HexUtil.decodeHex(buffer);
                byte[] bytes = decrypt(decrypt, keyMap.get(1));
                out.write(bytes);
            }
        }
    }
    
    public static byte[] copy(byte[] src, byte[] target)
    {
        byte[] bytes = new byte[src.length + target.length];
        System.arraycopy(src, 0, bytes, 0, src.length);
        System.arraycopy(target, 0, bytes, src.length, target.length);
        return bytes;
    }
    
    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException
    {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGenerator.initialize(2048, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        // 得到私钥字符串
        // 将公钥和私钥保存到Map
        keyMap.put(0, publicKey.getEncoded());  //0表示公钥
        keyMap.put(1, privateKey.getEncoded());  //1表示私钥
    }
    
    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public void loadKeyPair() throws NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException,
            KeyStoreException, IOException
    {
        keyMap.put(0, rsa.getPublicKey());  //0表示公钥
        keyMap.put(1, rsa.getPrivateKey());  //1表示私钥
    }
    
    /**
     * RSA公钥加密
     *
     * @param message   加密字符串
     * @param publicKey 公钥
     * @return byte[]
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(byte[] message, byte[] publicKey) throws Exception
    {
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(message);
    }
    
    /**
     * RSA私钥解密
     *
     * @param message    加密字符串
     * @param privateKey 私钥
     * @return byte[]
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(byte[] message, byte[] privateKey) throws Exception
    {
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(message);
    }
    
    /**
     * 对文件夹内的加密文件进行解密
     *
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws KeyStoreException
     * @throws InvalidKeySpecException
     * @throws IllegalBlockSizeException
     * @src 源文件夹
     */
    public void decrypt(String src) throws IOException, CertificateException, NoSuchAlgorithmException,
            UnrecoverableKeyException,
            InvalidKeyException, NoSuchPaddingException, BadPaddingException, KeyStoreException,
            InvalidKeySpecException, IllegalBlockSizeException
    {
        File dir = new File(src);
        List<File> listFile = FileUtil.loopFiles(dir, pathname -> pathname.getPath().contains("encrypt_"));
        for (File file : listFile)
        {
            String path = file.getParentFile().getCanonicalPath();
            String dest = path + File.separator + file.getName().replace("encrypt_", "decrypt_");
            boolean is = !new File(dest).exists() || new File(dest).delete();
            System.out.println(is);
            try (FileChannel out = new FileOutputStream(dest).getChannel();
                 FileChannel in = new FileInputStream(file).getChannel();)
            {
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                long size = in.size();
                // 通过RSA2048加密的文件，大小一定是size%256==0否则就是有问题的（1-245长度的byte[]都会被加密为byte[256]）
                System.out.println(size % 256);
                int len = -1;
                while ((len = in.read(byteBuffer)) != -1)
                {
                    byteBuffer.flip();
                    
                    ByteBuffer bytes = rsa.decrypt(byteBuffer.array());
                    out.write(bytes);
                    
                    byteBuffer.clear();
                }
            }
        }
    }
    
    /**
     * 对文件夹内的文件进行加密
     *
     * @param src    源文件夹
     * @param delete 是否删除原文件
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws KeyStoreException
     * @throws InvalidKeySpecException
     * @throws IllegalBlockSizeException
     */
    public void encrypt(String src, boolean delete) throws IOException, CertificateException, NoSuchAlgorithmException,
            UnrecoverableKeyException, InvalidKeyException, NoSuchPaddingException, BadPaddingException,
            KeyStoreException, InvalidKeySpecException, IllegalBlockSizeException
    {
        File dir = new File(src);
        List<File> listFile = FileUtil.loopFiles(dir, pathname -> !pathname.getPath().contains("encrypt_"));
        for (File file : listFile)
        {
            String path = file.getParentFile().getCanonicalPath();
            String dest = path + File.separator + "encrypt_" + file.getName();
            boolean is = !new File(dest).exists() || new File(dest).delete();
            System.out.println(is);
            try (FileChannel out = new FileOutputStream(path + File.separator + "encrypt_" + file.getName()).getChannel();
                 FileChannel in = new FileInputStream(file).getChannel();)
            {
                ByteBuffer byteBuffer = ByteBuffer.allocate(245);
                int len = -1;
                while ((len = in.read(byteBuffer)) != -1)
                {
                    byteBuffer.flip();
                    
                    if (byteBuffer.limit() < 245)
                    {
                        System.out.println();
                    }
                    // 读取文件最后部分的字节数可能小于245, ByteBuffer实际长度为limit, 所以每次根据limit的长度获取真实的byte[]
                    byte temp[] = new byte[byteBuffer.limit()];
                    byteBuffer.get(temp);
                    ByteBuffer bytes = rsa.encrypt(temp);
                    out.write(bytes);
                    
                    byteBuffer.clear();
                }
            }
            if (delete)
            {
                file.delete();
            }
        }
    }
}