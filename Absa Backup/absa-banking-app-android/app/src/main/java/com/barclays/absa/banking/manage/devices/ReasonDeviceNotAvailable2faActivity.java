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

import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.ReasonDeviceNotAvailable2faActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.manage.devices.linking.DelinkCurrentPrimaryDevice2faActivity;
import com.barclays.absa.banking.manage.devices.services.ManageDevicesInteractor;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult;
import com.barclays.absa.banking.presentation.sureCheck.SecurityCodeDelegate;
import com.barclays.absa.banking.presentation.sureCheckV2.SecurityCodeActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.crypto.SecureUtils;

import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_OBJECT;

public class ReasonDeviceNotAvailable2faActivity extends BaseActivity implements ReasonDeviceNotAvailableView {

    private ReasonDeviceNotAvailablePresenterInterface reasonDeviceNotAvailablePresenter;
    private ReasonDeviceNotAvailable2faActivityBinding binding;
    private final static String FILLER_TEXT = "fillerText";
    private final static String DELINK_REASON = "delinkReason";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.reason_device_not_available_2fa_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(getString(R.string.sure_check_device_not_available_title));
        final String[] reasonsDeviceNotAvailable = fillReasonsDeviceNotAvailable();
        final int[] reasonsDeviceIsNotAvailable = loadReasonsDeviceNotAvailable();

        binding.deviceNotAvailableRecyclerView.setAdapter(new ReasonDeviceNotAvailableAdapter(reasonsDeviceNotAvailable, this));
        binding.deviceNotAvailableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Device deviceInQuestion = (Device) getIntent().getSerializableExtra(DEVICE_OBJECT);
        reasonDeviceNotAvailablePresenter = new ReasonDeviceNotAvailablePresenter(this, deviceInQuestion, reasonsDeviceIsNotAvailable, new ManageDevicesInteractor());
    }

    @Override
    public String[] fillReasonsDeviceNotAvailable() {
        return new String[]{
                getResources().getString(R.string.reason_device_lost),
                getResources().getString(R.string.reason_device_stolen),
                getResources().getString(R.string.reason_device_broken),
                getResources().getString(R.string.reason_new_device),
                getResources().getString(R.string.reason_reinstall_deleted)};
    }

    public int[] loadReasonsDeviceNotAvailable() {
        return new int[]{
                R.string.reason_device_lost,
                R.string.reason_device_stolen,
                R.string.reason_device_broken,
                R.string.reason_new_device,
                R.string.reason_reinstall_deleted
        };
    }

    @Override
    public void onPrimaryDeviceChanged(ResponseObject response) {
        Intent deviceDelinkingSuccessIntent = new Intent(ReasonDeviceNotAvailable2faActivity.this, GenericResultActivity.class);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.IS_SUCCESS, true);
        deviceDelinkingSuccessIntent.putExtra(VerificationDeviceAvailableActivity.IMAGE_RESOURCE_ID, R.drawable.ic_lock);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.this_is_now_your_surecheck_2_0_device);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.surecheck_2_0_passcode_explanation);

        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();
    }

    @Override
    public void navigateToDelinkPrimaryDeviceConfirmationScreen(Device replacementDevice, int delinkReason, String fillerText) {
        final Intent intent = new Intent(this, DelinkCurrentPrimaryDevice2faActivity.class);
        intent.putExtra(FILLER_TEXT, fillerText);
        intent.putExtra(DELINK_REASON, delinkReason);
        intent.putExtra(DEVICE_OBJECT, replacementDevice);
        startActivity(intent);
        finish();
    }

    private final SecurityCodeDelegate securityCodeDelegate = new SecurityCodeDelegate() {
        @Override
        public void onSuccess() {
            changePrimaryDevice();
        }

        @Override
        public void onFailure(String errorMessage) {
            dismissProgressDialog();
            BaseAlertDialog.INSTANCE.showErrorAlertDialog(errorMessage);
        }
    };

    private void changePrimaryDevice() {
        String currentDeviceId = SecureUtils.INSTANCE.getDeviceID();
        reasonDeviceNotAvailablePresenter.changePrimaryDevice(currentDeviceId);
    }

    @Override
    public void navigateToSecurityCodeActivity() {
        getAppCacheService().setSecurityCodeDelegate(securityCodeDelegate);
        startActivity(new Intent(this, SecurityCodeActivity.class));
        finish();
    }

    @Override
    public void onReasonSelected(int position) {
        reasonDeviceNotAvailablePresenter.reasonOptionClicked(position);
    }

    @Override
    public void showSecurityCodeRequiredScreen() {
        @StringRes int securityCodeMessageToUse;
        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (featureSwitchingToggles.getSecurityCodeAvailableOnATM() == FeatureSwitchingStates.ACTIVE.getKey()) {
            securityCodeMessageToUse = R.string.passcode_revoked_description_atm;
        } else {
            securityCodeMessageToUse = R.string.passcode_revoked_description;
        }

        IntentFactory.IntentBuilder intentBuilder = IntentFactoryGenericResult.getGenericResultFailureBuilder(this);
        intentBuilder.setGenericResultHeaderMessage(R.string.security_code_required)
                .setGenericResultSubMessage(securityCodeMessageToUse)
                .setGenericResultIconToError()
                .setGenericResultBottomButton(R.string.ok_got_it, v -> {
                    Class destinationClass = getAppCacheService().isLinkingFlow() ? WelcomeActivity.class : DeviceListActivity.class;
                    Intent intent = new Intent(ReasonDeviceNotAvailable2faActivity.this, destinationClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
        Intent securityCodeRequiredIntent = intentBuilder.build();
        startActivity(securityCodeRequiredIntent);
    }
}