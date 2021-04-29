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

import android.app.Activity;
import android.content.Intent;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.explore.services.dto.BusinessBankOverdraftData;
import com.barclays.absa.banking.presentation.shared.IntentFactory;

public abstract class IntentFactoryOverdraft extends IntentFactory {
    public static final String OVERDRAFT_MAXIMUM_AMOUNT = "OVERDRAFT_MAXIMUM_AMOUNT";
    public static final String BUSINESS_OVERDRAFT_DATA = "BUSINESS_OVERDRAFT_DATA";

    public static Intent getPolicyDeclinedResultScreen(final Activity activity) {
        return getUnableToContinueScreen(activity,
                R.string.unable_to_continue,
                R.string.overdraft_policy_declined);
    }

    public static Intent getReferralResultScreen(final Activity activity) {
        return getUnableToContinueScreen(activity,
                R.string.unable_to_continue,
                R.string.overdraft_referral);
    }

    public static Intent getPostponeApplicationResultScreen(final Activity activity, String message) {
        return getPendingResultScreen(activity, R.string.overdraft_quote_awaiting, message);
    }

    public static Intent getQuoteWaitingResultScreen(final Activity activity, String message) {
        return getPendingResultScreen(activity, R.string.overdraft_quote_awaiting, message);
    }

    public static Intent getOverdraftIntro(final Activity activity, double newOverdraftLimit) {
        return new IntentBuilder(new Intent(activity, OverdraftIntroActivity.class)
                .putExtra(NEW_OVERDRAFT_LIMIT, newOverdraftLimit)).build();
    }

    public static Intent getVCLOverdraftIntro(final Activity activity, BusinessBankOverdraftData businessBankOverdraftData) {
        return new IntentBuilder(new Intent(activity, BusinessOverdraftActivity.class)
                .putExtra(BUSINESS_OVERDRAFT_DATA, businessBankOverdraftData)).build();
    }

    public static Intent getOverdraftSteps(Activity activity, double overdraftMaximumAmount) {
        return new IntentBuilder(new Intent().putExtra(OVERDRAFT_MAXIMUM_AMOUNT, overdraftMaximumAmount))
                .setClass(activity, OverdraftStepsActivity.class)
                .build();
    }
}