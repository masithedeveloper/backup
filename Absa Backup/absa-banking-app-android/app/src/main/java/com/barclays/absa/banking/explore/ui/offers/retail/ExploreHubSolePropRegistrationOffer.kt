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
import com.barclays.absa.banking.businessBanking.services.BusinessBankingAccountData
import com.barclays.absa.banking.businessBanking.ui.BusinessBankActivity
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.explore.ui.offers.OfferListener
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.IntentFactory
import styleguide.cards.Offer

class ExploreHubSolePropRegistrationOffer(var exploreHubFragment: NewExploreHubFragment, businessBankingAccountData: BusinessBankingAccountData?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer(), OfferListener {
    constructor(exploreHubFragment: NewExploreHubFragment, businessBankingAccountData: BusinessBankingAccountData?) : this(exploreHubFragment, businessBankingAccountData, null)

    override var offerTile: Offer = Offer(exploreHubFragment.getString(R.string.business_banking_offer_title), exploreHubFragment.getString(R.string.business_banking_offer_text), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_business_evolve_small
    override var cardDescription: String = ""
    override var buttonText: String = exploreHubFragment.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.soleProprietorRegistration
    override var buildConfigToggle: Boolean = true
    override var additionalToggleChecks: Boolean = businessBankingAccountData?.disableApply == false
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            exploreHubFragment.startActivity(IntentFactory.capabilityUnavailable(exploreHubFragment.activity, R.string.feature_unavailable, exploreHubFragment.getString(R.string.feature_unavailable_message, exploreHubFragment.getString(R.string.feature_switching_business_bank_account))))
        } else {
            exploreHubFragment.startActivity(Intent(exploreHubFragment.activity, BusinessBankActivity::class.java))
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {}
}