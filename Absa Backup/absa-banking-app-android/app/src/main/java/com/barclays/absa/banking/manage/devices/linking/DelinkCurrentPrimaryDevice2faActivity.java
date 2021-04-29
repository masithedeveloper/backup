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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.ManageDeviceResult;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.shared.dto.SecondFactorState;
import com.barclays.absa.banking.express.data.ClientTypeGroupKt;
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.linking.ui.LinkingActivity;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.manage.devices.services.dto.DeviceListResponse;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils;
import com.barclays.absa.banking.presentation.sureCheck.SecurityCodeDelegate;
import com.barclays.absa.banking.presentation.sureCheckV2.SecurityCodeActivity;
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckDevicePasscode2faActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.crypto.SecureUtils;

import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_OBJECT;
import static com.barclays.absa.banking.policy_beneficiaries.services.ManageBeneficiaryService.ID_NUMBER;

public class DelinkCurrentPrimaryDevice2faActivity extends BaseActivity implements DelinkCurrentSurecheckDeviceView, View.OnClickListener {

    private ManageDevicesInteractor manageDevicesInteractor = new ManageDevicesInteractor();
    private FeatureSwitching featureSwitching;
    private final String DELINK_REASON_KEY = "delinkReason";
    private final String FILLER_TEXT_KEY = "fillerText";

    private final SecurityCodeDelegate securityCodeDelegate = new SecurityCodeDelegate() {
        @Override
        public void onSuccess() {
            if (!getAppCacheService().isBioAuthenticated()) {
                changePrimaryDevice();
            }
        }

        @Override
        public void onFailure(String errorMessage) {
            dismissProgressDialog();
            BaseAlertDialog.INSTANCE.showErrorAlertDialog(errorMessage);
        }
    };

    private final ExtendedResponseListener<ManageDeviceResult> delinkDeviceListener = new ExtendedResponseListener<ManageDeviceResult>() {
        @Override
        public void onSuccess(ManageDeviceResult successResponse) {
            dismissProgressDialog();
            final SecureHomePageObject secureObject = getAppCacheService().getSecureHomePageObject();
            final CustomerProfileObject customerObject = secureObject == null ? null : secureObject.getCustomerProfile();
            if (BiometricStatus.shouldAllowIdentifyFlow(CustomerProfileObject.getInstance().getBiometricStatus())) {
                getAppCacheService().setChangePrimaryDeviceFlowFromSureCheck(true);
                getAppCacheService().setChangePrimaryDeviceFlowFailOver(true);
                Intent intent = new Intent(DelinkCurrentPrimaryDevice2faActivity.this, LinkingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                intent.putExtra(ID_NUMBER, CustomerProfileObject.getInstance().getIdNumber());
                startActivity(intent);
            } else if (customerObject != null && ClientTypeGroupKt.isBusiness(customerObject.getClientTypeGroup())) {
                // business client
                if (customerObject.getSecondFactorState() == SecondFactorState.SURECHECKV2_SECURITYCODE.ordinal()) {
                    // Enter Security Code
                    getAppCacheService().setSecurityCodeDelegate(securityCodeDelegate);
                    startActivity(new Intent(DelinkCurrentPrimaryDevice2faActivity.this, SecurityCodeActivity.class));
                } else if (customerObject.getSecondFactorState() == SecondFactorState.SURECHECKV2_NOPRIMARYDEVICE.ordinal()) {
                    GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();

                    Intent intent = new Intent(DelinkCurrentPrimaryDevice2faActivity.this, GenericResultActivity.class);
                    intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
                    intent.putExtra(GenericResultActivity.IS_FAILURE, true);
                    intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_failed);
                    intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
                    startActivity(intent);
                }
            } else {
                if (getAppCacheService().isBioAuthenticated()) {
                    Intent intent = new Intent(DelinkCurrentPrimaryDevice2faActivity.this, LinkingActivity.class);
                    startActivity(intent);
                } else if (featureSwitching.getBiometricVerification() == FeatureSwitchingStates.DISABLED.getKey() || !BiometricStatus.shouldAllowIdentifyFlow(CustomerProfileObject.getInstance().getBiometricStatus())) {
                    navigateToEnterSurecheckDevicePasscode();
                } else {
                    getAppCacheService().setChangePrimaryDeviceFlowFailOver(true);
                    getAppCacheService().setChangePrimaryDeviceFlowFromSureCheck(true);
                    Intent intent = new Intent(DelinkCurrentPrimaryDevice2faActivity.this, LinkingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    intent.putExtra(ID_NUMBER, CustomerProfileObject.getInstance().getIdNumber());
                    startActivity(intent);
                }
            }
        }
    };

    private final ExtendedResponseListener<DeviceListResponse> deviceListExtendedResponseListener = new ExtendedResponseListener<DeviceListResponse>() {
        @Override
        public void onSuccess(DeviceListResponse successResponse) {
            Device device = findPrimaryDevice(successResponse);
            if (device != null) {
                getAppCacheService().setCurrentPrimaryDevice(device);
                manageDevicesInteractor.delinkDevice(device, delinkDeviceListener);
            } else {
                dismissProgressDialog();
                if (getAppCacheService().isBioAuthenticated()) {
                    Intent intent = new Intent(DelinkCurrentPrimaryDevice2faActivity.this, LinkingActivity.class);
                    startActivity(intent);
                } else if (featureSwitching.getBiometricVerification() == FeatureSwitchingStates.DISABLED.getKey() || !BiometricStatus.shouldAllowIdentifyFlow(CustomerProfileObject.getInstance().getBiometricStatus())) {
                    navigateToEnterSurecheckDevicePasscode();
                } else {
                    getAppCacheService().setChangePrimaryDeviceFlowFailOver(true);
                    getAppCacheService().setChangePrimaryDeviceFlowFromSureCheck(true);
                    Intent intent = new Intent(DelinkCurrentPrimaryDevice2faActivity.this, LinkingActivity.class);
                    intent.putExtra(ID_NUMBER, CustomerProfileObject.getInstance().getIdNumber());
                    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    startActivity(intent);
                }
            }
        }
    };

    private final ExtendedResponseListener<ChangePrimaryDeviceResponse> genericValidationExtendedResponseListener = new ExtendedResponseListener<ChangePrimaryDeviceResponse>() {
        @Override
        public void onSuccess(final ChangePrimaryDeviceResponse successResponse) {
            dismissProgressDialog();

            if (SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                getAppCacheService().setPrimarySecondFactorDevice(true);
                Intent deviceDelinkingSuccessIntent = new Intent(DelinkCurrentPrimaryDevice2faActivity.this, GenericResultActivity.class);
                deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_lock);
                deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.this_is_now_your_surecheck_2_0_device);
                deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.surecheck_2_0_passcode_explanation);
                deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.IS_SUCCESS, true);

                deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
                GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();
            } else {
                showMessageError(successResponse.getTransactionMessage());
            }
        }
    };

    private void changePrimaryDevice() {
        String currentDeviceId = SecureUtils.INSTANCE.getDeviceID();
        if (getAppCacheService().isBioAuthenticated()) {
            Intent intent = new Intent(DelinkCurrentPrimaryDevice2faActivity.this, LinkingActivity.class);
            startActivity(intent);
        } else if (featureSwitching.getBiometricVerification() == FeatureSwitchingStates.DISABLED.getKey() || !BiometricStatus.shouldAllowIdentifyFlow(CustomerProfileObject.getInstance().getBiometricStatus())) {
            manageDevicesInteractor.changePrimaryDevice(currentDeviceId, genericValidationExtendedResponseListener);
        } else {
            getAppCacheService().setChangePrimaryDeviceFlowFailOver(true);
            getAppCacheService().setChangePrimaryDeviceFlowFromSureCheck(true);
            Intent intent = new Intent(DelinkCurrentPrimaryDevice2faActivity.this, LinkingActivity.class);
            intent.putExtra(ID_NUMBER, CustomerProfileObject.getInstance().getIdNumber());
            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delinkDeviceListener.setView(this);
        deviceListExtendedResponseListener.setView(this);
        genericValidationExtendedResponseListener.setView(this);

        setContentView(R.layout.activity_2fa_delink_current_surecheck);
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        featureSwitching = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();

        Button buttonConfirmDelink = findViewById(R.id.btn_delinkSecureDevice);
        buttonConfirmDelink.setOnClickListener(this);
        RebuildUtils.setupToolBar(this, null, R.drawable.ic_arrow_back_white, false, view -> getAppCacheService().setIsForgotPrimaryDeviceButtonClicked(false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sure_check_make_current_device_primary, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel) {
            getAppCacheService().setIsForgotPrimaryDeviceButtonClicked(false);
            showCancelFlowDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        getAppCacheService().setIsForgotPrimaryDeviceButtonClicked(false);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        getAppCacheService().setChangePrimaryDeviceFlow(true);
        String fillerText = getIntent().getStringExtra(FILLER_TEXT_KEY);
        manageDevicesInteractor.delinkCurrentPrimaryDevice(fillerText, delinkDeviceListener);
    }

    @Override
    public void navigateToEnterSurecheckDevicePasscode() {
        final Intent intent = new Intent(this, SureCheckDevicePasscode2faActivity.class);
        intent.putExtra(FILLER_TEXT_KEY, getIntent().getStringExtra(FILLER_TEXT_KEY));
        intent.putExtra(DELINK_REASON_KEY, getIntent().getIntExtra(DELINK_REASON_KEY, 0));
        intent.putExtra(DEVICE_OBJECT, getIntent().getSerializableExtra(DEVICE_OBJECT));
        startActivity(intent);
        finish();
    }

    @Override
    public void cancelSureCheckDeviceDelink() {
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }

    private void showCancelFlowDialog() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.sure_check_cancel_flow_message_title))
                .message(getString(R.string.sure_check_cancel_flow_message))
                .positiveDismissListener((dialog, which) -> {
                    AnalyticsUtils.getInstance().trackCustomScreenView("DelinkCurrentPrimaryDevice2faActivity", mScreenName, BMBConstants.TRUE_CONST);
                    if (getAppCacheService().isLinkingFlow()) {
                        goToLaunchScreen(this);
                    } else {
                        finish();
                    }
                }));

    }
}