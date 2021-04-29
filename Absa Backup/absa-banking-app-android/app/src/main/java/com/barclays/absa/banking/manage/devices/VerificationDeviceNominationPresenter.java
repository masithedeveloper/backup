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

import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;

class VerificationDeviceNominationPresenter {

    private VerificationDeviceNominationView view;
    private ManageDevicesInteractor manageDevicesInteractor;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private ExtendedResponseListener<ChangePrimaryDeviceResponse> manageDeviceResponseListener = new ExtendedResponseListener<ChangePrimaryDeviceResponse>() {

        @Override
        public void onSuccess(final ChangePrimaryDeviceResponse successResponse) {
            view.dismissProgressDialog();
            if (successResponse.getDeviceChanged()) {
                appCacheService.setPrimarySecondFactorDevice(true);
                view.showSurecheckConfirmation();
            } else {
                String sureCheckFlag = successResponse.getSureCheckFlag();
                if (sureCheckFlag == null || sureCheckFlag.isEmpty()) {
                    String status = successResponse.getTransactionStatus();
                    if (status != null) {
                        if (status.toLowerCase().equals(BMBConstants.FAILURE)) {
                            view.showMessageError(successResponse.getTransactionMessage());
                        }
                    }
                } else {
                    if ("TVMRequired".equalsIgnoreCase(sureCheckFlag)) {
                        sureCheckFlag = TransactionVerificationType.SURECHECKV1_FALLBACK.getKey();
                    }
                    TransactionVerificationType verificationType = TransactionVerificationType.valueOf(sureCheckFlag);
                    appCacheService.setSureCheckReferenceNumber(successResponse.getReferenceNumber());
                    appCacheService.setSureCheckCellphoneNumber(successResponse.getCellnumber());
                    appCacheService.setSureCheckEmail(successResponse.getEmail());
                    switch (verificationType) {
                        case SURECHECKV2Required:
                            view.goToSureCheckCountDownScreen(verificationType);
                            break;
                        case SURECHECKV2_FALLBACKRequired:
                            break;
                    }
                }
            }
        }
    };

    VerificationDeviceNominationPresenter(VerificationDeviceNominationView view) {
        this.view = view;
        manageDevicesInteractor = new ManageDevicesInteractor();
        manageDeviceResponseListener.setView(view);
    }

    void executeMakeSurecheck(String deviceImei) {
        if (appCacheService.isIdentificationAndVerificationFlow()) {
            String requestId = appCacheService.getRequestId();
            String reference = appCacheService.getBiometricReferenceNumber();
            manageDevicesInteractor.changePrimaryDevice(deviceImei, requestId, reference, manageDeviceResponseListener);
        } else {
            manageDevicesInteractor.changePrimaryDevice(deviceImei, manageDeviceResponseListener);
        }
    }
}
