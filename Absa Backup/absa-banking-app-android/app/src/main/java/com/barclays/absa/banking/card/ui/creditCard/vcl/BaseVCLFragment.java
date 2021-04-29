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
package com.barclays.absa.banking.card.ui.creditCard.vcl;

import android.app.Activity;

import androidx.databinding.ViewDataBinding;

import com.barclays.absa.banking.framework.AbsaBaseFragment;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;

import org.jetbrains.annotations.NotNull;

public abstract class BaseVCLFragment<T extends ViewDataBinding> extends AbsaBaseFragment<T> {

    public static final String BASE_VALUE = "0.00";
    public static final String ABSA = "absa";
    public static final String STANDARD_BANK = "STANDARD BANK";
    public static final String STANDARD_BANK_NORMAL = "Standard bank";
    public static final String NEDBANK = "NEDBANK";
    public static final String BANK_NAME = "bankName";
    public static final String BRANCH_CODE = "branchCode";
    public static final String ACCOUNT_TYPE = "accountType";
    public static final String CREDIT_CARD_VCL_FEATURE_NAME = "Credit card VCL";
    public static final int SURE_CHECK_DELAY_MILLIS = 250;

    public static final int REQUEST_CODE_FOR_BANK_NAME = 1000;
    public static final int REQUEST_CODE_FOR_BRANCH_NAME = 2000;
    public static final int REQUEST_CODE_FOR_ACCOUNT_TYPE = 3000;
    public static final int SLOW_SCROLL_TIME = 700;
    public static final int FAST_SCROLL_TIME = 150;

    public static final double MAX_VALUE_LIMIT = 99999999999.99;
    public static final double MIN_VALUE_LIMIT = 99999.99;

    public static final String MAX_LIMIT_IMCOME = "99999999999.99";
    public static final String MIN_LIMIT_INCOME = "99999.99";

    public CreditCardVCLView getParentActivity() {
        Activity activity = getActivity();
        if (activity == null){
            activity = BMBApplication.getInstance().getTopMostActivity();
        }
        return (CreditCardVCLView) activity ;
    }

    @NotNull
    public BaseActivity getBaseActivity() {
        Activity activity = getActivity();
        if (activity == null){
            activity = BMBApplication.getInstance().getTopMostActivity();
        }
        return (BaseActivity) activity;
    }
}
