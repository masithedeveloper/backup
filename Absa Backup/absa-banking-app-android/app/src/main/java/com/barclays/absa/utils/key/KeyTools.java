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
package com.barclays.absa.utils.key;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.ArrayMap;

import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.SharedPreferenceService;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.security.auth.x500.X500Principal;

@TargetApi(Build.VERSION_CODES.M)
public final class KeyTools {
    private static final String PROVIDER_NAME = "AndroidKeyStore";
    private static final String USER_AUTH_KEY_NAME = "ABSA_AUTH_KEY";
    private static final String ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    private static final String PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String TRANSFORMATION = ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING;
    private static final String TAG = KeyTools.class.getSimpleName();
    private static String USER_ENCRYPTION_KEY_NAME;

    private final Map<String, KeySpecGenerator> generators;
    private final Context context = BMBApplication.getInstance();
    private KeyStore keyStore;

    public static KeyTools newInstance(Context context) throws KeyToolsException {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(PROVIDER_NAME);
            keyStore.load(null);
        } catch (Exception e) {
            throw new KeyToolsException("Error initializing keystore: ", e);
        }
        Map<String, KeySpecGenerator> generators = new ArrayMap<>();
        generators.put(USER_AUTH_KEY_NAME, new UserAuthKeySpecGenerator(context, BLOCK_MODE, PADDING));
        return new KeyTools(keyStore, Collections.unmodifiableMap(generators));
    }

    public void loadKeys() {
        String alias = USER_ENCRYPTION_KEY_NAME;
        try {
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 2);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=ABSA Fingerprint, O=ABSA"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();

                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);
                generator.generateKeyPair();
            }
        } catch (Exception e) {
            BMBLogger.e(TAG, e.getMessage());
        }
    }

    @SuppressLint("HardwareIds")
    private KeyTools(KeyStore keyStore, Map<String, KeySpecGenerator> generators) {
        this.keyStore = keyStore;
        this.generators = generators;
        if (SharedPreferenceService.INSTANCE.getProfileMigrationVersion() > 1) {
            String keyToolsPassword = SharedPreferenceService.INSTANCE.getKeyToolsPassword();
            if (keyToolsPassword == null) {
                USER_ENCRYPTION_KEY_NAME = ProfileManager.getInstance().generateRandomUserId();
                SharedPreferenceService.INSTANCE.setKeyToolsPassword(USER_ENCRYPTION_KEY_NAME);
            } else {
                USER_ENCRYPTION_KEY_NAME = keyToolsPassword;
            }
        } else {
            USER_ENCRYPTION_KEY_NAME = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + "ABSA";
        }
    }

    public Cipher getEncryptionCipher() throws KeyToolsException {
        try {
            SecretKey secretKey = getKey(USER_AUTH_KEY_NAME);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher;
        } catch (KeyPermanentlyInvalidatedException e) {
            return null;
        } catch (KeyToolsException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException | NoSuchPaddingException | InvalidKeyException e) {
            throw new KeyToolsException("Failed to initialise encryption cipher with the provided key", e);
        }
    }

    public Cipher getDecryptionCipher() throws KeyToolsException {
        try {
            SecretKey secretKey = getKey(USER_AUTH_KEY_NAME);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] storedKeyToolsIv = SharedPreferenceService.INSTANCE.getKeyToolsIv();
            if (storedKeyToolsIv != null) {
                IvParameterSpec ivSpec = new IvParameterSpec(storedKeyToolsIv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            } else {
                byte[] cipherIV = cipher.getIV();
                if (cipherIV == null) {
                    cipherIV = SymmetricCryptoHelper.getInstance().getRandomIVBytes();
                }
                if (cipherIV != null) {
                    SharedPreferenceService.INSTANCE.setKeyToolsIv(cipherIV);
                }
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(cipherIV));
            }
            return cipher;
        } catch (KeyPermanentlyInvalidatedException e) {
            return null;
        } catch (KeyToolsException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalArgumentException e) {
            throw new KeyToolsException("Failed to initialise decryption cipher with the provided key", e);
        }
    }

    public Cipher getCipherNew() throws KeyToolsException {
        createKey(USER_AUTH_KEY_NAME);
        return getEncryptionCipher();
    }

    private SecretKey getKey(String keyName) throws KeyStoreException, KeyToolsException, UnrecoverableKeyException, NoSuchAlgorithmException {
        if (!keyStore.isKeyEntry(keyName)) {
            createKey(keyName);
        }
        return (SecretKey) keyStore.getKey(keyName, null);
    }

    private void createKey(String keyName) throws KeyToolsException, IllegalStateException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM, PROVIDER_NAME);
            KeyGenParameterSpec spec = getKeyGenParameterSpec(keyName);
            keyGenerator.init(spec);
            keyGenerator.generateKey();
        } catch (InvalidAlgorithmParameterException e) {
            if (e.getCause() instanceof IllegalStateException) {
                throw (IllegalStateException) e.getCause();
            }
            throw new KeyToolsException("Error creating key for " + keyName, e);
        } catch (Exception e) {
            throw new KeyToolsException("Error creating key for " + keyName, e);
        }
    }

    private KeyGenParameterSpec getKeyGenParameterSpec(String keyName) {
        KeySpecGenerator keySpecGenerator = generators.get(keyName);
        return keySpecGenerator != null ? keySpecGenerator.generate(keyName) : null;
    }

    public static class KeyToolsException extends Exception {
        KeyToolsException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }
}