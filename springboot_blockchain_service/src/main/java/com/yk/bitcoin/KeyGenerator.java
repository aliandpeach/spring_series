package com.yk.bitcoin;

import com.yk.crypto.Base58;
import com.yk.crypto.BinHexSHAUtil;
import com.yk.crypto.Sha256Hash;
import com.yk.crypto.Utils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.ByteBuffer;

@Service
public class KeyGenerator
{
    private ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");

    public String keyGen(byte[] privateKey, boolean compressed)
    {
        if (null == privateKey || privateKey.length != 32)
        {
            throw new IllegalArgumentException("privateKey not correct");
        }

        byte[] temp = new byte[privateKey.length + 1 + (compressed ? 1 : 0)];

        // 私钥前增加0x80
        byte[] single0x80 = new byte[]{(byte) 0x80};

        System.arraycopy(single0x80, 0, temp, 0, 1);
        System.arraycopy(privateKey, 0, temp, 1, privateKey.length);

        if (compressed)
        {
            // 私钥后增加0x01 -- 压缩格式 compressed format
            byte[] single0x01 = new byte[]{(byte) 0x01};
            System.arraycopy(single0x01, 0, temp, privateKey.length + 1, 1);
        }

        // temp进行两次sha256
        byte[] hash = Sha256Hash.hashTwice(temp);

        // 取两次hash256后的前四位 -- checksum
        byte[] checksum = new byte[4];
        ByteBuffer bbuffer = ByteBuffer.allocate(hash.length);
        bbuffer.put(hash);
        // 此时的position为32，必须flip后，把position改为0，否则get不到任何数据还会异常
        bbuffer.flip();
        bbuffer.get(checksum, 0, 4);

        // 下面把temp和checksum合并起来
        ByteBuffer buffer2PrivateKey = ByteBuffer.allocate(temp.length + 4);
        buffer2PrivateKey.put(temp);
        buffer2PrivateKey.put(checksum);
        buffer2PrivateKey.flip();
        byte[] f = new byte[temp.length + 4];
        buffer2PrivateKey.get(f);
        String privateString = Base58.encode(f);
        return privateString;
    }

    public String addressGen(byte[] privateKey)
    {
        // 使用上述的私钥 根据椭圆曲线生成公钥
        ECPoint pointQ = spec.getG().multiply(new BigInteger(1, privateKey));
        byte[] publicKey = pointQ.getEncoded(true);
        byte[] sha256Bytes = Sha256Hash.hash(publicKey);

        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(sha256Bytes, 0, sha256Bytes.length);
        byte[] ripemd160Bytes = new byte[digest.getDigestSize()];
        digest.doFinal(ripemd160Bytes, 0);


        byte[] networkID = new BigInteger("00", 16).toByteArray();

        byte[] extendedRipemd160Bytes = add(networkID, ripemd160Bytes);

        byte[] twiceSha256Bytes = Sha256Hash.hash(Sha256Hash.hash(extendedRipemd160Bytes));

        byte[] checksumPub = new byte[4];

        System.arraycopy(twiceSha256Bytes, 0, checksumPub, 0, 4);

        byte[] binaryBitcoinAddressBytes = add(extendedRipemd160Bytes, checksumPub);

        String bitcoinAddress = Base58.encode(binaryBitcoinAddressBytes);
        return bitcoinAddress;
    }

    /**
     * 两个byte[]数组相加
     *
     * @param dataF f
     * @param dataS s
     * @return byte[]
     */
    public static byte[] add(byte[] dataF, byte[] dataS)
    {
        byte[] result = new byte[dataF.length + dataS.length];
        System.arraycopy(dataF, 0, result, 0, dataF.length);
        System.arraycopy(dataS, 0, result, dataF.length, dataS.length);
        return result;
    }

    public byte[] convertKeyByBase58Key(String base58Key)
    {
        byte[] keyWtihChecksumBytes = Base58.decode(base58Key);
        if (keyWtihChecksumBytes.length < 37 || keyWtihChecksumBytes.length > 38)
        {
            return null;
        }

        boolean compressed = keyWtihChecksumBytes.length == 38;
        byte[] newkey = new byte[keyWtihChecksumBytes.length - 4];
        ByteBuffer byteBuffer = ByteBuffer.allocate(keyWtihChecksumBytes.length);
        byteBuffer.put(keyWtihChecksumBytes);
        byteBuffer.flip();
        byteBuffer.get(newkey, 0, newkey.length);

        byte[] key = new byte[32];
        System.arraycopy(newkey, 1, key, 0, key.length);
        return key;
    }

    public boolean isPubKeyCompressed(String base58Addr)
    {
        byte[] addrBytes = Base58.decode(base58Addr);
        boolean isPubKeyCompressed = isPubKeyCompressed(addrBytes);
        return isPubKeyCompressed;
    }

    /**
     * Returns true if the given pubkey is in its compressed form.
     */
    public boolean isPubKeyCompressed(byte[] encoded)
    {
        if (encoded.length == 33 && (encoded[0] == 0x02 || encoded[0] == 0x03))
            return true;
        else if (encoded.length == 65 && encoded[0] == 0x04)
            return false;
        else
            throw new IllegalArgumentException(Utils.HEX.encode(encoded));
    }
}
