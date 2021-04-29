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

package com.barclays.absa.banking.passcode.passcodeLogin;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.SureCheckFailedForgotPasscodeActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;

public class SureCheckFailedForgotPasscode extends BaseActivity implements View.OnClickListener {

    private SureCheckFailedForgotPasscodeActivityBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.sure_check_failed_forgot_passcode_activity, null, false);
        setContentView(binding.getRoot());

        binding.okGotItButton.setOnClickListener(this);
        setupToolbar();
        binding.contactView.setContact(TelephoneUtil.getCallCentreContact(this));
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) binding.toolbar.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.forgot_passcode);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}