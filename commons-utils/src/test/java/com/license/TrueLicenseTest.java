package com.license;

import de.schlichtherle.license.LicenseContent;
import org.junit.Test;

import java.net.URL;

/**
 * 1、首先利用KeyTool工具来生成私匙库：（-alias别名 –validity 3650表示10年有效）
 * keytool -genkey -alias license-test -keystore license-test.ks -validity 3650
 * 2、然后把私匙库内的公匙导出到一个文件当中：
 * keytool -export -alias license-test -file license-test.cer -keystore license-test.ks
 * 3、然后再把这个证书文件导入到公匙库：
 * keytool -import -alias license-test -file license-test.cer -keystore license-test.ts
 */
public class TrueLicenseTest
{
    @Test
    public void create()
    {
        URL url = getClass().getClassLoader().getResource("");
        System.out.println(url.getPath());
        CreateTrueLicense cLicense = new CreateTrueLicense();
        //获取参数
        cLicense.setParam("com/license/create.properties");
        //生成证书
        cLicense.create();
    }

    @Test
    public void install()
    {
        VerifyTrueLicense vLicense = new VerifyTrueLicense();
        //获取参数
        vLicense.setParam("com/license/verify.properties");
        //验证证书
        LicenseContent content = vLicense.install();
        System.out.println(content);
    }

    @Test
    public void verify()
    {
        VerifyTrueLicense vLicense = new VerifyTrueLicense();
        //获取参数
        vLicense.setParam("com/license/verify.properties");
        //验证证书
        LicenseContent content = vLicense.verify();
        System.out.println(content);
    }
}
