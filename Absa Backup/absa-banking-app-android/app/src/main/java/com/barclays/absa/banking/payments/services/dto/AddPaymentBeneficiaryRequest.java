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

import com.barclays.absa.banking.beneficiaries.services.AddPaymentBeneficiaryResponseParser;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.payments.PaymentsConstants;
import com.barclays.absa.banking.payments.services.PaymentsService;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0334_ADD_PAYMENT_BENEFICIARY;

public class AddPaymentBeneficiaryRequest<T> extends ExtendedRequest<T> {

    private AddBeneficiaryPaymentObject addBeneficiaryPaymentObject;
    private TransactionParams.Transaction notificationMethodDetails;

    public AddPaymentBeneficiaryRequest(AddBeneficiaryPaymentObject addBeneficiaryPaymentObject, TransactionParams.Transaction notificationMethodDetails, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        this.addBeneficiaryPaymentObject = addBeneficiaryPaymentObject;
        this.notificationMethodDetails = notificationMethodDetails;
        setResponseParser(new AddPaymentBeneficiaryResponseParser());
        setMockResponseFile(BeneficiariesMockFactory.Companion.saveBeneficiary());
        params = generateParams();
        printRequest();
    }

    private RequestParams generateParams() {
        RequestParams.Builder requestParams = new RequestParams.Builder();
        requestParams.put(OP0334_ADD_PAYMENT_BENEFICIARY);
        requestParams.put(TransactionParams.Transaction.SERVICE_BENEFICIARY_TYPE, PaymentsService.PAYMENT);
        requestParams.put(TransactionParams.Transaction.SERVICE_BENEFICIARY_STATUS_TYPE, addBeneficiaryPaymentObject.getBenStatusTyp());
        if (PaymentsConstants.BENEFICIARY_STATUS_BUSINESS.equalsIgnoreCase(addBeneficiaryPaymentObject.getBenStatusTyp()) || PaymentsConstants.BENEFICIARY_STATUS_BILL.equalsIgnoreCase(addBeneficiaryPaymentObject.getBenStatusTyp())) {
            requestParams.put(TransactionParams.Transaction.SERVICE_BENEFICIARY_STATUS_TYPE, PaymentsConstants.BENEFICIARY_STATUS_BUSINESS);
            requestParams.put(TransactionParams.Transaction.SERVICE_ACCOUNT_NUMBER, addBeneficiaryPaymentObject.getBankAccountNo());
            requestParams.put(TransactionParams.Transaction.SERVICE_BANK_ACCOUNT_HOLDER, addBeneficiaryPaymentObject.getBeneficiaryName());
            requestParams.put(TransactionParams.Transaction.SERVICE_INSTITUTION_CODE, addBeneficiaryPaymentObject.getInstCode() == null ? null : addBeneficiaryPaymentObject.getInstCode().trim());
            requestParams.put(TransactionParams.Transaction.SERVICE_BANK_ACCOUNT_NO, addBeneficiaryPaymentObject.getAcctAtInst());
        } else {
            requestParams.put(TransactionParams.Transaction.SERVICE_ACCOUNT_NUMBER, addBeneficiaryPaymentObject.getAccountNumber());
            requestParams.put(TransactionParams.Transaction.SERVICE_BEN_RECIPIENT_NAME, addBeneficiaryPaymentObject.getBeneficiaryName());
            requestParams.put(TransactionParams.Transaction.SERVICE_BENEFICIARY_NAME, addBeneficiaryPaymentObject.getBeneficiaryName());
        }
        requestParams.put(TransactionParams.Transaction.SERVICE_ACCOUNT_TYPE, addBeneficiaryPaymentObject.getAccountType());
        requestParams.put(TransactionParams.Transaction.SERVICE_MY_REFERENCE, addBeneficiaryPaymentObject.getMyReference());
        requestParams.put(TransactionParams.Transaction.SERVICE_BEN_REFERENCE, addBeneficiaryPaymentObject.getBeneficiaryReference());
        requestParams.put(TransactionParams.Transaction.SERVICE_IMAGE, addBeneficiaryPaymentObject.getBeneficiaryImageName());
        requestParams.put(TransactionParams.Transaction.SERVICE_IMAGE_NAME, addBeneficiaryPaymentObject.getBeneficiaryImageName());
        requestParams.put(TransactionParams.Transaction.SERVICE_BENEFICIARY_ID, addBeneficiaryPaymentObject.getBeneficiaryId());
        requestParams.put(TransactionParams.Transaction.SERVICE_BANK_NAME, addBeneficiaryPaymentObject.getBankName());
        requestParams.put(TransactionParams.Transaction.SERVICE_BRANCH_NAME, addBeneficiaryPaymentObject.getBranchName());
        requestParams.put(TransactionParams.Transaction.SERVICE_BRANCH_CODE, addBeneficiaryPaymentObject.getBranchCode());
        requestParams.put(TransactionParams.Transaction.SERVICE_BEN_NOTICE_TYPE, addBeneficiaryPaymentObject.getBeneficiaryMethod());
        requestParams.put(TransactionParams.Transaction.SERVICE_THEIR_DETAIL, addBeneficiaryPaymentObject.getBeneficiaryMethodDetails());
        String beneficiaryMethod = addBeneficiaryPaymentObject.getBeneficiaryMethod();
        if (beneficiaryMethod != null) {
            beneficiaryMethod = beneficiaryMethod.substring(0, 1);
            switch (beneficiaryMethod) {
                case "S":
                case BMBConstants.SMS:
                    requestParams.put(TransactionParams.Transaction.SERVICE_THEIR_MOBILE, addBeneficiaryPaymentObject.getBeneficiaryMethodDetails());
                    break;
                case "E":
                    requestParams.put(TransactionParams.Transaction.SERVICE_THEIR_EMAIL, addBeneficiaryPaymentObject.getBeneficiaryMethodDetails());
                    break;
                case "F":
                    requestParams.put(TransactionParams.Transaction.SERVICE_THEIR_FAX_CODE, addBeneficiaryPaymentObject.getBeneficiaryMethodDetails().substring(0, 3));
                    requestParams.put(TransactionParams.Transaction.SERVICE_THEIR_FAX_NUM, addBeneficiaryPaymentObject.getBeneficiaryMethodDetails().substring(3));
                    requestParams.put(TransactionParams.Transaction.SERVICE_THEIR_FAX_NUMBER, addBeneficiaryPaymentObject.getBeneficiaryMethodDetails().substring(3));
                    break;
                default:
                    requestParams.put(TransactionParams.Transaction.SERVICE_THEIR_DETAIL, "N");
            }
        }
        return requestParams.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) AddBeneficiaryPaymentObject.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}
