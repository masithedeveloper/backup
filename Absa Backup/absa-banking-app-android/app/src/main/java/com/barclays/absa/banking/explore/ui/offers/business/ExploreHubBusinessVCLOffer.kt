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

package com.barclays.absa.banking.explore.ui.offers.business

import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.services.dto.BusinessBankOverdraftData
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.explore.ui.offers.OfferListener
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.overdraft.ui.BusinessOverdraftActivity
import com.barclays.absa.banking.overdraft.ui.IntentFactoryOverdraft
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.TextFormatUtils
import styleguide.cards.Offer

class ExploreHubBusinessVCLOffer(var exploreHubFragment: NewExploreHubFragment, private val businessBankOverdraftData: BusinessBankOverdraftData?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer(), OfferListener {
    constructor(exploreHubFragment: NewExploreHubFragment, businessBankOverdraftData: BusinessBankOverdraftData?) : this(exploreHubFragment, businessBankOverdraftData, null)

    override var offerTile: Offer = Offer(exploreHubFragment.getString(R.string.emergency_funds), exploreHubFragment.getString(R.string.sole_prop_vcl_overdraft_intro_qualify_title, TextFormatUtils.formatBasicAmountAsRand(businessBankOverdraftData?.vclOfferAmt)), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_sole_prop_vcl_overdraft_small
    override var cardDescription: String = ""
    override var buttonText: String = exploreHubFragment.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.businessBankingOverdraftVCL
    override var buildConfigToggle: Boolean = BuildConfig.TOGGLE_DEF_VCL_BUSINESS_OVERDRAFT_ENABLED
    override var additionalToggleChecks: Boolean = businessBankOverdraftData?.disableApply == false && businessBankOverdraftData.vclOfferAmt.isNotEmpty()
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            exploreHubFragment.startActivity(IntentFactory.capabilityUnavailable(exploreHubFragment.baseActivity, R.string.feature_unavailable, exploreHubFragment.getString(R.string.feature_unavailable_message, exploreHubFragment.getString(R.string.feature_switching__apply_for_overdraft))))
        } else {
            AnalyticsUtil.trackAction(BusinessOverdraftActivity.BUSINESS_OVERDRAFT_ANALYTIC_TAG, "BBVCLOD_ExploreHub_TileTapped")
            exploreHubFragment.startActivity(IntentFactoryOverdraft.getVCLOverdraftIntro(exploreHubFragment.activity, businessBankOverdraftData))
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {}
}