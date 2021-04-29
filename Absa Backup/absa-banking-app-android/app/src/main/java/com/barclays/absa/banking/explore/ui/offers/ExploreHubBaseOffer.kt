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

package com.barclays.absa.banking.explore.ui.offers

import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import styleguide.cards.Offer

abstract class ExploreHubBaseOffer : OfferListener {
    protected var featureSwitchingToggles = FeatureSwitchingCache.featureSwitchingToggles

    abstract var offerTile: Offer
    abstract var offerBackgroundImage: Int
    abstract var cardDescription: String
    abstract var buttonText: String
    abstract var featureToggle: Int
    abstract var buildConfigToggle: Boolean
    abstract var additionalToggleChecks: Boolean
    abstract var onClickListener: () -> Unit

    fun isEnabled(): Boolean {
        return buildConfigToggle && featureToggle != FeatureSwitchingStates.GONE.key && additionalToggleChecks
    }
}