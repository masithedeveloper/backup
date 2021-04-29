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
package com.barclays.absa.banking.registration.services.dto;

import android.os.Build;
import android.util.Base64;

import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.login.services.LoginService;
import com.barclays.absa.crypto.AsymmetricCryptoHelper;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.crypto.SymmetricCryptoHelper;

import java.io.IOException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0998_REGISTER_CREDENTIALS;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0999_REGISTER_CREDENTIALS;

public class RegisterCredentialsRequest<T> extends ExtendedRequest<T> {

    private static final String TAG = RegisterCredentialsRequest.class.getSimpleName();
    private final String fiveDigitPasscode;
    private String fingerPrintRandomNumber;
    private String base64EncodedEncrypedSymmetricKey;
    private String encryptedBase64EncodedAlias;
    private String base64EncodedEncryptedPasscodeCredential;
    private String base64EncodedEncryptedFingerprintCredential;
    private SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
    private String sessionId;
    private final boolean authenticated;
    private final Create2faAliasResponse create2faAliasResponse;
    private final Boolean isIdentificationAndVerificationFlow;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public RegisterCredentialsRequest(String passCode, String randomAliasId, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        fiveDigitPasscode = passCode;
        fingerPrintRandomNumber = randomAliasId;
        isIdentificationAndVerificationFlow = appCacheService.isIdentificationAndVerificationFlow();
        final SecureHomePageObject secureHomePageObject = appCacheService.getSecureHomePageObject();
        authenticated = secureHomePageObject != null;
        create2faAliasResponse = appCacheService.getCreate2faAliasResponse();
        sessionId = authenticated ? secureHomePageObject.getCustomerProfile().getSessionId() : (create2faAliasResponse != null) ? create2faAliasResponse.getSessionId() : "";
        setMockResponseFile("registration/op0998_register_credentials_success.json");

        if (isIdentificationAndVerificationFlow) {
            sessionId = appCacheService.getEnterpriseSessionId();
        }

        try {
            generateSymmetricKeyAndEncodeEncryptedParams();
        } catch (SymmetricCryptoHelper.KeyGenerationFailureException | SymmetricCryptoHelper.EncryptionFailureException | SymmetricCryptoHelper.DecryptionFailureException | SymmetricCryptoHelper.KeyStoreEntryAccessException | IOException | AsymmetricCryptoHelper.AsymmetricKeyGenerationFailureException | AsymmetricCryptoHelper.AsymmetricEncryptionFailureException e) {
            BMBLogger.d(TAG, e.getMessage());
            BMBLogger.e(TAG, "Aborted request generate - unable to generate or encrypt symmetric key");
        } catch (Exception e) {
            BMBLogger.d(TAG, "Could not derive credential" + e);
        }
        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        final String deviceNickname = appCacheService.getDeviceNickname();

        String nickname;
        if (deviceNickname != null && !deviceNickname.isEmpty()) {
            nickname = deviceNickname;
        } else {
            nickname = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
        }

        String hashedDeviceId = SecureUtils.INSTANCE.getDeviceID();
        RequestParams.Builder builder = new RequestParams.Builder();
        if (authenticated) {
            builder.put(OP0998_REGISTER_CREDENTIALS);
            builder.put(TransactionParams.Transaction.SESSION_ID, sessionId);
        } else if (isIdentificationAndVerificationFlow) {
            builder.put(OP0998_REGISTER_CREDENTIALS);
            builder.put("sessionId", sessionId);
        } else {
            builder.put(OP0999_REGISTER_CREDENTIALS);
            builder.put("sessionId", sessionId);
        }
        builder.put(TransactionParams.Transaction.MANUFACTURER, android.os.Build.MANUFACTURER)
                .put(TransactionParams.Transaction.MODEL, android.os.Build.MODEL)
                .put(TransactionParams.Transaction.IMEI, hashedDeviceId)
                .put("imie", hashedDeviceId)
                .put("os", "Android")
                .put("osVersion", Build.VERSION.RELEASE)
                .put(TransactionParams.Transaction.APP_VERSION, LoginService.APP_VERSION)
                .put(TransactionParams.Transaction.DEVICE_ID, hashedDeviceId)
                .put(TransactionParams.Transaction.ACTION, "A")
                .put(TransactionParams.Transaction.SERVICE_DEVICE_INTEGRITY, BMBApplication.getDeviceIntegrityFlag())
                //.put(TransactionParams.Transaction.AUTH_TYPE, AUTH_TYPE_ANDROID_FINGERPRINT)
                .put(TransactionParams.Transaction.AUTH_TYPE, BMBConstants.AUTH_TYPE_TOUCHID)
                .put(TransactionParams.Transaction.ALIAS, encryptedBase64EncodedAlias)
                .put(TransactionParams.Transaction.PIN, base64EncodedEncryptedPasscodeCredential)
                .put(TransactionParams.Transaction.TOUCHID, base64EncodedEncryptedFingerprintCredential)
                .put(TransactionParams.Transaction.TRUST_TOKEN, appCacheService.getTrustToken())
                .put(TransactionParams.Transaction.DEVICE_NICKNAME, nickname)

                .put(TransactionParams.Transaction.SYMMETRIC_KEY, base64EncodedEncrypedSymmetricKey);

        if (isIdentificationAndVerificationFlow) {
            builder.put("enterpriseSessionID", appCacheService.getEnterpriseSessionId());
        }

        return builder.build();

    }

