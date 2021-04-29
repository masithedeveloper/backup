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

package com.barclays.absa.banking.cashSend.ui;

import android.content.Context;
import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.framework.app.BMBApplication;

import styleguide.forms.SelectorInterface;

public class AccountObjectWrapper implements SelectorInterface {
    private AccountObject accountObject;

    public AccountObjectWrapper(@NonNull AccountObject accountObject) {
        this.accountObject = accountObject;
    }

    public String getFormattedValue() {
        return accountObject.getAccountInformation();
    }

    @Override
    public String getDisplayValue() {
        return accountObject.getDescription();
    }

    @Override
    public String getDisplayValueLine2() {
        Context context = BMBApplication.getInstance().getTopMostActivity();
        return String.format("%s\n%s: %s", accountObject.getAccountNumberFormatted(), context.getString(R.string.account_to_pay_from_avlbl_blnc), accountObject.getAvailableBalance().toString());
    }

    public AccountObject getAccountObject() {
        return accountObject;
    }
}
