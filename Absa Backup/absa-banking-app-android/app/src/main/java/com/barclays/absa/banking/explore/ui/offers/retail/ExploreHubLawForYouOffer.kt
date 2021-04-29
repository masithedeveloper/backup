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

package com.barclays.absa.banking.explore.ui.offers.retail

import android.app.Activity
import android.content.Intent
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.cluster.ui.NewExploreHubInterface
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.ui.CheckType
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.lawForYou.services.dto.ApplyLawForYou
import com.barclays.absa.banking.lawForYou.ui.LawForYouActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import styleguide.cards.Offer

class ExploreHubLawForYouOffer(var exploreHubInterface: NewExploreHubInterface, private var lawForYouOffer: ApplyLawForYou?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer() {
    constructor(exploreHubInterface: NewExploreHubInterface, lawForYouOffer: ApplyLawForYou?) : this(exploreHubInterface, lawForYouOffer, null)

    private var context = exploreHubInterface.context()

    override var offerTile: Offer = Offer(context.getString(R.string.law_for_you_title), context.getString(R.string.law_for_you_protect_yourself), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_law_for_you_small
    override var cardDescription: String = ""
    override var buttonText: String = context.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.wimiLawForYou
    override var buildConfigToggle: Boolean = BuildConfig.TOGGLE_DEF_LAW_FOR_YOU_ENABLED
    override var additionalToggleChecks: Boolean = lawForYouOffer?.disableLawForYou == false
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            context.startActivity(IntentFactory.capabilityUnavailable(context as Activity, R.string.feature_unavailable, context.getString(R.string.feature_unavailable_message, context.getString(R.string.feature_switching_law_for_you))))
        } else {
            trackAction("Law For You", "LawForYou_ExploreHub_ApplyTapped")
            exploreHubInterface.performCasaAndFicaCheck(this, CheckType.ALL)
        }
    }

    private fun startLawForYouActivity(lawForYouOffer: ApplyLawForYou, casaReference: String) {
        Intent(context, LawForYouActivity::class.java).apply {
            putExtra(NewExploreHubFragment.LAW_FOR_YOU_OFFERS_RESPONSE, lawForYouOffer)
            putExtra(NewExploreHubFragment.LAW_FOR_YOU_CASA_REFERENCE, casaReference)
            context.startActivity(this)
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {
        exploreHubInterface.dismissProgressDialog()

        if (!casaAndFicaCheckStatus.casaApproved) {
            trackAction("Law For You", "LawForYou_CASAError_ScreenDisplayed")
            context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_casa_failure_message))
        } else if (!casaAndFicaCheckStatus.ficaApproved) {
            trackAction("Law For You", "LawForYou_FICAError_ScreenDisplayed")
            context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_fica_failure_message))
        } else {
            lawForYouOffer?.let { startLawForYouActivity(it, casaAndFicaCheckStatus.casaReference) }
        }
    }
}