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

import android.util.Base64;

import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.MockFactory;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.login.services.LoginService;
import com.barclays.absa.crypto.AsymmetricCryptoHelper;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.DeviceUtils;

import javax.crypto.SecretKey;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP1000_LOGIN_SECURE_HOME_PAGE;
import static com.barclays.absa.banking.framework.app.BMBConstants.AUTH_TYPE_DIGITPIN;
import static com.barclays.absa.banking.framework.app.BMBConstants.AUTH_TYPE_TOUCHID;
import static com.barclays.absa.banking.framework.app.BMBConstants.SIMPLIFIED_LOGIN_YES;

public class SimplifiedLoginRequest<T> extends ExtendedRequest<T> {

    private static final String TAG = SimplifiedLoginRequest.class.getSimpleName();
    private String credential;
    private String deviceId;
    private String base64EncodedEncryptedDerivedCredential;
    private String base64EncodedEncryptedSymmetricKey;
    private String base64EncodedEncryptedAlias;
    private UserProfile user;
    private boolean useBiometrics;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public SimplifiedLoginRequest(UserProfile userProfile, String passCode, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        setResponseParser(new SecureHomePageParser());
        setMockResponseFile(MockFactory.login());
        credential = passCode;
        deviceId = SecureUtils.INSTANCE.getDeviceID();
        user = userProfile;
        try {
            generateSecureParameters();
        } catch (Exception e) {
            BMBLogger.e(TAG, "Cannot generate secure parameters");
        }
        params = buildRequestParams();
        printRequest();
    }

    public SimplifiedLoginRequest(String encryptedAlias, String encryptedCredential, String encryptedSymmetricKey, ExtendedResponseListener<T> responseListener, boolean useBiometrics) {
        super(responseListener);
        setMockResponseFile(MockFactory.login());
        setResponseParser(new SecureHomePageParser());
        this.useBiometrics = useBiometrics;
        base64EncodedEncryptedAlias = encryptedAlias;
        base64EncodedEncryptedSymmetricKey = encryptedSymmetricKey;
        base64EncodedEncryptedDerivedCredential = encryptedCredential;
        params = buildRequestParams();
        printRequest();
    }

    private RequestParams buildRequestParams() {
        RequestParams.Builder requestBuilder = new RequestParams.Builder();
        requestBuilder.put(OP1000_LOGIN_SECURE_HOME_PAGE);
        requestBuilder.put(TransactionParams.Transaction.MANUFACTURER, android.os.Build.MANUFACTURER);
        requestBuilder.put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, DeviceUtils.getChannelId());
        requestBuilder.put(TransactionParams.Transaction.MODEL, android.os.Build.MODEL);
        String deviceID = SecureUtils.INSTANCE.getDeviceID();
        requestBuilder.put(TransactionParams.Transaction.IMEI, deviceID);
        requestBuilder.put(TransactionParams.Transaction.DEVICE_ID, deviceID);
        requestBuilder.put(TransactionParams.Transaction.NICKNAME, "android");
        requestBuilder.put(TransactionParams.Transaction.SERVICE_DEVICE_INTEGRITY, BMBApplication.getDeviceIntegrityFlag());
        requestBuilder.put(TransactionParams.Transaction.SERVICE_FAILED_LOGIN_ATTEMPTS, String.valueOf(BMBApplication.getInstance().TOTAL_FAILED_LOGIN_ATTEMPTS));
        requestBuilder.put(TransactionParams.Transaction.APP_VERSION, LoginService.APP_VERSION);
        requestBuilder.put(TransactionParams.Transaction.ALIAS, base64EncodedEncryptedAlias);
        requestBuilder.put(TransactionParams.Transaction.TRUST_TOKEN, appCacheService.getTrustToken());
        requestBuilder.put("customerSessionId", appCacheService.getCustomerSessionId());

        if (useBiometrics) {
            requestBuilder.put(TransactionParams.Transaction.TOUCHID, base64EncodedEncryptedDerivedCredential);
            requestBuilder.put(TransactionParams.Transaction.AUTH_TYPE, AUTH_TYPE_TOUCHID);
        } else {
            requestBuilder.put(TransactionParams.Transaction.PIN, base64EncodedEncryptedDerivedCredential);
            requestBuilder.put(TransactionParams.Transaction.AUTH_TYPE, AUTH_TYPE_DIGITPIN);
        }

        requestBuilder.put(TransactionParams.Transaction.SYMMETRIC_KEY, base64EncodedEncryptedSymmetricKey);
        requestBuilder.put(TransactionParams.Transaction.SIMPLIFIED_LOGIN, SIMPLIFIED_LOGIN_YES);

        return requestBuilder.build();
    }

    private void generateSecureParameters() throws Exception {
        SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        //generate a new symmetric key for each request
        SecretKey symmetricKey = symmetricCryptoHelper.generateKey();
        symmetricCryptoHelper.setSymmetricKey(symmetricKey);
        byte[] symmetricKeyBuffer = symmetricCryptoHelper.getSecretKeyBytes();
        byte[] publicKeyEncryptedSymmetricKeyBuffer = AsymmetricCryptoHelper.getInstance().encryptSymmetricKey(symmetricKeyBuffer);
        base64EncodedEncryptedSymmetricKey = Base64.encodeToString(publicKeyEncryptedSymmetricKeyBuffer, Base64.NO_WRAP);

        byte[] aliasEncryptedWithZeroKey;
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
        byte[] decrypterAlias = symmetricCryptoHelper.decryptAliasWithZeroKey(aliasEncryptedWithZeroKey);
        String alias = new String(decrypterAlias);
        byte[] credential = symmetricCryptoHelper.deriveCredential(alias, SimplifiedLoginRequest.this.credential, deviceId);
        byte[] encryptedCredential = symmetricCryptoHelper.encryptCredential(credential);
        /*return*/
        base64EncodedEncryptedDerivedCredential = Base64.encodeToString(encryptedCredential, Base64.NO_WRAP);
        byte[] encryptedAlias = symmetricCryptoHelper.encryptAlias(alias);
        base64EncodedEncryptedAlias = Base64.encodeToString(encryptedAlias, Base64.NO_WRAP);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) SecureHomePageObject.class;
    }

    @Override
    public Boolean isEncrypted() {
        return false;
    }
}