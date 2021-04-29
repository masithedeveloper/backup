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
package com.barclays.absa.banking.rewards.ui.rewardsHub;

import android.content.Context;

import com.barclays.absa.banking.framework.BaseView;

public final class ProgressHandler {

    private ProgressHandler() {
        throw new RuntimeException("Cannot be instatiated");
    }

    public static void handleProgress(Context context, State state) {
        BaseView view = (BaseView) context;
        if (state != null) {
            switch (state) {
                case STARTED:
                    view.showProgressDialog();
                    break;
                case COMPLETED:
                    view.dismissProgressDialog();
                    break;
                case FAILED:
                    view.dismissProgressDialog();
                    break;
                case QUEUE_COMPLETED:
                    view.dismissProgressDialog();
                    break;
            }
        }
    }
}
