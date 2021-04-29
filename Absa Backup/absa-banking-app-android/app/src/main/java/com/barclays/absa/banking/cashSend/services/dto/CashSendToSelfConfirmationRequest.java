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

import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;

import org.jetbrains.annotations.NotNull;

import static com.barclays.absa.banking.cashSend.services.CashSendService.CASH_SEND_PLUS;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.Transaction;

public class CashSendToSelfConfirmationRequest<T> extends CashSendConfirmationRequest<T> {

    public CashSendToSelfConfirmationRequest(CashSendBeneficiaryConfirmation cashSendBeneficiaryConfirmation, PINObject pinObject, boolean termsAccepted, String cellNumber, boolean isCashSendPlus, ExtendedResponseListener<T> responseListener) {
        super(cashSendBeneficiaryConfirmation, pinObject, termsAccepted, isCashSendPlus, responseListener);
        params = super.buildRequestParams()
                .put(Transaction.SERVICE_CELL_NO_CASHSEND, cellNumber)
                .put(Transaction.SERVICE_BEN_ACCOUNT_NUMBER_CASHSEND, cashSendBeneficiaryConfirmation.getCellNumber())
                .put(Transaction.IS_CASHSEND_TO_SELF, Boolean.TRUE.toString())
                .put(Transaction.SERVICE_BEN_NAME_CASHSEND, BMBConstants.SELF)
                .put(CASH_SEND_PLUS, Boolean.toString(isCashSendPlus))
                .build();
        setMockResponseFile("cash_send/op0613_send_beneficiary_cashsend_confirm.json");
        printRequest();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) CashSendBeneficiaryConfirmation.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}