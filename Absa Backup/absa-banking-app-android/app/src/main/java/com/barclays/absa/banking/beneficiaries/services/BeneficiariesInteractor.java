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
package com.barclays.absa.banking.beneficiaries.services;

import com.barclays.absa.banking.beneficiaries.services.dto.BeneficiaryDetailsRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.BeneficiaryListRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.BeneficiaryRemoveRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.ClientAgreementDetailsRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.DownloadBeneficiaryImageRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.EditPrepaidElectricityBeneficiaryRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.MyNotification;
import com.barclays.absa.banking.beneficiaries.services.dto.MyNotificationDetailsRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.UpdateClientAgreementDetailsRequest;
import com.barclays.absa.banking.beneficiaries.services.dto.UploadBeneficiaryImageRequest;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailsResponse;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.boundary.model.MeterNumberObject;
import com.barclays.absa.banking.boundary.model.airtime.AddedBeneficiary;
import com.barclays.absa.banking.boundary.model.beneficiary.BeneficiaryRemove;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.buy.services.prepaidElectricity.dto.ValidateMeterNumberRequest;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.RequestCompletionCallback;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.utils.AbsaCacheManager;

import java.util.ArrayList;
import java.util.List;

import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_AIRTIME;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_CASHSEND;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PAYMENT;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PREPAID_ELECTRICITY;

public class BeneficiariesInteractor extends AbstractInteractor implements BeneficiariesService {

