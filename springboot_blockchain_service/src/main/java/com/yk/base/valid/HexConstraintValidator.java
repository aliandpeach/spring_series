package com.yk.base.valid;

import cn.hutool.core.util.HexUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;

public class HexConstraintValidator implements ConstraintValidator<HexValid, Object>
{
    BigInteger min = new BigInteger("0", 16);
    BigInteger max = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

    @Override
    public void initialize(HexValid constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext)
    {
        if (null == o || StringUtils.isBlank(o.toString()))
        {
            return false;
        }
        boolean hex = HexUtil.isHexNumber(String.valueOf(o));
        BigInteger integer = new BigInteger(o.toString(), 16);
        int i = integer.compareTo(min);
        int k = integer.compareTo(max);
        return hex && i > 0 && k < 0;
    }
}
