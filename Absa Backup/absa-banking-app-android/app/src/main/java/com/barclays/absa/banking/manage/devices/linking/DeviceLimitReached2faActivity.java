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

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.databinding.DeviceLimitReachedActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils;
import com.barclays.absa.banking.presentation.shared.widget.RecyclerViewClickListener;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;

import java.util.List;

public class DeviceLimitReached2faActivity extends BaseActivity {
    private List<Device> deviceList;
    private SecureHomePageObject secureHomePageObject;
    private DeviceLimitReachedActivityBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.device_limit_reached_activity, null, false);
        setContentView(binding.getRoot());

        mScreenName = BMBConstants.DEVICE_LIMIT_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.DEVICE_LIMIT_CONST,
                BMBConstants.SIMPLIFIED_LOGIN_CONST, BMBConstants.TRUE_CONST);

        deviceList = getAppCacheService().getCreate2faAliasResponse().getDeviceList();
        secureHomePageObject = getAppCacheService().getSecureHomePageObject();

        onPopulateView();
        RebuildUtils.setupToolBar(this, null, R.drawable.ic_arrow_back_white, false, null);
    }

    private void onPopulateView() {
        if (deviceList != null) {
            RecyclerView.Adapter deviceItemAdapter = new RemoveDeviceListItemAdapter(deviceList);
            if (deviceList.size() > 0) {
                binding.devicesRecyclerView.setAdapter(deviceItemAdapter);
                binding.devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            }

            binding.devicesRecyclerView.addOnItemTouchListener(new RecyclerViewClickListener(this, (view, position) -> {
                Intent deviceLinkIntent = new Intent(DeviceLimitReached2faActivity.this, PreLogonDeviceDelink2faActivity.class);
                deviceLinkIntent.putExtra(BMBConstants.DEVICE_DECOUPLE_OBJ, deviceList.get(position));
                startActivity(deviceLinkIntent);
            }));
        }
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
}