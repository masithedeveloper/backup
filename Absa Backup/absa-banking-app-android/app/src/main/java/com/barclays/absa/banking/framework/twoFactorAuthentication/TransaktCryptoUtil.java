/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */

package com.barclays.absa.banking.framework.twoFactorAuthentication;


import com.barclays.absa.banking.framework.utils.BMBLogger;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TransaktCryptoUtil {
    private static final String TAG = TransaktCryptoUtil.class.getSimpleName();
    private static final String CRYPTO_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static byte[] encrypt(byte[] aliasBytes) throws Exception {
        byte[] zeroKeyBytes = generateZeroKeyBytes();
        return encrypt(aliasBytes, aliasBytes, zeroKeyBytes);
    }

    private static byte[] generateZeroKeyBytes () {
        final int keyBufferSize = 16;
        byte[] zeroKeyBuffer = new byte[keyBufferSize];
        Arrays.fill(zeroKeyBuffer, (byte) 0x00);
        return zeroKeyBuffer;
    }

    /**
     * Encrypt a byte array with using a the keyBytes as a key and the supplied initialization vector using AES 256bit.
     * IF the keyBytes is not large enough it will be duplicated until it is large enough
     * @param bytesToEncrypt what to encrypt
     * @param keyBytes buffer of key to use for encryption
     * @param initializationVector aha
     * @return encrypted services
     * @throws Exception in case something goes wrong
     */
    public static byte[] encrypt(byte[] bytesToEncrypt, byte[] keyBytes, byte[] initializationVector) throws Exception {
        // Generate the key from the supplied string
        Key key = byteArrayToAESKey(keyBytes, 256);

        // Encrypt the bytesToEncrypt with the newly generated key
        return encrypt(bytesToEncrypt, key, initializationVector);
    }

    public static byte[] encrypt(byte[] bytesToEncrypt,  Key key, byte[] ivBuffer) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
            IvParameterSpec initializationVector = new IvParameterSpec(ivBuffer);
            cipher.init(Cipher.ENCRYPT_MODE, key, initializationVector);
            return cipher.doFinal(bytesToEncrypt);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            BMBLogger.e(TAG, e.getMessage());
        }
        return null;
    }

    private static Key byteArrayToAESKey(final byte[] keyBytes, final int keySize) {

        try {
            // Check if the keyLength is valid for AES
            switch (keySize) {
                case 128:
                case 192:
                case 256:
                    break;
                default:
                    throw new IllegalArgumentException("Invalid key length for AES: " + keySize);
            }

            if (keyBytes == null) {
                throw new IllegalArgumentException("keyBytes cannot be null");
            }
        } catch (IllegalArgumentException e) {
            BMBLogger.e(TAG, "Could not generate a AES key from supplied keyBytes: " + e.getMessage());
        }

        final byte[] keyData = generateKeyBytes(keyBytes, keySize);

        // Generate the AES key using the
        return new SecretKeySpec(keyData, "AES/CBC/PKCS5Padding");
    }

    /**
     * Generate a new byte array containing a part of the passed bytes if bytes is bigger than the required length
     *  or multiple occurrences of bytes if the bytes passed is smaller than the required length
     * @param sourceBytes source buffer
     * @param length length required
     * @return padded key
     */
    private static byte[] generateKeyBytes(byte[] sourceBytes, int length) {
        // Calculate the length required in bits
        int bitLength = length / 8;
        // Create a byte array of the required length
        byte[] newByteArray = new byte[bitLength];

        // Check how many times the bytes have to be duplicated
        if (sourceBytes.length < bitLength) {
            int count = bitLength / sourceBytes.length;
            // The sourceBytes have to duplicated to fill the required byte array
            for (int i=0; i < count; i++) {
                System.arraycopy(sourceBytes, 0, newByteArray, sourceBytes.length * i, sourceBytes.length);
            }

            int remainder = bitLength % sourceBytes.length;
            if (remainder > 0) {
                System.arraycopy(sourceBytes, 0, newByteArray, newByteArray.length-remainder, remainder);
            }
        } else {
            // The sourceBytes have to be concatenated to fit in the required bytes
            System.arraycopy(sourceBytes, 0, newByteArray, 0, bitLength);
        }

        return newByteArray;
    }
}
