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

import com.barclays.absa.banking.boundary.model.AddBeneficiaryCashSendConfirmation;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0332_CONFIRM_EDIT_CASH_SEND_BENEFICIARY;

public class ConfirmEditCashSendBeneficiaryRequest<T> extends ExtendedRequest<T> {

    public ConfirmEditCashSendBeneficiaryRequest(BeneficiaryDetailObject beneficiaryDetailObject, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        BMBLogger.d("x-req", this.getClass().getSimpleName());
        params = new RequestParams.Builder()
                .put(OP0332_CONFIRM_EDIT_CASH_SEND_BENEFICIARY)
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_ID, beneficiaryDetailObject.getBeneficiaryId())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_NAME, beneficiaryDetailObject.getBeneficiaryName())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_TYPE, BMBConstants.PASS_CASHSEND.toLowerCase())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_SHORT_NAME, beneficiaryDetailObject.getBeneficiaryName())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_SUR_NAME, beneficiaryDetailObject.getBeneficiarySurName())
                .put(TransactionParams.Transaction.SERVICE_MY_REFERENCE, beneficiaryDetailObject.getMyReference())
                .put(TransactionParams.Transaction.SERVICE_CELL_NUMBER, beneficiaryDetailObject.getActNo())
                .put(TransactionParams.Transaction.SERVICE_IMAGE, beneficiaryDetailObject.getImageName())
                .put(TransactionParams.Transaction.SERVICE_IMAGE_NAME, beneficiaryDetailObject.getImageName())
                .build();
        // setResponseParser(new AddBeneficiaryCashSendResponseParser());
        setMockResponseFile("cash_send/op0332_edit_cash_send_beneficiary_confirmation.json");
        printRequest();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) AddBeneficiaryCashSendConfirmation.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}