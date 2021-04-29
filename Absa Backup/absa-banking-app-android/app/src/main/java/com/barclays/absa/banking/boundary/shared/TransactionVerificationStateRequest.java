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
package com.barclays.absa.banking.boundary.shared;

import android.os.Build;

import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationGetVerificationStateResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationMethod;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationOperation;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.DeviceUtils;

import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0812_TRANSACTION_VERIFICATION_STATUS;
import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS;

class TransactionVerificationStateRequest<T> extends ExtendedRequest<T> {

    private final boolean isPreLogonTransaction;
    private TransactionVerificationMethod transactionVerificationMethod;
    private TransactionVerificationOperation operation;
    private @OpCodeParams.OpCode String opCode;
    private final Class responseClass;

    TransactionVerificationStateRequest(TransactionVerificationMethod verificationMethod, TransactionVerificationOperation verificationOperation, ExtendedResponseListener<T> responseListener, boolean isPreLogonTransaction) {
        super(responseListener);
        setMockResponseFile("transaction_verification/op0812_transaction_verification.json");
        opCode = isPreLogonTransaction ? OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS : OP0812_TRANSACTION_VERIFICATION_STATUS;
        this.isPreLogonTransaction = isPreLogonTransaction;
        transactionVerificationMethod = verificationMethod;
        operation = verificationOperation;
        printRequest();
        responseClass = TransactionVerificationGetVerificationStateResponse.class;
    }

    @Override
    public RequestParams getRequestParams() {
        String deviceID = SecureUtils.INSTANCE.getDeviceID();
        RequestParams.Builder requestParamsBuilder = new RequestParams.Builder()
                .put(opCode)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, DeviceUtils.getChannelId())
                .put(TransactionParams.Transaction.MANUFACTURER, Build.MANUFACTURER)
                .put(TransactionParams.Transaction.MODEL, Build.MODEL)
                .put(TransactionParams.Transaction.IMEI, deviceID)
                .put(TransactionParams.Transaction.DEVICE_ID, deviceID);

        if (transactionVerificationMethod != null) {
            requestParamsBuilder.put(TransactionParams.Transaction.METHOD_NAME, transactionVerificationMethod.getKey());
        }
        if (operation != null) {
            requestParamsBuilder.put(TransactionParams.Transaction.OPERATION, operation.getKey());
        }

        return requestParamsBuilder.build();
    }

    @Override
    public Boolean isEncrypted() {
        return !isPreLogonTransaction;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getResponseClass() {
        return (Class<T>) responseClass;
    }
}