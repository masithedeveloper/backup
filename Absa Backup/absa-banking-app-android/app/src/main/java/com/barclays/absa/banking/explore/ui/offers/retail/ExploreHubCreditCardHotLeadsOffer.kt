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

import android.content.Intent
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.ui.creditCardHotLeads.HotLeadsHostActivity
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil
import styleguide.cards.Offer
import styleguide.utils.extensions.toSentenceCase

class ExploreHubCreditCardHotLeadsOffer(var exploreHubFragment: NewExploreHubFragment, showApplyCreditCard: Boolean?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer() {
    constructor(exploreHubFragment: NewExploreHubFragment, showApplyCreditCard: Boolean?) : this(exploreHubFragment, showApplyCreditCard, null)

    override var offerTile: Offer = Offer(exploreHubFragment.getString(R.string.hot_leads_call_me_back_application).toSentenceCase(), exploreHubFragment.getString(R.string.hot_leads_credit_card_intro_qualify_title), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_card_hot_leads_small
    override var cardDescription: String = exploreHubFragment.getString(R.string.hot_leads_accept_terms_and_conditions_title)
    override var buttonText: String = exploreHubFragment.getString(R.string.hot_leads_see_more)
    override var featureToggle: Int = featureSwitchingToggles.creditCardHotLeads
    override var buildConfigToggle: Boolean = true
    override var additionalToggleChecks: Boolean = showApplyCreditCard == true
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        AnalyticsUtil.trackAction("Credit Card Hot Leads", "AcquisitionsHotLeads_ExploreScreen_SeeMoreButtonClicked")
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            exploreHubFragment.startActivity(IntentFactory.capabilityUnavailable(exploreHubFragment.activity, R.string.feature_unavailable, exploreHubFragment.getString(R.string.feature_unavailable_message, exploreHubFragment.getString(R.string.feature_switching_apply_for_personal_loan_vcl))))
        } else {
            exploreHubFragment.startActivity(Intent(exploreHubFragment.activity, HotLeadsHostActivity::class.java))
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {}
}