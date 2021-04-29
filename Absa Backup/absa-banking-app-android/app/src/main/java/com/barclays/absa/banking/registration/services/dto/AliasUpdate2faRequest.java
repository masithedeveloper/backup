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

import com.barclays.absa.banking.boundary.shared.dto.AliasIdUserUpdateRequest;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.crypto.AsymmetricCryptoHelper;
import com.barclays.absa.crypto.SymmetricCryptoHelper;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchProviderException;

import javax.crypto.SecretKey;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0813_UPDATE_USER_ID;

public class AliasUpdate2faRequest<T> extends ExtendedRequest<T> {

    private static final String TAG = AliasIdUserUpdateRequest.class.getSimpleName();
    private final String idNumber;
    private final String idType;
    private String base64EncodedEncrypedSymmetricKey;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public AliasUpdate2faRequest(String idNumber, String idType, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        this.idNumber = idNumber;
        this.idType = idType;
        setMockResponseFile("registration/op0813_update_user_id.json");
        printRequest();
    }

    private void generateSymmetricKey() {
        try {
            SymmetricCryptoHelper cryptoHelper = SymmetricCryptoHelper.getInstance();
            SecretKey symmetricKey = cryptoHelper.generateKey();
            cryptoHelper.storeAliasCreationSymmetricKey(symmetricKey);
            byte[] encryptedSymmetricKey;
            try {
                encryptedSymmetricKey = AsymmetricCryptoHelper.getInstance().encryptSymmetricKey(symmetricKey.getEncoded());
                base64EncodedEncrypedSymmetricKey = Base64.encodeToString(encryptedSymmetricKey, Base64.NO_WRAP);
            } catch (AsymmetricCryptoHelper.AsymmetricKeyGenerationFailureException | AsymmetricCryptoHelper.AsymmetricEncryptionFailureException e) {
                BMBLogger.e(TAG, e.getMessage());
            }

        } catch (SymmetricCryptoHelper.KeyGenerationFailureException | SymmetricCryptoHelper.KeyStoreEntryAccessException | NoSuchProviderException | UnsupportedEncodingException e) {
            BMBLogger.e(TAG, "Aborted request generate - unable to generate, store or encrypt symmetric key");
            BMBLogger.e(TAG, e.getMessage());
        }
    }

    @Override
    public RequestParams getRequestParams() {
        generateSymmetricKey();
        return new RequestParams.Builder()
                .put(OP0813_UPDATE_USER_ID)
                .put(TransactionParams.Transaction.ID_NUMBER, idNumber)
                .put(TransactionParams.Transaction.ID_TYPE, idType)
                .put(TransactionParams.Transaction.TRUST_TOKEN, appCacheService.getTrustToken())
                .put(TransactionParams.Transaction.SYMMETRIC_KEY, base64EncodedEncrypedSymmetricKey)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) AliasUpdate2faResponse.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
