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

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.rewards.ui.BuyAirtimeWithAbsaRewardsActivity
import com.barclays.absa.banking.rewards.ui.redemptions.donations.RedeemRewardsActivity
import com.barclays.absa.banking.rewards.ui.redemptions.points.RedeemShoppingPointsActivity
import com.barclays.absa.banking.rewards.ui.redemptions.vouchers.VoucherRedemptionActivity
import com.barclays.absa.banking.transfer.TransferConstants
import com.barclays.absa.banking.transfer.TransferFundsActivity
import kotlinx.android.synthetic.main.behavioural_rewards_spend_fragment.*

class BehaviouralRewardsSpendFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_spend_fragment) {

    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    companion object {
        const val DONATE_TO_CHARITY = "DonateToCharity"
        const val KEY = "FRAGMENT_TO_START"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar(R.string.behavioural_rewards_spend_rewards_title)
        showToolBar()

        redeemCashActionButton.setOnClickListener { navigateToRedeemCash() }
        buyAirtimeActionButton.setOnClickListener { navigateToBuyAirtime() }
        reedeemVouchersActionButton.setOnClickListener { navigateToRedeemVouchers() }
        redeemShoppingPointsActionButton.setOnClickListener { navigateToRedeemShoppingPoints() }
        donateToCharityActionButton.setOnClickListener { navigateToDonateToCharity() }

        trackAnalytics("SpendCashBack_ScreenDisplayed")
    }

    private fun navigateToRedeemCash() {
        startActivity(Intent(context, TransferFundsActivity::class.java).apply {
            putExtra(TransferConstants.FROM_ACCOUNT, rewardsCacheService.getRewardsAccount())
            putExtra(TransferConstants.IS_FROM_REWARDS, true)
        })
    }

    private fun navigateToBuyAirtime() {
        startActivity(Intent(context, BuyAirtimeWithAbsaRewardsActivity::class.java))
    }

    private fun navigateToRedeemVouchers() {
        startActivity(Intent(context, VoucherRedemptionActivity::class.java))
    }

    private fun navigateToRedeemShoppingPoints() {
        startActivity(Intent(context, RedeemShoppingPointsActivity::class.java)) //need to investigate this asap
    }

    private fun navigateToDonateToCharity() {
        startActivity(Intent(context, RedeemRewardsActivity::class.java).apply {
            putExtra(KEY, DONATE_TO_CHARITY)
        })
    }
}