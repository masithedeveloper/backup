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

import com.barclays.absa.banking.boundary.model.CancelCashSendResponse;
import com.barclays.absa.banking.boundary.model.TransactionUnredeem;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;

import static com.barclays.absa.banking.cashSend.services.CashSendService.CASH_SEND_PLUS;
import static com.barclays.absa.banking.cashSend.services.CashSendService.OP0808_CANCEL_TRANSACTION;

public class CancelCashSendTransactionRequest<T> extends ExtendedRequest<T> {

    public CancelCashSendTransactionRequest(boolean isCashSendPlus, TransactionUnredeem transactionUnredeem, ExtendedResponseListener<T> cancelCashSendResponseListener) {
        super(cancelCashSendResponseListener);
        params = new RequestParams.Builder()
                .put(OP0808_CANCEL_TRANSACTION)
                .put(TransactionParams.Transaction.PAYMENT_NO, transactionUnredeem.getTransactionReferenceNumber())
                .put(TransactionParams.Transaction.TRANSACTION_REF_NUMBER, transactionUnredeem.getUniqueEFT())
                .put(CASH_SEND_PLUS, String.valueOf(isCashSendPlus))
                .build();
        setMockResponseFile("cash_send/op0808_cancel_transaction.json");
        printRequest();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) CancelCashSendResponse.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}