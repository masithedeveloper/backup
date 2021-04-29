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

package com.barclays.absa.banking.rewards.ui.redemptions.donations;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.BaseActivity;

public class RedeemRewardsActivity extends BaseActivity {
    public String screenToDisplay;
    public static final String KEY = "FRAGMENT_TO_START";
    public static final String DONATE_TO_CHARITY = "DonateToCharity";
    public static final String SHOPPING_POINTS = "ShoppingPoints";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redeem_rewards_activity);

        setToolBarBack(R.string.redeem_rewards);

        if (getIntent().getExtras() != null) {
            screenToDisplay = (String) getIntent().getSerializableExtra(KEY);
        }

        initFragmentData(screenToDisplay);
    }

    private void initFragmentData(String screen) {
        if (DONATE_TO_CHARITY.equalsIgnoreCase(screen)) {
            changeFragment(DonateToCharityFragment.newInstance(), null);
        } else if (SHOPPING_POINTS.equalsIgnoreCase(screen)) {
            finish();
        }
    }

    public void changeFragment(Fragment fragment, Fragment currentFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            transaction.remove(currentFragment);
        }
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void startFragment(Fragment fragment) {
        startFragment(fragment, R.id.fragmentContainer, true, AnimationType.FADE, true, fragment.getClass().getName());
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}