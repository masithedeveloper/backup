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

package com.barclays.absa.banking.lotto.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.LottoActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.lotto.services.dto.LottoGameRules
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.extensions.getNavHostFragment
import com.barclays.absa.utils.viewModel

class LottoActivity : BaseActivity(R.layout.lotto_activity) {

    val binding by viewBinding(LottoActivityBinding::bind)

    private lateinit var viewModel: LottoViewModel
    private lateinit var navController: NavController

    companion object {
        const val LOTTO_GAME_RULES_EXTRA = "lottoGameRulesExtra"
        const val LOTTO_DRAW_SELECTOR_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModel()

        navController = getNavHostFragment().navController

        viewModel.lottoGameRulesListLiveData.value = intent?.extras?.getParcelableArrayList<LottoGameRules>(LOTTO_GAME_RULES_EXTRA)
        AnalyticsUtil.trackAction("LottoAndPowerBall", "Lotto_LottoAndPowerBall_ScreenDisplayed")
    }

    override fun onBackPressed() {
        backActionEventTagging()
        when (navController.currentDestination?.id) {
            R.id.lottoPurchaseSuccessFragment -> return
            R.id.lottoNumberSelectionFragment -> {
                (currentFragment as LottoNumberSelectionFragment).apply { onBackPressed() }
                return
            }
        }
        super.onBackPressed()
    }

    fun superOnBackPressed() {
        super.onBackPressed()
    }

    private fun backActionEventTagging() {
        navController.currentDestination?.let {
            when (it.id) {
                R.id.lottoAndPowerBallFragment -> AnalyticsUtil.trackAction("LottoAndPowerBall", "Lotto_LottoAndPowerBallHubScreen_BackButtonClicked")
                R.id.lottoNewTicketFragment -> tagLottoAndPowerBallEvent("NewTicketScreen_BackButtonClicked")
                R.id.lottoPlayTheLottoFragment -> tagLottoAndPowerBallEvent("NewTicketAdditionalGamesScreen_BackButtonClicked")
                R.id.lottoTicketConfirmationFragment -> tagLottoAndPowerBallEvent("ConfirmPurchaseTicket_BackButtonClicked")
                R.id.lottoNumberSelectionFragment -> tagLottoAndPowerBallEvent("BetSelectorScreen_BackButtonClicked")
                R.id.lottoPurchasedTicketsFragment -> tagLottoAndPowerBallEvent("PurchasedTicketsScreen_BackButtonClicked")
                R.id.lottoQuickPickFragment -> tagLottoAndPowerBallEvent("PlayQuickPickScreen_BackButtonClicked")
                R.id.lottoGameResultsFragment -> tagLottoAndPowerBallEvent("ViewGameResultsScreen_BackButtonClicked")
                R.id.lottoTicketFragment -> tagLottoAndPowerBallEvent("ViewTicketDetailScreen_BackButtonClicked")
            }
        }
    }

    override val currentFragment: Fragment?
        get() {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigationHostFragment)
            return navHostFragment?.childFragmentManager?.fragments?.get(0)
        }

    fun tagLottoAndPowerBallEvent(screenName: String) {
        val isPowerBall = viewModel.currentGameRules.gameType == LottoViewModel.GAME_TYPE_POWERBALL
        val flowTag = if (isPowerBall) "PowerBall" else "Lotto"
        AnalyticsUtil.trackAction("LottoAndPowerBall", "${flowTag}_${screenName}")
    }
}