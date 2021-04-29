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

import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.shared.dto.SecondFactorState;
import com.barclays.absa.banking.express.data.ClientTypeGroupKt;
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesService;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;
import com.barclays.absa.banking.manage.devices.services.dto.Device;

import java.lang.ref.WeakReference;

import static com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS;

class ReasonDeviceNotAvailablePresenter implements ReasonDeviceNotAvailablePresenterInterface {

    private final Device replacementDevice;
    private WeakReference<ReasonDeviceNotAvailableView> view;
    private int[] reasonsDeviceNotAvailable;
    private ManageDevicesService manageDevicesService;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
    private ExtendedResponseListener<ChangePrimaryDeviceResponse> changePrimaryDeviceResponseListener = new ExtendedResponseListener<ChangePrimaryDeviceResponse>() {
        @Override
        public void onSuccess(final ChangePrimaryDeviceResponse response) {
            ReasonDeviceNotAvailableView view = ReasonDeviceNotAvailablePresenter.this.view.get();
            if (view == null) {
                return;
            }
            view.dismissProgressDialog();
            ReasonDeviceNotAvailableView viewDismiss = ReasonDeviceNotAvailablePresenter.this.view.get();
            if (viewDismiss == null) {
                return;
            }
            if (SUCCESS.equalsIgnoreCase(response.getTransactionStatus())) {
                viewDismiss.onPrimaryDeviceChanged(response);
            } else {
                viewDismiss.showMessageError(response.getErrorMessage());
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            ReasonDeviceNotAvailableView view = ReasonDeviceNotAvailablePresenter.this.view.get();
            if (view == null) {
                return;
            }
            view.dismissProgressDialog();
            ReasonDeviceNotAvailableView viewDismiss = ReasonDeviceNotAvailablePresenter.this.view.get();
            if (viewDismiss == null) {
                return;
            }
            viewDismiss.showMessageError(failureResponse.getResponseMessage());
        }
    };

    ReasonDeviceNotAvailablePresenter(ReasonDeviceNotAvailableView reasonNotAvailableView, Device deviceInQuestion, int[] reasonsDeviceIsNotAvailable, ManageDevicesService manageDevicesService) {
        view = new WeakReference<>(reasonNotAvailableView);
        reasonsDeviceNotAvailable = reasonsDeviceIsNotAvailable;
        replacementDevice = deviceInQuestion;
        this.manageDevicesService = manageDevicesService;
        changePrimaryDeviceResponseListener.setView(reasonNotAvailableView);
    }

    @Override
    public String sendReasonForNoSureCheck(int reasonForNoSurecheck) {
        String fillerText;
        switch (reasonForNoSurecheck) {
            case 0:
                fillerText = "lost";
                break;
            case 1:
                fillerText = "stolen";
                break;
            case 2:
                fillerText = "broken";
                break;
            case 3:
                fillerText = "old";
                break;
            case 4:
                fillerText = "deleted app";
                break;
            default:
                fillerText = "old";
                break;
        }
        return fillerText;

    }

    @Override
    public void reasonOptionClicked(int positionClicked) {
        int delinkReasonStringId = reasonsDeviceNotAvailable[positionClicked];
        String fillerText = sendReasonForNoSureCheck(positionClicked);

        final SecureHomePageObject secureObject = appCacheService.getSecureHomePageObject();
        final CustomerProfileObject customerObject = (secureObject == null) ? null : secureObject.getCustomerProfile();

        ReasonDeviceNotAvailableView view = ReasonDeviceNotAvailablePresenter.this.view.get();
        if (view == null) {
            return;
        }
        if (BiometricStatus.shouldAllowIdentifyFlow(CustomerProfileObject.getInstance().getBiometricStatus())) {
            view.navigateToDelinkPrimaryDeviceConfirmationScreen(replacementDevice, delinkReasonStringId, fillerText);
        } else if (customerObject != null && ClientTypeGroupKt.isBusiness(customerObject.getClientTypeGroup())) {
            if (SecondFactorState.SURECHECKV2_SECURITYCODE.getValue() == customerObject.getSecondFactorState()) {
                view.navigateToSecurityCodeActivity();
            } else {
                view.showSecurityCodeRequiredScreen();
            }
        } else {
            view.navigateToDelinkPrimaryDeviceConfirmationScreen(replacementDevice, delinkReasonStringId, fillerText);
        }
    }

    @Override
    public void changePrimaryDevice(String currentDeviceId) {
        manageDevicesService.changePrimaryDevice(currentDeviceId, changePrimaryDeviceResponseListener);
    }
}