package com.yk.bitcoin;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A Sha256Hash just wraps a byte[] so that equals and hashcode work correctly, allowing it to be used as keys in a
 * map. It also checks that the length is correct and provides a bit more type safety.
 */
public class Sha256Hash
{


    public static MessageDigest newDigest()
    {
        try
        {
            return MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hashTwice(byte[] input, int offset, int length)
    {
        MessageDigest digest = newDigest();
        digest.update(input, offset, length);
        return digest.digest(digest.digest());
    }

    public static byte[] hash(byte[] input)
    {
        return hash(input, 0, input.length);
    }

    /**
     * Calculates the SHA-256 hash of the given byte range.
     *
     * @param input  the array containing the bytes to hash
     * @param offset the offset within the array of the bytes to hash
     * @param length the number of bytes to hash
     * @return the hash (in big-endian order)
     */
    public static byte[] hash(byte[] input, int offset, int length)
    {
        MessageDigest digest = newDigest();
        digest.update(input, offset, length);
        return digest.digest();
    }

    public static byte[] hashTwice(byte[] input1, byte[] input2)
    {
        MessageDigest digest = newDigest();
        digest.update(input1);
        digest.update(input2);
        return digest.digest(digest.digest());
    }

    public static byte[] hashTwice(byte[] input)
    {
        return hashTwice(input, 0, input.length);
    }
}

