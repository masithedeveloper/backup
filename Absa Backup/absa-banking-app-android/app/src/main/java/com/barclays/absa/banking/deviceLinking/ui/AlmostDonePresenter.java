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

import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.registration.services.RegistrationInteractor;
import com.barclays.absa.banking.registration.services.dto.Create2faAliasResponse;

class AlmostDonePresenter {

    private static final String TAG = AlmostDonePresenter.class.getSimpleName();
    private final RegistrationInteractor registrationInteractor;
    private AlmostDoneView view;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private final ExtendedResponseListener<Create2faAliasResponse> create2faResponseListener = new ExtendedResponseListener<Create2faAliasResponse>() {

        @Override
        public void onSuccess(final Create2faAliasResponse successResponse) {
            view.dismissProgressDialog();
            BMBLogger.d(TAG, successResponse.toString());
            if (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTxnStatus())) {
                String errorMessage = successResponse.getTxnMessage();
                if (successResponse.getTxnMessage() != null && errorMessage.contains("DeviceID already linked to MappedUser")) {
                    errorMessage = "Your account is already linked on this device.";
                }
                view.showLinkingFailedScreen(errorMessage);
            } else {
                String aliasId = successResponse.getAliasID();
                if (aliasId != null) {
                    appCacheService.setEnrollingUserAliasID(aliasId);
                }
                if (successResponse.isIdNoRequired()) {
                    view.goToVerifyAliasIdScreen();
                } else {
                    appCacheService.setCreate2faAliasResponse(successResponse);
                    if (successResponse.getReachedDeviceMaxLimit()) {
                        view.showDeviceLimitReachedScreen();
                    } else {
                        view.showCreatePasscodeScreen();
                    }
                }
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            if (failureResponse != null) {
                BMBLogger.d(TAG, failureResponse.toString());
                view.showFailureDialog(failureResponse.getResponseMessage());
            }
        }
    };

    AlmostDonePresenter(AlmostDoneView view) {
        this.view = view;
        registrationInteractor = new RegistrationInteractor();
        create2faResponseListener.setView(view);
    }

    void onContinueInvoked() {
        registrationInteractor.create2faAlias(create2faResponseListener);
    }
}