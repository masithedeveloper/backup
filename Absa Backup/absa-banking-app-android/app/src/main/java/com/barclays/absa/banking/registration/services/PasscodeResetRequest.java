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
package com.barclays.absa.banking.registration.services;

import android.util.Base64;

import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.registration.services.dto.RegisterCredentialsRequest;
import com.barclays.absa.banking.registration.services.dto.RegisterCredentialsResponse;
import com.barclays.absa.crypto.AsymmetricCryptoHelper;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.ProfileManager;

import java.io.IOException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0841_REGISTER_CREDENTIALS;

class PasscodeResetRequest<T> extends ExtendedRequest<T> {

    private static final String TAG = RegisterCredentialsRequest.class.getSimpleName();
    private String fiveDigitPasscode;
    private String base64EncodedEncrypedSymmetricKey;
    private String encryptedBase64EncodedAlias;
    private String base64EncodedEncryptedPasscode;
    private SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    PasscodeResetRequest(String passCode, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        fiveDigitPasscode = passCode;
        try {
            generateSymmetricKeyAndEncodeEncryptedParams();
        } catch (SymmetricCryptoHelper.KeyGenerationFailureException | SymmetricCryptoHelper.EncryptionFailureException | SymmetricCryptoHelper.DecryptionFailureException | SymmetricCryptoHelper.KeyStoreEntryAccessException | IOException | AsymmetricCryptoHelper.AsymmetricKeyGenerationFailureException | AsymmetricCryptoHelper.AsymmetricEncryptionFailureException e) {
            BMBLogger.d(TAG, e.getMessage());
            BMBLogger.e(TAG, "Aborted request generate - unable to generate or encrypt symmetric key");
        } catch (Exception e) {
            BMBLogger.d(TAG, "Could not derive credential");
        }

        String hashedDeviceId = SecureUtils.INSTANCE.getDeviceID();
        params = new RequestParams.Builder()
                .put(OP0841_REGISTER_CREDENTIALS)
                .put(TransactionParams.Transaction.AUTH_TYPE, BMBConstants.AUTH_TYPE_DIGITPIN)
                .put(TransactionParams.Transaction.PIN, base64EncodedEncryptedPasscode)
                .put(TransactionParams.Transaction.ALIAS, encryptedBase64EncodedAlias)
                .put(TransactionParams.Transaction.SERIAL_NUMBER, hashedDeviceId)
                .put(TransactionParams.Transaction.TRUST_TOKEN, appCacheService.getTrustToken())
                .put(TransactionParams.Transaction.SYMMETRIC_KEY, base64EncodedEncrypedSymmetricKey)
                .build();

        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        return params;
    }

    private void generateSymmetricKeyAndEncodeEncryptedParams() throws Exception {
        // retrieve alias
        UserProfile userProfile = ProfileManager.getInstance().getActiveUserProfile();
        byte[] zeroEncryptedAliasBytes = symmetricCryptoHelper.retrieveAlias(userProfile.getUserId());
        String userProfileAlias = new String(symmetricCryptoHelper.decryptAliasWithZeroKey(zeroEncryptedAliasBytes));
        BMBLogger.d("x-response", "alias: " + userProfileAlias);

        symmetricCryptoHelper.setSymmetricKey(symmetricCryptoHelper.generateKey());

        byte[] encryptedAlias = symmetricCryptoHelper.encryptAlias(userProfileAlias);
        encryptedBase64EncodedAlias = Base64.encodeToString(encryptedAlias, Base64.NO_WRAP);
        BMBLogger.d("x-response", "Encrypted alias: " + encryptedBase64EncodedAlias);

        // symmetricCryptoHelper.setSymmetricKey(symmetricCryptoHelper.generateKey());

        SecretKey symmetricKey = new SecretKeySpec(symmetricCryptoHelper.getSecretKeyBytes(), "AES");
        byte[] encryptedSymmetricKey = AsymmetricCryptoHelper.getInstance().encryptSymmetricKey(symmetricKey.getEncoded());
        base64EncodedEncrypedSymmetricKey = Base64.encodeToString(encryptedSymmetricKey, Base64.NO_WRAP);
        final String deviceID = SecureUtils.INSTANCE.getDeviceID();
        // PIN
        byte[] credentialBytes = symmetricCryptoHelper.deriveCredential(userProfileAlias, fiveDigitPasscode, deviceID);
        byte[] encryptedCredentialBytes = symmetricCryptoHelper.encryptCredential(credentialBytes);
        base64EncodedEncryptedPasscode = Base64.encodeToString(encryptedCredentialBytes, Base64.NO_WRAP);
    }

    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) RegisterCredentialsResponse.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
