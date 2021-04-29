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

import com.barclays.absa.banking.boundary.model.cashSend.CashSendOnceOffResult;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;

import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0611_ONCE_OFF_CASHSEND_RESULT;

public class OnceOffCashSendResultRequest<T> extends ExtendedRequest<T> {
    private final String transactionReference;

    public OnceOffCashSendResultRequest(String transactionReference, ExtendedResponseListener<T> performOnceOffCashSendResponseListener) {
        super(performOnceOffCashSendResponseListener);
        this.transactionReference = transactionReference;
        setMockResponseFile("cash_send/op0611_onceoff_cashsend_result.json");
        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        return new RequestParams.Builder()
                .put(OP0611_ONCE_OFF_CASHSEND_RESULT)
                .put(TransactionParams.Transaction.SERVICE_TXN_REF_CASHSEND, transactionReference)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) CashSendOnceOffResult.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}