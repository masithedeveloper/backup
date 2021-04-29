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
package com.barclays.absa.banking.login.services.dto;

import android.os.Build;
import android.util.Base64;

import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.login.services.LoginService;
import com.barclays.absa.crypto.AsymmetricCryptoHelper;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.ProfileManager;

import java.io.IOException;
import java.security.NoSuchProviderException;

import javax.crypto.SecretKey;

import static com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction.ALIAS;
import static com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction.APP_VERSION;
import static com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction.AUTH_TYPE;
import static com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction.MANUFACTURER;
import static com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction.MODEL;
import static com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction.PIN;
import static com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction.SERIAL_NUMBER;
import static com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction.SYMMETRIC_KEY;
import static com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction.TRUST_TOKEN;
import static com.barclays.absa.banking.login.services.LoginService.OP0817_AUTHENTICATE_ALIAS;

public class AuthenticateAliasRequest<T> extends ExtendedRequest<T> {

    private static final String TAG = AuthenticateAliasRequest.class.getSimpleName();
    private UserProfile user;
    private String credential;
    private final boolean useBiometrics;
    private String base64EncodedEncryptedDerivedCredential;
    private String base64EncodedEncryptedSymmetricKey;
    private String base64EncodedEncryptedAlias;
    private SymmetricCryptoHelper symmetricCryptoHelper;
    private String deviceId = SecureUtils.INSTANCE.getDeviceID();
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public AuthenticateAliasRequest(UserProfile userProfile, String credential, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        user = userProfile;
        this.credential = credential;
        this.useBiometrics = false;
        appCacheService.setAuthCredentialType(TransaktDelegate.CREDENTIAL_TYPE_5_DIGIT_PASSCODE);

        try {
            generateSecureParameters();
            params = createRequestParams();
        } catch (Exception e) {
            BMBLogger.e(TAG, "Cannot generate secure parameters" + e.getMessage());
        }
        printRequest();
    }

    public AuthenticateAliasRequest(UserProfile userProfile, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        user = userProfile;
        this.useBiometrics = true;
        appCacheService.setAuthCredentialType(TransaktDelegate.CREDENTIAL_TYPE_BIOMETRIC);

        try {
            generateSecureParameters();
            params = createRequestParams();
        } catch (Exception e) {
            BMBLogger.e(TAG, "Cannot generate secure parameters" + e.getMessage());
        }
        printRequest();

    }

    private void generateSecureParameters() throws AsymmetricCryptoHelper.AsymmetricEncryptionFailureException, AsymmetricCryptoHelper.AsymmetricKeyGenerationFailureException, SymmetricCryptoHelper.DecryptionFailureException, IOException, SymmetricCryptoHelper.KeyGenerationFailureException, SymmetricCryptoHelper.EncryptionFailureException, SymmetricCryptoHelper.KeyStoreEntryAccessException, NoSuchProviderException {
        symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        byte[] aliasEncryptedWithZeroKey;
        byte[] randomNumberDecryptedWithZeroKey;
        byte[] credentialBuffer;
        byte[] derivedCredential;
        String alias;
        if (symmetricCryptoHelper.hasAliasRegistered()) {
            try {
                String EXISTING_USER_ALIAS_KEY = "alias_key";
                aliasEncryptedWithZeroKey = symmetricCryptoHelper.retrieveAlias(EXISTING_USER_ALIAS_KEY);
            } catch (SymmetricCryptoHelper.KeyStoreEntryAccessException e) {
                throw new RuntimeException("Failed to retrieve alias from key chain");
            }
        } else {
            aliasEncryptedWithZeroKey = symmetricCryptoHelper.retrieveAlias(user.getUserId());
        }

        byte[] decryptedAlias = symmetricCryptoHelper.decryptAliasWithZeroKey(aliasEncryptedWithZeroKey);
        alias = new String(decryptedAlias);
        if (useBiometrics) {
            if (ProfileManager.getInstance().getActiveUserProfile().getMigrationVersion() > 1) {
                credentialBuffer = user.getFingerprintId(); /* NEVER change this to user.getRandomAliasId() as this will break auth completely */
            } else {
                randomNumberDecryptedWithZeroKey = symmetricCryptoHelper.retrieveFingerprintId(user.getUserId());
                if (randomNumberDecryptedWithZeroKey == null) {
                    randomNumberDecryptedWithZeroKey = symmetricCryptoHelper.retrieveAliasId(user.getUserId());
                }
                if (randomNumberDecryptedWithZeroKey == null) {
                    randomNumberDecryptedWithZeroKey = symmetricCryptoHelper.retrieveAliasId(alias);
                }
                if (randomNumberDecryptedWithZeroKey == null) {
                    throw new RuntimeException("Could not find a fingerprint credential for this user");
                }
                credentialBuffer = randomNumberDecryptedWithZeroKey;
            }
            credential = new String(credentialBuffer);
        }
        derivedCredential = symmetricCryptoHelper.deriveCredential(alias, credential, deviceId);

        if (credential != null) {
            appCacheService.setAuthCredential(credential);
        }
        SecretKey symmetricKey = symmetricCryptoHelper.generateKey();
        symmetricCryptoHelper.setSymmetricKey(symmetricKey);
        byte[] encryptedAlias = symmetricCryptoHelper.encryptAlias(alias);
        base64EncodedEncryptedAlias = Base64.encodeToString(encryptedAlias, Base64.NO_WRAP);
        byte[] encryptedCredential = symmetricCryptoHelper.encryptCredential(derivedCredential);
        base64EncodedEncryptedDerivedCredential = Base64.encodeToString(encryptedCredential, Base64.NO_WRAP);
        byte[] symmetricKeyBuffer = symmetricCryptoHelper.getSecretKeyBytes();
        byte[] publicKeyEncryptedSymmetricKeyBuffer = AsymmetricCryptoHelper.getInstance().encryptSymmetricKey(symmetricKeyBuffer);
        base64EncodedEncryptedSymmetricKey = Base64.encodeToString(publicKeyEncryptedSymmetricKeyBuffer, Base64.NO_WRAP);
    }

    private RequestParams createRequestParams() {
        RequestParams.Builder paramsBuilder = new RequestParams.Builder()
                .put(OP0817_AUTHENTICATE_ALIAS)
                .put(ALIAS, base64EncodedEncryptedAlias)
                .put(APP_VERSION, LoginService.APP_VERSION)
                .put(SYMMETRIC_KEY, base64EncodedEncryptedSymmetricKey)
                .put(SERIAL_NUMBER, deviceId)
                .put(MODEL, Build.MODEL)
                .put(MANUFACTURER, Build.MANUFACTURER)
                .put(TRUST_TOKEN, appCacheService.getTrustToken());

        if (useBiometrics) {
            paramsBuilder.put(AUTH_TYPE, BMBConstants.AUTH_TYPE_TOUCHID);
            paramsBuilder.put(TransactionParams.Transaction.TOUCHID, base64EncodedEncryptedDerivedCredential);
            paramsBuilder.put(BMBConstants.AUTH_TYPE_ANDROID_FINGERPRINT, base64EncodedEncryptedDerivedCredential);
        } else {
            paramsBuilder.put(AUTH_TYPE, BMBConstants.AUTH_TYPE_DIGITPIN);
            paramsBuilder.put(PIN, base64EncodedEncryptedDerivedCredential);
        }
        return paramsBuilder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) AuthenticateAliasResponse.class;
    }

    @Override
    public Boolean isEncrypted() {
        return false;
    }
}