    private RequestCompletionCallback<Void> requestCompletionCallback;
    private IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);
    private ExtendedResponseListener<BeneficiaryListObject> paymentBeneficiaryListResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {

        @Override
        public void onSuccess(BeneficiaryListObject successResponse) {
            beneficiaryCacheService.setPaymentsBeneficiaries(successResponse.getPaymentBeneficiaryList());
            beneficiaryCacheService.setPaymentRecentTransactionList(successResponse.getLatestTransactionBeneficiaryList());
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_PAYMENT);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            if (AppConstants.FTR00565_NO_BENEFICIARIES_FOUND.equalsIgnoreCase(failureResponse.getResponseCode())) {
                beneficiaryCacheService.setPaymentsBeneficiaries(new ArrayList<>());
            } else {
                requestCompletionCallback.onFailure(failureResponse);
            }
        }
    };

    private ExtendedResponseListener<BeneficiaryListObject> prepaidBeneficiaryListResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {
        @Override
        public void onSuccess(BeneficiaryListObject successResponse) {
            beneficiaryCacheService.setPrepaidBeneficiaries(successResponse.getAirtimeBeneficiaryList());
            beneficiaryCacheService.setPrepaidRecentTransactionList(successResponse.getLatestTransactionBeneficiaryList());
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_AIRTIME);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            //this means that the beneficiary list is actually empty
            if (AppConstants.FTR00565_NO_BENEFICIARIES_FOUND.equalsIgnoreCase(failureResponse.getResponseCode())) {
                beneficiaryCacheService.setPrepaidBeneficiaries(new ArrayList<>());
            }
        }
    };

    private ExtendedResponseListener<BeneficiaryListObject> cashSendBeneficiaryListResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {
        @Override
        public void onSuccess(BeneficiaryListObject successResponse) {
            beneficiaryCacheService.setCashSendBeneficiaries(successResponse.getCashsendBeneficiaryList());
            beneficiaryCacheService.setCashSendRecentTransactionList(successResponse.getLatestTransactionBeneficiaryList());
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_CASHSEND);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            if (failureResponse != null && AppConstants.FTR00565_NO_BENEFICIARIES_FOUND.equalsIgnoreCase(failureResponse.getResponseCode())) {
                beneficiaryCacheService.setCashSendBeneficiaries(new ArrayList<>());
            }
        }
    };

    private ExtendedResponseListener<BeneficiaryListObject> electricityBeneficiaryListResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {
        @Override
        public void onSuccess(BeneficiaryListObject successResponse) {
            beneficiaryCacheService.setElectricityBeneficiaries(successResponse.getElectricityBeneficiaryList());
            AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_PREPAID_ELECTRICITY);
            requestCompletionCallback.onSuccess();
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            if (failureResponse != null && AppConstants.FTR00565_NO_BENEFICIARIES_FOUND.equalsIgnoreCase(failureResponse.getResponseCode())) {
                beneficiaryCacheService.setElectricityBeneficiaries(new ArrayList<>());
                requestCompletionCallback.onSuccess();
            }
        }
    };

    @Override
    public void fetchBeneficiaryList(String beneficiaryType, ExtendedResponseListener<BeneficiaryListObject> beneficiaryListListener) {
        BeneficiaryListRequest<BeneficiaryListObject> beneficiaryListRequest = new BeneficiaryListRequest<>(beneficiaryType, beneficiaryListListener);
        submitRequest(beneficiaryListRequest);
    }

    @Override
    public void fetchBeneficiaryDetails(String beneficiaryId, String beneficiaryType, ExtendedResponseListener<BeneficiaryDetailsResponse> beneficiaryDetailsListener) {
        BeneficiaryDetailsRequest<BeneficiaryDetailsResponse> beneficiaryDetailsRequest = new BeneficiaryDetailsRequest<>(beneficiaryId, beneficiaryType, beneficiaryDetailsListener);
        submitRequest(beneficiaryDetailsRequest);
    }

    @Override
    public void downloadBeneficiaryImage(String beneficiaryId, String beneficiaryType, String timestamp, ExtendedResponseListener<AddBeneficiaryObject> downloadImageListener) {
        DownloadBeneficiaryImageRequest<AddBeneficiaryObject> downloadBeneficiaryImageRequest = new DownloadBeneficiaryImageRequest<>(beneficiaryId, beneficiaryType, timestamp, downloadImageListener);
        submitRequest(downloadBeneficiaryImageRequest);
    }

    @Override
    public void uploadBeneficiaryImage(AddedBeneficiary addedBeneficiary, String mimeType, String actionType, ExtendedResponseListener<AddBeneficiaryObject> uploadImageListener) {
        UploadBeneficiaryImageRequest<AddBeneficiaryObject> uploadBeneficiaryImageRequest = new UploadBeneficiaryImageRequest<>(addedBeneficiary, mimeType, actionType, uploadImageListener);
        submitRequest(uploadBeneficiaryImageRequest);
    }

    @Override
    public void uploadBeneficiaryImage(String beneficiaryId, String beneficiaryType, String base64Image, String mimeType, String actionType, ExtendedResponseListener<AddBeneficiaryObject> uploadImageListener) {
        UploadBeneficiaryImageRequest<AddBeneficiaryObject> uploadBeneficiaryImageRequest = new UploadBeneficiaryImageRequest<>(beneficiaryId, beneficiaryType, base64Image, mimeType, actionType, uploadImageListener);
        submitRequest(uploadBeneficiaryImageRequest);
    }

    @Override
    public void fetchClientAgreementDetails(ExtendedResponseListener<ClientAgreementDetails> clientAgreementDetailsListener) {
        ClientAgreementDetailsRequest<ClientAgreementDetails> clientAgreementDetailsRequest = new ClientAgreementDetailsRequest<>(clientAgreementDetailsListener);
        submitRequest(clientAgreementDetailsRequest);
    }

    @Override
    public void updateClientAgreementDetails(ExtendedResponseListener<TransactionResponse> updateClientAgreementDetailsListener) {
        UpdateClientAgreementDetailsRequest<TransactionResponse> updateClientAgreementDetailsRequest = new UpdateClientAgreementDetailsRequest<>(updateClientAgreementDetailsListener);
        submitRequest(updateClientAgreementDetailsRequest);
    }

    @Override
    public void removeBeneficiaryRequest(String beneficiaryId, String beneficiaryType, ExtendedResponseListener<BeneficiaryRemove> beneficiaryRemoveResponseListener) {
        BeneficiaryRemoveRequest<BeneficiaryRemove> beneficiaryRemoveRequest = new BeneficiaryRemoveRequest<>(beneficiaryId, beneficiaryType, beneficiaryRemoveResponseListener);
        submitRequest(beneficiaryRemoveRequest);
    }

    @Override
    public void fetchMyNotificationDetails(ExtendedResponseListener<MyNotification> myNotificationDetailsResponseListener) {
        MyNotificationDetailsRequest<MyNotification> myNotificationDetailsRequest = new MyNotificationDetailsRequest<>(myNotificationDetailsResponseListener);
        submitRequest(myNotificationDetailsRequest);
    }

    @Override
    public void updatePrepaidElectricityBeneficiaryDetails(String beneficiaryName, BeneficiaryDetailObject beneficiaryDetailObject, MeterNumberObject meterNumberObject, ExtendedResponseListener<TransactionResponse> editPrepaidBeneficiaryResponseObjectExtendedResponseListener) {
        EditPrepaidElectricityBeneficiaryRequest<TransactionResponse> editPrepaidElectricityBeneficiaryRequest = new EditPrepaidElectricityBeneficiaryRequest<>(beneficiaryName, beneficiaryDetailObject, meterNumberObject, editPrepaidBeneficiaryResponseObjectExtendedResponseListener);
        submitRequest(editPrepaidElectricityBeneficiaryRequest);
    }

    @Override
    public void fetchAllBeneficiaryLists(RequestCompletionCallback<Void> completionCallback) {
        requestCompletionCallback = completionCallback;
        List<ExtendedRequest> beneficiaryListRequests = new ArrayList<>();
        if (!AbsaCacheManager.getInstance().isBeneficiariesCached(PASS_PAYMENT)) {
            BeneficiaryListRequest<BeneficiaryListObject> paymentBeneficiaryListRequest = new BeneficiaryListRequest<>(PASS_PAYMENT, paymentBeneficiaryListResponseListener);
            beneficiaryListRequests.add(paymentBeneficiaryListRequest);
        }

        if (!AbsaCacheManager.getInstance().isBeneficiariesCached(PASS_AIRTIME)) {
            BeneficiaryListRequest<BeneficiaryListObject> prepaidBenefiaryListRequest = new BeneficiaryListRequest<>(PASS_AIRTIME, prepaidBeneficiaryListResponseListener);
            beneficiaryListRequests.add(prepaidBenefiaryListRequest);
        }

        if (!AbsaCacheManager.getInstance().isBeneficiariesCached(PASS_CASHSEND)) {
            BeneficiaryListRequest<BeneficiaryListObject> cashSendBeneficiaryListRequest = new BeneficiaryListRequest<>(PASS_CASHSEND, cashSendBeneficiaryListResponseListener);
            beneficiaryListRequests.add(cashSendBeneficiaryListRequest);
        }

        if (!AbsaCacheManager.getInstance().isBeneficiariesCached(PASS_PREPAID_ELECTRICITY)) {
            BeneficiaryListRequest<BeneficiaryListObject> cashSendBeneficiaryListRequest = new BeneficiaryListRequest<>(PASS_PREPAID_ELECTRICITY, electricityBeneficiaryListResponseListener);
            beneficiaryListRequests.add(cashSendBeneficiaryListRequest);
        }

        submitQueuedRequests(beneficiaryListRequests, completionCallback::onSuccess);
    }

    public void fetchPrepaidElectricityBeneficiaryDetails(BeneficiaryObject beneficiaryDetail, ExtendedResponseListener<BeneficiaryDetailsResponse> beneficiaryDetailResponseListener, ExtendedResponseListener<MeterNumberObject> validateRecipientMeterNumberExtendedResponseListener) {
        List<ExtendedRequest> beneficiaryListRequests = new ArrayList<>();
        BeneficiaryDetailsRequest<BeneficiaryDetailsResponse> beneficiaryDetailsRequest = new BeneficiaryDetailsRequest<>(beneficiaryDetail.getBeneficiaryID(), PASS_PREPAID_ELECTRICITY, beneficiaryDetailResponseListener);
        ValidateMeterNumberRequest<MeterNumberObject> validateMeterNumberRequest = new ValidateMeterNumberRequest<>(beneficiaryDetail.getBeneficiaryAccountNumber(), validateRecipientMeterNumberExtendedResponseListener);
        beneficiaryListRequests.add(beneficiaryDetailsRequest);
        beneficiaryListRequests.add(validateMeterNumberRequest);
        submitQueuedRequests(beneficiaryListRequests);
    }

    @Override
    public void updateBeneficiaryImage(BeneficiaryDetailObject beneficiaryDetail, ExtendedResponseListener<AddBeneficiaryObject> responseListener) {
        UploadBeneficiaryImageRequest<AddBeneficiaryObject> uploadBeneficiaryImageRequest = new UploadBeneficiaryImageRequest<>(beneficiaryDetail, responseListener);
        submitRequest(uploadBeneficiaryImageRequest);
    }

    @Override
    public void addPaymentBeneficiary(ExtendedResponseListener<BeneficiaryDetailObject> responseListener) {
        RequestParams params = new RequestParams.Builder()
                .put(OP0786_MY_NOTIFICATION_DETAILS)
                .put(TransactionParams.Transaction.SERVICE_PAYMENT_TYPE, BMBConstants.PASS_PAYMENT)
                .build();

        ExtendedRequest<BeneficiaryDetailObject> airtimeRequest = new ExtendedRequest<>(params, responseListener, BeneficiaryDetailObject.class);
        airtimeRequest.setEncrypted(true);
        airtimeRequest.setMockResponseFile("beneficiaries/op0786_add_payment_own_notification.json");
        ServiceClient serviceClient = new ServiceClient(airtimeRequest);
        serviceClient.submitRequest();
    }
}