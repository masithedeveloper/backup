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
package com.barclays.absa.banking.payments.services;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.BankBranches;
import com.barclays.absa.banking.boundary.model.BankDetails;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.ExergyBankListResponse;
import com.barclays.absa.banking.boundary.model.ExergyBranchListResponse;
import com.barclays.absa.banking.boundary.model.Institutions;
import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationObject;
import com.barclays.absa.banking.boundary.model.OnceOffPaymentConfirmationResponse;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.boundary.model.ResendNoticeOfPayment;
import com.barclays.absa.banking.boundary.model.ViewTransactionDetails;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.payments.services.dto.FutureDatedPaymentListResponse;
import com.barclays.absa.banking.payments.services.dto.PaymentConfirmationResponse;

public interface PaymentsService {
    String OP0866_FETCH_FUTURE_DATED_PAYMENTS = "OP0866";
    String OP0926_RESEND_NOTICE_OF_PAYMENT = "OP0926";
    String OP2195_FETCH_EXERGY_BANK_DETAILS = "OP2195";
    String PAYMENT = "payment";

    void fetchFutureDatedPaymentsList(String futureDatedType, String account, ExtendedResponseListener<FutureDatedPaymentListResponse> responseListener);
    void addPaymentBeneficiary(AddBeneficiaryPaymentObject addBeneficiaryPaymentObject, TransactionParams.Transaction notificationMethodDetails, ExtendedResponseListener<AddBeneficiaryPaymentObject> addPaymentBeneficiaryListener);
    void fetchExergyBankList(ExtendedResponseListener<ExergyBankListResponse> responseListener);
    void fetchExergyBranchList(String bankName, ExtendedResponseListener<ExergyBranchListResponse> responseListener);
    void fetchBankList(ExtendedResponseListener<BankDetails> bankListListener);
    void fetchBranchList(String bankName, ExtendedResponseListener<BankBranches> branchListListener);
    void fetchInstitutionList(ExtendedResponseListener<Institutions> institutionListListener);
    void validatePayment(PayBeneficiaryPaymentConfirmationObject payBeneficiaryConfirmationObject, ExtendedResponseListener<PayBeneficiaryPaymentConfirmationObject> validatePaymentListener);
    void performPayment(String transactionReferenceId, ExtendedResponseListener<PaymentConfirmationResponse> paymentResultListener);
    void validateOnceOffPayment(OnceOffPaymentConfirmationObject onceOffPaymentConfirmationObject, ExtendedResponseListener<OnceOffPaymentConfirmationResponse> validateOnceOffPaymentListener);
    void performOnceOffPayment(String referenceId, ExtendedResponseListener<PaymentConfirmationResponse> performOncePaymentListener);
    void editBeneficiaryConfirmation(BeneficiaryDetailObject beneficiaryDetailObject, TransactionParams.Transaction notificationMethodDetails, ExtendedResponseListener<AddBeneficiaryPaymentObject> editPaymentConfirmationListener);
    void editBeneficiaryResult(String referenceNumber, String hasImage, ExtendedResponseListener<AddBeneficiaryPaymentObject> editPaymentResultResponseListener);
    void resendProofOfPayment(ViewTransactionDetails viewTransactionDetails, Bundle bundle, ExtendedResponseListener<ResendNoticeOfPayment> resendNoticeOfPaymentResponseListener);
    void addNewBeneficiaryConfirmation(AddBeneficiaryPaymentObject addBeneficiaryPaymentObject, Bitmap beneficiaryImage, boolean hasImage, ExtendedResponseListener<AddBeneficiaryPaymentObject> responseListener);
    void addNewBeneficiaryResult(String referenceNumber, String hasImage, ExtendedResponseListener<AddBeneficiaryPaymentObject> addNewBeneficiaryResultListener);
}