    private void generateSymmetricKeyAndEncodeEncryptedParams() throws AsymmetricCryptoHelper.AsymmetricEncryptionFailureException, AsymmetricCryptoHelper.AsymmetricKeyGenerationFailureException, IOException, SymmetricCryptoHelper.KeyStoreEntryAccessException, SymmetricCryptoHelper.DecryptionFailureException, SymmetricCryptoHelper.EncryptionFailureException, SymmetricCryptoHelper.KeyGenerationFailureException {
        SecretKey symmetricKey = new SecretKeySpec(symmetricCryptoHelper.getSecretKeyBytes(), "AES");
        byte[] encryptedSymmetricKey = AsymmetricCryptoHelper.getInstance().encryptSymmetricKey(symmetricKey.getEncoded());
        final String base64EncodedEncryptedAliasId = appCacheService.getEnrollingUserAliasID();
        BMBLogger.d(TAG, "Base64 encoded encrypted alias is " + base64EncodedEncryptedAliasId);
        byte[] encryptedAliasId = Base64.decode(base64EncodedEncryptedAliasId, Base64.DEFAULT);

        BMBLogger.d(TAG, "Base64 decoded encrypted alias is now " + new String(encryptedAliasId));
        BMBLogger.d(TAG, "Base64 encodeBytes result is " + Base64.encodeToString(encryptedAliasId, Base64.NO_WRAP));

        byte[] bytesOfSymmetricKeyThatWasUsedForCreatingAlias = symmetricCryptoHelper.retrieveAlias(SymmetricCryptoHelper.CREATE_ALIAS_SYMMETRIC_KEY);

        SecretKey createAliasSymmetricKey = new SecretKeySpec(bytesOfSymmetricKeyThatWasUsedForCreatingAlias, "AES");
        BMBLogger.d(TAG, "$$$$$$$$$$$$$$ Secret key -> [ " + createAliasSymmetricKey.getAlgorithm() + ": " + new String(createAliasSymmetricKey.getEncoded()) + " ]");

        byte[] decryptedAlias = symmetricCryptoHelper.decryptAlias(createAliasSymmetricKey, encryptedAliasId);
        String aliasID = new String(decryptedAlias);

        BMBLogger.d(TAG, "AliasID -> " + aliasID);

        byte[] reEncryptedAliasId = symmetricCryptoHelper.encryptAlias(aliasID);
        encryptedBase64EncodedAlias = Base64.encodeToString(reEncryptedAliasId, Base64.NO_WRAP);
        BMBLogger.d(TAG, "Encrypted alias: " + encryptedBase64EncodedAlias);
        base64EncodedEncrypedSymmetricKey = Base64.encodeToString(encryptedSymmetricKey, Base64.NO_WRAP);
        final String deviceID = SecureUtils.INSTANCE.getDeviceID();
        byte[] passcodeCredentialBytes = symmetricCryptoHelper.deriveCredential(aliasID, fiveDigitPasscode, deviceID);
        byte[] encryptedPasscodeCredentialBytes = symmetricCryptoHelper.encryptCredential(passcodeCredentialBytes);
        byte[] fingerprintCredentialBytes = symmetricCryptoHelper.deriveCredential(aliasID, fingerPrintRandomNumber, deviceID);
        byte[] encryptedFingerprintCredentialBytes = symmetricCryptoHelper.encryptCredential(fingerprintCredentialBytes);
        base64EncodedEncryptedPasscodeCredential = Base64.encodeToString(encryptedPasscodeCredentialBytes, Base64.NO_WRAP);
        base64EncodedEncryptedFingerprintCredential = Base64.encodeToString(encryptedFingerprintCredentialBytes, Base64.NO_WRAP);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) RegisterCredentialsResponse.class;
    }

    @Override
    public Boolean isEncrypted() {
        return authenticated;
    }
}