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
package com.barclays.absa.banking.presentation.inAppNotifications

import android.content.Intent
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.moneyMarket.ui.ACTIVE_ORBIT_STATUS
import com.barclays.absa.banking.moneyMarket.ui.MONEY_MARKET_ACCOUNT
import com.barclays.absa.banking.moneyMarket.ui.MoneyMarketActivity
import com.barclays.absa.banking.presentation.inAppNotifications.DeepLink.BEHAVIOURAL_REWARDS
import com.barclays.absa.banking.presentation.inAppNotifications.DeepLink.ORBIT
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.BehaviouralRewardsActivity
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AnalyticsUtil.trackAction

object DeepLinkUtil {
    private val topMostActivity
        get() = BMBApplication.getInstance().topMostActivity

    fun deepLinkTo(deepLinkUri: String) {
        val rewardsCacheService: IRewardsCacheService = getServiceInterface()
        when {
            deepLinkUri.equals(BEHAVIOURAL_REWARDS.key, ignoreCase = true) -> {
                if (rewardsCacheService.hasRewards()) {
                    trackAction("Behavioural Rewards", "BehaviouralRewards_InAppMessaging_RewardsLinkClicked")
                } else {
                    trackAction("Behavioural Rewards", "BehaviouralRewardsNonAbsa_InAppMessaging_RewardsLinkClicked")
                }
                navigateToBehaviouralRewards()
            }
            deepLinkUri.equals(ORBIT.key, ignoreCase = true) -> navigateToOrbit()
        }
    }

    private fun navigateToBehaviouralRewards() {
        topMostActivity.startActivity(Intent(topMostActivity, BehaviouralRewardsActivity::class.java))
    }

    private fun navigateToOrbit() {
        val homeCacheService: IHomeCacheService = getServiceInterface()
        if (featureSwitchingToggles.projectOrbit == FeatureSwitchingStates.ACTIVE.key) {
            val orbitAccount = homeCacheService.getMoneyMarketStatusList().firstOrNull { orbitAccount -> orbitAccount.status == ACTIVE_ORBIT_STATUS }
            var accountObject: AccountObject? = null
            orbitAccount?.let {
                accountObject = AbsaCacheManager.getInstance().accountsList.accountsList.firstOrNull { accountObject -> accountObject.accountNumber == orbitAccount.account }
            }

            accountObject?.let {
                val moneyMarketIntent = Intent(topMostActivity, MoneyMarketActivity::class.java)
                moneyMarketIntent.putExtra(MONEY_MARKET_ACCOUNT, accountObject)
                topMostActivity.startActivity(moneyMarketIntent)
            }
        }
    }
}

enum class DeepLink(var key: String) {
    BEHAVIOURAL_REWARDS("absabankingappurlscheme://rewards"),
    ORBIT("absabankingappurlscheme://orbit")
}