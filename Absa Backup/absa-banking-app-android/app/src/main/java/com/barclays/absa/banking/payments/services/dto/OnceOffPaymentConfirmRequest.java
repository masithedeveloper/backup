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
package com.barclays.absa.banking.payments.services.dto;

import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationObject;
import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationResponse;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.payments.PaymentsConstants;
import com.barclays.absa.banking.payments.services.OnceOffPaymentConfirmationResponseParser;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0522_VALIDATE_ONCE_OFF_PAYMENT;

public class OnceOffPaymentConfirmRequest<T> extends ExtendedRequest<T> {

    private OnceOffPaymentConfirmationObject onceOffPaymentConfirmationObject;

    public OnceOffPaymentConfirmRequest(OnceOffPaymentConfirmationObject onceOffPaymentConfirmationObject, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        this.onceOffPaymentConfirmationObject = onceOffPaymentConfirmationObject;
        params = buildRequestParameters();
        setResponseParser(new OnceOffPaymentConfirmationResponseParser());
        setMockResponseFile("cash_send/op0522_onceoff_payment_confirm.json");
        printRequest();
    }

    private RequestParams buildRequestParameters() {
        RequestParams.Builder requestParamsBuilder = new RequestParams.Builder()
                .put(OP0522_VALIDATE_ONCE_OFF_PAYMENT)
                .put(TransactionParams.Transaction.SERVICE_FROM_ACCOUNT, onceOffPaymentConfirmationObject.getFromAccountNumber())
                .put(TransactionParams.Transaction.SERVICE_FROM_ACCOUNT_TYPE, onceOffPaymentConfirmationObject.getFromAccountType())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_TYPE, BMBConstants.ANOTHER_BANK.equalsIgnoreCase(onceOffPaymentConfirmationObject.getBeneficiaryType()) ? "Private" : onceOffPaymentConfirmationObject.getBeneficiaryType())
                .put(TransactionParams.Transaction.SERVICE_BRANCH_NAME, onceOffPaymentConfirmationObject.getBranchName())
                .put(TransactionParams.Transaction.SERVICE_MY_REFERENCE, onceOffPaymentConfirmationObject.getMyReference())
                .put(TransactionParams.Transaction.SERVICE_MY_NOTICE, onceOffPaymentConfirmationObject.getMyNotice())
                .put(TransactionParams.Transaction.SERVICE_MY_METHOD, onceOffPaymentConfirmationObject.getMyMethod())
                .put(TransactionParams.Transaction.SERVICE_MY_DETAIL, onceOffPaymentConfirmationObject.getMyMethodDetails() != null ? onceOffPaymentConfirmationObject.getMyMethodDetails().replace(" ", "") : "")
                .put(TransactionParams.Transaction.SERVICE_MY_FAX_CODE, onceOffPaymentConfirmationObject.getMyFaxCode())
                .put(TransactionParams.Transaction.SERVICE_THEIR_NOTICE, onceOffPaymentConfirmationObject.getBeneficiaryNotice())
                .put(TransactionParams.Transaction.SERVICE_THEIR_METHOD, onceOffPaymentConfirmationObject.getBeneficiaryMethod())
                .put(TransactionParams.Transaction.SERVICE_THEIR_DETAIL, onceOffPaymentConfirmationObject.getBeneficiaryMethodDetails() != null ? onceOffPaymentConfirmationObject.getBeneficiaryMethodDetails().replace(" ", "") : "")
                .put(TransactionParams.Transaction.SERVICE_THEIR_FAX_CODE, onceOffPaymentConfirmationObject.getBenFaxCode())
                .put(TransactionParams.Transaction.SERVICE_INSTITUTION_CODE, onceOffPaymentConfirmationObject.getInstCode())
                .put(TransactionParams.Transaction.SERVICE_BILL_ACC_NUMBER, onceOffPaymentConfirmationObject.getAccountNumber())
                .put(TransactionParams.Transaction.SERVICE_BANK_ACCOUNT_HOLDER, onceOffPaymentConfirmationObject.getBankAccountHolder())
                .put(TransactionParams.Transaction.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_PAYMENT)
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_ACCOUNT_TYPE_OTHERBANK, onceOffPaymentConfirmationObject.getAccountType())
                .put(TransactionParams.Transaction.SERVICE_AMOUNT, onceOffPaymentConfirmationObject.getTransactionAmount().getAmount())
                .put(TransactionParams.Transaction.SERVICE_CURRENCY, onceOffPaymentConfirmationObject.getTransactionAmount().getCurrency())
                .put(TransactionParams.Transaction.SERVICE_BRANCH_CODE, onceOffPaymentConfirmationObject.getBranchCode() == null ? null : onceOffPaymentConfirmationObject.getBranchCode().trim()
                );

        if (BMBConstants.BILL.equalsIgnoreCase(onceOffPaymentConfirmationObject.getBeneficiaryType())) {
            requestParamsBuilder
                    .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_ACCOUNT_NUMBER, onceOffPaymentConfirmationObject.getAccountNumber())
                    .put(TransactionParams.Transaction.SERVICE_BRANCH_NAME, onceOffPaymentConfirmationObject.getBeneficiaryName())
                    .put(TransactionParams.Transaction.SERVICE_BRANCH_CODE,
                            onceOffPaymentConfirmationObject.getInstCode() == null ?
                                    null : onceOffPaymentConfirmationObject.getInstCode().trim())
                    .put(TransactionParams.Transaction.SERVICE_THEIR_REFERENCE, onceOffPaymentConfirmationObject.getBankAccountHolder())
                    .put(TransactionParams.Transaction.INSTITUTION_NAME, onceOffPaymentConfirmationObject.getBeneficiaryName());

        } else {
            requestParamsBuilder
                    .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_ACCOUNT_NUMBER, onceOffPaymentConfirmationObject.getAccountNumber())
                    .put(TransactionParams.Transaction.SERVICE_BANK_NAME, onceOffPaymentConfirmationObject.getBankName())
                    .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_NAME, onceOffPaymentConfirmationObject.getBeneficiaryName().trim())
                    .put(TransactionParams.Transaction.SERVICE_THEIR_REFERENCE, onceOffPaymentConfirmationObject.getBeneficiaryReference());
        }

        if (BMBConstants.NOW.equalsIgnoreCase(onceOffPaymentConfirmationObject.getPaymentDate())) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_NOW_FLAG, BMBConstants.NOW);
        } else {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_NOW_FLAG, BMBConstants.OWN);
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_FUTURE_DATE, onceOffPaymentConfirmationObject.getPaymentDate());
        }

        if (BMBConstants.OTHER_BANK.equalsIgnoreCase(onceOffPaymentConfirmationObject.getBeneficiaryType()) || PaymentsConstants.BENEFICIARY_STATUS_TYPE_PRIVATE.equalsIgnoreCase(onceOffPaymentConfirmationObject.getBeneficiaryType()) || BMBConstants.ANOTHER_BANK.equalsIgnoreCase(onceOffPaymentConfirmationObject.getBeneficiaryType())) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_IMMEDIATE_PAY, onceOffPaymentConfirmationObject.getImmediatePay());
        }
        return requestParamsBuilder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) OnceOffPaymentConfirmationResponse.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
