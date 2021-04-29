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

import com.barclays.absa.banking.boundary.model.ManageDeviceResult;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;
import com.barclays.absa.banking.manage.devices.services.dto.Device;

import java.lang.ref.WeakReference;

class ManageDevicePresenter {

    private WeakReference<ManageDeviceView> manageDeviceViewWeakReference;
    private ManageDevicesInteractor manageDevicesInteractor;
    private boolean isCurrentDeviceDeleted;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private ExtendedResponseListener<ChangePrimaryDeviceResponse> changePrimaryDeviceResponseListener = new ExtendedResponseListener<ChangePrimaryDeviceResponse>() {
        @Override
        public void onSuccess(final ChangePrimaryDeviceResponse response) {
            ManageDeviceView view = manageDeviceViewWeakReference.get();
            if (view == null) {
                return;
            }
            if (response.getDeviceChanged()) {
                view.dismissProgressDialog();
                view.showPrimaryDeviceChangeSuccessfulScreen();
            } else {
                final String sureCheckFlag = response.getSureCheckFlag();
                view.dismissProgressDialog();
                if (sureCheckFlag == null || sureCheckFlag.isEmpty()) {
                    String status = response.getTransactionStatus();
                    if (BMBConstants.FAILURE.equalsIgnoreCase(status)) {
                        view.showServerErrorFromDevice(response.getTransactionMessage());
                    }
                } else {
                    TransactionVerificationType verificationType = TransactionVerificationType.valueOf(sureCheckFlag);
                    appCacheService.setSureCheckReferenceNumber(response.getReferenceNumber());
                    appCacheService.setSureCheckCellphoneNumber(response.getCellnumber());
                    appCacheService.setSureCheckEmail(response.getEmail());
                    view.onReceivedSureCheckVertificationType(verificationType);
                }
            }
        }
    };

    private final ExtendedResponseListener<ManageDeviceResult> editNicknameResponseListener = new ExtendedResponseListener<ManageDeviceResult>() {
        @Override
        public void onSuccess(final ManageDeviceResult successResponse) {
            ManageDeviceView view = manageDeviceViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.responseEditDeviceNickname(successResponse);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            ManageDeviceView view = manageDeviceViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
            }
        }
    };

    private final ExtendedResponseListener<ManageDeviceResult> delinkDeviceResponseListener = new ExtendedResponseListener<ManageDeviceResult>() {
        @Override
        public void onSuccess(ManageDeviceResult successResponse) {
            ManageDeviceView view = manageDeviceViewWeakReference.get();
            if (view == null) {
                return;
            }
            view.dismissProgressDialog();
            view.navigateToGenericResultScreen(false, isCurrentDeviceDeleted);

        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            ManageDeviceView view = manageDeviceViewWeakReference.get();
            if (view == null) {
                return;
            }
            view.dismissProgressDialog();
            view.navigateToGenericResultScreen(true, isCurrentDeviceDeleted);
        }
    };

    ManageDevicePresenter(ManageDeviceView view) {
        manageDeviceViewWeakReference = new WeakReference<>(view);
        manageDevicesInteractor = new ManageDevicesInteractor();
        editNicknameResponseListener.setView(view);
        BMBApplication.getInstance().getDeviceProfilingInteractor().notifyTransaction();
    }

    void requestEditDeviceNickname(Device device, String deviceNickname) {
        manageDevicesInteractor.editDeviceNickname(device, deviceNickname, editNicknameResponseListener);
    }

    void delinkDeviceInvoked(Device deviceDetails, boolean isCurrentDevice) {
        isCurrentDeviceDeleted = isCurrentDevice;
        manageDevicesInteractor.delinkDevice(deviceDetails, delinkDeviceResponseListener);
    }

    void isSurecheckDeviceAvailableInvoked() {
        ManageDeviceView view = manageDeviceViewWeakReference.get();
        if (view == null) {
            return;
        }
        view.navigateToIsSurecheckDeviceAvailableScreen();
    }

    void changePrimaryDevice(Device newPrimaryDevice) {
        manageDevicesInteractor.changePrimaryDevice(newPrimaryDevice, changePrimaryDeviceResponseListener);
    }
}