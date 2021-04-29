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
 */
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import kotlinx.android.synthetic.main.behavioural_rewards_earn_rates_fragment.*

class BehaviouralRewardsEarnRatesFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_earn_rates_fragment) {

    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        trackAnalytics("EarnRates_ScreenDisplayed")
    }

    private fun setupViews() {
        val earnRate = rewardsCacheService.getEarnRate() ?: return
        with(earnRate) {
            bankCreditCardEarnRateLineItemView.setLineItemViewContent(bankCreditEarnRate)
            bankDebitCardEarnRateLineItemView.setLineItemViewContent(bankDebitEarnRate)
            fuelDebitCardEarnRateLineItemView.setLineItemViewContent(fuelDebitEarnRate)
            fuelCreditCardEarnRateLineItemView.setLineItemViewContent(fuelCreditEarnRate)
            groceryDebitCardEarnRateLineItemView.setLineItemViewContent(groceryDebitEarnRate)
            groceryCreditCardEarnRateLineItemView.setLineItemViewContent(groceryCreditEarnRate)
        }
    }
}