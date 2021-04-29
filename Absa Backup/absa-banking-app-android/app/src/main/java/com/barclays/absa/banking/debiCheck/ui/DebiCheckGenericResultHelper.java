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
package com.barclays.absa.banking.debiCheck.ui;

import android.app.Activity;
import android.content.Intent;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.home.ui.HomeContainerActivity;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;

class DebiCheckGenericResultHelper {

    private static Intent buildResultIntent(final Activity activity, int messageResId) {
        GenericResultActivity.topOnClickListener = v -> {
            Intent intent = new Intent(activity, DebiCheckActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        };

        GenericResultActivity.bottomOnClickListener = v -> {
            Intent intent = new Intent(activity, HomeContainerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(AppConstants.CALLING_ACTIVITY, GenericResultActivity.class.getSimpleName());
            activity.startActivity(intent);
        };
        Intent intent = new Intent(activity, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_tick);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, messageResId);
        intent.putExtra(GenericResultActivity.IS_SUCCESS, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.debicheck_debit_orders_next);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
        return intent;
    }

    static void showApproveResultScreen(Activity activity) {
        activity.startActivity(buildResultIntent(activity, R.string.debicheck_approved_message));
    }

    static void showRejectedResultScreen(Activity activity) {
        activity.startActivity(buildResultIntent(activity, R.string.debicheck_rejected_message));
    }

    static void showSurecheckFailedResultScreen(Activity activity) {
        activity.startActivity(buildResultIntent(activity, R.string.surecheck_failed));
    }
}