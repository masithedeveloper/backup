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
package com.barclays.absa.utils;

import android.content.Context;
import android.content.Intent;

import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryLandingActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;

public class DrawerOptions implements BMBConstants {

    private static Intent createManageBeneficiaryCall(Context context, String type) {
        Intent i = new Intent(context, BeneficiaryLandingActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false);
        i.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, type);
        return i;
    }

    public static void callPaymentManageBeneficiary(Context context) {
        context.startActivity(createManageBeneficiaryCall(context, BMBConstants.PASS_PAYMENT));
    }

    public static void callPrepaidManageBeneficiary(Context context) {
        context.startActivity(createManageBeneficiaryCall(context, BMBConstants.PASS_AIRTIME));
    }

    public static void callCashSendManageBeneficiary(Context context) {
        context.startActivity(createManageBeneficiaryCall(context, BMBConstants.PASS_CASHSEND));
    }
}
