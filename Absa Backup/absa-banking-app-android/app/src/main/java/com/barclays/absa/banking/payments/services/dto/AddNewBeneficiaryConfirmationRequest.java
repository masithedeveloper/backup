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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import com.barclays.absa.banking.beneficiaries.services.AddPaymentBeneficiaryResponseParser;
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryUtils;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0329_ADD_NEW_BENEFICIARY_CONFIRMATION;

public class AddNewBeneficiaryConfirmationRequest<T> extends ExtendedRequest<T> {

    private AddBeneficiaryPaymentObject addBeneficiaryPaymentObject;
    private Bitmap beneficiaryImage;
    private boolean hasImage;

    public AddNewBeneficiaryConfirmationRequest(AddBeneficiaryPaymentObject addBeneficiaryPaymentObject, Bitmap beneficiaryImage, boolean hasImage, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        this.addBeneficiaryPaymentObject = addBeneficiaryPaymentObject;
        this.beneficiaryImage = beneficiaryImage;
        this.hasImage = hasImage;
        params = generateRequestParameters();
        setResponseParser(new AddPaymentBeneficiaryResponseParser());
        setMockResponseFile(BeneficiariesMockFactory.Companion.addPaymentConfirmation());
        printRequest();
    }

    private RequestParams generateRequestParameters() {
        String institutionCode = addBeneficiaryPaymentObject.getInstCode() == null ?
                addBeneficiaryPaymentObject.getInstCode() :
                addBeneficiaryPaymentObject.getInstCode().trim();

        Bundle instanceState = new Bundle();
        instanceState.putParcelable(BMBConstants.BENEFICIARY_IMG_DATA, beneficiaryImage);
        instanceState.putBoolean(BMBConstants.HAS_IMAGE, hasImage);
        instanceState.putBoolean(BMBConstants.CHANGE_IMAGE, false);

        RequestParams.Builder requestParamsBuilder = new RequestParams.Builder()
                .put(OP0329_ADD_NEW_BENEFICIARY_CONFIRMATION)
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_NAME, addBeneficiaryPaymentObject.getBeneficiaryName())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_TYPE, BMBConstants.PASS_PAYMENT.toLowerCase())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_TIEBNUMBER, addBeneficiaryPaymentObject.getTiebNumber())
                .put(TransactionParams.Transaction.SERVICE_BENEFICIARY_STATUS_TYPE, addBeneficiaryPaymentObject.getBenStatusTyp())
                .put(TransactionParams.Transaction.SERVICE_ACCOUNT_NUMBER, addBeneficiaryPaymentObject.getAccountNumber())
                .put(TransactionParams.Transaction.SERVICE_ACCOUNT_TYPE, addBeneficiaryPaymentObject.getAccountType())
                .put(TransactionParams.Transaction.SERVICE_MY_REFERENCE, addBeneficiaryPaymentObject.getMyReference())
                .put(TransactionParams.Transaction.SERVICE_MY_NOTICE_TYPE, addBeneficiaryPaymentObject.getMyMethod())
                .put(TransactionParams.Transaction.SERVICE_BEN_REFERENCE, addBeneficiaryPaymentObject.getBeneficiaryReference())
                .put(TransactionParams.Transaction.SERVICE_BEN_NOTICE_TYPE, addBeneficiaryPaymentObject.getBeneficiaryMethod())
                .put(TransactionParams.Transaction.SERVICE_IMAGE, addBeneficiaryPaymentObject.getBeneficiaryImageName())
                .put(TransactionParams.Transaction.SERVICE_IMAGE_NAME, addBeneficiaryPaymentObject.getBeneficiaryImageName())
                .put(TransactionParams.Transaction.SERVICE_BENEFICAIRY_FAVORITE, addBeneficiaryPaymentObject.getAddToFavourite())
                .put(TransactionParams.Transaction.SERVICE_BANK_ACCOUNT_NO, addBeneficiaryPaymentObject.getAcctAtInst())
                .put(TransactionParams.Transaction.SERVICE_INSTITUTION_CODE, institutionCode)
                .put(TransactionParams.Transaction.SERVICE_BANK_ACCOUNT_HOLDER, addBeneficiaryPaymentObject.getAccountHolderName())
                .put(TransactionParams.Transaction.SERVICE_BEN_RECIPIENT_NAME, addBeneficiaryPaymentObject.getBeneficiaryName());

        if (!TextUtils.isEmpty(addBeneficiaryPaymentObject.getBeneficiaryId())) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_BENEFICIARY_ID, addBeneficiaryPaymentObject.getBeneficiaryId());
        }

        if (!TextUtils.isEmpty(addBeneficiaryPaymentObject.getBankName())) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_BANK_NAME, addBeneficiaryPaymentObject.getBankName());
        }

        if (!TextUtils.isEmpty(addBeneficiaryPaymentObject.getBranchName())) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_BRANCH_NAME, addBeneficiaryPaymentObject.getBranchName());
        }

        if (!TextUtils.isEmpty(addBeneficiaryPaymentObject.getBranchCode())) {
            requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_BRANCH_CODE, addBeneficiaryPaymentObject.getBranchCode());
        }

        switch (BeneficiaryUtils.getMethodTypeIndex(addBeneficiaryPaymentObject.getMyMethod())) {
            case 0:
                requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_MY_MOBILE, addBeneficiaryPaymentObject.getMyMethodDetails());
                break;
            case 1:
                requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_MY_EMAIL, addBeneficiaryPaymentObject.getMyMethodDetails());
                break;
            case 2:
                requestParamsBuilder
                        .put(TransactionParams.Transaction.SERVICE_MY_FAX_NUM, addBeneficiaryPaymentObject.getMyMethodDetails())
                        .put(TransactionParams.Transaction.SERVICE_MY_FAX_CODE, addBeneficiaryPaymentObject.getMyFaxCode());
                break;
        }

        switch (BeneficiaryUtils.getMethodTypeIndex(addBeneficiaryPaymentObject.getMyMethod())) {
            case 0:
                requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_THEIR_MOBILE,
                        addBeneficiaryPaymentObject.getBeneficiaryMethodDetails());
                break;
            case 1:
                requestParamsBuilder.put(TransactionParams.Transaction.SERVICE_THEIR_EMAIL,
                        addBeneficiaryPaymentObject.getBeneficiaryMethodDetails());
                break;
            case 2:
                requestParamsBuilder
                        .put(TransactionParams.Transaction.SERVICE_THEIR_FAX_NUM, addBeneficiaryPaymentObject.getBeneficiaryMethodDetails())
                        .put(TransactionParams.Transaction.SERVICE_THEIR_FAX_CODE, addBeneficiaryPaymentObject.getBenFaxCode());
                break;
        }
        return requestParamsBuilder.build();
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
