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
import android.content.res.AssetManager;
import android.util.Base64;

import com.barclays.absa.banking.framework.app.BMBApplication;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AsymmetricCryptoHelper {

    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String KEY_FILE = "mobileapp.pem.cer";
    private static final AsymmetricCryptoHelper ASYMMETRIC_CRYPTO_HELPER;
    private Context applicationContext;

    static {
        ASYMMETRIC_CRYPTO_HELPER = new AsymmetricCryptoHelper();
        AsymmetricCryptoHelper.getInstance().applicationContext = BMBApplication.getInstance();
    }

    private AsymmetricCryptoHelper() {
    }

    public static AsymmetricCryptoHelper getInstance() {
        return ASYMMETRIC_CRYPTO_HELPER;
    }

    public final byte[] encryptSymmetricKey(byte[] symmetricKey) throws AsymmetricKeyGenerationFailureException, AsymmetricEncryptionFailureException {
        PublicKey publicKey = loadPublicKey();
        return encrypt(publicKey, symmetricKey);
    }

    private PublicKey loadPublicKey() throws AsymmetricKeyGenerationFailureException {
        try {
            InputStream inputStream = readPublicKeyFile();

            byte[] keyBytes = new byte[inputStream.available()];
            inputStream.read(keyBytes);
            inputStream.close();

            String pubKey = new String(keyBytes, StandardCharsets.UTF_8);
            pubKey = pubKey.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");

            keyBytes = Base64.decode(pubKey, Base64.DEFAULT);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | NullPointerException e) {
            throw new AsymmetricKeyGenerationFailureException(e);
        }
    }

    private InputStream readPublicKeyFile() throws IOException {
        AssetManager assetManager = applicationContext.getAssets();
        return assetManager.open(KEY_FILE);
    }

    private byte[] encrypt(PublicKey publicKey, byte[] plainData) throws AsymmetricEncryptionFailureException {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new AsymmetricEncryptionFailureException(e);
        }
    }

    public static final class AsymmetricEncryptionFailureException extends Exception {
        AsymmetricEncryptionFailureException(Throwable e) {
            super(e);
        }
    }

    public static final class AsymmetricKeyGenerationFailureException extends Exception {
        AsymmetricKeyGenerationFailureException(Throwable e) {
            super(e);
        }
    }
}