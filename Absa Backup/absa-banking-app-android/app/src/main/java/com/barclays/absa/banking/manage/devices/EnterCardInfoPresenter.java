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
package com.barclays.absa.banking.manage.devices;

import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TDataResponse;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.manage.devices.services.dto.GenericValidationResponse;
import com.barclays.absa.banking.presentation.shared.PinBlockInteractor;
import com.barclays.absa.banking.registration.services.RegistrationInteractor;
import com.barclays.absa.banking.registration.services.dto.Create2faAliasResponse;
import com.entersekt.sdk.Error;
import com.entersekt.sdk.Notify;

import java.util.HashMap;

import za.co.absa.twoFactor.TransaktEngine;

import static com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS;

class EnterCardInfoPresenter implements EnterCardInfoPresenterInterface {

    private final String currentDeviceID;
    private String passcode;
    private String atmCardNumber;
    private EnterCardInfoView enterCardInfoView;
    private ManageDevicesInteractor manageDevicesInteractor;
    private RegistrationInteractor registrationInteractor;
    private static short attemptsUsedUp = 0;
    private TransaktHandler transaktHandler;

    EnterCardInfoPresenter(EnterCardInfoView atmCardInfo, Device device, String currentDeviceID) {
        this.enterCardInfoView = atmCardInfo;
        manageDevicesInteractor = new ManageDevicesInteractor();
        registrationInteractor = new RegistrationInteractor();
        this.currentDeviceID = currentDeviceID;
        changePrimaryDeviceResponseListener.setView(atmCardInfo);
        primaryDeviceValidationResponseListener.setView(atmCardInfo);
        pinBlockResponseListener.setView(atmCardInfo);

        transaktHandler = BMBApplication.getInstance().getTransaktHandler();
        transaktHandler.setConnectCallbackTriggeredFlag(false);
    }

    private ExtendedResponseListener<ChangePrimaryDeviceResponse> changePrimaryDeviceResponseListener = new ExtendedResponseListener<ChangePrimaryDeviceResponse>() {
        @Override
        public void onSuccess(final ChangePrimaryDeviceResponse response) {
            enterCardInfoView.dismissProgressDialog();
            if (SUCCESS.equalsIgnoreCase(response.getTransactionStatus())) {
                enterCardInfoView.navigateToSureCheckConfirmationScreen();
            } else {
                enterCardInfoView.showFailure(response.getTransactionMessage());
            }
        }
    };

