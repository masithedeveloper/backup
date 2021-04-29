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
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.registration.services.RegistrationInteractor;
import com.barclays.absa.banking.registration.services.dto.RegisterAOLProfileResponse;

class RegisterCreatePinPresenter {
    private RegisterCreatePinView view;
    private RegisterProfileDetail registerProfileDetail;
    private RegistrationInteractor registrationInteractor;

    private ExtendedResponseListener<RegisterAOLProfileResponse> partialRegistrationResponseListener = new ExtendedResponseListener<RegisterAOLProfileResponse>() {

        @Override
        public void onSuccess(final RegisterAOLProfileResponse successResponse) {
            view.dismissProgressDialog();
            view.launchRegistrationResultActivity(successResponse);
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            if (failureResponse.getErrorMessage() != null && failureResponse.getErrorMessage().contains(RegistrationResultActivity.ALREADY_REGISTERED_FAILURE_CODE)) {
                view.showAlreadyRegisteredErrorDialog();
            } else {
                view.registrationFailed(failureResponse.getResponseMessage() != null ? failureResponse.getResponseMessage() : failureResponse.getErrorMessage());
            }
        }
    };

    RegisterCreatePinPresenter(RegisterCreatePinView view, Object registerProfileDetail) {
        this.view = view;
        this.registerProfileDetail = (RegisterProfileDetail) registerProfileDetail;
        registrationInteractor = new RegistrationInteractor();
        partialRegistrationResponseListener.setView(view);
    }

    void nextButtonTapped(String pin, String confirmPin) {
        if (pin == null || pin.length() != 5) {
            view.onPinInvalidInput();
            return;
        }
        if (confirmPin == null || confirmPin.length() != 5) {
            view.onConfirmPinInvalidInput();
            return;
        }
        if (!pin.equals(confirmPin)) {
            view.onPinDoesNotMatch();
            return;
        }
        onValidPinInput(pin);
    }

    private void onValidPinInput(String pin) {
        registerProfileDetail.setOnlinePin(pin);
        if (registerProfileDetail.shouldShowPasswordScreen()) {
            view.launchCreatePasswordActivity(registerProfileDetail);
        } else {
            view.requestDeviceStateAccessPermission();
        }
    }

    void onPermissionDenied() {
        view.requestDeviceStateAccessPermission();
    }

    void registerUserProfile() {
        registrationInteractor.decoupleRegistration(registerProfileDetail, partialRegistrationResponseListener);
    }

}
