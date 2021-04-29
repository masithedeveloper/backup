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
package com.barclays.absa.banking.card.ui.secondaryCard

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.GetSecondaryCardMandateResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.AnalyticsUtil

class SecondaryCardActivity : BaseActivity(R.layout.secondary_card_activity) {

    companion object {
        const val SECONDARY_CARD_OBJECT = "SECONDARY_CARD_OBJECT"
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.secondary_card_access_title)
        val bundle = Bundle().apply {
            putParcelable(SECONDARY_CARD_OBJECT, intent.getParcelableExtra<GetSecondaryCardMandateResponse>(SECONDARY_CARD_OBJECT))
        }
        navController = findNavController(R.id.manageSecondaryCardNavHostFragment)
        findNavController(R.id.manageSecondaryCardNavHostFragment).setGraph(R.navigation.manage_secondary_card_navigation_graph, bundle)
    }

    override fun onBackPressed() {
        navController.currentDestination?.let {
            when (it.id) {
                R.id.secondaryCardManageFragment -> AnalyticsUtil.trackAction("Secondary card", "SecondaryCard_EnableAccessScreen_BackButtonClicked")
                R.id.secondaryCardTermsAndConditionsFragment -> AnalyticsUtil.trackAction("Secondary card", "SecondaryCard_TermsAndConditionsScreen_BackButtonClicked")
                R.id.genericResultScreenFragment -> return
            }
        }
        super.onBackPressed()
    }
}