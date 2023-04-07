package com.yk.base.valid;

import cn.hutool.core.util.HexUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;

public class HexConstraintValidator implements ConstraintValidator<HexValid, String>
{
    private static final BigInteger min = new BigInteger("0", 16);
    private static final BigInteger max = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140", 16);

    @Override
    public void initialize(HexValid constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(String hex, ConstraintValidatorContext constraintValidatorContext)
    {
        if (StringUtils.isBlank(hex))
        {
            return true;
        }
        boolean isHexNumber = HexUtil.isHexNumber("0x" + hex);
        BigInteger integer = new BigInteger(hex, 16);
        int i = integer.compareTo(min);
        int k = integer.compareTo(max);
        return isHexNumber && i > 0 && k < 0;
    }
}
