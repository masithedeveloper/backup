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

package com.barclays.absa.banking.cashSend.services.dto;

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory;
import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0609_CASHSEND_PIN_ENCRYPTION;

public class CashSendPinEncryptionRequest<T> extends ExtendedRequest<T> {
    private final RequestParams params;

    public CashSendPinEncryptionRequest(String pin, ExtendedResponseListener<T> extendedResponseListener) {
        super(BuildConfigHelper.INSTANCE.getPinEncryptServerPath(), extendedResponseListener);
        params = new RequestParams.Builder()
                .put(OP0609_CASHSEND_PIN_ENCRYPTION)
                .put(TransactionParams.Transaction.SERVICE_ACCESS_PIN, pin)
                .build();
        setMockResponseFile(BeneficiariesMockFactory.Companion.onceOffCashSendEncyption());
        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        return params;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) PINObject.class;
    }

    @Override
    public Boolean isEncrypted() {
        return false;
    }
}