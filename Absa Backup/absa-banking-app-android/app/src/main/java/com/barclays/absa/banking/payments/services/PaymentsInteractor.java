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

import androidx.annotation.NonNull;

import com.barclays.absa.banking.beneficiaries.services.dto.ConfirmEditBeneficiaryRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.EditBeneficiaryResultRequest;
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
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.funeralCover.services.dto.FetchExergyBankListRequest;
import com.barclays.absa.banking.funeralCover.services.dto.FetchExergyBranchListRequest;
import com.barclays.absa.banking.payments.services.dto.AddNewBeneficiaryConfirmationRequest;
import com.barclays.absa.banking.payments.services.dto.AddNewBeneficiaryResultRequest;
import com.barclays.absa.banking.payments.services.dto.AddPaymentBeneficiaryRequest;
import com.barclays.absa.banking.payments.services.dto.BankBranchListRequest;
import com.barclays.absa.banking.payments.services.dto.BankListRequest;
import com.barclays.absa.banking.payments.services.dto.FutureDatedPaymentListResponse;
import com.barclays.absa.banking.payments.services.dto.FutureDatedPaymentsRequest;
import com.barclays.absa.banking.payments.services.dto.InstitutionListRequest;
import com.barclays.absa.banking.payments.services.dto.OnceOffPaymentConfirmRequest;
import com.barclays.absa.banking.payments.services.dto.OnceOffPaymentResultRequest;
import com.barclays.absa.banking.payments.services.dto.PaymentConfirmRequest;
import com.barclays.absa.banking.payments.services.dto.PaymentConfirmationResponse;
import com.barclays.absa.banking.payments.services.dto.PaymentResultRequest;
import com.barclays.absa.banking.payments.services.dto.ResendNoticeOfPaymentRequest;

public class PaymentsInteractor extends AbstractInteractor implements PaymentsService {

    @Override
    public void fetchFutureDatedPaymentsList(String futureDatedPaymentType, String accountNumber, ExtendedResponseListener<FutureDatedPaymentListResponse> responseListener) {
        FutureDatedPaymentsRequest<FutureDatedPaymentListResponse> futureDatedPaymentsRequest = new FutureDatedPaymentsRequest<>(futureDatedPaymentType, accountNumber, responseListener);
        futureDatedPaymentsRequest.setMockResponseFile("payments/op0866_future_dated_payments_list.json");
        submitRequest(futureDatedPaymentsRequest);
    }

    @Override
    public void addPaymentBeneficiary(AddBeneficiaryPaymentObject addBeneficiaryPaymentObject, TransactionParams.Transaction notificationMethodDetails, ExtendedResponseListener<AddBeneficiaryPaymentObject> addPaymentBeneficiaryListener) {
        AddPaymentBeneficiaryRequest<AddBeneficiaryPaymentObject> addPaymentBeneficiaryRequest = new AddPaymentBeneficiaryRequest<>(addBeneficiaryPaymentObject, notificationMethodDetails, addPaymentBeneficiaryListener);
        submitRequest(addPaymentBeneficiaryRequest);
    }

    @Override
    public void fetchExergyBankList(ExtendedResponseListener<ExergyBankListResponse> responseListener) {
        FetchExergyBankListRequest<ExergyBankListResponse> getExergyBankDetailsRequest = new FetchExergyBankListRequest(responseListener);
        submitRequest(getExergyBankDetailsRequest);
    }

    @Override
    public void fetchExergyBranchList(String bankName, ExtendedResponseListener<ExergyBranchListResponse> responseListener) {
        FetchExergyBranchListRequest<ExergyBranchListResponse> getExergyBranchDetailsRequest = new FetchExergyBranchListRequest(bankName, responseListener);
        submitRequest(getExergyBranchDetailsRequest);
    }

    @Override
    public void fetchBankList(ExtendedResponseListener<BankDetails> bankListListener) {
        BankListRequest<BankDetails> bankListRequest = new BankListRequest<>(bankListListener);
        submitRequest(bankListRequest);
    }

    @Override
    public void fetchBranchList(String bankName, ExtendedResponseListener<BankBranches> branchListListener) {
        BankBranchListRequest<BankBranches> bankBranchListRequest = new BankBranchListRequest<>(bankName, branchListListener);
        submitRequest(bankBranchListRequest);
    }

    @Override
    public void fetchInstitutionList(ExtendedResponseListener<Institutions> institutionListListener) {
        InstitutionListRequest<Institutions> institutionListRequest = new InstitutionListRequest<>(institutionListListener);
        submitRequest(institutionListRequest);
    }

