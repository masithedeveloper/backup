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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.ManageDeviceResult;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.databinding.VeriticationDeviceAvailableActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.DeviceUtils;

import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_OBJECT;

public class VerificationDeviceAvailableActivity extends ConnectivityMonitorActivity implements View.OnClickListener {
    private static final String TAG = VerificationDeviceAvailableActivity.class.getSimpleName();
    private ManageDevicesInteractor manageDevicesInteractor;
    private Device nominatedPrimaryDevice;

    public static final String IMAGE_RESOURCE_ID = "image_resource_id";

    private SureCheckDelegate sureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                            manageDevicesInteractor.changePrimaryDevice(nominatedPrimaryDevice,
                                    changePrimaryDeviceResponseListener),
                    250);
        }

        @Override
        public void onSureCheckCancelled() {
            super.onSureCheckCancelled(VerificationDeviceAvailableActivity.this);
        }

        @Override
        public void onSureCheckFailed() {
            super.onSureCheckFailed();
        }

        @Override
        public void onSureCheckRejected() {
            Intent deviceDelinkingLinkingFailedIntent = new Intent(VerificationDeviceAvailableActivity.this, GenericResultActivity.class);
            deviceDelinkingLinkingFailedIntent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
            deviceDelinkingLinkingFailedIntent.putExtra(GenericResultActivity.IS_FAILURE, true);
            deviceDelinkingLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_delink_failure_message);
            deviceDelinkingLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.device_linking_error_explanation);
            deviceDelinkingLinkingFailedIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.cancel);
            deviceDelinkingLinkingFailedIntent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.manage_devices);
            GenericResultActivity.topOnClickListener = v -> {
                mScreenName = mSiteSection = BMBConstants.MANAGE_DEVICE;
                AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);
                Intent deviceSelectIntent = new Intent(VerificationDeviceAvailableActivity.this, DeviceListActivity.class);
                deviceSelectIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(IntentFactory.getManageDevices(VerificationDeviceAvailableActivity.this));
            };

            GenericResultActivity.bottomOnClickListener = v -> {
                Intent deviceSelectIntent = new Intent(VerificationDeviceAvailableActivity.this, DeviceListActivity.class);
                deviceSelectIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(deviceSelectIntent);
                finish();
            };
            deviceDelinkingLinkingFailedIntent.putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.helpline_number));
            deviceDelinkingLinkingFailedIntent.putExtra(BMBConstants.POST_LOGIN_LAYOUT, true);
            startActivity(deviceDelinkingLinkingFailedIntent);
        }
    };

    private ExtendedResponseListener<ChangePrimaryDeviceResponse> changePrimaryDeviceResponseListener = new ExtendedResponseListener<ChangePrimaryDeviceResponse>() {
        @Override
        public void onSuccess(final ChangePrimaryDeviceResponse successResponse) {
            dismissProgressDialog();
            if (successResponse.getDeviceChanged()) {
                showPrimaryDeviceChangeSuccessfulScreen(VerificationDeviceAvailableActivity.this, nominatedPrimaryDevice);
            } else {
                final String sureCheckFlag = successResponse.getSureCheckFlag();
                if (sureCheckFlag == null || sureCheckFlag.isEmpty()) {
                /* TODO server responded with
                        "txnStatus": "Failure",
                        "txnMessage": "Security/AccessControl/Error/AccountOnHold"
                        */
                    String status = successResponse.getTransactionStatus();
                    if (status != null) {
                        if (status.toLowerCase().equals(BMBConstants.FAILURE) && successResponse.getTransactionMessage() != null) {
                            BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                                    .title(getString(R.string.manage_device_server_failure))
                                    .message(successResponse.getTransactionMessage())
                                    .build());
                        }
                    }
                } else {
                    BMBLogger.d(TAG, sureCheckFlag);
                    TransactionVerificationType verificationType = TransactionVerificationType.valueOf(sureCheckFlag);
                    getAppCacheService().setSureCheckReferenceNumber(successResponse.getReferenceNumber());
                    getAppCacheService().setSureCheckCellphoneNumber(successResponse.getCellnumber());
                    getAppCacheService().setSureCheckEmail(successResponse.getEmail());
                    switch (verificationType) {
                        case SURECHECKV1:
                        case SURECHECKV1Required:
                        case SURECHECKV1_FALLBACKRequired:
                        case SURECHECKV1_FALLBACK:
                        case SURECHECKV2:
                        case SURECHECKV2_FALLBACK:
                        case SecurityCode:
                        case NotNeeded:
                        case Failed:
                            break;
                        case SURECHECKV2Required:
                            goToSureCheckCountDownScreen(verificationType);
                            break;
                        case SURECHECKV2_FALLBACKRequired:
                            break;
                        case NoPrimaryDevice:
                            showNoPrimaryDeviceScreen();
                            break;
                    }
                }
            }
        }
    };

    public void showPrimaryDeviceChangeSuccessfulScreen(final BaseActivity activity, Device nominatedPrimaryDevice) {
        Intent deviceDelinkingSuccessIntent = new Intent(activity, GenericResultActivity.class);
        deviceDelinkingSuccessIntent.putExtra(IMAGE_RESOURCE_ID, R.drawable.ic_lock);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.IS_SUCCESS, true);
        if (DeviceUtils.isCurrentDevice(nominatedPrimaryDevice)) {
            getAppCacheService().setPrimarySecondFactorDevice(true);
        } else {
            String deviceNickName = nominatedPrimaryDevice.getNickname();
            deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.DEVICE_NICKNAME, deviceNickName);
        }

        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.this_is_now_your_surecheck_2_0_device);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.surecheck_2_0_instruction);

        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.manage_devices);
        GenericResultActivity.topOnClickListener = v -> {
            Intent deviceSelectIntent = new Intent(activity, DeviceListActivity.class);
            deviceSelectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(deviceSelectIntent);
            activity.finish();
        };

        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        GenericResultActivity.bottomOnClickListener = v -> activity.loadAccountsAndShowHomeScreenWithAccountsList();

        final Device delinkedPrimaryDevice = getAppCacheService().getDelinkedPrimaryDevice();
        if (delinkedPrimaryDevice != null) {
            deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, -1);// hide it
            deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close);

            // Delink
            GenericResultActivity.bottomOnClickListener = v -> new ManageDevicesInteractor().delinkDevice(delinkedPrimaryDevice,
                    new ExtendedResponseListener<ManageDeviceResult>(activity) {

                        @Override
                        public void onSuccess(ManageDeviceResult successResponse) {
                            getBaseView().dismissProgressDialog();
                            activity.logoutAndGoToStartScreen();
                        }

                        @Override
                        public void onFailure(final ResponseObject failureResponse) {
                            getBaseView().dismissProgressDialog();
                            BaseAlertDialog.INSTANCE.showErrorAlertDialog(failureResponse.getErrorMessage(), (dialog, which) -> activity.logoutAndGoToStartScreen());
                        }
                    });
        }

        deviceDelinkingSuccessIntent.putExtra(BMBConstants.POST_LOGIN_LAYOUT, true);
        activity.startActivity(deviceDelinkingSuccessIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changePrimaryDeviceResponseListener.setView(this);
        VeriticationDeviceAvailableActivityBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.veritication_device_available_activity, null, false);
        setContentView(binding.getRoot());

        binding.btnYes.setOnClickListener(this);
        binding.btnNo.setOnClickListener(this);
        nominatedPrimaryDevice = (Device) getIntent().getSerializableExtra(DEVICE_OBJECT);
        manageDevicesInteractor = new ManageDevicesInteractor();
        if (getAppCacheService().isPrimarySecondFactorDevice()) {
            onClick(binding.btnYes);
        }
    }

    @Override
    public void onClick(View v) {
        preventDoubleClick(v);
        switch (v.getId()) {
            case R.id.btn_yes:
                showProgressDialog();
                manageDevicesInteractor.changePrimaryDevice(nominatedPrimaryDevice, changePrimaryDeviceResponseListener);
                break;
            case R.id.btn_no:
                goToPrimaryDeviceNotAvailableReasonsScreen();
                break;
            default:
                break;
        }
    }

    private void goToPrimaryDeviceNotAvailableReasonsScreen() {
        Intent reasonPrimaryDeviceNotAvailableIntent = new Intent(this, ReasonDeviceNotAvailable2faActivity.class);
        reasonPrimaryDeviceNotAvailableIntent.putExtra(DEVICE_OBJECT, nominatedPrimaryDevice);
        reasonPrimaryDeviceNotAvailableIntent.putExtra(BMBConstants.POST_LOGIN_LAYOUT, true);
        startActivity(reasonPrimaryDeviceNotAvailableIntent);
    }

    private void goToSureCheckCountDownScreen(TransactionVerificationType verificationType) {
        if (verificationType == TransactionVerificationType.SURECHECKV2Required) {
            sureCheckDelegate.initiateV2CountDownScreen();
        } else if (verificationType == TransactionVerificationType.SURECHECKV2_FALLBACKRequired) {
            sureCheckDelegate.initiateOfflineOtpScreen();
        }
    }
}