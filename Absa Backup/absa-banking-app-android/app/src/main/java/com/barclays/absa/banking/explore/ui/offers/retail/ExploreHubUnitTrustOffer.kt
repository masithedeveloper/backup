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
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.explore.ui.offers.OfferListener
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustData
import com.barclays.absa.banking.unitTrusts.ui.buy.BuyUnitTrustHostActivity
import styleguide.cards.Offer

class ExploreHubUnitTrustOffer(var exploreHubInterface: NewExploreHubInterface, private val unitTrustData: UnitTrustData?, val hasUnitTrustAccount: Boolean?, private val buyUnitTrustModel: UnitTrustData?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer(), OfferListener {
    constructor(exploreHubInterface: NewExploreHubInterface, unitTrustData: UnitTrustData?, hasUnitTrustAccount: Boolean?, buyUnitTrustModel: UnitTrustData?) : this(exploreHubInterface, unitTrustData, hasUnitTrustAccount, buyUnitTrustModel, null)

    private val context = exploreHubInterface.context()

    override var offerTile: Offer = Offer(context.getString(R.string.buy_unit_trust_explore_title), context.getString(R.string.buy_unit_trust_explore_description), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_unit_trust_small
    override var cardDescription: String = ""
    override var buttonText: String = context.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.buyUnitTrusts
    override var buildConfigToggle: Boolean = BuildConfig.TOGGLE_DEF_BUY_UNIT_TRUST_ENABLED
    override var additionalToggleChecks: Boolean = buyUnitTrustModel?.hasUnitTrustAccount == false
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        when {
            featureToggle == FeatureSwitchingStates.DISABLED.key -> {
                context.startActivity(IntentFactory.capabilityUnavailable(context as Activity, R.string.feature_unavailable, context.getString(R.string.feature_unavailable_message, context.getString(R.string.feature_switching_buy_unit_trust))))
            }
            buyUnitTrustModel?.validateClientStatus == true -> {
                exploreHubInterface.performCasaAndFicaCheck(this, CheckType.FICA)
            }
            else -> {
                context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.buy_unit_trust_unable_to_continue_message_rba))
            }
        }
    }

    private fun launchUnitTrustHub() {
        Intent(context, BuyUnitTrustHostActivity::class.java).apply {
            putExtra(BuyUnitTrustHostActivity.UNIT_TRUST_DATA, buyUnitTrustModel)
            context.startActivity(this)
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {
        exploreHubInterface.dismissProgressDialog()

        if (!casaAndFicaCheckStatus.ficaApproved) {
            context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_fica_failure_message))
        } else {
            launchUnitTrustHub()
        }
    }
}