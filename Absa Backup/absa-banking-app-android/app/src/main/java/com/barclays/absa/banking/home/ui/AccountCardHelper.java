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

import android.content.Context;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.utils.OperatorPermissionUtils;

public class AccountCardHelper {

    @NonNull
    public static String getCardNumber(AccountObject accountObject) {
        if ("onlineShareTrading".equalsIgnoreCase(accountObject.getAccountType())) {
            return "";
        } else {
            return accountObject.getAccountNumber().trim();
        }
    }

    public static String getAccountAmount(AccountObject accountObject, boolean isAccountSupported) {

        if ("Y".equals(accountObject.isBalanceMasked())) {
            return (AccountObject.MASK_BALANCE);
        } else if (!isAccountSupported) {
            return accountObject.getCurrentBalance().toString();
        }

        if (AccountTypes.HOME_LOAN.equalsIgnoreCase(accountObject.getAccountType()) || AccountTypes.PERSONAL_LOAN.equalsIgnoreCase(accountObject.getAccountType())) {
            return accountObject.getCurrentBalance().toString();
        } else if (AccountTypes.ABSA_REWARDS.equalsIgnoreCase(accountObject.getAccountType())) {
            return accountObject.getAvailableBalance().toString();
        } else if (AccountTypes.UNIT_TRUST_ACCOUNT.equalsIgnoreCase(accountObject.getAccountType())) {
            return accountObject.getCurrentBalance().toString();
        } else if (AccountTypes.CURRENT_INVESTMENT_ACCOUNT.equalsIgnoreCase(accountObject.getAccountType())) {
            return accountObject.getCurrentBalance().toString();
        } else if (!OperatorPermissionUtils.canViewBalances(accountObject)) {
            return (AccountObject.MASK_BALANCE);
        } else {
            return accountObject.getAvailableBalance().toString();
        }
    }

    public static String getAccountDescription(AccountObject accountObject) {
        String accountDescription = accountObject.getDescription();
        boolean descriptionNotAvailable = accountDescription == null ||
                accountDescription.equalsIgnoreCase("") ||
                accountDescription.equalsIgnoreCase("null");

        return descriptionNotAvailable ? accountObject.getAccountType().trim() : accountDescription.trim();
    }

    public static String getBalanceLabel(AccountObject accountObject, boolean isAccountSupported, Context context) {
        if (!isAccountSupported) {
            return context.getString(R.string.current_balance);
        }

        if (AccountTypes.HOME_LOAN.equalsIgnoreCase(accountObject.getAccountType())) {
            return context.getString(R.string.balance_owed);
        } else if (AccountTypes.ABSA_REWARDS.equalsIgnoreCase(accountObject.getAccountType()) || AccountTypes.PERSONAL_LOAN.equalsIgnoreCase(accountObject.getAccountType()) || AccountTypes.UNIT_TRUST_ACCOUNT.equalsIgnoreCase(accountObject.getAccountType())) {
            return context.getString(R.string.current_balance);
        } else if (!OperatorPermissionUtils.canViewBalances(accountObject)) {
            return "";
        } else {
            return context.getString(R.string.available_balance);
        }
    }

    public static String getCiaAccountName(AccountObject accountObject, boolean multipleAccounts, Context context) {
        String accountDescription = getAccountDescription(accountObject);
        if (accountDescription.equalsIgnoreCase(accountObject.getAccountType())) {
            accountDescription = context.getString(R.string.currency_investment_account);
        }

        return multipleAccounts ? context.getString(R.string.currency_investment_accounts) : accountDescription;
    }
}
