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

import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationGetVerificationStateResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationState;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;

class SureCheckCountdownPresenter {

    private final int timerDuration;
    private final TransactionVerificationInteractor transactionVerificationInteractor;
    private SureCheckCountdownView view;
    private int remainingRetries;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
    private final ExtendedResponseListener<TransactionVerificationResponse> transactionVerificationResendResponseListener = new ExtendedResponseListener<TransactionVerificationResponse>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final TransactionVerificationResponse successResponse) {

            if (successResponse.getRetries() > 0) {
                view.close();
                appCacheService.setSureCheckReferenceNumber(successResponse.getReferenceNumber());
                if (!successResponse.getCellnumber().isEmpty()) {
                    appCacheService.setSureCheckCellphoneNumber(successResponse.getCellnumber());
                }

                if (!successResponse.getEmail().isEmpty()) {
                    appCacheService.setSureCheckEmail(successResponse.getEmail());
                }

                if (!successResponse.getNotificationMethod().isEmpty()) {
                    appCacheService.setSureCheckNotificationMethod(successResponse.getNotificationMethod());
                }

                String txnVerificationType = successResponse.getTransactionVerificationType();
                if (!txnVerificationType.isEmpty()) {
                    TransactionVerificationType transactionVerificationType = TransactionVerificationType.valueOf(txnVerificationType.toUpperCase());
                    SureCheckDelegate sureCheckDelegate = appCacheService.getSureCheckDelegate();
                    if (sureCheckDelegate != null) {
                        sureCheckDelegate.onResendSuccess(transactionVerificationType);
                    }
                    remainingRetries = successResponse.getRetries();
                } else {
                    view.showGenericErrorMessageThenFinish();
                }
            } else {
                view.maxRetriesExceeded();
                view.disableResendOption();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {

        }
    };

    private String referenceNumber;
    private boolean hasPollReturned = true;
    private final ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> responseListener = new ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final TransactionVerificationGetVerificationStateResponse successResponse) {

            if (!successResponse.getTransactionVerificationState().isEmpty() && !successResponse.getTransactionVerificationState().isEmpty()) {
                TransactionVerificationState state = TransactionVerificationState.valueOf(successResponse.getTransactionVerificationState().toUpperCase());
                switch (state) {
                    case PROCESSING:
                    case PROCESSING_CAPS:
                        hasPollReturned = true;
                        break;
                    case PROCESSED:
                        view.stopTimer();
                        view.sureCheckProcessed();
                        break;
                    case REJECTED:
                        view.stopTimer();
                        view.showSureCheckRejected();
                        break;
                    case FAILED:
                        view.stopTimer();
                        view.displayFailureResult();
                    case RESENDREQUIRED:
                        view.stopTimer();
                        view.displayFailureResult();
                        break;
                    default:
                        view.stopTimer();
                        view.displayFailureResult();
                        break;
                }
            } else {
                if (!successResponse.getTxnMessage().isEmpty()) {
                    view.showError(successResponse.getTxnMessage());
                }
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.stopTimer();
            String RESPONSE_CODE_FOR_KNOWN_ERROR_RESPONSES = "FTR00980";
            if (RESPONSE_CODE_FOR_KNOWN_ERROR_RESPONSES.equalsIgnoreCase(failureResponse.getResponseCode())) {
                String REJECTION_ERROR_MESSAGE = "You selected the rejection option on your cellphone. The transaction has been cancelled";
                String TIMEOUT_ERROR_MESSAGE = "We did not receive your verification message. Please resend the verification message or try again";
                String SENDING_ERROR_MESSAGE = "Error sending Surecheck: null";
                if (TIMEOUT_ERROR_MESSAGE.equalsIgnoreCase(failureResponse.getErrorMessage())) {
                    onTimerRanOut();
                } else if (REJECTION_ERROR_MESSAGE.equalsIgnoreCase(failureResponse.getErrorMessage())) {
                    view.showSureCheckRejected();
                } else if (SENDING_ERROR_MESSAGE.equalsIgnoreCase(failureResponse.getErrorMessage())) {
                    view.displayFailureResult();
                } else {
                    view.displayFailureResult();
                }
            } else {
                //show error result by default
                view.displayFailureResult();
            }
        }
    };

    SureCheckCountdownPresenter(SureCheckCountdownView countdownView, int totalDuration) {
        view = countdownView;
        timerDuration = totalDuration;
        transactionVerificationInteractor = new TransactionVerificationInteractor();
        responseListener.setView(countdownView);
        transactionVerificationResendResponseListener.setView(countdownView);
    }

    void onTimerTicked(int secondsRemaining) {
        view.updateCountDownCircle(secondsRemaining);
        int POLL_EVERY_SECONDS = 5;
        if (timerDuration > secondsRemaining && secondsRemaining % POLL_EVERY_SECONDS == 0) {
            referenceNumber = appCacheService.getSureCheckReferenceNumber();
            if (hasPollReturned) {
                hasPollReturned = false;
                pollVerificationStatus();
            }
        }
    }

    private void pollVerificationStatus() {
        if (appCacheService.getSecureHomePageObject() != null) {
            transactionVerificationInteractor.checkVerificationStatusPostLogon(referenceNumber, responseListener);
        } else if (appCacheService.isIdentificationAndVerificationFlow() && !appCacheService.isIdentityAndVerificationPostLogin()) {
            transactionVerificationInteractor.biometricAuthenticationVerificationStatusPreLogin(referenceNumber, appCacheService.getLinkingTransactionDetails(), responseListener);
        } else if (appCacheService.isIdentityAndVerificationPostLogin()) {
            transactionVerificationInteractor.biometricAuthenticationVerificationStatusPostLogin(referenceNumber, appCacheService.getLinkingTransactionDetails(), responseListener);
        } else {
            transactionVerificationInteractor.checkVerificationStatus(referenceNumber, responseListener);
        }
    }

    void onTimerRanOut() {
        if (remainingRetries > 0) {
            view.displayResendOption();
        } else {
            view.displayFailureResult();
        }
        view.stopTimer();
    }

    void onCancelClicked() {
        view.stopTimer();
        view.cancelSureCheck();
        view.close();
    }

    void onResendClicked() {
        if (appCacheService.getSecureHomePageObject() != null) {
            transactionVerificationInteractor.resendTransactionVerificationPostLogon(referenceNumber, transactionVerificationResendResponseListener);
        } else {
            transactionVerificationInteractor.resendTransactionVerification(referenceNumber, transactionVerificationResendResponseListener);
        }
    }

    void onViewCreated() {
        view.startTimer();
    }
}