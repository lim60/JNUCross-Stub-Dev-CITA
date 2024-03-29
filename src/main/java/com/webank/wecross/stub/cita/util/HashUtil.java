package com.webank.wecross.stub.cita.util;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.SM3;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

/** Cryptographic hash functions. */
public class HashUtil {
    private HashUtil() {}

    /**
     * Keccak-256 hash function.
     *
     * @param hexInput hex encoded input data without optional 0x prefix
     * @return hash value as hex encoded string
     */
    public static String keccak256(String hexInput) {
        byte[] bytes = Hex.decode(hexInput);
        byte[] result = keccak256(bytes);
        return Hex.toHexString(result);
    }

    /**
     * Keccak-256 hash function.
     *
     * @param input binary encoded input data
     * @param offset of start of data
     * @param length of data
     * @return hash value
     */
    public static byte[] keccak256(byte[] input, int offset, int length) {
        Keccak.DigestKeccak kecc = new Keccak.Digest256();
        kecc.update(input, offset, length);
        return kecc.digest();
    }

    /**
     * Keccak-256 hash function.
     *
     * @param input binary encoded input data
     * @return hash value
     */
    public static byte[] keccak256(byte[] input) {
        return keccak256(input, 0, input.length);
    }

    /**
     * Keccak-256 hash function that operates on a UTF-8 encoded String.
     *
     * @param utf8String UTF-8 encoded string
     * @return hash value as hex encoded string
     */
    public static String keccak256String(String utf8String) {
        return Hex.toHexString(keccak256(utf8String.getBytes(StandardCharsets.UTF_8)));
    }
//
//    /**
//     * SM3 hash function.
//     *
//     * @param hexInput hex encoded input data without optional 0x prefix
//     * @return hash value as hex encoded string
//     */
//    public static String sm3(String hexInput) {
//        byte[] bytes = Hex.decode(hexInput);
//        byte[] result = sm3(bytes);
//        return Hex.toHexString(result);
//    }
//
//    /**
//     * SM3 hash function.
//     *
//     * @param input binary encoded input data
//     * @param offset of start of data
//     * @param length of data
//     * @return hash value
//     */
//    public static byte[] sm3(byte[] input, int offset, int length) {
//        SM3.Digest digestSm3 = new SM3.Digest();
//        digestSm3.update(input, offset, length);
//        return digestSm3.digest();
//    }
//
//    /**
//     * SM3 hash function.
//     *
//     * @param input binary encoded input data
//     * @return hash value
//     */
//    public static byte[] sm3(byte[] input) {
//        return sm3(input, 0, input.length);
//    }
//
//    /**
//     * SM3 hash function that operates on a UTF-8 encoded String.
//     *
//     * @param utf8String UTF-8 encoded string
//     * @return hash value as hex encoded string
//     */
//    public static String sm3String(String utf8String) {
//        return Hex.toHexString(sm3(utf8String.getBytes(StandardCharsets.UTF_8)));
//    }
}
