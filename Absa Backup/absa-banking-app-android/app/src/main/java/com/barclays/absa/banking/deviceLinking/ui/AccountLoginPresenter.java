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

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.boundary.shared.dto.SecondFactorState;
import com.barclays.absa.banking.express.data.ClientTypeGroupKt;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.login.services.LoginInteractor;
import com.barclays.absa.banking.login.services.LoginService;
import com.barclays.absa.integration.DeviceProfilingInteractor;

import org.jetbrains.annotations.TestOnly;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP1000_LOGIN_SECURE_HOME_PAGE;
import static com.barclays.absa.banking.framework.utils.AppConstants.PIN_LOCKED;

class AccountLoginPresenter {
    private static final String WARNING_RESPONSE = "00002";
    private static final String PROFILE_LOCKED_ERROR = "Sorry. This Absa profile has been locked. Visit Absa Online or your branch to unlock this profile.";
    private static final String PLEASE_USE_BIOMETRIC_AUTHENTICATION = "BiometricValidationFailedIdNumberExists";
    private static final String INCORRECT_CREDENTIALS_ERROR = "The credentials you entered are incorrect. Please try again";
    private static final String DOING_ROUTINE_MAINTENANCE = "We are doing routine maintenance and the service should be restored shortly.";
    private AccountLoginView view;
    private DeviceProfilingInteractor deviceProfilingInteractor;
    private MonitoringInteractor monitoringInteractor;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private final ExtendedResponseListener<SecureHomePageObject> responseListener = new ExtendedResponseListener<SecureHomePageObject>() {

        @Override
        public void onSuccess(final SecureHomePageObject secureHomePageObject) {
            if (deviceProfilingInteractor == null) {
                deviceProfilingInteractor = BMBApplication.getInstance().getDeviceProfilingInteractor();
            }
            BMBLogger.d("AccountLoginPresenter", "Creating linking login session...");
            deviceProfilingInteractor.createPostLoginSession(appCacheService.getCustomerSessionId(), CustomerProfileObject.getInstance().getPermanentUserId(), CustomerProfileObject.getInstance().getUserId());
            BMBApplication.TOTAL_FAILED_LOGIN_ATTEMPTS = 0;
            view.dismissProgressDialog();
            appCacheService.setLinkingFlow(true);
            appCacheService.setAccessAccountLogin(false);
            appCacheService.setSecureHomePageObject(secureHomePageObject);
            boolean isDeviceNotLinked = BMBConstants.RESPONSE_ID_DEVICE_NOT_LINKED.equals(secureHomePageObject.getResponseId());
            view.clearLoginDetails();
            if (isDeviceNotLinked) {
                navigateToRelevantPasswordScreen(secureHomePageObject);
            } else if (BMBConstants.SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID.equals(secureHomePageObject.getResponseId())) {
                if (secureHomePageObject.getCustomerProfile().getIdNumberRequired()) {
                    view.navigateToAliasVerificationScreen(secureHomePageObject);
                } else if (isOperator(secureHomePageObject)) {
                    navigateToRelevantPasswordScreen(secureHomePageObject);
                } else if (passwordNotSet(secureHomePageObject)) {
                    view.launchCreatePasswordScreen(secureHomePageObject);
                } else if (isPartiallyRegistered(secureHomePageObject)) {
                    view.navigateToEnterPasswordScreen(secureHomePageObject);
                } else {
                    handleSecondFactorState(secureHomePageObject);
                }

                appCacheService.setScanQRFlow(false);
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            view.clearLoginDetails();

            if (monitoringInteractor == null) {
                monitoringInteractor = new MonitoringInteractor();
            }

            if (failureResponse != null) {
                if (OP1000_LOGIN_SECURE_HOME_PAGE.equalsIgnoreCase(failureResponse.getOpCode())) {
                    BMBApplication.TOTAL_FAILED_LOGIN_ATTEMPTS = BMBApplication.TOTAL_FAILED_LOGIN_ATTEMPTS + 1;
                    String error = failureResponse.getErrorMessage();
                    monitoringInteractor.logEvent(MonitoringService.MONITORING_EVENT_NAME_INCORRECT_AOL_LOGIN, failureResponse);
                    if (PIN_LOCKED.equalsIgnoreCase(failureResponse.getResponseCode())) {
                        monitoringInteractor.logEvent(MonitoringService.MONITORING_EVENT_NAME_LINKING_FAILED, failureResponse, "Profile locked");
                        view.goToAccountLockedScreen();
                    } else if (error != null) {
                        switch (error) {
                            case PROFILE_LOCKED_ERROR:
                                monitoringInteractor.logEvent(MonitoringService.MONITORING_EVENT_NAME_LINKING_FAILED, failureResponse, "Profile locked");
                                view.goToAccountLockedScreen();
                                break;
                            case PLEASE_USE_BIOMETRIC_AUTHENTICATION:
                                view.showPleaseUseBiometricAuthenticationScreen();
                                break;
                            case DOING_ROUTINE_MAINTENANCE:
                                view.showCurrentlyOfflineScreen(error);
                                break;
                            case INCORRECT_CREDENTIALS_ERROR:
                                view.showErrorDialog(R.string.enter_correct_information, R.string.enter_correct_information_content);
                                break;
                            default:
                                if (AppConstants.RESPONSE_CODE_SERVICE_NOT_ACTIVE.equalsIgnoreCase(failureResponse.getResponseCode())) {
                                    view.showAccountSuspendedScreen();
                                } else if (AppConstants.RESPONSE_CODE_FRAUD_HOLD.equalsIgnoreCase(failureResponse.getResponseCode())) {
                                    view.showFraudLockScreen();
                                } else {
                                    monitoringInteractor.logEvent(MonitoringService.MONITORING_EVENT_NAME_UNEXPECTED_ERROR_AT_LOGIN_PASSCODE, failureResponse);
                                    view.showGenericErrorMessage();
                                }
                                break;
                        }
                    }
                    String responseCode = failureResponse.getResponseCode();
                    if (WARNING_RESPONSE.equalsIgnoreCase(responseCode)) {
                        view.navigateToForgotPasscodeScreen();
                    }
                }
            }
            appCacheService.setLinkingFlow(true);
        }
    };

    private boolean isPartiallyRegistered(SecureHomePageObject secureHomePageObject) {
        CustomerProfileObject customerProfile = secureHomePageObject.getCustomerProfile();
        return "true".equalsIgnoreCase(customerProfile.getLimitsNotSet()) && customerProfile.getAuthPassNotRegistered() == null;
    }

    private boolean isOperator(SecureHomePageObject secureHomePageObject) {
        return secureHomePageObject.getAccessPrivileges() != null && secureHomePageObject.getAccessPrivileges().isOperator();
    }

    private void handleSecondFactorState(SecureHomePageObject secureHomePageObject) {
        if (secureHomePageObject == null) {
            return;
        }
        CustomerProfileObject customerProfile = secureHomePageObject.getCustomerProfile();
        final SecondFactorState secondFactorState = SecondFactorState.fromValue(customerProfile.getSecondFactorState());
        if (secondFactorState != null) {
            switch (secondFactorState) {
                case SURECHECKV2_SECURITYCODEREVOKED:
                    view.goToSecurityCodeRevokedScreen();
                    break;
                case SURECHECKV2_SECURITYCODEEXPIRED:
                    view.goToSecurityCodeExpiredScreen();
                    break;
                case SURECHECKV2_NOPRIMARYDEVICE:
                    if (ClientTypeGroupKt.isBusiness(customerProfile.getClientTypeGroup())) {
                        view.showGoToBranchForSecurityCodeMessage();
                    } else {
                        view.goToNoPrimaryDeviceScreen(secureHomePageObject);
                    }
                    break;
                case SURECHECKV2_SECURITYCODE:
                default:
                    appCacheService.setLinkingFlow(true);
                    view.navigateToEnterPasswordScreen(secureHomePageObject);
                    break;
            }
        }
    }

    private void navigateToRelevantPasswordScreen(SecureHomePageObject responseModel) {
        if (passwordNotSet(responseModel)) {
            view.launchCreatePasswordScreen(responseModel);
        } else {
            view.navigateToEnterPasswordScreen(responseModel);
        }
    }

    private boolean passwordNotSet(SecureHomePageObject responseModel) {
        // Because isAuthPassNotRegistered comes back as null , this is the solution that works
        return "true".equalsIgnoreCase(responseModel.getCustomerProfile().getAuthPassNotRegistered());
    }

    private LoginService loginInteractor;

    AccountLoginPresenter(AccountLoginView view) {
        this.view = view;
        responseListener.setView(view);
        loginInteractor = new LoginInteractor();
    }

    @TestOnly
    AccountLoginPresenter(AccountLoginView view, DeviceProfilingInteractor deviceProfilingInteractor, MonitoringInteractor monitoringInteractor) {
        this.view = view;
        responseListener.setView(view);
        loginInteractor = new LoginInteractor();
        this.deviceProfilingInteractor = deviceProfilingInteractor;
        this.monitoringInteractor = monitoringInteractor;
    }

    void login() {
        if (view != null) {
            view.login();
        }
    }

    void termsAndConditionsInvoked() {
        view.navigateToTermsAndConditionsScreen();
    }

    void onlineBankingViewInvoked() {
        view.navigateToOnlineView();
    }

    void loginInvoked(String accessAccountNumber, String accessPin, String userNumber) {
        if (isValidLoginCredentials(accessAccountNumber, accessPin, userNumber)) {
            appCacheService.setAccessAccountLogin(true);
            loginInteractor.performAccessAccountLogin(accessAccountNumber, accessPin, userNumber, responseListener);
        }
    }

    private boolean isValidLoginCredentials(String accessAccountNumber, String accessPin, String userNumber) {
        if (accessAccountNumber.isEmpty()) {
            view.showError(R.string.access_account_number_2fa_errormessage);
            return false;
        }

        if (accessPin.isEmpty()) {
            view.showError(R.string.access_pin_errormessage);
            return false;
        }

        if (userNumber.isEmpty()) {
            view.setDefaultUserNumber();
            return true;
        }
        return true;
    }

    void setLoginInteractor(LoginService loginService) {
        this.loginInteractor = loginService;
    }
}