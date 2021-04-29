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
package com.barclays.absa.banking.home.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.push.CustomTags;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.passcode.passcodeLogin.PasscodeLoginView;

public class FetchPolicyAuthorizationsFromPasscodeLogin implements FetchPolicyAuthorizations.OnRequestComplete {

    private String currentShortcut;
    private boolean showAccountsList;
    private PasscodeLoginView passcodeLoginView;
    private CustomTags customTags;

    public void fetch(String currentShortcutId, boolean showAccounts) {
        currentShortcut = currentShortcutId;
        showAccountsList = showAccounts;
        fetch();
    }

    public void fetch(String currentShortcutId, CustomTags customTags, boolean showAccounts, PasscodeLoginView passcodeLoginView) {
        this.passcodeLoginView = passcodeLoginView;
        this.customTags = customTags;
        fetch(currentShortcutId, showAccounts);
    }

    private void fetch() {
        new FetchPolicyAuthorizations(this).fetchHomeScreenData();
    }

    @Override
    public void onSuccess() {
        Activity activity = BMBApplication.getInstance().getTopMostActivity();
        if (passcodeLoginView != null) {
            passcodeLoginView.onLoginCompleted();
            new Handler(Looper.getMainLooper()).postDelayed(() -> navigateToHome(activity), 1500);
        } else {
            try {
                BaseActivity baseActivity = (BaseActivity) activity;
                baseActivity.dismissProgressDialog();
            } catch (ClassCastException e) {
                BMBLogger.e(FetchPolicyAuthorizationsFromPasscodeLogin.class.getSimpleName(), e.getMessage());
            }
            navigateToHome(activity);
        }
    }

    private void navigateToHome(Activity activity) {
        boolean isTransactionalUser = CustomerProfileObject.getInstance().isTransactionalUser();
        Intent intent = new Intent(activity, isTransactionalUser ? HomeContainerActivity.class : StandaloneHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(HomeContainerActivity.DISPLAY_ACCOUNTS_LIST, showAccountsList);

        if (currentShortcut != null) {
            intent.putExtra("shortcut", currentShortcut);
        }

        if (customTags != null) {
            intent.putExtra("customTags", customTags);
        }
        activity.startActivity(intent);
        activity.finish();
    }
}