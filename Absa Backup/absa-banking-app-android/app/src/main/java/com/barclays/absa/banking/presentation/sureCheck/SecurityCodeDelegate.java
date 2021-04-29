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
package com.barclays.absa.banking.presentation.sureCheck;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;
import com.barclays.absa.banking.presentation.shared.widget.InformationDialogFragment;
import com.barclays.absa.banking.presentation.sureCheckV2.SecurityCodeActivity;
import com.barclays.absa.banking.shared.ActionHandler;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.crypto.SecureUtils;

public abstract class SecurityCodeDelegate {

    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    protected SecurityCodeDelegate() {
        appCacheService.setSecurityCodeDelegate(this);
    }

    public abstract void onSuccess();

    public void onCancel() {
    }

    public abstract void onFailure(String errorMessage);

    static void changePrimary(@NonNull final BaseActivity activity, @NonNull final DialogInterface.OnDismissListener dismissListener) {
        String deviceImei = SecureUtils.INSTANCE.getDeviceID();
        ManageDevicesInteractor manageDevicesInteractor = new ManageDevicesInteractor();
        manageDevicesInteractor.changePrimaryDevice(deviceImei, new ExtendedResponseListener<ChangePrimaryDeviceResponse>(activity) {
            @Override
            public void onSuccess(ChangePrimaryDeviceResponse successResponse) {
                if (successResponse.getDeviceChanged()) {
                    InformationDialogFragment.showDismissAlertDialog(activity,
                            activity.getString(R.string.ok),
                            activity.getString(R.string.security_code_et_title),
                            activity.getString(R.string.this_device_is_now_your_primary),
                            dismissListener);
                }
            }

            @Override
            public void onFailure(ResponseObject failureResponse) {
                BaseAlertDialog.INSTANCE.showErrorAlertDialog(failureResponse.getErrorMessage());
            }
        });
    }

    public static void handleSecurityCodeCaseAndChangePrimary(final BaseActivity activity) {
        IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

        SecurityCodeDelegate securityCodeDelegate = new SecurityCodeDelegate() {
            @Override
            public void onSuccess() {
                activity.dismissProgressDialog();
                changePrimary(activity, dialog -> activity.finish());
            }

            @Override
            public void onFailure(String errorMessage) {
                activity.dismissProgressDialog();
                BaseAlertDialog.INSTANCE.showErrorAlertDialog(errorMessage);
            }
        };
        appCacheService.setSecurityCodeDelegate(securityCodeDelegate);
        activity.startActivity(new Intent(activity, SecurityCodeActivity.class));
    }

    static void handleSecurityCodeCaseChangePrimaryAndRetriggerAction(final BaseActivity activity, final ActionHandler retriggerHandler) {
        IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

        SecurityCodeDelegate securityCodeDelegate = new SecurityCodeDelegate() {
            @Override
            public void onSuccess() {
                activity.dismissProgressDialog();
                changePrimary(activity, dialog -> {
                    activity.finish();
                    retriggerHandler.performAction();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                activity.dismissProgressDialog();
                BaseAlertDialog.INSTANCE.showErrorAlertDialog(errorMessage);
            }
        };
        appCacheService.setSecurityCodeDelegate(securityCodeDelegate);
        navigateToSecurityCodeScreen(activity);
    }

    private static void navigateToSecurityCodeScreen(BaseActivity activity) {
        activity.startActivity(new Intent(activity, SecurityCodeActivity.class));
    }

    public static void handleSecurityCode(final BaseActivity activity, final ActionHandler actionHandler) {
        IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

        SecurityCodeDelegate securityCodeDelegate = new SecurityCodeDelegate() {
            @Override
            public void onSuccess() {
                if (activity != null) {
                    activity.dismissProgressDialog();
                }
                if (actionHandler != null) {
                    actionHandler.performAction();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                activity.dismissProgressDialog();
                BaseAlertDialog.INSTANCE.showErrorAlertDialog(errorMessage);
            }
        };
        appCacheService.setSecurityCodeDelegate(securityCodeDelegate);
        navigateToSecurityCodeScreen(activity);
    }

}