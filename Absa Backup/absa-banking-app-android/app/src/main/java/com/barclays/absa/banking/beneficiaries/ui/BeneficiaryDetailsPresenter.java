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
package com.barclays.absa.banking.beneficiaries.ui;

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.beneficiaries.services.dto.TransactionDetailsRequest;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailsResponse;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.ViewTransactionDetails;
import com.barclays.absa.banking.boundary.model.airtime.AirtimeBuyBeneficiary;
import com.barclays.absa.banking.buy.services.airtime.PrepaidInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.utils.AbsaCacheManager;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PAYMENT;

class BeneficiaryDetailsPresenter {

    private final WeakReference<BeneficiaryDetailsView> weakReference;
    private BeneficiaryDetailsView view;
    private BeneficiariesInteractor beneficiariesInteractor;
    private IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    private ExtendedResponseListener<BeneficiaryDetailsResponse> performCashSendExtendedResponseListener = new ExtendedResponseListener<BeneficiaryDetailsResponse>() {
        @Override
        public void onSuccess(final BeneficiaryDetailsResponse successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToCashSendBeneficiaryDetail(successResponse.getBeneficiaryDetails());
            }
        }
    };

    private ExtendedResponseListener<AirtimeBuyBeneficiary> manageAirtimeExtendedResponseListener = new ExtendedResponseListener<AirtimeBuyBeneficiary>() {
        @Override
        public void onSuccess(final AirtimeBuyBeneficiary successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                if (successResponse != null) {
                    view.navigateToAirtimeDetailView(successResponse);
                }
            }
        }
    };

    private ExtendedResponseListener<BeneficiaryDetailsResponse> beneficiaryDetailExtendedResponseListener = new ExtendedResponseListener<BeneficiaryDetailsResponse>() {
        @Override
        public void onSuccess(final BeneficiaryDetailsResponse successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.setTransactionList(successResponse.getBeneficiaryDetails());
            }
        }
    };

    private ExtendedResponseListener<BeneficiaryListObject> manageBeneficiaryExtendedResponseListener = new ExtendedResponseListener<BeneficiaryListObject>() {
        @Override
        public void onSuccess(final BeneficiaryListObject successResponse) {
            view = weakReference.get();
            if (view != null) {
                beneficiaryCacheService.setPaymentsBeneficiaries(successResponse.getPaymentBeneficiaryList());
                beneficiaryCacheService.setPaymentRecentTransactionList(successResponse.getLatestTransactionBeneficiaryList());
                AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(true, PASS_PAYMENT);
                view.dismissProgressDialog();
                view.navigateBackToBeneficiaryList(successResponse);
            }
        }
    };

    private ExtendedResponseListener<ViewTransactionDetails> getTransactionDetailsResponseListener = new ExtendedResponseListener<ViewTransactionDetails>() {
        @Override
        public void onSuccess(final ViewTransactionDetails successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToBeneficiaryTransactionItem(successResponse);
            }
        }
    };

    BeneficiaryDetailsPresenter(BeneficiaryDetailsView view) {
        weakReference = new WeakReference<>(view);
        performCashSendExtendedResponseListener.setView(view);
        manageAirtimeExtendedResponseListener.setView(view);
        beneficiaryDetailExtendedResponseListener.setView(view);
        manageBeneficiaryExtendedResponseListener.setView(view);
        getTransactionDetailsResponseListener.setView(view);
        requestImageExtendedResponseListener.setView(view);
        beneficiariesInteractor = new BeneficiariesInteractor();
    }

    void onPaymentBeneficiaryClicked() {
        view = weakReference.get();
        if (view != null) {
            view.navigateToPaymentBeneficiaryDetail();
        }
    }

    void onCashSendBeneficiaryClicked(String beneficiaryId) {
        beneficiariesInteractor.fetchBeneficiaryDetails(beneficiaryId, BMBConstants.PASS_CASHSEND, performCashSendExtendedResponseListener);
    }

    void onBuyBeneficiaryClicked(String beneficiaryId) {
        new PrepaidInteractor().fetchAirtimeBeneficiaryDetails(beneficiaryId, manageAirtimeExtendedResponseListener);
    }

    void onElectricityBeneficiaryClicked() {
        view = weakReference.get();
        if (view != null) {
            view.navigateToElectricityBeneficiaryDetail();
        }
    }

    void onResultReceivedFromActivity(String beneficiaryId, String beneficiaryType) {
        beneficiariesInteractor.fetchBeneficiaryDetails(beneficiaryId, beneficiaryType, beneficiaryDetailExtendedResponseListener);
    }

    void onBackClicked() {
        beneficiariesInteractor.fetchBeneficiaryList(PASS_PAYMENT, manageBeneficiaryExtendedResponseListener);
    }

    void downloadBeneficiaryImage(BeneficiaryDetailObject object) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(BMBConstants.SERVICE_TIMESTAMP_FORMAT, BMBApplication.getApplicationLocale());
        String timestamp = dateFormat.format(new Date());
        new BeneficiariesInteractor().downloadBeneficiaryImage(object.getBeneficiaryId(), object.getBeneficiaryType(), timestamp, requestImageExtendedResponseListener);
    }

    private ExtendedResponseListener<AddBeneficiaryObject> requestImageExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryObject>() {

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(final AddBeneficiaryObject successResponse) {
            if (successResponse.getBenImage() != null) {
                view = weakReference.get();
                if (view != null) {
                    view.setBeneficiaryImage(successResponse.getBenImage());
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            //do nothing
        }
    };


    void onTransactionItemClicked(String beneficiaryId, String referenceNumber, String beneficiaryType) {
        TransactionDetailsRequest<ViewTransactionDetails> transactionDetailsRequest = new TransactionDetailsRequest<>(getTransactionDetailsResponseListener, beneficiaryId,
                referenceNumber, beneficiaryType.toLowerCase());
        ServiceClient serviceClient = new ServiceClient(transactionDetailsRequest);
        serviceClient.submitRequest();
    }
}
