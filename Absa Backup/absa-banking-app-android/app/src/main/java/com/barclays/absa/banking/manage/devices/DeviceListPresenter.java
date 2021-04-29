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

import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.DeviceListResponse;

import java.lang.ref.WeakReference;

public class DeviceListPresenter {

    private WeakReference<DeviceListView> viewWeakReference;
    private ManageDevicesInteractor manageDevicesInteractor;

    private ExtendedResponseListener<DeviceListResponse> responseListener = new ExtendedResponseListener<DeviceListResponse>() {
        @Override
        public void onSuccess(final DeviceListResponse deviceList) {
            DeviceListView view = (DeviceListView) viewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.populateDeviceList(deviceList);
                return;
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            DeviceListView view = (DeviceListView) viewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
            }
        }
    };

    DeviceListPresenter(DeviceListView view) {
        viewWeakReference = new WeakReference<>(view);
        responseListener.setView(view);
        manageDevicesInteractor = new ManageDevicesInteractor();
    }

    public void requestDeviceList() {
        manageDevicesInteractor.pullDeviceList(responseListener);
    }
}