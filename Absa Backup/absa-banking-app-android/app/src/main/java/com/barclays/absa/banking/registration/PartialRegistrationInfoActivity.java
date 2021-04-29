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
package com.barclays.absa.banking.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.databinding.RegistrationFlowPartialNoticeActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.SharedPreferenceService;

public class PartialRegistrationInfoActivity extends BaseActivity implements View.OnClickListener {

    private RegisterProfileDetail registerProfileDetailObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RegistrationFlowPartialNoticeActivityBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.registration_flow_partial_notice_activity, null, false);
        setContentView(binding.getRoot());

        BMBLogger.d("x-class", "isPartial = " + SharedPreferenceService.INSTANCE.isPartialRegistration());
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            registerProfileDetailObj = (RegisterProfileDetail) extras.getSerializable(getString(R.string.register_profile_detail_obj));
        }
        binding.continueButton.setOnClickListener(this);
        setToolBarBack("");
    }

    @Override
    public void onClick(View v) {
        Intent launchRegisterDetailsIntent = new Intent(this, RegisterActivity.class);
        launchRegisterDetailsIntent.putExtra(getString(R.string.register_profile_detail_obj), registerProfileDetailObj);
        startActivity(launchRegisterDetailsIntent);
        finish();
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
            CommonUtils.showAlertDialogWelcomeScreen(this);
        }
        return super.onOptionsItemSelected(item);
    }
}

