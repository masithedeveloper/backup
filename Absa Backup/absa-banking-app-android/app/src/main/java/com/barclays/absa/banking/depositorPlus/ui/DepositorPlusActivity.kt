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

package com.barclays.absa.banking.depositorPlus.ui

import android.os.Bundle
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.DepositorPlusActivityBinding
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestActivity
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestProductType

class DepositorPlusActivity : SaveAndInvestActivity() {

    companion object {
        const val DEPOSITOR_PLUS = "Depositor Plus"
        const val DEPOSITOR_PLUS_UPPER_CASE = "DEPOSITORPLUS"
    }

    private val binding by viewBinding(DepositorPlusActivityBinding::inflate)
    override var featureName: String = DEPOSITOR_PLUS
    override var productType: SaveAndInvestProductType = SaveAndInvestProductType.DEPOSITOR_PLUS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressIndicatorView = binding.progressIndicatorView
        toolbar = binding.toolbar.toolbar
    }

    override fun onBackPressed() {
        if (findNavController(R.id.navigationHostFragment).currentDestination?.id == R.id.depositorPlusApplyFragment) {
            trackAnalyticsAction("InformationScreen_BackButtonClicked")
        }
        super.onBackPressed()
    }
}