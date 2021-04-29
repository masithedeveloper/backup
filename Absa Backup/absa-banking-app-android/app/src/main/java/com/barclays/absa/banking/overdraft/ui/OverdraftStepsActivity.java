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
package com.barclays.absa.banking.overdraft.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject;
import com.barclays.absa.banking.databinding.OverdraftStepsActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftCheckSetupAndConfirmFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftDeclarationFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftIllustrativeCostFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftMarketingConsentFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftSetupFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftTellUsAboutYourSelfFragment;
import com.barclays.absa.banking.overdraft.ui.fiveSteps.OverdraftTermsAndConditionsFragment;
import com.barclays.absa.utils.AnalyticsUtil;

import static com.barclays.absa.banking.overdraft.ui.OverdraftIntroActivity.OVERDRAFT;

public class OverdraftStepsActivity extends BaseActivity {

    private OverdraftStepsActivityBinding binding;
    private double overdraftMaximumAmount, overdraftSelectedAmount;
    private String selectedAccountNumber;
    private AccountObject selectedChequeAccount;
    private OverdraftQuoteDetailsObject overdraftQuoteDetailsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.overdraft_steps_activity, null, false);
        setContentView(binding.getRoot());

        setToolBar(R.string.overdraft, view -> onBackPressed());
        if (savedInstanceState == null) {
            startFragment(OverdraftTellUsAboutYourSelfFragment.getInstance(), false, AnimationType.NONE);
        } else {
            selectedAccountNumber = savedInstanceState.getString("selectedAccountNumber", null);
            overdraftMaximumAmount = savedInstanceState.getDouble("overdraftMaximumAmount", 0.0);
            overdraftSelectedAmount = savedInstanceState.getDouble("overdraftSelectedAmount", 0.0);
            overdraftQuoteDetailsObject = (OverdraftQuoteDetailsObject) savedInstanceState.getSerializable("overdraftQuoteDetailsObject");
            selectedChequeAccount = (AccountObject) savedInstanceState.getSerializable("selectedChecqueAccount");
        }
        if (getIntent().getExtras() != null) {
            setOverdraftMaximumAmount(getIntent().getExtras().getDouble(IntentFactoryOverdraft.OVERDRAFT_MAXIMUM_AMOUNT, 0));
        }

        setSupportActionBar((Toolbar) binding.toolbar.toolbar);
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getCurrentFragment();

        if (fragment != null) {
            if (fragment instanceof OverdraftDeclarationFragment) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_DeclarationScreen_BackButtonClicked");
            } else if (fragment instanceof OverdraftSetupFragment) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_SetupScreen_BackButtonClicked");
            } else if (fragment instanceof OverdraftCheckSetupAndConfirmFragment) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_ConfirmApplicationScreen_BackButtonClicked");
            } else if (fragment instanceof OverdraftIllustrativeCostFragment) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IllustrativeCostScreen_BackButtonClicked");
            } else if (fragment instanceof OverdraftIncomeAndExpenseFragment) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_IncomeAndExpenseScreen_BackButtonButtonClicked");
            } else if (fragment instanceof OverdraftTermsAndConditionsFragment) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_TermsAndConditionsScreen_BackButtonClicked");
            } else if (fragment instanceof OverdraftMarketingConsentFragment) {
                AnalyticsUtil.INSTANCE.trackAction(OVERDRAFT, "Overdraft_CreditMarketingConsentScreen_BackButtonClicked");
            }
        }

        if (backStackEntryCount == 1) {
            finish();
        } else if (backStackEntryCount > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void setStep(int step) {
        binding.progressBarView.setNextStep(step);
        binding.progressBarView.setVisibility(View.VISIBLE);
    }

    public void hideBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    public void hideSteps() {
        binding.progressBarView.setVisibility(View.GONE);
    }

    public double getOverdraftMaximumAmount() {
        return overdraftMaximumAmount;
    }

    public void setOverdraftMaximumAmount(double amount) {
        this.overdraftMaximumAmount = amount;
    }

    public void setOverdraftSelectedAmount(double amount) {
        this.overdraftSelectedAmount = amount;
    }

    @Override
    protected synchronized void onSaveInstanceState(Bundle outState) {
        outState.putString("selectedAccountNumber", selectedAccountNumber);
        outState.putDouble("overdraftMaximumAmount", overdraftMaximumAmount);
        outState.putDouble("overdraftSelectedAmount", overdraftSelectedAmount);
        outState.putSerializable("overdraftQuoteDetailsObject", overdraftQuoteDetailsObject);
        outState.putSerializable("selectedChecqueAccount", selectedChequeAccount);
        super.onSaveInstanceState(outState);
    }
}