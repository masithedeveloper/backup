/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.ui.CheckType
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.futurePlan.FuturePlanActivity
import com.barclays.absa.banking.futurePlan.FuturePlanActivity.Companion.FUTURE_PLAN
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.saveAndInvest.CASA_REFERENCE
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import styleguide.cards.Offer

class ExploreHubFuturePlanOffer(var exploreHubFragment: NewExploreHubFragment) : ExploreHubBaseOffer() {

    override var offerTile: Offer = Offer(exploreHubFragment.getString(R.string.future_plan_title), exploreHubFragment.getString(R.string.future_plan_apply_header))
    override var offerBackgroundImage: Int = R.drawable.future_plan_explore_wide_background
    override var cardDescription: String = ""
    override var buttonText: String = exploreHubFragment.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.futurePlan
    override var buildConfigToggle: Boolean = BuildConfig.TOGGLE_DEF_FUTURE_PLAN_ENABLED
    override var additionalToggleChecks: Boolean = !exploreHubFragment.isBusinessAccount && !exploreHubFragment.isSoleProprietor && exploreHubFragment.isSouthAfricanResident && getServiceInterface<IAppCacheService>().isPrimarySecondFactorDevice()
    override var onClickListener: () -> Unit = { onOfferClick() }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {
        exploreHubFragment.dismissProgressDialog()
        val intent = when {
            !casaAndFicaCheckStatus.casaApproved -> IntentFactory.getUnableToContinueScreen(exploreHubFragment.activity, R.string.unable_to_continue, R.string.risk_based_approach_casa_failure_message)
            !casaAndFicaCheckStatus.ficaApproved -> IntentFactory.getUnableToContinueScreen(exploreHubFragment.activity, R.string.unable_to_continue, R.string.risk_based_approach_fica_failure_message)
            else -> Intent(exploreHubFragment.activity, FuturePlanActivity::class.java).putExtra(CASA_REFERENCE, casaAndFicaCheckStatus.casaReference)
        }
        exploreHubFragment.startActivity(intent)
    }

    private fun onOfferClick() {
        trackAction(FUTURE_PLAN, "FuturePlan_ExploreScreen_DashboardFuturePlanCardClicked")
        exploreHubFragment.performCasaAndFicaCheck(this, CheckType.ALL)
    }
}