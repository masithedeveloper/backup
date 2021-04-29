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
package com.barclays.absa.crypto;

import android.util.Base64;
import android.util.Log;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.framework.utils.BMBLogger;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class AESEncryption {
    private static final String TAG = "AESEncryption";
    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_FULL = "AES/CBC/PKCS5Padding";
    private static byte[] salt = null;
    private static SecretKey secretKey;

    private static byte[] getSalt() {
        if (salt != null) {
            return salt;
        }
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        try {
            salt = symmetricCryptoHelper.retrieveSalt();
            if (salt == null) {
                salt = generateSalt();
                symmetricCryptoHelper.storeSalt(salt);
            }
        } catch (SymmetricCryptoHelper.KeyStoreEntryAccessException e) {
            BMBLogger.d(TAG + "-x-", e.toString());
            if (salt == null) {
                try {
                    salt = generateSalt();
                    symmetricCryptoHelper.storeSalt(salt);
                } catch (SymmetricCryptoHelper.KeyStoreEntryAccessException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return salt;
    }

    private AESEncryption() {
    }

    public static String decrypt(String rawKey, String cipherData, boolean shouldUseNewCrypto) throws Exception {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        if (shouldUseNewCrypto) {
            secretKey = generatePBEKey(rawKey.toCharArray());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } else {
            Key key = generateSecretKey(rawKey);
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        final byte[] decodedData = Base64.decode(cipherData, Base64.DEFAULT);
        final byte[] plainData = cipher.doFinal(decodedData);
        return new String(plainData);
    }

    public static String encrypt(String rawKey, String plainData) throws Exception {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        secretKey = generatePBEKey(rawKey.toCharArray());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        final byte[] cipherData = cipher.doFinal(plainData.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(cipherData, Base64.DEFAULT);
    }

    public static String decrypt(String iv, String rawKey, String cipherData) throws Exception {
        final byte[] decodedData = Base64.decode(cipherData, Base64.URL_SAFE);
        final AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        final SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey.getBytes(StandardCharsets.UTF_8), "AES");
        final Cipher cipher = Cipher.getInstance(ALGORITHM_FULL);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        final byte[] plainData = cipher.doFinal(decodedData);
        return new String(plainData);
    }

    public static String encrypt(String iv, String rawKey, String plainData) throws Exception {
        final AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        final SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey.getBytes(StandardCharsets.UTF_8), "AES");
        final Cipher cipher = Cipher.getInstance(ALGORITHM_FULL);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        final byte[] cipherData = cipher.doFinal(plainData.getBytes());
        return Base64.encodeToString(cipherData, Base64.URL_SAFE);
    }

    private static SecretKey generatePBEKey(char[] password) {
        final int KEY_LENGTH = 128;
        final int ITERATION_COUNT = 100;
        KeySpec pbeKeySpec = new PBEKeySpec(password, getSalt(), ITERATION_COUNT, KEY_LENGTH);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to generate the secret key", e);
            }
            return null;
        }
    }

    private static byte[] generateSalt() {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] seed = new byte[16];
        secureRandom.nextBytes(seed);
        return seed;
    }

    /**
     * Generate secret key.
     *
     * @param key the key
     * @return the key
     * @throws Exception the exception
     */
    private static Key generateSecretKey(String key) throws Exception {
        return new SecretKeySpec(getRawKey(key.getBytes()), ALGORITHM);
    }


    /**
     * Gets the raw key.
     *
     * @param seed the seed
     * @return the raw key
     * @throws Exception the exception
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {
        final int JELLY_BEAN_4_2 = 17;
        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);

        // AES Issue fixed for Jelly bean
        SecureRandom sr;
        if (android.os.Build.VERSION.SDK_INT >= JELLY_BEAN_4_2) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }

        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }
}
