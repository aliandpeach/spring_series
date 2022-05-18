package com.yk.base.x509;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.Principal;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/17 17:11:12
 *
 * clientAuth=want 只有客户端发来证书才会进行认证
 *
 * 外置tomcat 该类需打包后 置于tomcat-lib目录下
 *
 *
 * <Connector port="443" protocol="org.apache.coyote.http11.Http11NioProtocol"
 * SSLEnabled="true"
 * maxThreads="500" scheme="https" secure="true"
 * URIEncoding="UTF-8"
 * connectionTimeout="8000"
 * clientAuth="want"
 * allowTrace="false"
 * keystoreFile="key.keystore"
 * keystorePass="pwd" keyPass="pwd"
 * truststoreFile="trust.keystore"
 * truststorePass="pwd"
 * ciphers="TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,TLS_RSA_WITH_AES_256_GCM_SHA384,TLS_RSA_WITH_AES_128_GCM_SHA256,TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_RSA_WITH_AES_128_CBC_SHA256"
 * sslProtocol="TLS" sslEnabledProtocols="TLSv1.2"
 * server=" " trustManagerClassName="com.yk.base.x509.ServerX509TrustManager"
 * />
 *
 * 在Server.xml中配置了 trustManagerClassName 当客户端携带私钥，
 * 则 request.getAttribute("javax.servlet.request.X509Certificate") 可获取客户端证书 (而且都不用配置 truststoreFile )
 *
 * 如果不配置 trustManagerClassName, 则 request.getAttribute("javax.servlet.request.X509Certificate") 要获取客户端证书，
 * 必须在 truststoreFile信任库导入客户端的证书, 通过ca签发过的客户端证书，还需要导入ca证书
 * clientAuth 也需要设置为want
 *
 *
 * JSSESupport - getPeerCertificateChain 是获取证书的地方，在这里获取后，setAttribute("javax.servlet.request.X509Certificate", certs);
 */
public class ServerX509TrustManager implements X509TrustManager
{
    private Logger logger = LoggerFactory.getLogger("base");

