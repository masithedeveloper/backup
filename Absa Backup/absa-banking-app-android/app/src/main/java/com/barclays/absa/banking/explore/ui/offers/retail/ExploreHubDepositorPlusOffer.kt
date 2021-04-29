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

package com.barclays.absa.banking.explore.ui.offers.retail

import android.content.Intent
import com.barclays.absa.banking.R
import com.barclays.absa.banking.depositorPlus.ui.DepositorPlusActivity
import com.barclays.absa.banking.depositorPlus.ui.DepositorPlusActivity.Companion.DEPOSITOR_PLUS
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.ui.CheckType
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.saveAndInvest.CASA_REFERENCE
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import styleguide.cards.Offer

class ExploreHubDepositorPlusOffer(var exploreHubFragment: NewExploreHubFragment) : ExploreHubBaseOffer() {
    override var offerTile: Offer = Offer(exploreHubFragment.getString(R.string.depositor_plus_title), exploreHubFragment.getString(R.string.depositor_plus_apply_header))
    override var offerBackgroundImage: Int = R.drawable.depositor_plus_explore_wide_background
    override var cardDescription: String = ""
    override var buttonText: String = exploreHubFragment.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.depositorPlus
    override var buildConfigToggle: Boolean = true
    private val appCacheService: IAppCacheService = getServiceInterface()
    override var additionalToggleChecks: Boolean = !exploreHubFragment.isBusinessAccount && !exploreHubFragment.isSoleProprietor && exploreHubFragment.isSouthAfricanResident && appCacheService.isPrimarySecondFactorDevice()
    override var onClickListener: () -> Unit = { onOfferClick() }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {
        exploreHubFragment.dismissProgressDialog()
        val intent = when {
            !casaAndFicaCheckStatus.casaApproved -> IntentFactory.getUnableToContinueScreen(exploreHubFragment.activity, R.string.unable_to_continue, R.string.risk_based_approach_casa_failure_message)
            !casaAndFicaCheckStatus.ficaApproved -> IntentFactory.getUnableToContinueScreen(exploreHubFragment.activity, R.string.unable_to_continue, R.string.risk_based_approach_fica_failure_message)
            else -> Intent(exploreHubFragment.activity, DepositorPlusActivity::class.java).putExtra(CASA_REFERENCE, casaAndFicaCheckStatus.casaReference)
        }
        exploreHubFragment.startActivity(intent)
    }

    private fun onOfferClick() {
        trackAction(DEPOSITOR_PLUS, "DepositorPlus_ExploreScreen_DashboardDepositorPlusCardClicked")
        exploreHubFragment.performCasaAndFicaCheck(this, CheckType.ALL)
    }
}