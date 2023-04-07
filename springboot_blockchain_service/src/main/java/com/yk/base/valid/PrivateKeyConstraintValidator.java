package com.yk.base.valid;

import cn.hutool.core.util.HexUtil;
import com.yk.bitcoin.KeyGenerator;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;

public class PrivateKeyConstraintValidator implements ConstraintValidator<PrivateKeyValid, String>
{
    private static final BigInteger min = new BigInteger("0", 16);
    private static final BigInteger max = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140", 16);

    private KeyGenerator keyGenerator;

    @Override
    public void initialize(PrivateKeyValid constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(String privateKey, ConstraintValidatorContext constraintValidatorContext)
    {
        if (StringUtils.isBlank(privateKey))
        {
            return false;
        }
        byte[] biKey = keyGenerator.convertKeyByBase58Key(privateKey);
        if (null == biKey)
        {
            return false;
        }
        BigInteger integer = new BigInteger(1, biKey);
        int i = integer.compareTo(min);
        int k = integer.compareTo(max);
        return i > 0 && k < 0;
    }
}
