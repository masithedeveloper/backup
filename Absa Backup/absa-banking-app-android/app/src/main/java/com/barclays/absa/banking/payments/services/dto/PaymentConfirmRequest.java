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

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.payments.PaymentsConstants;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0525_VALIDATE_PAYMENT;

public class PaymentConfirmRequest<T> extends ExtendedRequest<T> {
    private PayBeneficiaryPaymentConfirmationObject payBeneficiaryConfirmationObject;

    public PaymentConfirmRequest(PayBeneficiaryPaymentConfirmationObject payBeneficiaryConfirmationObject, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        this.payBeneficiaryConfirmationObject = payBeneficiaryConfirmationObject;
        setMockResponseFile(BeneficiariesMockFactory.Companion.beneficiaryPaymentConfirmation());
        params = generateRequestParams();
        printRequest();
    }

    private RequestParams generateRequestParams() {
        final String beneficiaryMethod = payBeneficiaryConfirmationObject.getBeneficiaryMethod();
        RequestParams.Builder requestParamsBuilder = new RequestParams.Builder()
                .put(OP0525_VALIDATE_PAYMENT)
                .put(TransactionParams.Transaction.SERVICE_FROM_ACCOUNT, payBeneficiaryConfirmationObject.getFromAccountNumber())
                .put(TransactionParams.Transaction.SERVICE_FROM_ACCOUNT_TYPE, payBeneficiaryConfirmationObject.getFromAccountType())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_ID, payBeneficiaryConfirmationObject.getBeneficiaryId())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_TYPE, payBeneficiaryConfirmationObject.getBeneficiaryType())
                .put(TransactionParams.Transaction.SERVICE_BANK_NAME, payBeneficiaryConfirmationObject.getBankName())
                .put(TransactionParams.Transaction.SERVICE_BRANCH_NAME, payBeneficiaryConfirmationObject.getBranchName())
                .put(TransactionParams.Transaction.SERVICE_BRANCH_CODE, payBeneficiaryConfirmationObject.getBranchCode())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_ACCOUNT_NUMBER, payBeneficiaryConfirmationObject.getAccountNumber())
                .put(TransactionParams.Transaction.SERVICE_AMOUNT, payBeneficiaryConfirmationObject.getTransactionAmount().getAmount())
                .put(TransactionParams.Transaction.SERVICE_CURRENCY, payBeneficiaryConfirmationObject.getTransactionAmount().getCurrency())
                .put(TransactionParams.Transaction.SERVICE_IMMEDIATE_PAY, payBeneficiaryConfirmationObject.getImmediatePay() != null ? payBeneficiaryConfirmationObject.getImmediatePay() : BMBConstants.NO)
                .put(TransactionParams.Transaction.SERVICE_MY_REFERENCE, payBeneficiaryConfirmationObject.getMyReference())
                .put(TransactionParams.Transaction.SERVICE_MY_NOTICE, payBeneficiaryConfirmationObject.getMyNotice())
                .put(TransactionParams.Transaction.SERVICE_THEIR_METHOD, payBeneficiaryConfirmationObject.getBeneficiaryMethod())
                .put(TransactionParams.Transaction.SERVICE_MY_METHOD, payBeneficiaryConfirmationObject.getMyMethod())
                .put(TransactionParams.Transaction.SERVICE_MY_FAX_CODE, payBeneficiaryConfirmationObject.getMyFaxCode())
                .put(TransactionParams.Transaction.SERVICE_THEIR_REFERENCE, payBeneficiaryConfirmationObject.getBeneficiaryReference())
                .put(TransactionParams.Transaction.SERVICE_THEIR_NOTICE, payBeneficiaryConfirmationObject.getBeneficiaryNotice())
                .put(TransactionParams.Transaction.SERVICE_THEIR_FAX_CODE, payBeneficiaryConfirmationObject.getBenFaxCode())
                .put(TransactionParams.Transaction.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_PAYMENT)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, BMBConstants.SMARTPHONE_CHANNEL_IND)
                .put(TransactionParams.Transaction.SERVICE_BILL_ACC_NUMBER, payBeneficiaryConfirmationObject.getAcctAtInst());
        if (beneficiaryMethod != null) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_THEIR_METHOD, beneficiaryMethod.replaceAll(" ", ""));
        }
        final String myMethodDetails = payBeneficiaryConfirmationObject.getMyMethodDetails();
        if (myMethodDetails != null) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_MY_DETAIL, myMethodDetails.replaceAll(" ", ""));
        }

        if (payBeneficiaryConfirmationObject.getBeneficiaryMethodDetails() != null) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_THEIR_DETAIL, payBeneficiaryConfirmationObject.getBeneficiaryMethodDetails().replaceAll(" ", ""));
        }

        if (PaymentsConstants.BENEFICIARY_STATUS_BUSINESS.equalsIgnoreCase(payBeneficiaryConfirmationObject.getBeneficiaryType()) || BMBConstants.BILL.equalsIgnoreCase(payBeneficiaryConfirmationObject.getBeneficiaryType())) {
            requestParamsBuilder.put(TransactionParams.Transaction.INSTITUTION_NAME, payBeneficiaryConfirmationObject.getBeneficiaryName());
        } else {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_BENEFICIARY_NAME, payBeneficiaryConfirmationObject.getBeneficiaryName());
        }

        if (BMBConstants.NOW.equalsIgnoreCase(payBeneficiaryConfirmationObject.getPaymentDate())) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_NOW_FLAG, BMBConstants.NOW);
        } else {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_NOW_FLAG, BMBConstants.OWN);
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_FUTURE_DATE, payBeneficiaryConfirmationObject.getPaymentDate());
        }
        return requestParamsBuilder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) PayBeneficiaryPaymentConfirmationObject.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
