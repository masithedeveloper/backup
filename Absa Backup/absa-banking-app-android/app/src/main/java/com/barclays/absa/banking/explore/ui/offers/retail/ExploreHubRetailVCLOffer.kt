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
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardGadget
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel
import com.barclays.absa.banking.card.ui.CardCache
import com.barclays.absa.banking.card.ui.creditCard.vcl.CreditCardVCLBaseActivity
import com.barclays.absa.banking.card.ui.creditCard.vcl.CreditCardVCLQualifyActivity
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.TextFormatUtils
import styleguide.cards.Offer
import styleguide.utils.extensions.toSentenceCase

private const val CREDIT_CARD_OFFER_AVAILABLE = "CREDIT_CARD_CLI"

class ExploreHubRetailVCLOffer(var exploreHubFragment: NewExploreHubFragment, private val vclParcelableModel: VCLParcelableModel?, creditCardGadget: CreditCardGadget?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer() {
    constructor(exploreHubFragment: NewExploreHubFragment, vclParcelableModel: VCLParcelableModel?, creditCardGadget: CreditCardGadget?) : this(exploreHubFragment, vclParcelableModel, creditCardGadget, null)

    override var offerTile: Offer = Offer(exploreHubFragment.getString(R.string.vcl_qualify_title).toSentenceCase(), exploreHubFragment.getString(R.string.vcl_offer_increase_limit, TextFormatUtils.formatBasicAmountAsRand(creditCardGadget?.creditLimitIncreaseAmount)), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_credit_card_small
    override var cardDescription: String = ""
    override var buttonText: String = exploreHubFragment.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.creditCardVCL
    override var buildConfigToggle: Boolean = true
    override var additionalToggleChecks: Boolean = CREDIT_CARD_OFFER_AVAILABLE.equals(creditCardGadget?.offersCreditCardEnum, ignoreCase = true)
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        exploreHubFragment.dismissProgressDialog()

        CardCache.getInstance().storeVCLParcelableModel(vclParcelableModel)
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            exploreHubFragment.startActivity(IntentFactory.capabilityUnavailable(exploreHubFragment.activity, R.string.feature_unavailable, exploreHubFragment.getString(R.string.feature_unavailable_message, exploreHubFragment.getString(R.string.feature_switching_apply_for_vcl))))
        } else {
            Intent(exploreHubFragment.activity, CreditCardVCLQualifyActivity::class.java).apply {
                putExtra(CreditCardVCLBaseActivity.IS_FROM_EXPLORE, true)
                putExtra(CreditCardVCLBaseActivity.VCL_DATA, vclParcelableModel)
                exploreHubFragment.startActivity(this)
                AnalyticsUtil.trackAction("Credit Card", "Credit Card VCL from Explore")
            }
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {}
}