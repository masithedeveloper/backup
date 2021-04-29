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

package com.barclays.absa.banking.funeralCover.ui

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails
import com.barclays.absa.banking.framework.BaseActivity
import kotlinx.android.synthetic.main.funeral_cover_activity.*

class FuneralCoverActivity : BaseActivity(R.layout.funeral_cover_activity) {
    lateinit var funeralCoverDetails: FuneralCoverDetails
    var beneficiarySelected: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        funeralCoverDetails = intent.getSerializableExtra(FuneralCoverAddMainMemberActivity.FUNERAL_COVER_DETAILS) as FuneralCoverDetails
        startFragment(FuneralCoverDebitOrderDetailsFragment.newInstance(), true, AnimationType.NONE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.fragments.isEmpty()) {
            finish()
        }
    }

    fun setStep(step: Int) {
        progressIndicator.setNextStep(step)
    }
}