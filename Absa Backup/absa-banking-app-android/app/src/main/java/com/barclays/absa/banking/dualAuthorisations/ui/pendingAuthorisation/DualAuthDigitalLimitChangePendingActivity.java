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
package com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation;

import android.content.Intent;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.settings.ui.ManageDigitalLimitsActivity;

public class DualAuthDigitalLimitChangePendingActivity extends DualAuthPendingActivity {

    @Override
    String getAuthTitle() {
        return getString(R.string.auth_title_digital_limit_change_pending);
    }

    @Override
    String getAuthExpiryMessage() {
        return getString(R.string.auth_expire_message);
    }

    @Override
    String getAuthContactMessage() {
        return getString(R.string.auth_contact_message);
    }

    @Override
    String getAuthPrimaryButtonText() {
        return getString(R.string.btn_manage_digital_limits);
    }

    @Override
    void onPrimaryButtonClicked() {
        Intent intent = new Intent(DualAuthDigitalLimitChangePendingActivity.this, ManageDigitalLimitsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
