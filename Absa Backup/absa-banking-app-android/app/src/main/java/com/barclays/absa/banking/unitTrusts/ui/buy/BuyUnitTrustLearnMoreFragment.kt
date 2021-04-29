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
import android.view.View
import com.barclays.absa.banking.R
import kotlinx.android.synthetic.main.buy_unit_trust_learn_more_fragment.*

class BuyUnitTrustLearnMoreFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_learn_more_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_LearnMore")
        setClickListener()
    }

    private fun setClickListener() {
        whatIsUnitTrustButtonView.setOnClickListener {
            navigate(BuyUnitTrustLearnMoreFragmentDirections.actionBuyUnitTrustLearnMoreFragmentToBuyUnitTrustWhatIsUnitTrustFragment())
        }
        whatWillItCostButtonView.setOnClickListener {
            navigate(BuyUnitTrustLearnMoreFragmentDirections.actionBuyUnitTrustLearnMoreFragmentToBuyUnitTrustCostFragment())
        }
        riskConsiderationButtonView.setOnClickListener {
            navigate(BuyUnitTrustLearnMoreFragmentDirections.actionBuyUnitTrustLearnMoreFragmentToBuyUnitTrustRiskConsiderationFragment())
        }
        dividentTaxButtonView.setOnClickListener {
            navigate(BuyUnitTrustLearnMoreFragmentDirections.actionBuyUnitTrustLearnMoreFragmentToBuyUnitTrustDividentTaxFragment())
        }
        contactDetailsButtonView.setOnClickListener {
            navigate(BuyUnitTrustLearnMoreFragmentDirections.actionBuyUnitTrustLearnMoreFragmentToBuyUnitTrustContactUsFragment())
        }
    }

    private fun setUpToolBar() {
        hostActivity.apply {
            setToolBarBack(getString(R.string.learn_more_text))
            showToolbar()
        }
    }
}