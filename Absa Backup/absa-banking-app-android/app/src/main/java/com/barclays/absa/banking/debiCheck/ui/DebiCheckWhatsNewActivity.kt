/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.debiCheck.ui

import android.content.Intent
import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.SharedPreferenceService
import kotlinx.android.synthetic.main.debi_check_whatsnew_activity.*


class DebiCheckWhatsNewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debi_check_whatsnew_activity)
        setToolBarBack(getString(R.string.debicheck_title))

        primaryButton.setOnClickListener {
            SharedPreferenceService.setDebiCheckFirstVisitDone(true)
            startActivity(Intent(this, DebiCheckActivity::class.java))
            finish()
        }
    }
}