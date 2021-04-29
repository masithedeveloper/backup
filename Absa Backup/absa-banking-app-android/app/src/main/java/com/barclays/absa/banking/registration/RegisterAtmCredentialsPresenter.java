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

import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.registration.services.RegistrationInteractor;

import java.lang.ref.WeakReference;
import java.util.HashMap;

class RegisterAtmCredentialsPresenter implements RegisterAtmCredentialsPresenterInterface {

    private static final String ALREADY_REGISTERED_MESSAGE = "You are already registered for online banking";
    private static final String PROFILE_ALREADY_EXIST = "H0794 APP PROFILE ALREADY EXIST. PLEASE LINK PROFILE/DEVICE";
    private static final String BUSINESS_PROFILE_CODE = "H0683"; //H0683 BUSINESS CANNOT REGISTER ONLINE.VISIT BRANCH TO REGIST
    private static final String SOLE_PROP_CLIENT_TYPE_RESPONSE_CODE_KEY = "Portfolio/Register/Error/ClientType";
    private final WeakReference<RegisterAtmCredentialsView> weakReference;
    private final RegistrationInteractor interactor;

    private final ExtendedResponseListener<RegisterProfileDetail> responseListener = new ExtendedResponseListener<RegisterProfileDetail>() {
        @Override
        public void onSuccess(final RegisterProfileDetail registerProfileDetail) {
            RegisterAtmCredentialsView view = weakReference.get();
            if (view != null) {
                if ("INVALID_PIN".equals(registerProfileDetail.getFailureCode())) {
                    new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_ATTRIBUTE_AUTH_TYPE_VALUE_ATM_CARD_PIN, registerProfileDetail);
                    //show invalid pin screen
                    view.showCardNumberAndPinFailureDialog(registerProfileDetail);
                } else if ("INVALID_CARD".equals(registerProfileDetail.getFailureCode())) {
                    new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_ATTRIBUTE_AUTH_TYPE_VALUE_ATM_CARD_PIN, registerProfileDetail);
                    view.showInvalidCardNumberDialog(registerProfileDetail.getFailureMessage());
                } else if (SOLE_PROP_CLIENT_TYPE_RESPONSE_CODE_KEY.equalsIgnoreCase(registerProfileDetail.getResponseCode())) {
                    view.showSolePropErrorMessage();
                } else {
                    if (registerProfileDetail.getFailureMessage() != null && registerProfileDetail.getFailureMessage().contains(ALREADY_REGISTERED_MESSAGE) || registerProfileDetail.getResponseMessage() != null && registerProfileDetail.getResponseMessage().contains(PROFILE_ALREADY_EXIST)) {
                        new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_DIGITAL_PROFILE_ALREADY_EXISTS, registerProfileDetail);
                        view.showAlreadyRegisterDialog();
                    } else if (registerProfileDetail.getStatus() != null && !registerProfileDetail.getStatus().equalsIgnoreCase("true")) {
                        view.showCardNumberAndPinFailureDialog(registerProfileDetail);
                    } else if (registerProfileDetail.isMobileRecordNotFound()) {
                        HashMap<String, Object> eventData = new HashMap<>();
                        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE, "Mobile record not found");
                        new MonitoringInteractor().logMonitoringEvent(MonitoringService.MONITORING_EVENT_ATTRIBUTE_AUTH_TYPE_VALUE_ATM_CARD_PIN, eventData);
                        view.onMobileRecordNotFound(registerProfileDetail);
                    } else {
                        view.goToConfirmContactDetailScreen(registerProfileDetail);
                    }
                }
                view.dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            RegisterAtmCredentialsView view = weakReference.get();
            if (view != null) {
                new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_REGISTRATION_FAILED, failureResponse);
                if (failureResponse != null) {
                    if (SOLE_PROP_CLIENT_TYPE_RESPONSE_CODE_KEY.equalsIgnoreCase(failureResponse.getResponseCode())) {
                        view.showSolePropErrorMessage();
                    } else if (failureResponse.getResponseCode() != null && failureResponse.getResponseCode().startsWith(BUSINESS_PROFILE_CODE)) {
                        view.showBusinessBankingProfileErrorMessage();
                    } else {
                        String errorMessage = ResponseObject.extractErrorMessage(failureResponse);
                        if (errorMessage.contains(PROFILE_ALREADY_EXIST) || (failureResponse.getResponseCode() != null && failureResponse.getResponseCode().equalsIgnoreCase(PROFILE_ALREADY_EXIST))) {
                            view.showAlreadyRegisterDialog();
                        } else {
                            view.showErrorDialog(errorMessage);
                        }
                    }
                }
                view.dismissProgressDialog();
            }
        }
    };

    RegisterAtmCredentialsPresenter(RegisterAtmCredentialsView view) {
        weakReference = new WeakReference<>(view);
        interactor = new RegistrationInteractor();
        responseListener.setView(view);
    }

    @Override
    public void onContinueClicked(String cardNumber, String pinNumber) {
        RegisterAtmCredentialsView view = weakReference.get();
        if (view != null) {
            if (!validateCardNumberDetails(cardNumber)) {
                view.onInvalidCardNumber(true);
            } else if (!validatePinNumberDetails(pinNumber)) {
                view.onInvalidPin(true);
            } else {
                interactor.getCustomerProfileDetails(cardNumber, pinNumber, BuildConfigHelper.INSTANCE.getNCipherServerPath(), responseListener);
            }
        }
    }

    private boolean validateCardNumberDetails(String cardNumber) {
        String textValue = cardNumber.trim().replaceAll("\\s", "");
        final int ATM_CARD_NUMBER_LENGTH = 16;
        return textValue.length() == ATM_CARD_NUMBER_LENGTH;
    }

    private boolean validatePinNumberDetails(String pinValue) {
        final int MIN_ATM_PIN_LENGTH = 3;
        final int MAX_ATM_PIN_LENGTH = 7;
        return pinValue.length() > MIN_ATM_PIN_LENGTH && pinValue.length() < MAX_ATM_PIN_LENGTH;
    }
}