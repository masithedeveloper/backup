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

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult;
import com.barclays.absa.banking.presentation.sureCheck.SecurityCodeDelegate;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.registration.services.RegistrationInteractor;
import com.barclays.absa.banking.registration.services.dto.CreatePasswordResult;
import com.barclays.absa.banking.registration.services.dto.RegisterAOLProfileResponse;
import com.barclays.absa.banking.shared.ActionHandler;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.PasswordValidator;

import java.util.HashMap;
import java.util.Map;

class RegisterCreatePasswordPresenter implements RegisterCreatePassword2faPresenterInterface {

    private static final String TAG = RegisterCreatePasswordPresenter.class.getSimpleName();
    private final Map<String, Boolean> passwordRulesMap = new HashMap<>();
    private RegisterCreatePasswordView view;
    private final RegisterCreatePasswordActivity activity;
    private boolean isPasswordValid;
    private String password;
    private final SureCheckDelegate createPasswordSureCheckDelegate;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
    private final boolean isPartialToFullRegistration = appCacheService.getSecureHomePageObject() != null && appCacheService.getSecureHomePageObject().getCustomerProfile().getAuthPassNotRegistered() != null && !appCacheService.isPasswordResetFlow();

    private RegistrationInteractor registrationInteractor;

    public boolean isUserLoggedIn() {
        return appCacheService.getSecureHomePageObject() != null;
    }

    public boolean isUserLoggedInViaPasscodeLogin() {
        return appCacheService.getAuthCredential() != null;
    }

