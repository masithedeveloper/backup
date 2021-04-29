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

import android.util.Base64;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.crypto.AsymmetricCryptoHelper;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.crypto.SymmetricCryptoHelper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static com.barclays.absa.banking.registration.services.RegistrationService.OP0815_CREATE_2FA_ALIAS_UNAUTHENTICATED_OPCODE;
import static com.barclays.absa.banking.registration.services.RegistrationService.OP0821_CREATE_2FA_ALIAS_OPCODE;

public class Create2faAliasRequest<T> extends ExtendedRequest<T> {
    private boolean isPostLoginCall;
    private static final String TAG = Create2faAliasRequest.class.getSimpleName();
    private static final int MAX_THREADS = 4;
    private final SymmetricCryptoHelper symmetricCryptoHelper;
    private final AsymmetricCryptoHelper asymmetricCryptoHelper;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private final FutureTask<String> encryptedSymmetricKeyTask = new FutureTask<>(new Callable<String>() {
        @Override
        public String call() throws Exception {
            byte[] symmetricKey = symmetricCryptoHelper.getSecretKeyBytes();
            SecretKey secretKey = new SecretKeySpec(symmetricKey, "AES");
            symmetricCryptoHelper.storeAliasCreationSymmetricKey(secretKey);
            byte[] encryptedSymmetricKeyBuffer = asymmetricCryptoHelper.encryptSymmetricKey(symmetricKey);
            return Base64.encodeToString(encryptedSymmetricKeyBuffer, Base64.NO_WRAP);
        }
    });
    private String encryptedSymmetricKey;

    public Create2faAliasRequest(ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        setMockResponseFile("registration/op0821_create_2fa_alias.json");
        symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
        asymmetricCryptoHelper = AsymmetricCryptoHelper.getInstance();
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
        executorService.execute(encryptedSymmetricKeyTask);
        createRequestParameters();
        printRequest();
    }

    private void createRequestParameters() {
        try {
            if (encryptedSymmetricKey == null) {
                encryptedSymmetricKey = encryptedSymmetricKeyTask.get();
            }
            isPostLoginCall = appCacheService.getSecureHomePageObject() != null;
            RequestParams.Builder paramsBuilder = new RequestParams.Builder()
                    .put(TransactionParams.Transaction.DEVICE_ID, SecureUtils.INSTANCE.getDeviceID())
                    .put(TransactionParams.Transaction.TRUST_TOKEN, appCacheService.getTrustToken())
                    .put(TransactionParams.Transaction.SYMMETRIC_KEY, encryptedSymmetricKey);

            paramsBuilder.put(isPostLoginCall ? OP0821_CREATE_2FA_ALIAS_OPCODE : OP0815_CREATE_2FA_ALIAS_UNAUTHENTICATED_OPCODE);

            if (BuildConfig.TOGGLE_DEF_DEVICE_PROFILING_SCAN_QR_ENABLED && BMBApplication.getInstance().isDeviceProfilingActive() && !isPostLoginCall) {
                paramsBuilder.put("customerSessionId", appCacheService.getCustomerSessionId());
            }

            if (appCacheService.isIdentificationAndVerificationFlow()) {
                paramsBuilder.put(TransactionParams.Transaction.SESSION_ID, appCacheService.getEnterpriseSessionId());
                paramsBuilder.put(OP0821_CREATE_2FA_ALIAS_OPCODE);
            }

            params = paramsBuilder.build();
        } catch (NullPointerException | InterruptedException | ExecutionException e) {
            BMBLogger.e(TAG, "Aborted request generate - unable to generate, store or encrypt symmetric key");
            BMBLogger.e(TAG, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) Create2faAliasResponse.class;
    }

    @Override
    public Boolean isEncrypted() {
        return isPostLoginCall;
    }
}