    private ExtendedResponseListener<GenericValidationResponse> primaryDeviceValidationResponseListener = new ExtendedResponseListener<GenericValidationResponse>() {

        private boolean hasGeneratedTrustToken;
        private boolean hasSignUpOrCallbackAlreadyFired;
        private boolean isGeneratedTrustTokenUsed;
        private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

        @Override
        public void onSuccess(final GenericValidationResponse response) {
            if (SUCCESS.equalsIgnoreCase(response.getTransactionStatus())) {
                transaktHandler.setTransaktDelegate(new TransaktDelegate() {
                    @Override
                    protected void onConnected() {
                        super.onConnected();
                        if (appCacheService.isLinkingFlow() && !appCacheService.isPasswordResetFlow()) {
                            enterCardInfoView.dismissProgressDialog();
                            enterCardInfoView.navigateCreateNicknameScreen();
                        } else if (appCacheService.getUserLoggedInStatus()) {
                            transaktHandler.generateTrustToken();
                        } else {
                            transaktHandler.signUp();
                        }
                    }

                    @Override
                    protected void onError(final String errorMessage) {
                        super.onError(errorMessage);
                        HashMap<String, Object> eventData = new HashMap<>();
                        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE, errorMessage);
                        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_OPCODE, response.getOpCode());
                        eventData.put(MonitoringService.MONITORING_EVENT_NAME_ERROR_RESPONSE, response.getResponseHeaderMessage());
                        new MonitoringInteractor().logMonitoringEvent(MonitoringService.MONITORING_EVENT_NAME_NEGATIVE_FLOW_AUTH_FAILED, eventData);
                        enterCardInfoView.dismissProgressDialog();
                        enterCardInfoView.showFailure(errorMessage);
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
                            enterCardInfoView.dismissProgressDialog();
                            enterCardInfoView.showGenericErrorMessage();
                        }
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
                    protected void onSignupError(Error error) {
                        super.onSignupError(error);
                        enterCardInfoView.showFailure(error.toString());
                    }

                    @Override
                    protected void onNotifyReceived(final Notify notify) {
                        super.onNotifyReceived(notify);
                        enterCardInfoView.dismissProgressDialog();
                        enterCardInfoView.showMessage(notify.getType(), notify.getText(), null);
                    }

                    @Override
                    public synchronized void onGenerateTrustTokenSuccess(String trustToken) {
                        if (!isGeneratedTrustTokenUsed) {
                            isGeneratedTrustTokenUsed = true;
                            super.onGenerateTrustTokenSuccess(trustToken);
                            if (!appCacheService.isLinkingFlow()) {
                                manageDevicesInteractor.changePrimaryDevice(currentDeviceID, changePrimaryDeviceResponseListener);
                            } else {
                                registerAlias(response);
                            }
                            BMBApplication.getInstance().listenForAuth();
                        }
                    }
                });

                transaktHandler.start();
            } else {
                ++attemptsUsedUp;
                enterCardInfoView.dismissProgressDialog();
                new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_NEGATIVE_FLOW_AUTH_FAILED, response);
                String transactionMessage = response.getTransactionMessage();
                if (attemptsUsedUp > 2) {
                    enterCardInfoView.showSecurityCodeMessage();
                    return;
                }
                final String VISIT_BRANCH_MESSAGE = "Unable to process your request, Please visit your nearest branch";
                final String AUTHENTICATION_FAILED_MESSAGE = "Authentication failed";
                if ("ONBEKEND".equalsIgnoreCase(transactionMessage) || "UNKNOWN".equalsIgnoreCase(transactionMessage) || AUTHENTICATION_FAILED_MESSAGE.equalsIgnoreCase(transactionMessage) || VISIT_BRANCH_MESSAGE.equalsIgnoreCase(transactionMessage) || transactionMessage == null) {
                    enterCardInfoView.showInvalidPasscodeOrCredentialsError();
                } else if ("InvalidAuthLevel".equalsIgnoreCase(response.getResponseCode()) || transactionMessage.toLowerCase(BMBApplication.getApplicationLocale()).contains("Invalid auth level".toLowerCase(BMBApplication.getApplicationLocale()))) {
                    enterCardInfoView.showMessage("Timed out", "You have been logged out due to inactivity. You will need to start again", (dialog, which) -> {
                        dialog.dismiss();
                        enterCardInfoView.logoutAndGoToStartScreen();
                    });
                } else {
                    enterCardInfoView.showFailure(transactionMessage);
                }
            }
        }

        @Override
        public void onFailure(final ResponseObject response) {
            new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_NEGATIVE_FLOW_AUTH_FAILED, response);
            enterCardInfoView.dismissProgressDialog();
            showError(response);
        }
    };

    private void registerAlias(GenericValidationResponse response) {
        registrationInteractor.create2faAlias(new ExtendedResponseListener<Create2faAliasResponse>() {
            @Override
            public void onRequestStarted() {
            }

            @Override
            public void onSuccess(final Create2faAliasResponse successResponse) {
                enterCardInfoView.navigateToSureCheckConfirmationScreen();
            }

            @Override
            public void onFailure(ResponseObject failureResponse) {
                new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_NEGATIVE_FLOW_AUTH_FAILED, response);
                enterCardInfoView.dismissProgressDialog();
                enterCardInfoView.showInvalidPasscodeOrCredentialsError();
            }
        });
    }

    private void showError(ResponseObject response) {
        TransactionResponse transactionResponse = null;
        String transactionMessage = "";
        if (response instanceof TransactionResponse) {
            transactionResponse = (TransactionResponse) response;
            transactionMessage = transactionResponse.getTransactionMessage();
        }
        if ("InvalidAuthLevel".equalsIgnoreCase(response.getResponseCode()) || transactionResponse != null && transactionMessage != null && transactionMessage.contains("Invalid auth level".toLowerCase(BMBApplication.getApplicationLocale()))) {
            enterCardInfoView.showMessage("Timed out", "You have been logged out due to inactivity. You will need to start again", (dialog, which) -> {
                dialog.dismiss();
                enterCardInfoView.logoutAndGoToStartScreen();
            });
        } else {
            String msg;
            if (response instanceof GenericValidationResponse) {
                GenericValidationResponse genericValidationResponse = (GenericValidationResponse) response;
                msg = genericValidationResponse.getTransactionMessage();
            } else {
                msg = response.getErrorMessage();
            }
            enterCardInfoView.showFailure(msg);
        }
    }

    private ExtendedResponseListener<PINObject> pinBlockResponseListener = new ExtendedResponseListener<PINObject>() {
        @Override
        public void onSuccess(PINObject pinBlock) {
            manageDevicesInteractor.validatePrimaryDevicePasscodeAndAtmPin(passcode, atmCardNumber, pinBlock, primaryDeviceValidationResponseListener);
        }

        @Override
        public void onFailure(final ResponseObject response) {
            HashMap<String, Object> eventData = new HashMap<>();
            eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE, response.getErrorMessage());
            eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_OPCODE, response.getOpCode());
            eventData.put(MonitoringService.MONITORING_EVENT_NAME_ERROR_RESPONSE, response.getResponseHeaderMessage());
            eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_EXTRA_INFO, "Failed to retrieve PinBlock");
            new MonitoringInteractor().logMonitoringEvent(MonitoringService.MONITORING_EVENT_NAME_NEGATIVE_FLOW_AUTH_FAILED, eventData);
            enterCardInfoView.dismissProgressDialog();
            showError(response);
        }
    };

    @Override
    public void onConfirmCardInfoButtonClicked(String passcode, String atmNumber, String atmPin) {
        this.passcode = passcode;
        this.atmCardNumber = atmNumber;
        PinBlockInteractor pinBlockInteractor = new PinBlockInteractor();
        pinBlockInteractor.submitPinBlockRequest(atmPin, pinBlockResponseListener);
    }
}