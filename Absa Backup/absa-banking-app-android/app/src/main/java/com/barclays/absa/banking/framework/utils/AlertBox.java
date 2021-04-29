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
package com.barclays.absa.banking.framework.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.barclays.absa.banking.R;

public class AlertBox {

    public static abstract class AlertRetryListener {
        public abstract void retry();

        public void cancel() {
        }
    }

    public static void setTitleBar(Context ctx, final Dialog alertDialog) {
        int titleDividerId = ctx.getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = alertDialog.findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_FF374154));
        }
    }
}
