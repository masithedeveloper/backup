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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.Activity2faPasscodeRevokedBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.sureCheckV2.SecurityCodeActivity;

public class PasscodeRevokedActivity extends BaseActivity implements PasscodeRevokedView, View.OnClickListener {

    private Activity2faPasscodeRevokedBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_passcode_revoked, null, false);
        setContentView(binding.getRoot());

        binding.btnEnterSecurityCode.setOnClickListener(this);
        String name = getString(R.string.linking_please_contact);
        String number = getString(R.string.support_contact_number);
        binding.contactView.setContact(name, number);
        setLayout();
        setupToolbar();
    }

    private void setLayout() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(BMBConstants.POST_LOGIN_LAYOUT)) {
            binding.ivRevokedIcon.setImageResource(R.drawable.ic_question_mark);
            binding.btnEnterSecurityCode.setText(R.string.ok_got_it);
            binding.tvPasscodeRevoked.setText(R.string.forgot_passcode_text);
            binding.tvDescriptionText.setText(R.string.passcode_2fa_revoked_description);
            binding.tvNotReceivingSureCheck.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        navigateToEnterSecureCode();
    }

    @Override
    public void navigateToEnterSecureCode() {
        startActivity(new Intent(this, SecurityCodeActivity.class));
        finish();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) binding.toolbar.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.forgot_passcode);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}