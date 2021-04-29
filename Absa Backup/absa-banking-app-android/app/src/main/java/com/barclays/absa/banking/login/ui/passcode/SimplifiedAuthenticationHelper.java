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
package com.barclays.absa.banking.login.ui.passcode;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.login.services.LoginInteractor;
import com.barclays.absa.banking.login.services.LoginService;
import com.barclays.absa.crypto.AsymmetricCryptoHelper;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.key.KeyTools;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.crypto.Cipher;

public final class SimplifiedAuthenticationHelper {
    private static final String TAG = SimplifiedAuthenticationHelper.class.getSimpleName();
    private static final int MAX_THREADS = 4;
    private final ExecutorService executorService;
    private final String deviceId;

    private KeyTools keyTools;
    private String encryptedAlias;
    private String encryptedCredential;
    private String encryptedSymmetricKey;
    private FutureTask<String> aliasEncryptionTask;
    private FutureTask<String> credentialDerivationAndEncryptionTask;
    private FutureTask<String> symmetricKeyEncryptionTask;
    private ExtendedResponseListener<SecureHomePageObject> extendedResponseListener;
    private static SymmetricCryptoHelper symmetricCryptoHelper;
    private LoginService loginService = new LoginInteractor();

    public SimplifiedAuthenticationHelper(Context context) {
        deviceId = SecureUtils.INSTANCE.getDeviceID();
        executorService = Executors.newFixedThreadPool(MAX_THREADS);
        symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        initKeyTools(context);
    }

    private void initKeyTools(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            try {
                keyTools = KeyTools.newInstance(BMBApplication.getInstance());
                keyTools.loadKeys();
            } catch (KeyTools.KeyToolsException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Failed to initialise keytools", e);
                }
            }
        }
    }

    public void setExtendedResponseListener(ExtendedResponseListener<SecureHomePageObject> extendedResponseListener) {
        this.extendedResponseListener = extendedResponseListener;
    }

    public Cipher getCipher() throws KeyTools.KeyToolsException {
        return keyTools.getDecryptionCipher();
    }

    public void performAuthentication(String alias, String secret, boolean isBiometricAuthentication) {
        fireAliasEncryptionTask(alias);
        fireCredentialDerivationAndEncryptionTask(deviceId, alias, secret);
        fireSymmetricKeyEncryptionTask();

        initializeEncryptedAlias();
        initialiseCredential();
        initializeEncryptedSymmetricKey();

        fireCredentialAuthenticationRequest(isBiometricAuthentication);
    }

    private void initializeEncryptedAlias() {
        try {
            encryptedAlias = aliasEncryptionTask.get();
        } catch (InterruptedException | ExecutionException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to load the encrypted symmetric from keychain", e);
            }
        }
    }

    private void initializeEncryptedSymmetricKey() {
        try {
            encryptedSymmetricKey = symmetricKeyEncryptionTask.get();
        } catch (InterruptedException | ExecutionException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to load the symmetric from keychain", e);
            }
        }
    }

    private void initialiseCredential() {
        try {
            encryptedCredential = credentialDerivationAndEncryptionTask.get();
        } catch (InterruptedException | ExecutionException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to derive the user credential....", e);
            }
        }
    }

    private void fireSymmetricKeyEncryptionTask() {
        symmetricKeyEncryptionTask = new FutureTask<>(new SymmetricKeyEncryptionCallable());
        executorService.execute(symmetricKeyEncryptionTask);
    }

    private void fireAliasEncryptionTask(String alias) {
        aliasEncryptionTask = new FutureTask<>(new AliasEncryptionCallable(alias));
        executorService.execute(aliasEncryptionTask);
    }

    private void fireCredentialDerivationAndEncryptionTask(String deviceId, String alias, String passcode) {
        credentialDerivationAndEncryptionTask = new FutureTask<>(new CredentialDerivationAndEncryptionCallable(deviceId, alias, passcode));
        executorService.execute(credentialDerivationAndEncryptionTask);
    }

    private void fireCredentialAuthenticationRequest(boolean useFingerPrint) {
        if (isParametersNonNull()) {
            if (useFingerPrint) {
                loginService.performBiometricLogin(encryptedAlias, encryptedCredential, encryptedSymmetricKey, extendedResponseListener);
            } else {
                loginService.performPasscodeLogin(encryptedAlias, encryptedCredential, encryptedSymmetricKey, extendedResponseListener);
            }
        }
    }

    private boolean isParametersNonNull() {
        return (!TextUtils.isEmpty(encryptedAlias)) &&
                (!TextUtils.isEmpty(encryptedCredential)) &&
                (!TextUtils.isEmpty(encryptedSymmetricKey));
    }

    private static final class AliasEncryptionCallable implements Callable<String> {
        private String alias;

        AliasEncryptionCallable(String alias) {
            this.alias = alias;
        }

        @Override
        public String call() throws Exception {
            byte[] encryptedAliasBuffer = symmetricCryptoHelper.encryptAlias(alias);
            return Base64.encodeToString(encryptedAliasBuffer, Base64.NO_WRAP);
        }
    }

    private static final class CredentialDerivationAndEncryptionCallable implements Callable<String> {

        private String deviceId;
        private String alias;
        private String passcode;

        CredentialDerivationAndEncryptionCallable(String deviceId, String alias, String passcode) {
            this.deviceId = deviceId;
            this.alias = alias;
            this.passcode = passcode;
        }

        @Override
        public String call() throws Exception {
            byte[] credentialBytes = symmetricCryptoHelper.deriveCredential(alias, passcode, deviceId);
            byte[] encryptedCredentialBytes = symmetricCryptoHelper.encryptCredential(credentialBytes);
            return Base64.encodeToString(encryptedCredentialBytes, Base64.NO_WRAP);
        }
    }

    private static final class SymmetricKeyEncryptionCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            final AsymmetricCryptoHelper asymmetricCryptoHelper = AsymmetricCryptoHelper.getInstance();
            byte[] symmetricKey = symmetricCryptoHelper.getSecretKeyBytes();
            byte[] encryptedSymmetricKeyBuffer = asymmetricCryptoHelper.encryptSymmetricKey(symmetricKey);
            return Base64.encodeToString(encryptedSymmetricKeyBuffer, Base64.NO_WRAP);
        }
    }
}
