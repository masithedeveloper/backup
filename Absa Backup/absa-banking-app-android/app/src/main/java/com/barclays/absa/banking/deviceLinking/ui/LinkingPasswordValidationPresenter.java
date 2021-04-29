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
package com.barclays.absa.banking.deviceLinking.ui;

import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.ValidatePasswordResponse;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor;
import com.barclays.absa.banking.boundary.shared.dto.SecondFactorState;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationGetVerificationStateResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationOperation;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.login.services.LoginInteractor;
import com.barclays.absa.banking.presentation.sureCheck.SecurityCodeDelegate;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.integration.DeviceProfilingInteractor;

import java.util.HashMap;

import static com.barclays.absa.banking.boundary.shared.dto.SecondFactorState.SURECHECKV2_SECURITYCODE;

class LinkingPasswordValidationPresenter {
    private final static String TAG = "PasswordValidation";
    private LinkingPasswordValidationView view;
    private final LoginInteractor loginInteractor;
    private final TransactionVerificationInteractor transactionVerificationInteractor;
    private boolean isSecurityCodeState = false;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private final ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> transactionVerificationStateResponseListener = new ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final TransactionVerificationGetVerificationStateResponse successResponse) {
            view.dismissProgressDialog();
            view.showError(successResponse.getTxnMessage());
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            if (failureResponse != null && failureResponse.getResponseMessage() != null) {
                view.showError(failureResponse.getResponseMessage());
            } else {
                view.showError("Technical Difficulty. Please try again. If the problem persists call us on 0860111123.");
            }
        }

    };
    private SureCheckDelegate sureCheckDelegate;

    private final ExtendedResponseListener<TransactionVerificationResponse> transactionVerificationResponseListener = new ExtendedResponseListener<TransactionVerificationResponse>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final TransactionVerificationResponse successResponse) {
            BMBLogger.d(TAG, successResponse.toString());
            appCacheService.setLatestResponse(successResponse);
            appCacheService.setSureCheckNotificationMethod(successResponse.getNotificationMethod());
            appCacheService.setSureCheckReferenceNumber(successResponse.getReferenceNumber());
            appCacheService.setSureCheckCellphoneNumber(successResponse.getCellnumber());
            appCacheService.setSureCheckEmail(successResponse.getEmail());
            final String transactionVerificationType = successResponse.getTransactionVerificationType();
            if (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTxnStatus())) {
                view.dismissProgressDialog();
                if (successResponse.getTxnMessage() != null) {
                    view.showError(successResponse.getTxnMessage());
                } else {
                    view.showGenericErrorMessage();
                }
                return;
            }
            if (transactionVerificationType.isEmpty()) {
                transactionVerificationInteractor.checkTransactionVerificationState(TransactionVerificationOperation.REGISTER_CREDENTIALS, transactionVerificationStateResponseListener);
            } else {
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
                    case SURECHECKV2_FALLBACKRequired:
                        sureCheckDelegate.initiateOfflineOtpScreen();
                        break;
                    case SecurityCode:
                        SecurityCodeDelegate.handleSecurityCodeCaseAndChangePrimary((LinkingPasswordValidationActivity) view);
                        break;
                    case NotNeeded:
                        sureCheckDelegate.onSureCheckProcessed();
                        break;
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            view.dismissProgressDialog();
            view.showError("Technical Difficulty. Please try again. If the problem persists call us on 0860111123.");
        }
    };

    private final ExtendedResponseListener<ValidatePasswordResponse> validatePasswordResponseListener = new ExtendedResponseListener<ValidatePasswordResponse>() {

        @Override
        public void onSuccess(ValidatePasswordResponse successResponse) {
            if ("ValidPassword".equalsIgnoreCase(successResponse.getResult())) {
                DeviceProfilingInteractor deviceProfilingInteractor = new DeviceProfilingInteractor();
                deviceProfilingInteractor.callForDeviceProfilingScoreForLogin(() -> {
                    SecureHomePageObject secureHomePageObject = appCacheService.getSecureHomePageObject();
                    final SecondFactorState secondFactorState = SecondFactorState.fromValue(secureHomePageObject.getCustomerProfile().getSecondFactorState());
                    isSecurityCodeState = secondFactorState == SURECHECKV2_SECURITYCODE;

                    final boolean userHasNoPrimaryDevice = view.isNoPrimaryState();
                    final boolean isPasscodeResetFlow = appCacheService.isPasscodeResetFlow();
                    if (!isSecurityCodeState &&
                            !userHasNoPrimaryDevice &&
                            !isPasscodeResetFlow) {
                        transactionVerificationInteractor.checkIfTransactionVerificationIsRequiredPostLogon(TransactionVerificationOperation.REGISTER_CREDENTIALS, transactionVerificationResponseListener);
                    } else {
                        view.dismissProgressDialog();
                        if (isSecurityCodeState) {
                            view.goToSecurityCodeEntryScreen();
                        } else if (userHasNoPrimaryDevice) {
                            view.showNoPrimaryFailure();
                        } else if (isPasscodeResetFlow) {
                            view.showAlmostDoneScreen();
                        }
                    }
                });
            } else if ("InvalidPassword".equalsIgnoreCase(successResponse.getResult())) {
                recordMonitoringEvent(MonitoringService.MONITORING_EVENT_NAME_INCORRECT_ONLINE_BANKING_PASSWORD);
                view.dismissProgressDialog();
                view.resetRequiredPasswordFields();
                view.showAttemptsErrorMessage();

            } else if ("PasswordLocked".equalsIgnoreCase(successResponse.getResult())) {
                recordPasswordLockedMonitoringEvent(MonitoringService.MONITORING_EVENT_NAME_INCORRECT_ONLINE_BANKING_PASSWORD);
                view.dismissProgressDialog();
                view.goToForgotPasswordScreen();
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            String errorMessage = failureResponse.getErrorMessage();
            if (errorMessage == null) {
                errorMessage = failureResponse.getResponseMessage();
            }
            view.showError(errorMessage);
        }
    };

    private void recordPasswordLockedMonitoringEvent(String eventName) {
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_TIMESTAMP, System.currentTimeMillis());
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_EXTRA_INFO, "PasswordLocked");
        new MonitoringInteractor().logMonitoringEvent(eventName, eventData);
    }

    private void recordMonitoringEvent(String eventName) {
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_TIMESTAMP, System.currentTimeMillis());
        new MonitoringInteractor().logMonitoringEvent(eventName, eventData);
    }

    LinkingPasswordValidationPresenter(LinkingPasswordValidationView linkingPasswordValidationView, SureCheckDelegate sureCheckDelegate) {
        view = linkingPasswordValidationView;
        this.sureCheckDelegate = sureCheckDelegate;
        loginInteractor = new LoginInteractor();
        transactionVerificationInteractor = new TransactionVerificationInteractor();
        transactionVerificationStateResponseListener.setView(linkingPasswordValidationView);
        transactionVerificationResponseListener.setView(linkingPasswordValidationView);
        validatePasswordResponseListener.setView(linkingPasswordValidationView);
    }

    void onContinueInvoked(String value1, String value2, String value3) {
        if (!value1.isEmpty() && value1.trim().length() == 1 &&
                !value2.isEmpty() && value2.trim().length() == 1 &&
                !value3.isEmpty() && value3.trim().length() == 1) {
            validatePassword(value1, value2, value3);
        } else {
            view.showInvalidPasswordCharactersError();
        }
    }

    private void validatePassword(String value1, String value2, String value3) {
        loginInteractor.validatePassword(value1, value2, value3, validatePasswordResponseListener);
    }
}