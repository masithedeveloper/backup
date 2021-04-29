/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.explore.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.utils.IntentUtil
import com.barclays.absa.utils.CommonUtils
import kotlinx.android.synthetic.main.fraud_notification_activity.*

class FraudNotificationAlertActivity : BaseActivity(R.layout.fraud_notification_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (toolbar as Toolbar).setNavigationIcon(R.drawable.ic_left_arrow_light)
        setToolBarBack(getString(R.string.security))
        setUpOnClickListeners()

        CommonUtils.makeTextClickable(this, R.string.fraud_notifications_letter_whats_new_content, getString(R.string.fraud_number_no_space), object : ClickableSpan() {
            override fun onClick(widget: View) {
                val fraudCallIntent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:0860577577") }
                if (IntentUtil.isIntentAvailable(this@FraudNotificationAlertActivity, fraudCallIntent)) {
                    startActivity(fraudCallIntent)
                }
            }
        }, fraudMessageTextView)
    }

    private fun setUpOnClickListeners() {
        iUnderstandButton.setOnClickListener {
            finish()
        }
    }
}