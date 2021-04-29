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

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.RegisterAccountLimitsActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.registration.services.dto.RegisterAOLProfileResponse;

public class RegisterAccountLimitsActivity extends BaseActivity {

    private RegisterAccountLimitsActivityBinding binding;
    private RegisterAOLProfileResponse registrationAOLResultObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.register_account_limits_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(R.string.register);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            registrationAOLResultObj = (RegisterAOLProfileResponse) extras.getSerializable(AppConstants.RESULT);
            if (registrationAOLResultObj != null) {
                binding.paymentDailyLimitView.setContentText(getString(R.string.currency_with_amount, registrationAOLResultObj.getPaymentLimit()));
                binding.interAccountDailyLimitView.setContentText(getString(R.string.currency_with_amount, registrationAOLResultObj.getInterAccountTransferLimit()));
            } else {
                binding.paymentDailyLimitView.setContentText("R 0.00");
                binding.interAccountDailyLimitView.setContentText("R 100 000.00");
            }
        }
    }
}