    @Override
    public void validatePayment(PayBeneficiaryPaymentConfirmationObject payBeneficiaryConfirmationObject, ExtendedResponseListener<PayBeneficiaryPaymentConfirmationObject> validatePaymentListener) {
        PaymentConfirmRequest<PayBeneficiaryPaymentConfirmationObject> paymentConfirmRequest = new PaymentConfirmRequest<>(payBeneficiaryConfirmationObject, validatePaymentListener);
        submitRequest(paymentConfirmRequest);
    }

    @Override
    public void performPayment(String transactionReferenceId, ExtendedResponseListener<PaymentConfirmationResponse> paymentResultListener) {
        PaymentResultRequest<PaymentConfirmationResponse> performPaymentRequest = new PaymentResultRequest<>(transactionReferenceId, paymentResultListener);
        submitRequest(performPaymentRequest);
    }

    @Override
    public void validateOnceOffPayment(@NonNull OnceOffPaymentConfirmationObject onceOffPaymentConfirmationObject, ExtendedResponseListener<OnceOffPaymentConfirmationResponse> validateOnceOffPaymentListener) {
        OnceOffPaymentConfirmRequest<OnceOffPaymentConfirmationResponse> onceOffPaymentConfirmRequest = new OnceOffPaymentConfirmRequest<>(onceOffPaymentConfirmationObject, validateOnceOffPaymentListener);
        submitRequest(onceOffPaymentConfirmRequest);
    }

    @Override
    public void performOnceOffPayment(String referenceId, ExtendedResponseListener<PaymentConfirmationResponse> performOncePaymentListener) {
        OnceOffPaymentResultRequest<PaymentConfirmationResponse> onceOffPaymentResultRequest = new OnceOffPaymentResultRequest<>(referenceId, performOncePaymentListener);
        submitRequest(onceOffPaymentResultRequest);
    }

    @Override
    public void editBeneficiaryConfirmation(BeneficiaryDetailObject beneficiaryDetailObject, TransactionParams.Transaction notificationMethodDetails, ExtendedResponseListener<AddBeneficiaryPaymentObject> editPaymentConfirmationListener) {
        ConfirmEditBeneficiaryRequest<AddBeneficiaryPaymentObject> confirmEditBeneficiaryRequest = new ConfirmEditBeneficiaryRequest<>(beneficiaryDetailObject, notificationMethodDetails, editPaymentConfirmationListener);
        submitRequest(confirmEditBeneficiaryRequest);
    }

    @Override
    public void editBeneficiaryResult(String referenceNumber, String hasImage, ExtendedResponseListener<AddBeneficiaryPaymentObject> editPaymentResultResponseListener) {
        EditBeneficiaryResultRequest<AddBeneficiaryPaymentObject> editBeneficiaryResultRequest = new EditBeneficiaryResultRequest<>(referenceNumber, hasImage, editPaymentResultResponseListener);
        submitRequest(editBeneficiaryResultRequest);
    }

    @Override
    public void resendProofOfPayment(ViewTransactionDetails viewTransactionDetails, Bundle bundle, ExtendedResponseListener<ResendNoticeOfPayment> resendNoticeOfPaymentResponseListener) {
        ResendNoticeOfPaymentRequest<ResendNoticeOfPayment> resendNoticeOfPaymentRequest = new ResendNoticeOfPaymentRequest<>(resendNoticeOfPaymentResponseListener, viewTransactionDetails, bundle);
        submitRequest(resendNoticeOfPaymentRequest);
    }

    @Override
    public void addNewBeneficiaryConfirmation(AddBeneficiaryPaymentObject addBeneficiaryPaymentObject, Bitmap beneficiaryImage, boolean hasImage, ExtendedResponseListener<AddBeneficiaryPaymentObject> responseListener) {
        AddNewBeneficiaryConfirmationRequest<AddBeneficiaryPaymentObject> addNewBeneficiaryConfirmationRequest = new AddNewBeneficiaryConfirmationRequest<>(addBeneficiaryPaymentObject, beneficiaryImage, hasImage, responseListener);
        submitRequest(addNewBeneficiaryConfirmationRequest);
    }

    @Override
    public void addNewBeneficiaryResult(String referenceNumber, String hasImage, ExtendedResponseListener<AddBeneficiaryPaymentObject> addNewBeneficiaryResultListener) {
        AddNewBeneficiaryResultRequest<AddBeneficiaryPaymentObject> addNewBeneficiaryResultRequest = new AddNewBeneficiaryResultRequest<>(referenceNumber, hasImage, addNewBeneficiaryResultListener);
        submitRequest(addNewBeneficiaryResultRequest);
    }
}