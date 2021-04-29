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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.databinding.PreLoginDeviceDelinkActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.PermissionFacade;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.passcode.createPasscode.CreatePasscodeActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;

public class PreLogonDeviceDelink2faActivity extends BaseActivity implements PreLogonDeviceDelinkView {

    private Device device;
    private PreLoginDeviceDelinkActivityBinding binding;
    private PreLogonDeviceDelinkPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.pre_login_device_delink_activity, null, false);
        setContentView(binding.getRoot());

        presenter = new PreLogonDeviceDelinkPresenter(this);

        device = (Device) getIntent().getSerializableExtra(BMBConstants.DEVICE_DECOUPLE_OBJ);
        onPopulateView();
        setToolBarBack(getString(R.string.manage_device));
    }

    private void onPopulateView() {
        mScreenName = BMBConstants.DELINK_DEVICE_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.DELINK_DEVICE_CONST, BMBConstants.SIMPLIFIED_LOGIN_CONST, BMBConstants.TRUE_CONST);

        if (device != null) {
            binding.delinkDeviceButton.setText(R.string.remove);
        }

        populateUIComponents();

        binding.delinkDeviceButton.setOnClickListener(v -> PermissionFacade.requestDeviceStatePermission(PreLogonDeviceDelink2faActivity.this, () -> presenter.delinkDevice(device)));
    }

    private void populateUIComponents() {
        binding.deviceNickname.setText(device.getNickname());
        binding.deviceModel.setText(device.getModel());

        binding.nicknameInputView.setSelectedValue(device.getNickname());

        if (device.getManufacturer() != null) {
            if (!getString(R.string.manage_device_manufacturer_name_apple).equalsIgnoreCase(device.getManufacturer())) {
                binding.deviceManufacturerImageView.setImageResource(R.drawable.ic_device_android);
            } else {
                binding.deviceManufacturerImageView.setImageResource(R.drawable.ic_device_apple);
            }
        } else {
            binding.deviceManufacturerImageView.setBackgroundResource(-1);
        }

        binding.primaryDeviceOptionActionView.setVisibility(device.isPrimarySecondFactorDevice() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cancel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                    .title(getString(R.string.alert_dialog_header))
                    .message(getString(R.string.alert_dialog_text))
                    .positiveDismissListener((dialog, which) -> {
                        AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection);
                        BaseAlertDialog.INSTANCE.dismissAlertDialog();
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        CustomerProfileObject.updateCustomerProfileObject(new CustomerProfileObject());
                        startActivity(intent);
                        finish();
                    }));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void launchCreatePasscodeScreen() {
        Intent passcodeIntent = new Intent(this, CreatePasscodeActivity.class);
        startActivity(passcodeIntent);
    }
}