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

package com.barclays.absa.banking.manage.devices.linking;

import com.barclays.absa.banking.boundary.model.ManageDeviceResult;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.Device;

class PreLogonDeviceDelinkPresenter {

    private ManageDevicesInteractor manageDevicesInteractor;

    PreLogonDeviceDelinkPresenter(PreLogonDeviceDelinkView view) {
        manageDevicesInteractor = new ManageDevicesInteractor();
        delinkDeviceResponseListener.setView(view);
    }

    private final ExtendedResponseListener<ManageDeviceResult> delinkDeviceResponseListener = new ExtendedResponseListener<ManageDeviceResult>() {
        @Override
        public void onSuccess(final ManageDeviceResult successResponse) {
            final PreLogonDeviceDelinkView view = (PreLogonDeviceDelinkView) viewWeakReference.get();
            if (view == null) {
                return;
            }
            view.dismissProgressDialog();
            if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getStatusMessage())) {
                view.launchCreatePasscodeScreen();
            }
        }
    };

    void delinkDevice(Device device) {
        manageDevicesInteractor.delinkDevice(device, delinkDeviceResponseListener);
    }
}