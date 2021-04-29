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

import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.OverdraftGadgets
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.explore.ui.offers.OfferListener
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.overdraft.ui.IntentFactoryOverdraft
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.TextFormatUtils
import styleguide.cards.Offer
import styleguide.utils.extensions.toSentenceCase

private const val NEW_OVERDRAFT_LIMIT = "NEW_OVERDRAFT_LIMIT"

class ExploreHubOverdraftOffer(var exploreHubFragment: NewExploreHubFragment, private val overdraftGadgets: OverdraftGadgets?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer(), OfferListener {
    constructor(exploreHubFragment: NewExploreHubFragment, overdraftGadgets: OverdraftGadgets?) : this(exploreHubFragment, overdraftGadgets, null)

    private val overdraftNewLimit = overdraftGadgets?.getNewOverdraftLimitValue().toString()

    override var offerTile: Offer = Offer(exploreHubFragment.getString(R.string.overdraft).toSentenceCase(), exploreHubFragment.getString(R.string.overdraft_intro_qualify_title, TextFormatUtils.formatBasicAmount(overdraftNewLimit)), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_overdraft_vcl_small
    override var cardDescription: String = ""
    override var buttonText: String = exploreHubFragment.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.overdraftVCL
    override var buildConfigToggle: Boolean = true
    override var additionalToggleChecks: Boolean = NEW_OVERDRAFT_LIMIT.equals(overdraftGadgets?.offersOverdraftEnum, ignoreCase = true) && overdraftGadgets?.getNewOverdraftLimitValue() != 0.0
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        AnalyticsUtil.trackAction("Overdraft tile clicked")
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            exploreHubFragment.startActivity(IntentFactory.capabilityUnavailable(exploreHubFragment.activity, R.string.feature_unavailable, exploreHubFragment.getString(R.string.feature_unavailable_message, exploreHubFragment.getString(R.string.feature_switching__apply_for_overdraft))))
        } else {
            exploreHubFragment.startActivity(overdraftGadgets?.getNewOverdraftLimitValue()?.let { IntentFactoryOverdraft.getOverdraftIntro(exploreHubFragment.activity, it) })
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {}
}