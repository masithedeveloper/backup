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
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TDataResponse;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.registration.services.RegistrationInteractor;
import com.barclays.absa.banking.registration.services.dto.Create2faAliasResponse;
import com.entersekt.sdk.Notify;

import za.co.absa.twoFactor.TransaktEngine;

class CreateNicknamePresenter {

    private static final String TAG = CreateNicknamePresenter.class.getSimpleName();
    private final CreateNicknameView view;
    private TransaktHandler transaktHandler;
    private final RegistrationInteractor registrationInteractor;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
    private final ExtendedResponseListener<Create2faAliasResponse> create2faAliasResponseListener = new ExtendedResponseListener<Create2faAliasResponse>() {

        @Override
        public void onSuccess(final Create2faAliasResponse successResponse) {
            BMBLogger.d(TAG, successResponse.toString());
            if (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTxnStatus())) {
                String errorMessage = successResponse.getTxnMessage();
                if (successResponse.getTxnMessage() != null && errorMessage != null && errorMessage.contains("DeviceID already linked to MappedUser")) {
                    errorMessage = "Your account is already linked on this device.";
                } else if (errorMessage != null && ("InvalidAuthLevel".equalsIgnoreCase(errorMessage) || errorMessage.toLowerCase(BMBApplication.getApplicationLocale()).contains("Invalid auth level".toLowerCase(BMBApplication.getApplicationLocale())))) {
                    errorMessage = "You have been logged out due to inactivity. You will need to start again";
                }
                view.showLinkingFailedScreen(errorMessage);
            } else {
                String aliasId = successResponse.getAliasID();
                if (aliasId != null) {
                    appCacheService.setEnrollingUserAliasID(successResponse.getAliasID());
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
            view.dismissProgressDialog();
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

    CreateNicknamePresenter(CreateNicknameView createNicknameView) {
        view = createNicknameView;
        registrationInteractor = new RegistrationInteractor();
        create2faAliasResponseListener.setView(view);
    }

    void onProceed() {
        final TransaktDelegate transaktDelegate = new TransaktDelegate() {
            private boolean hasGeneratedTrustToken;
            private boolean hasSignUpOrCallbackAlreadyFired;
            private boolean isGeneratedTrustTokenUsed;

            @Override
            protected void onConnected() {
                super.onConnected();
                if (appCacheService.isScanQRFlow()) {
                    transaktHandler.generateTrustToken();
                } else {
                    transaktHandler.signUp();
                }
            }

            @Override
            protected void onError(final String errorMessage) {
                super.onError(errorMessage);
                view.dismissProgressDialog();
                view.showTransaktErrorMessage();
            }

            @Override
            protected void onRegisterSuccess() {
                super.onRegisterSuccess();
                if (hasSignUpOrCallbackAlreadyFired && !hasGeneratedTrustToken) {
                    transaktHandler.generateTrustToken();
                    hasGeneratedTrustToken = true;
                }
            }

            @Override
            protected void onTDataReceived(TDataResponse tDataResponse) {
                super.onTDataReceived(tDataResponse);
                String tDataCommand = tDataResponse.getCommand();
                if (CONTINUE_ENROLLMENT.equalsIgnoreCase(tDataCommand)) {
                    if (TransaktEngine.isRegistered() && !hasGeneratedTrustToken) {
                        transaktHandler.generateTrustToken();
                        hasGeneratedTrustToken = true;
                    }
                    hasSignUpOrCallbackAlreadyFired = true;
                } else {
                    view.dismissProgressDialog();
                    view.showTransaktErrorMessage();
                }
            }

            @Override
            protected void onNotifyReceived(final Notify notify) {
                super.onNotifyReceived(notify);
                view.dismissProgressDialog();
                view.showMessage(notify.getType(), notify.getText(), null);
            }

            @Override
            public synchronized void onGenerateTrustTokenSuccess(String trustToken) {
                if (!isGeneratedTrustTokenUsed) {
                    isGeneratedTrustTokenUsed = true;
                    super.onGenerateTrustTokenSuccess(trustToken);
                    onTrustTokenGenerated();
                    BMBApplication.getInstance().listenForAuth();
                }
            }
        };

        view.showProgressDialog();

        transaktHandler = BMBApplication.getInstance().getTransaktHandler();
        transaktHandler.setConnectCallbackTriggeredFlag(false);
        transaktHandler.setTransaktDelegate(transaktDelegate);
        transaktHandler.start();
    }

    private void onTrustTokenGenerated() {
        if (appCacheService.isPasscodeResetFlow()) {
            view.showCreatePasscodeScreen();
        } else {
            registrationInteractor.create2faAlias(create2faAliasResponseListener);
        }
    }
}
