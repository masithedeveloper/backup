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

package com.barclays.absa.banking.registration;

import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationOperation;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;

class RegisterConfirmContactDetailsPresenter {

    private static final String TAG = RegisterConfirmContactDetailsPresenter.class.getSimpleName();
    private RegisterConfirmContactDetailsView view;
    private TransactionVerificationInteractor interactor;
    private IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private final SureCheckDelegate sureCheckDelegate;
    private final ExtendedResponseListener<TransactionVerificationResponse> transactionVerificationResponseListener
            = new ExtendedResponseListener<TransactionVerificationResponse>() {

        @Override
        public void onSuccess(TransactionVerificationResponse successResponse) {
            BMBLogger.d(TAG, successResponse.toString());
            appCacheService.setLatestResponse(successResponse);
            appCacheService.setSureCheckReferenceNumber(successResponse.getReferenceNumber());
            appCacheService.setSureCheckCellphoneNumber(successResponse.getCellnumber());
            appCacheService.setSureCheckEmail(successResponse.getEmail());
            appCacheService.setSureCheckNotificationMethod(successResponse.getNotificationMethod());

            final String transactionVerificationType = successResponse.getTransactionVerificationType();
            if (!transactionVerificationType.isEmpty()) {
                view.dismissProgressDialog();
                TransactionVerificationType verificationType = TransactionVerificationType.valueOf(transactionVerificationType);
                switch (verificationType) {
                    case SURECHECKV1:
                    case SURECHECKV2:
                        view.goToCountDownTimerScreen(verificationType);
                        break;
                    case SURECHECKV1_FALLBACK:
                        view.goToOtpEntryScreen();
                        break;
                    case SURECHECKV2_FALLBACK:
                        sureCheckDelegate.initiateOfflineOtpScreen();
                        break;
                    case NotNeeded:
                        view.navigateToPersonalDetailsScreen();
                        break;
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            view.dismissProgressDialog();
        }
    };

    RegisterConfirmContactDetailsPresenter(RegisterConfirmContactDetailsView view, SureCheckDelegate sureCheckDelegate) {
        this.view = view;
        transactionVerificationResponseListener.setView(view);
        this.sureCheckDelegate = sureCheckDelegate;
        interactor = new TransactionVerificationInteractor();
    }

    void onYesButtonClicked() {
        interactor.checkIfTransactionVerificationIsRequired(TransactionVerificationOperation.MOBILE_REGISTRATION, transactionVerificationResponseListener);
    }

    void onNoButtonClicked() {
        view.navigateToPartialRegisterActivity();
    }

}
