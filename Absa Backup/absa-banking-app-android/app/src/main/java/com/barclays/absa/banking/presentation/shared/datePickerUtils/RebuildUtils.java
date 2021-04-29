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
package com.barclays.absa.banking.presentation.shared.datePickerUtils;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.RegisterAccountListObject;
import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class RebuildUtils {
    /**
     * Set up tool bar for activity.
     */
    public static ActionBar setupToolBar(final Context context, String title, int homeIcon, boolean isHomeAsUpEnabled, View.OnClickListener onClickListener) {
        Toolbar toolbar = ((BaseActivity) context).findViewById(R.id.toolBar);
        if (toolbar == null) {
            toolbar = ((BaseActivity) context).findViewById(R.id.toolbar);
        }
        int dpAsPixels = context.getResources().getDimensionPixelOffset(R.dimen.medium_space);
        if (!isHomeAsUpEnabled) {
            toolbar.setPadding(dpAsPixels, 0, 0, 0);
        }

        return setupToolBar(toolbar, context, title, homeIcon, isHomeAsUpEnabled, onClickListener);
    }

    public static ActionBar setupToolBar(@NonNull Toolbar toolbar, final Context context, String title, int homeIcon, boolean isHomeAsUpEnabled, View.OnClickListener onClickListener) {
        ((BaseActivity) context).setSupportActionBar(toolbar);
        ActionBar actionBar = ((BaseActivity) context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(isHomeAsUpEnabled);
            actionBar.setTitle(title);
            if (homeIcon != -1) {
                actionBar.setHomeAsUpIndicator(homeIcon);
            }
            if (onClickListener != null)
                toolbar.setNavigationOnClickListener(onClickListener);
        }
        return actionBar;
    }

    public static ActionBar setupToolBar(final Context context, String title, String subtitle, int homeIcon, boolean isHomeAsUpEnabled, View.OnClickListener onClickListener) {
        ActionBar actionBar = setupToolBar(context, title, homeIcon, isHomeAsUpEnabled, onClickListener);
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
        return actionBar;
    }

    public static boolean isPolicySupported(Policy policy, Context context) {
        if (policy.getStatus() == null) {
            return false;
        }
        String[] ltOrExergySupportedStatus = context.getResources().getStringArray(R.array.lt_or_exergy_supported_status);
        String[] stSupportedStatus = context.getResources().getStringArray(R.array.lt_or_exergy_supported_status);
        if (BMBConstants.LONG_TERM_POLICY_TYPE.equalsIgnoreCase(policy.getType()) || BMBConstants.EXERGY_POLICY_TYPE.equalsIgnoreCase(policy.getType())) {
            return Arrays.asList(ltOrExergySupportedStatus).contains(policy.getStatus());
        } else if (BMBConstants.SHORT_TERM_POLICY_TYPE.equalsIgnoreCase(policy.getType())) {
            return Arrays.asList(stSupportedStatus).contains(policy.getStatus());
        }
        return false;
    }

    public static ArrayList<RegisterAccountListObject> getAccessAccount(RegisterProfileDetail registerProfileDetail) {
        ArrayList<RegisterAccountListObject> accessAccountList = new ArrayList<>();
        ArrayList<RegisterAccountListObject> accounts = registerProfileDetail.getAccounts();
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        for (RegisterAccountListObject account : accounts) {
            if (account.isAccessAccount()) {
                accessAccountList.add(account);
            }
        }
        return accessAccountList;
    }

    public static ArrayList<RegisterAccountListObject> getBillingsAccount(RegisterProfileDetail registerProfileDetail) {
        ArrayList<RegisterAccountListObject> billingAccountList = new ArrayList<>();
        ArrayList<RegisterAccountListObject> accounts = registerProfileDetail.getAccounts();
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        for (RegisterAccountListObject account : accounts) {
            if (account.isBillingAccount()) {
                billingAccountList.add(account);
            }
        }
        return billingAccountList;
    }

}