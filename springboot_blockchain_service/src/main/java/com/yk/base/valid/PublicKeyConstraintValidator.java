package com.yk.base.valid;

import com.yk.bitcoin.KeyGenerator;
import com.yk.crypto.Base58;
import com.yk.crypto.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;

public class PublicKeyConstraintValidator implements ConstraintValidator<PublicKeyValid, String>
{
    private static final BigInteger min = new BigInteger("0", 16);
    private static final BigInteger max = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140", 16);

    private KeyGenerator keyGenerator;

    @Override
    public void initialize(PublicKeyValid constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(String publicKey, ConstraintValidatorContext constraintValidatorContext)
    {
        if (StringUtils.isBlank(publicKey))
        {
            return false;
        }
        boolean isPubKeyCompressed = keyGenerator.isPubKeyCompressed(publicKey);
        byte[] decodePublicKey = Base58.decode(publicKey);
        BigInteger integer = new BigInteger(1, decodePublicKey);
        int i = integer.compareTo(min);
        int k = integer.compareTo(max);
        return i > 0 && k < 0;
    }
}
