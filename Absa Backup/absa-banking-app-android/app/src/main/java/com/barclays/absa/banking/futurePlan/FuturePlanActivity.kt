/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */

package com.barclays.absa.banking.futurePlan

import android.os.Bundle
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.FuturePlanActivityBinding
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestActivity
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestProductType

class FuturePlanActivity : SaveAndInvestActivity() {

    private val binding by viewBinding(FuturePlanActivityBinding::inflate)

    override var featureName = FUTURE_PLAN
    override var productType: SaveAndInvestProductType = SaveAndInvestProductType.FUTURE_PLAN

    companion object {
        const val FUTURE_PLAN = "Future Plan"
        const val FUTURE_PLAN_UPPER_CASE = "FUTUREPLAN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressIndicatorView = binding.progressIndicatorView
        toolbar = binding.toolbar.toolbar
    }

    override fun onBackPressed() {
        if (findNavController(R.id.navigationHostFragment).currentDestination?.id == R.id.futurePlanApplyFragment) {
            trackAnalyticsAction("InformationScreen_BackButtonClicked")
        }
        super.onBackPressed()
    }
}