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

package com.barclays.absa.banking.unitTrusts.ui.buy

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.buy_unit_trust_start_journey_fragment.*
import styleguide.utils.extensions.toTitleCase

class BuyUnitTrustStartJourneyFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_start_journey_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_Offer")
    }

    private fun setUpUI() {
        val unitTrustData = buyUnitTrustViewModel.unitTrustData
        unitTrustData.let {
            descriptionTextView.text = String.format(getString(R.string.buy_unit_trust_start_journey_description, TextFormatUtils.formatBasicAmount(it.allFundsMinDebitOrderAmt)))
        }

        setHasOptionsMenu(true)
        hostActivity.hideToolbar()
        startJourneyToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            visibility = View.VISIBLE
            title = getString(R.string.unit_trust).toTitleCase()
            setNavigationOnClickListener {
                hostActivity.onBackPressed()
            }
        }

        learnMoreButtonView.setOnClickListener {
            navigate(BuyUnitTrustStartJourneyFragmentDirections.actionBuyUnitTrustStartJourneyFragmentToBuyUnitTrustLearnMoreFragment())
        }
        applyNowButton.setOnClickListener {
            navigate(BuyUnitTrustStartJourneyFragmentDirections.actionBuyUnitTrustStartJourneyFragmentToBuyUnitTrustFundsFragment())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hostActivity.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}