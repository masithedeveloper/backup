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

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.SharedPreferenceService;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class SymmetricCryptoHelper {

    private static final String TAG = SymmetricCryptoHelper.class.getSimpleName();
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String OUTPUT_KEY_DERIVATION_ALGORITHM = "AES";
    private static final String ALIAS_KEY = "alias_key";
    private static final String ALIAS_ID_KEY = "alias_id";
    private static final String FINGERPRINT_ID_KEY = "fingerprint_id";
    private static final String SALT_KEY = "salt_alias";
    private static final int OUTPUT_KEY_LENGTH = 256;
    private static final String FILE_NAME = "jks.keystore";
    private static final String NEW_KEYSTORE_FILENAME = "absa_app.keystore";
    private static char[] KEYSTORE_PASS = "iamnotsecretyet".toCharArray();
    public static final String CREATE_ALIAS_SYMMETRIC_KEY = "CREATE_ALIAS_SYMMETRIC_KEY";
    public static final String EXPRESS_SECRET_KEY = "EXPRESS_SECRET_KEY";
    public static final String NEW_RELIC_SECURITY_KEY = "@bs@CVMM0b1l3_!_";
    private KeyStore keyStore;
    private SecretKey secretKey;
    private SecretKey zeroKey;
    private final AlgorithmParameterSpec ivSpec;
    private final IvParameterSpec randomIvSpec;
    private static SymmetricCryptoHelper SYMMETRIC_CRYPTO_HELPER;
    private final File keyStoreFile;

    private final static Object lock = new Object(); // This must be static

    private SymmetricCryptoHelper() {
        File tempFile;
        Context applicationContext = BMBApplication.getInstance();
        tempFile = new File(applicationContext.getFilesDir(), FILE_NAME);
        ivSpec = generateIV();
        randomIvSpec = generateRandomIV();
        if (!tempFile.exists()) {
            tempFile = new File(applicationContext.getFilesDir(), NEW_KEYSTORE_FILENAME);
            String userKeystorePassword = SharedPreferenceService.INSTANCE.getKeystorePassword();
            if (userKeystorePassword == null) {
                String randomPassword = generateRandom16ByteString();
                KEYSTORE_PASS = randomPassword.toCharArray();
                SharedPreferenceService.INSTANCE.setKeystorePassword(KEYSTORE_PASS);
            } else {
                KEYSTORE_PASS = userKeystorePassword.toCharArray();
            }
        }
        keyStoreFile = tempFile;
        try {
            keyStore = loadOrCreateKeyStore();
            zeroKey = generateZeroKey();
            secretKey = generateKey();
        } catch (KeyStoreLoadingException | KeyGenerationFailureException | UnsupportedEncodingException | NoSuchProviderException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @NotNull
    private String generateRandom16ByteString() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomPasswordBuffer = new byte[16];
        secureRandom.nextBytes(randomPasswordBuffer);
        return new String(randomPasswordBuffer);
    }

    public static SymmetricCryptoHelper getInstance() {
        if (SYMMETRIC_CRYPTO_HELPER == null) { // surprisingly it can be null, even though it was instantiated when declared
            BMBLogger.d(TAG + "-x-", "surprisingly it can be null, even though it was instantiated when declared");
            return SYMMETRIC_CRYPTO_HELPER = new SymmetricCryptoHelper();
        }
        return SYMMETRIC_CRYPTO_HELPER;
    }

    private KeyStore loadOrCreateKeyStore() throws KeyStoreLoadingException {

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        if (keyStoreFile == null) {
            throw new IllegalStateException("Keystore file not specified");
        }
        BMBLogger.d(TAG, "KeyStore password is: " + new String(KEYSTORE_PASS));
        synchronized (lock) {
            try {
                final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                if (keyStoreFile.exists()) {
                    try {
                        // retrieve store
                        fileInputStream = new FileInputStream(keyStoreFile);
                        keyStore.load(fileInputStream, KEYSTORE_PASS);
                    } catch (IOException e) {
                        // create store
                        keyStore.load(null, null);
                        fileOutputStream = new FileOutputStream(keyStoreFile);
                        keyStore.store(fileOutputStream, KEYSTORE_PASS);
                    }
                } else {
                    // create store
                    keyStore.load(null, null);
                    fileOutputStream = new FileOutputStream(keyStoreFile);
                    keyStore.store(fileOutputStream, KEYSTORE_PASS);
                }
                return keyStore;
            } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
                throw new KeyStoreLoadingException(e);
            } finally {
                closeInputStream(fileInputStream);
                closeOutputStream(fileOutputStream);
            }
        }
    }

    private void closeInputStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            BMBLogger.e(TAG, "Exception closing input stream: " + e.getMessage());
        }
    }

    public byte[] getSecretKeyBytes() {
        return secretKey.getEncoded();
    }

    public byte[] getRandomIVBytes() {
        return randomIvSpec.getIV();
    }

    public void deleteAlias(String randomId) throws KeyStoreEntryAccessException {
        deleteKey(randomId);
    }

    private void closeOutputStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            BMBLogger.e(TAG, "Exception closing output stream: " + e.getMessage());
        }
    }

    public byte[] retrieveAliasId(String randomId) throws KeyStoreEntryAccessException, DecryptionFailureException {
        return getDecipheredBytes(getKey(ALIAS_ID_KEY.concat(randomId)));
    }

    public void storeAliasId(String randomId, byte[] aliasIdValue) throws KeyStoreEntryAccessException {
        storeKey(ALIAS_ID_KEY.concat(randomId), aliasIdValue);
    }

    public void deleteAliasId(String randomId) throws KeyStoreEntryAccessException {
        deleteKey(ALIAS_ID_KEY.concat(randomId));
    }

    public final void storeAlias(String key, byte[] aliasValue) throws KeyStoreEntryAccessException {
        storeKey(key, aliasValue);
    }

    public void storeFingerprintId(String randomId, byte[] fingerprintIdValue) throws KeyStoreEntryAccessException {
        storeKey(FINGERPRINT_ID_KEY.concat(randomId), fingerprintIdValue);
    }

    public void deleteFingerprintId(String randomId) throws KeyStoreEntryAccessException {
        deleteKey(FINGERPRINT_ID_KEY.concat(randomId));
    }

    public byte[] retrieveFingerprintId(String randomId) throws KeyStoreEntryAccessException, DecryptionFailureException {
        return getDecipheredBytes(getKey(FINGERPRINT_ID_KEY.concat(randomId)));
    }

    public void deleteLegacyAlias() throws KeyStoreEntryAccessException {
        deleteKey(ALIAS_KEY);
    }

    public byte[] retrieveLegacyAlias() throws KeyStoreEntryAccessException, DecryptionFailureException {
        return getDecipheredBytes(getKey(ALIAS_KEY));
    }

    byte[] retrieveSalt() throws KeyStoreEntryAccessException {
        return getKey(SALT_KEY);
    }

    void storeSalt(byte[] salt) throws KeyStoreEntryAccessException {
        storeKey(SALT_KEY, salt);
    }

    private void storeKey(String alias, byte[] keyBytes) throws KeyStoreEntryAccessException {
        synchronized (lock) {
            FileOutputStream fileOutputStream = null;
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            try {
                keyStore.setKeyEntry(alias, key, null, null);
                fileOutputStream = new FileOutputStream(keyStoreFile);
                keyStore.store(fileOutputStream, KEYSTORE_PASS);
            } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
                throw new KeyStoreEntryAccessException(e);
            } finally {
                closeOutputStream(fileOutputStream);
            }
        }
    }

    public void storeAliasCreationSymmetricKey(SecretKey secretKey) throws KeyStoreEntryAccessException {
        BMBLogger.d(TAG, "^^^^^^^^^^^^ Secret key -> [ " + secretKey.getAlgorithm() + ": " + new String(secretKey.getEncoded()));
        storeKey(CREATE_ALIAS_SYMMETRIC_KEY, secretKey);
    }

    public void storeExpressSecretKey(byte[] key) throws KeyStoreEntryAccessException {
        storeKey(EXPRESS_SECRET_KEY, key);
    }

    public void storeData(String keyName, byte[] key) {
        try {
            storeKey(keyName, key);
        } catch (Exception e) {
            BMBLogger.e(TAG, "STORE DATA FAILED");
        }
    }

    private void storeKey(String alias, SecretKey secretKey) throws KeyStoreEntryAccessException {
        synchronized (lock) {
            FileOutputStream fileOutputStream = null;
            try {
                keyStore.setKeyEntry(alias, secretKey, null, null);
                fileOutputStream = new FileOutputStream(keyStoreFile);
                keyStore.store(fileOutputStream, KEYSTORE_PASS);
            } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
                throw new KeyStoreEntryAccessException(e);
            } finally {
                closeOutputStream(fileOutputStream);
            }
        }
    }

    public final void clearKeys() throws KeyStoreEntryAccessException {
        synchronized (lock) {
            FileOutputStream fileOutputStream = null;
            try {
                if (keyStore != null) {
                    keyStore.deleteEntry(ALIAS_KEY);
                    fileOutputStream = new FileOutputStream(keyStoreFile);
                    keyStore.store(fileOutputStream, KEYSTORE_PASS);
                }
            } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
                throw new KeyStoreEntryAccessException(e);
            } finally {
                closeOutputStream(fileOutputStream);
            }
        }
    }

    public boolean hasAliasRegistered() {
        try {
            return keyStore.containsAlias(ALIAS_KEY);
        } catch (KeyStoreException e) {
            return false;
        }
    }

    public boolean hasFingerprintRegistered(String userId) {
        try {
            return keyStore.containsAlias(FINGERPRINT_ID_KEY.concat(userId));
        } catch (KeyStoreException e) {
            return false;
        }
    }

    public final byte[] retrieveAlias(String key) throws KeyStoreEntryAccessException {
        return getKey(key);
    }

    public boolean containsKey(String key) {
        try {
            return keyStore.containsAlias(key);
        } catch (KeyStoreException e) {
            return false;
        }
    }

    public byte[] getKey(String keyAlias) throws KeyStoreEntryAccessException {
        synchronized (lock) {
            try {
                if (keyStore == null) {
                    throw new KeyStoreException("Keystore is null");
                }
                if (!keyStore.containsAlias(keyAlias)) {
                    return null;
                }
                KeyStore.Entry entry = keyStore.getEntry(keyAlias, null);
                KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) entry;
                return secretKeyEntry.getSecretKey().getEncoded();
            } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
                throw new KeyStoreEntryAccessException(e);
            }
        }
    }

    public void deleteKey(String alias) throws KeyStoreEntryAccessException {
        synchronized (lock) {
            FileOutputStream fileOutputStream = null;
            try {
                if (keyStore == null) {
                    throw new KeyStoreException("Keystore is null");
                }

                if (!keyStore.containsAlias(alias)) {
                    return;
                }

                keyStore.deleteEntry(alias);
                fileOutputStream = new FileOutputStream(keyStoreFile);
                keyStore.store(fileOutputStream, KEYSTORE_PASS);

            } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
                throw new KeyStoreEntryAccessException(e);
            } finally {
                closeOutputStream(fileOutputStream);
            }
        }
    }

    public byte[] decryptCardDetails(byte[] encryptedCardDetails) throws DecryptionFailureException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, randomIvSpec);
            return cipher.doFinal(encryptedCardDetails);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalArgumentException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new DecryptionFailureException(e);
        }
    }

    public final byte[] decryptAlias(SecretKey key, byte[] encryptedAlias) throws DecryptionFailureException {
        return decrypt(key, encryptedAlias, "AES/CBC/PKCS5Padding");
    }

    public byte[] encryptAlias(String unencryptedAlias) throws EncryptionFailureException {
        return encrypt(secretKey, stringToBytesArray(unencryptedAlias));
    }

    public String encryptString(String stringToEncrypt, String password) throws EncryptionFailureException {
        SecretKey secretKey = new SecretKeySpec(password.getBytes(), 0, password.length(), OUTPUT_KEY_DERIVATION_ALGORITHM);
        byte[] encrypted = encrypt(secretKey, stringToEncrypt.getBytes());
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    @SuppressWarnings("unused")
    public String decryptString(String stringToDecrypt, String password) throws IOException, DecryptionFailureException {
        SecretKey secretKey = new SecretKeySpec(password.getBytes(), 0, password.length(), OUTPUT_KEY_DERIVATION_ALGORITHM);
        byte[] decoded = Base64.decode(stringToDecrypt, Base64.NO_WRAP);
        byte[] decrypted = decrypt(secretKey, decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public byte[] encryptAlias(String unencryptedAlias, SecretKey symmetricKey, AlgorithmParameterSpec initializationVector) throws EncryptionFailureException {
        return encrypt(symmetricKey, initializationVector, stringToBytesArray(unencryptedAlias));
    }

    public byte[] encryptCredential(byte[] passcode) throws EncryptionFailureException {
        return encrypt(secretKey, passcode);
    }

    public byte[] encryptCredential(byte[] passcode, IvParameterSpec iv, SecretKey secretKey) throws EncryptionFailureException {
        return encrypt(secretKey, iv, passcode);
    }

    public byte[] encryptAliasWithZeroKey(byte[] unencryptedAlias) throws EncryptionFailureException {
        return encrypt(zeroKey, unencryptedAlias);
    }

    public byte[] decryptAliasWithZeroKey(byte[] encryptedAlias) throws DecryptionFailureException {
        return decrypt(zeroKey, encryptedAlias);
    }

    public byte[] deriveCredential(String unencryptedAlias, String rawPin, String deviceId) throws IOException, KeyGenerationFailureException {
        String aliasAndRawPin = unencryptedAlias.concat(rawPin);
        char[] aliasAndRawPinBuffer = aliasAndRawPin.toCharArray();
        byte[] deviceIdBytes = deviceId.getBytes(DEFAULT_CHARSET);
        return generatePBDKF2Key(aliasAndRawPinBuffer, deviceIdBytes).getEncoded();
    }

    private long byteArrayToLong(byte[] bytes) {
        long result = 0;
        if (bytes == null) {
            return result;
        }

        for (byte aByte : bytes) {
            result = (result << 8) + (aByte & 0xff);
        }
        if (result < 0) {
            result = result * -1;
        }
        return result;
    }

    //Generate 256 bits long symmetric key using PBDKF2
    private SecretKey generatePBDKF2Key(char[] password, byte[] seed) throws KeyGenerationFailureException {
        final int iterations = 1000;
        KeySpec keySpec = new PBEKeySpec(password, seed, iterations, OUTPUT_KEY_LENGTH);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return secretKeyFactory.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new KeyGenerationFailureException(e);
        }
    }

    public void setSymmetricKey(SecretKey symmetricKey) {
        secretKey = symmetricKey;
    }

    //Generate the 256 bits long random symmetric key
    public SecretKey generateKey() throws KeyGenerationFailureException, NoSuchProviderException, UnsupportedEncodingException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(OUTPUT_KEY_DERIVATION_ALGORITHM);
            SecureRandom secureRandom = new SecureRandom();
            keyGenerator.init(OUTPUT_KEY_LENGTH, secureRandom);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new KeyGenerationFailureException(e);
        }
    }

    //Generates a 256 bits long symmetric key filled with zeros
    private SecretKey generateZeroKey() throws KeyGenerationFailureException {
        final int keyBufferSize = 32;
        byte[] zeroKeyBuffer = new byte[keyBufferSize];
        Arrays.fill(zeroKeyBuffer, (byte) 0x00);
        return new SecretKeySpec(zeroKeyBuffer, OUTPUT_KEY_DERIVATION_ALGORITHM);
    }

    //Generates a 128 bits initialisation vector filled with zeros
    private AlgorithmParameterSpec generateIV() {
        final int ivLength = 16;
        byte[] ivBuffer = new byte[ivLength];
        Arrays.fill(ivBuffer, (byte) 0x00);
        return new IvParameterSpec(ivBuffer);
    }

    //Generates a 128 bit random initialisation vector
    private IvParameterSpec generateRandomIV() {
        SecureRandom secureRandom = new SecureRandom();
        final int ivLength = 16;
        byte[] ivBuffer = new byte[ivLength];
        secureRandom.nextBytes(ivBuffer);
        return new IvParameterSpec(ivBuffer);
    }

    private byte[] encrypt(SecretKey secretKey, byte[] plainData) throws EncryptionFailureException {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            return cipher.doFinal(plainData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalArgumentException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionFailureException(e);
        }
    }

    private byte[] encrypt(SecretKey secretKey, IvParameterSpec ivSpec, byte[] plainData) throws EncryptionFailureException {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            return cipher.doFinal(plainData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalArgumentException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionFailureException(e);
        }
    }

    private byte[] encrypt(SecretKey secretKey, AlgorithmParameterSpec initializationVector, byte[] plainData) throws EncryptionFailureException {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, initializationVector);
            return cipher.doFinal(plainData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalArgumentException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionFailureException(e);
        }
    }

    public byte[] decrypt(SecretKey secretKey, byte[] cipherData) throws DecryptionFailureException {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalArgumentException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new DecryptionFailureException(e);
        }
    }

    public byte[] decrypt(SecretKey secretKey, byte[] cipherData, String cipherAlgorithm) throws DecryptionFailureException {
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalArgumentException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new DecryptionFailureException(e);
        }
    }

    private byte[] stringToBytesArray(String data) {
        try {
            return data.getBytes(DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to convert string to byte array", e);
            return null;
        }
    }

    private static byte[] getDecipheredBytes(byte[] cipherBytes) throws SymmetricCryptoHelper.DecryptionFailureException {
        final int ZERO_LENGTH = 0;
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        if (cipherBytes != null && cipherBytes.length > 0) {
            byte[] decipheredBytes = symmetricCryptoHelper.decryptAliasWithZeroKey(cipherBytes);
            if (decipheredBytes != null && decipheredBytes.length > 0) {
                return decipheredBytes;
            } else {
                return new byte[ZERO_LENGTH];
            }
        } else {
            return new byte[ZERO_LENGTH];
        }
    }

    public static final class EncryptionFailureException extends Exception {
        EncryptionFailureException(Throwable e) {
            super(e);
        }
    }

    public static final class DecryptionFailureException extends Exception {
        DecryptionFailureException(Throwable e) {
            super(e);
        }
    }

    public static final class KeyGenerationFailureException extends Exception {
        KeyGenerationFailureException(Throwable e) {
            super(e);
        }
    }

    private static final class KeyStoreLoadingException extends Exception {
        KeyStoreLoadingException(Throwable e) {
            super(e);
        }
    }

    public static final class KeyStoreEntryAccessException extends Exception {
        KeyStoreEntryAccessException(Throwable e) {
            super(e);
        }
    }

    public byte[] retrieveOtpSeed() {
        try {
            // initially the otpSeed is zero encrypted, base 64 encoded and encrypted with some key
            byte[] otpZeryoEncrypted = retrieveAlias(BMBConstants.OTP_SEED);
            if (otpZeryoEncrypted != null) {
                byte[] otpBase64Encoded = decryptAliasWithZeroKey(otpZeryoEncrypted);
                if (otpBase64Encoded != null) {
                    byte[] otpSeed_AesEncrypted = Base64.decode(otpBase64Encoded, Base64.DEFAULT);
                    byte[] key = retrieveAlias(SymmetricCryptoHelper.CREATE_ALIAS_SYMMETRIC_KEY);
                    byte[] otpSeed = decryptAlias(new SecretKeySpec(key, "AES"), otpSeed_AesEncrypted);
                    return otpSeed;
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
        return new byte[]{0};
    }

}
