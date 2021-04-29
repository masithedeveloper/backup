/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.funeralCover.ui;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.ActivityInsuranceManagePaymentDetailsBinding;
import com.barclays.absa.banking.framework.BaseActivity;

import java.util.HashMap;

import styleguide.bars.ProgressIndicatorView;
import styleguide.utils.extensions.StringExtensions;

public class InsuranceManagePaymentDetailsActivity extends BaseActivity {
    private ActivityInsuranceManagePaymentDetailsBinding binding;
    public static final String CURRENT_ACCOUNT = "currentAccount";
    public static final String SAVINGS_ACCOUNT = "savingsAccount";
    public static final String CREDIT_CARD = "creditCard";
    public static final String TRANSMISSION_ACCOUNT = "transmissionAccount";
    public static final String BOND_ACCOUNT = "bondAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_insurance_manage_payment_details, null, false);
        setContentView(binding.getRoot());

        startFragment(InsurancePolicyPaymentDetailsFragment.newInstance(), true, AnimationType.FADE);
        Toolbar toolbar = (Toolbar) binding.toolbar.toolbar;
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public ProgressIndicatorView getProgressIndicatorView() {
        return binding.paymentDetailsProgressIndicator;
    }

    public void adjustProgressIndicator(int previousStep, int nextStep) {
        binding.paymentDetailsProgressIndicator.setNextStepWithIncrement();
        binding.paymentDetailsProgressIndicator.setNextStep(nextStep);
        binding.paymentDetailsProgressIndicator.setPreviousStep(previousStep);
    }

    public HashMap<String, String> getAccountDescription() {
        HashMap<String, String> accountDescriptionMap = new HashMap<>();
        accountDescriptionMap.put(SAVINGS_ACCOUNT, StringExtensions.toTitleCase(getString(R.string.savingAccount)));
        accountDescriptionMap.put(CURRENT_ACCOUNT, StringExtensions.toTitleCase(getString(R.string.chequeAccount)));
        accountDescriptionMap.put(CREDIT_CARD, StringExtensions.toTitleCase(getString(R.string.credit_card)));
        accountDescriptionMap.put(TRANSMISSION_ACCOUNT, StringExtensions.toTitleCase(getString(R.string.transmission_account)));
        accountDescriptionMap.put(BOND_ACCOUNT, StringExtensions.toTitleCase(getString(R.string.bond_account)));
        return accountDescriptionMap;
    }
}