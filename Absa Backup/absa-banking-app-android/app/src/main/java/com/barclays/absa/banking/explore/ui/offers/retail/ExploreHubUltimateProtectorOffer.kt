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
import com.barclays.absa.banking.ultimateProtector.services.dto.UltimateProtectorData
import com.barclays.absa.banking.ultimateProtector.ui.UltimateProtectorHostActivity
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import styleguide.cards.Offer

class ExploreHubUltimateProtectorOffer(var exploreHubInterface: NewExploreHubInterface, private val ultimateProtectorData: UltimateProtectorData?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer(), OfferListener {
    constructor(exploreHubInterface: NewExploreHubInterface, ultimateProtectorData: UltimateProtectorData?) : this(exploreHubInterface, ultimateProtectorData, null)

    private val context = exploreHubInterface.context()
    override var offerTile: Offer = Offer(context.getString(R.string.ultimate_protector_explore_title), context.getString(R.string.ultimate_protector_explore_description), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_life_cover_small
    override var cardDescription: String = ""
    override var buttonText: String = context.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.ultimateProtector
    override var buildConfigToggle: Boolean = BuildConfig.TOGGLE_DEF_ULTIMATE_PROTECTOR
    override var additionalToggleChecks: Boolean = ultimateProtectorData?.disableUP == false
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        if (featureSwitchingToggles.ultimateProtector == FeatureSwitchingStates.DISABLED.key) {
            context.startActivity(IntentFactory.capabilityUnavailable(context as Activity, R.string.feature_unavailable, context.getString(R.string.feature_unavailable_message, context.getString(R.string.feature_switching_ultimate_protector))))
        } else {
            ultimateProtectorData?.let {
                trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Apply")
                exploreHubInterface.performCasaAndFicaCheck(this, CheckType.ALL)
            }
        }
    }

    private fun launchUltimateProtectorHub(ultimateProtectorDataModel: UltimateProtectorData?, casaReference: String?) {
        Intent(context, UltimateProtectorHostActivity::class.java).apply {
            ultimateProtectorDataModel?.casaReference = casaReference
            putExtra(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_DATA, ultimateProtectorDataModel)
            context.startActivity(this)
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {
        exploreHubInterface.dismissProgressDialog()

        if (ultimateProtectorData != null) {
            if (!casaAndFicaCheckStatus.casaApproved) {
                trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_UP_Apply_Sanctions")
                context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_casa_failure_message))
            } else if (!casaAndFicaCheckStatus.ficaApproved) {
                trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_FICHold")
                context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_fica_failure_message))
            } else {
                launchUltimateProtectorHub(ultimateProtectorData, casaAndFicaCheckStatus.casaReference)
            }
        }
    }
}