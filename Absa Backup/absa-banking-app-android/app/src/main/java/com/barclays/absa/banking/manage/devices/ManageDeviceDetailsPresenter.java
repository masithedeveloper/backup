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
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.manage.devices.services.dto.DeviceListResponse;
import com.barclays.absa.utils.ProfileManager;

class ManageDeviceDetailsPresenter implements ManageDeviceDetailsPresenterInterface {

    private boolean isCurrentDeviceDeletion;
    private final ManageDeviceDetailsView view;
    private final ManageDevicesInteractor manageDevicesInteractor;

    private final ExtendedResponseListener<DeviceListResponse> deviceListResponseListener = new ExtendedResponseListener<DeviceListResponse>() {
        @Override
        public void onSuccess(final DeviceListResponse deviceList) {
            view.dismissProgressDialog();
            view.navigateToDeviceListScreen(deviceList);
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view.dismissProgressDialog();
            view.showError(failureResponse.getErrorMessage());
        }

    };

    private final ExtendedResponseListener<ManageDeviceResult> delinkDeviceResponseListener = new ExtendedResponseListener<ManageDeviceResult>() {
        @Override
        public void onSuccess(ManageDeviceResult successResponse) {
            if (isCurrentDeviceDeletion) {
                UserProfile userProfile = ProfileManager.getInstance().getActiveUserProfile();
                if (userProfile != null) {
                    ProfileManager.getInstance().deleteProfile(userProfile, new ProfileManager.SimpleCallback() {

                        @Override
                        public void onSuccess() {
                            view.dismissProgressDialog();
                            view.navigateToGenericResultScreen(false);
                        }

                        @Override
                        public void onFailure() {
                            view.dismissProgressDialog();
                            view.navigateToGenericResultScreen(true);
                        }
                    });
                } else {
                    view.dismissProgressDialog();
                    view.navigateToGenericResultScreen(false);
                }
            } else {
                view.dismissProgressDialog();
                view.navigateToGenericResultScreen(false);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            view.dismissProgressDialog();
            view.navigateToGenericResultScreen(true);
        }
    };

    ManageDeviceDetailsPresenter(ManageDeviceDetailsView view, ManageDevicesInteractor manageDevicesInteractor) {
        this.view = view;
        this.manageDevicesInteractor = manageDevicesInteractor;
        deviceListResponseListener.setView(view);
        delinkDeviceResponseListener.setView(view);
    }

    @Override
    public void delinkDeviceInvoked(Device deviceDetails, boolean isCurrentDevice) {
        isCurrentDeviceDeletion = isCurrentDevice;
        manageDevicesInteractor.delinkDevice(deviceDetails, delinkDeviceResponseListener);
    }

    @Override
    public void requestDeviceListInvoked() {
        manageDevicesInteractor.pullDeviceList(deviceListResponseListener);
    }

    @Override
    public void editNicknameInvoked() {
        view.navigateToEditNicknameScreen();
    }

    @Override
    public void isSurecheckDeviceAvailableInvoked() {
        view.navigateToIsSurecheckDeviceAvailableScreen();
    }

}