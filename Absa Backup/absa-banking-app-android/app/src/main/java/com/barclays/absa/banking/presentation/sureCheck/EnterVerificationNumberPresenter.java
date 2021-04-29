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

package com.barclays.absa.banking.presentation.sureCheck;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationValidateCodeResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;

class EnterVerificationNumberPresenter {

    private static final String TAG = EnterVerificationNumberPresenter.class.getSimpleName();
    private EnterVerificationNumberView view;
    private TransactionVerificationInteractor interactor;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
    private ExtendedResponseListener<TransactionVerificationValidateCodeResponse> codeVerificationListener = new ExtendedResponseListener<TransactionVerificationValidateCodeResponse>() {

        @Override
        public void onSuccess(final TransactionVerificationValidateCodeResponse successResponse) {
            view.dismissProgressDialog();
            String transactionStatus = successResponse.getTxnStatus();

            if (BMBConstants.SUCCESS.equalsIgnoreCase(transactionStatus)) {
                BMBLogger.d(TAG, "Verf success:" + successResponse.toString());
                view.showSuccessOutcome();
            } else if (BMBConstants.FAILURE.equalsIgnoreCase(transactionStatus)) {
                BMBLogger.d(TAG, "Verf failure:" + successResponse.toString());
                view.incorrectVerificationNumber(successResponse.getTxnMessage());
            } else {
                BMBLogger.d(TAG, "Verf default case => showing failure:" + successResponse.toString());
                view.showFailureOutcome();
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            BMBLogger.e(TAG, failureResponse.toString());
            view.showFailureOutcome();
        }

    };
    private ExtendedResponseListener<TransactionVerificationResponse> resendTransactionVerificationListener = new ExtendedResponseListener<TransactionVerificationResponse>(view) {

        @Override
        public void onSuccess(final TransactionVerificationResponse successResponse) {
            view.dismissProgressDialog();

            final String transactionVerificationType1 = successResponse.getTransactionVerificationType();
            if (transactionVerificationType1 != null && !transactionVerificationType1.isEmpty()) {
                TransactionVerificationType transactionVerificationType = TransactionVerificationType.valueOf(transactionVerificationType1.toUpperCase());
                switch (transactionVerificationType) {
                    case SURECHECKV1:
                        view.showSureCheckScreen();
                        break;
                    case SURECHECKV1_FALLBACK:
                        view.showOtpEntryScreen();
                        break;
                    case NoPrimaryDevice:
                        view.showNoPrimaryDeviceScreen();
                    default:
                        view.showFailureOutcome();
                        break;
                }
            } else if ("Security/AccessControl/Error/RVNResendsExceeded".equals(successResponse.getTxnMessage())) {
                view.showRetriesExceeded();
            } else {
                view.showGenericErrorMessage();
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            BMBLogger.e(TAG, failureResponse.toString());
            view.showFailureOutcome();
        }
    };

    EnterVerificationNumberPresenter(EnterVerificationNumberView view) {
        this.view = view;
        interactor = new TransactionVerificationInteractor();
        codeVerificationListener.setView(view);
        resendTransactionVerificationListener.setView(view);
    }

    void submitButtonInvoked(String verificationCode) {
        if (!verificationCode.isEmpty()) {
            String refNumber = appCacheService.getSureCheckReferenceNumber();
            if (appCacheService.getSecureHomePageObject() != null) {
                interactor.validateOtpPostLogon(verificationCode, refNumber, codeVerificationListener);
            } else {
                interactor.validateOtp(verificationCode, refNumber, codeVerificationListener);
            }
        } else {
            view.showValidationError(R.string.pleaseEnterValid);
        }
    }

    void resendVerficationNumber() {
        String refNumber = appCacheService.getSureCheckReferenceNumber();
        interactor.resendTransactionVerificationPostLogon(refNumber, resendTransactionVerificationListener);
    }
}