    private X509TrustManager x509TrustManager;

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
    {
        logger.info("checkClientTrusted...");
        logger.info(s);
        logger.info(Arrays.toString(x509Certificates));
        if (null == x509TrustManager)
        {
            return;
        }
        x509TrustManager.checkClientTrusted(x509Certificates, s);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
    {
        logger.info("checkServerTrusted...");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[0];
    }

    /**
     * 验证 简书网的证书链 证书签名校验
     */
    public static void main(String args[]) throws Exception
    {
        String digitCA = "C:\\Users\\yangkai\\Desktop\\digit.der.cer";
        String middleCA = "C:\\Users\\yangkai\\Desktop\\midd.der.cer";
        String js = "C:\\Users\\yangkai\\Desktop\\js.der.cer";
        CertificateFactory digitCertFac = CertificateFactory.getInstance("X.509");
        X509Certificate digitCACer = (X509Certificate) digitCertFac.generateCertificate(new FileInputStream(digitCA));

        CertificateFactory midCertFac = CertificateFactory.getInstance("X.509");
        X509Certificate middleCACer = (X509Certificate) midCertFac.generateCertificate(new FileInputStream(middleCA));

        CertificateFactory jsCertFac = CertificateFactory.getInstance("X.509");
        X509Certificate jsCer = (X509Certificate) jsCertFac.generateCertificate(new FileInputStream(js));

        try
        {
            jsCer.verify(middleCACer.getPublicKey());// true
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            middleCACer.verify(digitCACer.getPublicKey());// true
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            jsCer.verify(digitCACer.getPublicKey());// false 第三级证书不能被root证书校验, 签名只能逐级校验
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        X509CertImpl xxx = new X509CertImpl(jsCer.getEncoded());
        Field field = xxx.getClass().getDeclaredField("info");
        field.setAccessible(true);
        X509CertInfo b = (X509CertInfo)field.get(xxx);
        String middleCACerSigAlgName = middleCACer.getSigAlgName(); // SHA256withRSA
        boolean boo = verifySign(middleCACer, jsCer.getSignature(), b.getEncodedInfo(), "SHA256withRSA");
        System.out.println(boo);// true

        X509CertImpl yyy = new X509CertImpl(middleCACer.getEncoded());
        Field fieldInfo = yyy.getClass().getDeclaredField("info");
        fieldInfo.setAccessible(true);
        X509CertInfo x509CertInfo = (X509CertInfo)fieldInfo.get(yyy);
        String digitCACerSigAlgOID = digitCACer.getSigAlgOID();
        String digitCACerSigAlgName = digitCACer.getSigAlgName();
        // 这里获取的是 SHA1withRSA, 但是代入 Signature.getInstance 的参数应该是SHA256withRSA 否则报 Signature encoding error
        boolean boo2 = verifySign(digitCACer, middleCACer.getSignature(), x509CertInfo.getEncodedInfo(), "SHA256withRSA");
        System.out.println(boo2);
    }

    public static boolean verify(X509Certificate X509certificateRoot,
                                 Collection<X509Certificate> chain, X509CRL X509crl, String stringTarget)
    {
        //获取证书链长度
        int nSize = chain.size();
        //将证书链转化为数组
        X509Certificate[] arX509certificate = new X509Certificate[nSize];
        chain.toArray(arX509certificate);
        //声明list，存储证书链中证书主体信息
        ArrayList<BigInteger> list = new ArrayList<>();
        //沿证书链自上而下，验证证书的所有者是下一个证书的颁布者
        Principal principalLast = null;
        for (int i = 0; i < nSize; i++)
        {   //遍历arX509certificate
            X509Certificate x509Certificate = arX509certificate[i];
            //获取发布者标识
            Principal principalIssuer = x509Certificate.getIssuerDN();
            //获取证书的主体标识
            Principal principalSubject = x509Certificate.getSubjectDN();
            //保存证书的序列号
            list.add(x509Certificate.getSerialNumber());

            if (principalLast != null)
            {
                //验证证书的颁布者是上一个证书的所有者
                if (principalIssuer.equals(principalLast))
                {
                    try
                    {
                        //获取上个证书的公钥
                        PublicKey publickey = arX509certificate[i - 1].getPublicKey();
                        //验证是否已使用与指定公钥相应的私钥签署了此证书
                        arX509certificate[i].verify(publickey);
                    }
                    catch (Exception e)
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
            principalLast = principalSubject;

        }
        //验证根证书是否在chain列表中
        try
        {
            if (!X509crl.getIssuerDN().equals(X509certificateRoot.getSubjectDN())) return false;
            X509crl.verify(X509certificateRoot.getPublicKey());
        }
        catch (Exception e)
        {
            return false;
        }
        //在当前时间下，验证证书链中每个证书是否存在撤销列表中
        if (X509crl != null)
        {
            try
            {
                //获取CRL中所有的项
                Set setEntries = X509crl.getRevokedCertificates();

                if (setEntries == null && setEntries.isEmpty() == false)
                {
                    Iterator iterator = setEntries.iterator();
                    while (iterator.hasNext())
                    {
                        X509CRLEntry X509crlentry = (X509CRLEntry) iterator.next();

                        if (list.contains(X509crlentry.getSerialNumber())) return false;
                    }
                }
            }
            catch (Exception e)
            {
                return false;
            }
        }
        //证明证书链中的第一个证书由用户所信任的CA颁布
        try
        {
            PublicKey publickey = X509certificateRoot.getPublicKey();
            arX509certificate[0].verify(publickey);
        }
        catch (Exception e)
        {
            return false;
        }
        //证明证书链中的最后一个证书的所有者正是现在通信对象
        Principal principalSubject = arX509certificate[nSize - 1].getSubjectDN();
        if (!stringTarget.equals(principalSubject.getName())) return false;
        //验证证书链里每个证书是否在有效期里
        Date date = new Date();
        for (int i = 0; i < nSize; i++)
        {
            try
            {
                arX509certificate[i].checkValidity(date);
            }
            catch (Exception e)
            {
                return false;
            }
        }
        return true;
    }

    public static boolean verifySign(X509Certificate X509certificateCA, byte[] sign, byte[] original, String sigAlgName)
    {
        try
        {
            //获得签名实例
            Signature signature = Signature.getInstance(sigAlgName);
            //用证书公钥进行初始化
            signature.initVerify(X509certificateCA.getPublicKey());
            //更新源数据
            signature.update(original);
            //验证数字签名
            return signature.verify(sign);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