    private final ExtendedResponseListener<RegisterAOLProfileResponse> registrationResponseLister = new ExtendedResponseListener<RegisterAOLProfileResponse>() {

        @Override
        public void onSuccess(final RegisterAOLProfileResponse response) {
            view.dismissProgressDialog();
            view.launchRegistrationResultScreen(response);
        }

        @Override
        public void onFailure(final ResponseObject response) {
            view.dismissProgressDialog();
            new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_LINKING_FAILED, response);
            view.launchRegistrationResultScreen(response);
        }
    };
    private ExtendedResponseListener<ChangePrimaryDeviceResponse> changePrimaryDeviceResponseListener = new ExtendedResponseListener<ChangePrimaryDeviceResponse>(view) {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final ChangePrimaryDeviceResponse response) {

            view.dismissProgressDialog();
            String sureCheckFlag = response.getSureCheckFlag();
            if (sureCheckFlag != null) {
                try {
                    TransactionVerificationType transactionVerificationType = TransactionVerificationType.valueOf(sureCheckFlag);
                    appCacheService.setSureCheckReferenceNumber(response.getReferenceNumber());
                    appCacheService.setSureCheckCellphoneNumber(response.getCellnumber());
                    appCacheService.setSureCheckEmail(response.getEmail());

                    switch (transactionVerificationType) {
                        case NotNeeded:
                            registrationInteractor.createPassword(password, createPasswordResponseLister);
                            break;
                        case SURECHECKV1Required:
                        case SURECHECKV1:
                            createPasswordSureCheckDelegate.initiateV1CountDownScreen();
                            break;
                        case SURECHECKV2Required:
                        case SURECHECKV2:
                            createPasswordSureCheckDelegate.initiateV2CountDownScreen();
                            break;
                        case SURECHECKV1_FALLBACK:
                        case SURECHECKV1_FALLBACKRequired:
                            createPasswordSureCheckDelegate.initiateTransactionVerificationEntryScreen();
                            break;
                        case SURECHECKV2_FALLBACK:
                        case SURECHECKV2_FALLBACKRequired:
                            createPasswordSureCheckDelegate.initiateOfflineOtpScreen();
                            break;
                        case NoPrimaryDevice:
                            view.showNoPrimaryDeviceScreen();
                            break;
                        default:

                            break;
                    }
                } catch (IllegalArgumentException e) {
                    BMBLogger.e(TAG, "No such transaction verification type found for SureCheckFlag [" + sureCheckFlag + "]" + " due to " + e);
                }

            } else {
                registrationInteractor.createPassword(password, createPasswordResponseLister);
            }

        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            String errorMessage = failureResponse.getResponseMessage();
            if (errorMessage == null) {
                errorMessage = failureResponse.getErrorMessage();
            }
            new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_LINKING_FAILED, failureResponse);
            view.showMessageError(errorMessage);
        }
    };
    private final ExtendedResponseListener<CreatePasswordResult> createPasswordResponseLister = new ExtendedResponseListener<CreatePasswordResult>(view) {
        @Override
        public void onSuccess(final CreatePasswordResult response) {
            view.dismissProgressDialog();
            String sureCheckFlag = response.getSureCheckFlag();
            BaseActivity baseActivity = (BaseActivity) view;
            if (sureCheckFlag != null) {
                try {
                    TransactionVerificationType transactionVerificationType = TransactionVerificationType.valueOf(sureCheckFlag);
                    appCacheService.setSureCheckReferenceNumber(response.getReferenceNumber());
                    appCacheService.setSureCheckCellphoneNumber(response.getCellnumber());
                    appCacheService.setSureCheckEmail(response.getEmail());
                    appCacheService.setSureCheckNotificationMethod(response.getNotificationMethod());

                    switch (transactionVerificationType) {
                        case NotNeeded:
                            view.launchRegistrationResultScreen(response);
                            break;
                        case SURECHECKV1Required:
                        case SURECHECKV1:
                            createPasswordSureCheckDelegate.initiateV1CountDownScreen();
                            break;
                        case SURECHECKV2Required:
                        case SURECHECKV2:
                            createPasswordSureCheckDelegate.initiateV2CountDownScreen();
                            break;
                        case SURECHECKV1_FALLBACK:
                        case SURECHECKV1_FALLBACKRequired:
                            createPasswordSureCheckDelegate.initiateTransactionVerificationEntryScreen();
                            break;
                        case NoPrimaryDevice:
                            appCacheService.setReturnToScreen(RegisterCreatePasswordActivity.class);
                            view.showNoPrimaryDeviceScreen();
                            break;
                        case SecurityCode:
                            if (isUserLoggedIn()) {
                                ActionHandler actionHandler = () -> {
                                    //re-trigger the required password creation
                                    makeCurrentDevicePrimary();
                                };
                                SecurityCodeDelegate.handleSecurityCode(baseActivity, actionHandler);
                            } else {
                                SecurityCodeDelegate.handleSecurityCodeCaseAndChangePrimary((RegisterCreatePasswordActivity) RegisterCreatePasswordPresenter.this.view);
                            }
                            break;
                        default:
                            selectAppropriateFlow(response);
                            break;
                    }
                } catch (IllegalArgumentException e) {
                    BMBLogger.e(TAG, "No such transaction verification type found for SureCheckFlag [" + sureCheckFlag + "]" + " due to " + e);
                }

            } else {
                selectAppropriateFlow(response);
            }

        }

        @Override
        public void onFailure(final ResponseObject response) {
            appCacheService.setIsPasswordResetFlow(false);
            view.dismissProgressDialog();
            new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_LINKING_FAILED, response);
            view.launchRegistrationResultScreen(response);
        }
    };

    RegisterCreatePasswordPresenter(final RegisterCreatePasswordView view) {
        this.view = view;
        activity = (RegisterCreatePasswordActivity) view;
        createPasswordSureCheckDelegate = new SureCheckDelegate(activity) {
            @Override
            public void onSureCheckProcessed() {
                new Handler(Looper.getMainLooper()).postDelayed(() -> registrationInteractor.createPassword(password, createPasswordResponseLister), 250);
            }

            @Override
            public void onSureCheckRejected() {
                IntentFactory.IntentBuilder intentBuilder = IntentFactoryGenericResult.getFailureResultBuilder(activity)
                        .setFinishActivityOnDone(activity)
                        .setGenericResultHeaderMessage(R.string.transaction_rejected);
                activity.startActivity(intentBuilder.build());
            }

            @Override
            public void onSureCheckFailed() {
                Intent failureIntent = IntentFactoryGenericResult.getFailureResultBuilder(activity).setGenericResultHeaderMessage(R.string.surecheck_failed)
                        .setGenericResultSubMessage(R.string.something_went_wrong_message)
                        .setFinishActivityOnDone(activity)
                        .setFinishOnBackPressed()
                        .build();
                activity.startActivity(failureIntent);
            }
        };
        registrationResponseLister.setView(view);
        changePrimaryDeviceResponseListener.setView(view);
        createPasswordResponseLister.setView(view);
    }

    private void selectAppropriateFlow(CreatePasswordResult response) {
        if (isPartialToFullRegistration) {
            view.goToLoginScreen();
        } else if (isUserLoggedIn() && isUserLoggedInViaPasscodeLogin()) {
            BMBApplication.getInstance().getDeviceProfilingInteractor().notifyTransaction();
            appCacheService.setIsPasswordResetFlow(false);
            BMBApplication.getInstance().setUserLoggedInStatus(true);
            view.loadAccountsAndGoHome();
        } else if (isUserLoggedIn()) { //i.e, the user is in the linking flow
            view.goToCreateNicknameScreen();
            BMBApplication.getInstance().getDeviceProfilingInteractor().notifyTransaction();
        } else {
            view.launchRegistrationResultScreen(response);
        }
    }

    private void makeCurrentDevicePrimary() {
        ManageDevicesInteractor manageDevicesInteractor = new ManageDevicesInteractor();
        manageDevicesInteractor.changePrimaryDevice(SecureUtils.INSTANCE.getDeviceID(), changePrimaryDeviceResponseListener);
    }

    @Override
    public void validatePassword(CharSequence password, String nameOfUser) {
        if (password.length() != 0) {
            passwordRulesMap.clear();
            String passwordToValidate = password.toString();
            passwordRulesMap.putAll(PasswordValidator.buildValidator(passwordToValidate, nameOfUser));
            checkPasswordStatus(password.toString());
        }
    }

    private void checkPasswordStatus(String password) {
        isPasswordValid = true;

        if (password.length() == 0) {
            isPasswordValid = false;
            view.markAllPasswordRulesInvalid();
            return;
        }


        Boolean isMixedCase = passwordRulesMap.get(PasswordValidator.UPPER_N_LOWERCASE);
        Boolean hasDigits = passwordRulesMap.get(PasswordValidator.DIGIT);
        boolean containsBothLettersAndNumbers = isMixedCase != null && isMixedCase && hasDigits != null && hasDigits;
        isPasswordValid = containsBothLettersAndNumbers;
        view.markAlphanumericValidationRule(containsBothLettersAndNumbers);

        Boolean hasValidLength = passwordRulesMap.get(PasswordValidator.LENGTH_RESTRICTION);
        isPasswordValid = hasValidLength != null && hasValidLength;
        hasValidLength = hasValidLength != null && hasValidLength;
        view.markLengthValidationRule(hasValidLength);

        Boolean hasSpecialCase = passwordRulesMap.get(PasswordValidator.SPECIAL_CASE);
        Boolean hasWhitespace = passwordRulesMap.get(PasswordValidator.WHITESPACE);
        boolean hasNotSpacesOrSpecialCharacters = hasSpecialCase != null && !hasSpecialCase && hasWhitespace != null && !hasWhitespace;
        isPasswordValid = hasNotSpacesOrSpecialCharacters;
        view.markSpacesAndSpecialCharactersValidationRule(hasNotSpacesOrSpecialCharacters);

        Boolean hasName = passwordRulesMap.get(PasswordValidator.NAME);
        boolean doesNotContainName = hasName != null && !hasName;
        isPasswordValid = doesNotContainName;
        view.markNameOfUserValidationRule(doesNotContainName);

        Boolean hasSequentialAscendingNumbers = passwordRulesMap.get(PasswordValidator.SEQUENTIAL_ASCENDING);
        boolean doesNotContainSequence = hasSequentialAscendingNumbers != null && !hasSequentialAscendingNumbers;
        isPasswordValid = doesNotContainSequence;
        view.markSequenceValidationRule(doesNotContainSequence);

        view.returnValidity(containsBothLettersAndNumbers && hasValidLength && hasNotSpacesOrSpecialCharacters && doesNotContainName && doesNotContainSequence);
    }

    @Override
    public void doneButtonTapped() {
        view.requestDeviceStatePermissions();
    }

    @Override
    public void onDeviceStatePermissionGranted(String password, String confirmationPassword, RegisterProfileDetail registerProfileDetail, String navigatedFrom) {
        registerProfile(password.trim(), confirmationPassword.trim(), registerProfileDetail, navigatedFrom);
    }

    @Override
    public void registerProfile(String password, String confirmationPassword, RegisterProfileDetail registerProfileDetail, String navigatedFrom) {
        if (isPasswordValid) {
            if (password.equals(confirmationPassword)) {
                this.password = password;
                if ((navigatedFrom != null && navigatedFrom.equalsIgnoreCase("DS2")) || appCacheService.isPasswordResetFlow()) {
                    registrationInteractor = new RegistrationInteractor();
                    registrationInteractor.createPassword(password, createPasswordResponseLister);
                } else {
                    registrationInteractor = new RegistrationInteractor();
                    registrationInteractor.registerOnlineProfile(registerProfileDetail, password, registrationResponseLister);
                }
            } else {
                view.showPasswordsDoNotMatch();
            }
        } else {
            view.showInvalidPasswordMessage();
        }
    }
}