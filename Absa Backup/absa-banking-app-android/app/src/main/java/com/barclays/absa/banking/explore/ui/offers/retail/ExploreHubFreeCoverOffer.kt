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
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.freeCover.ui.FreeCoverActivity
import com.barclays.absa.banking.freeCover.ui.FreeCoverActivity.Companion.FREE_COVER_CASA_REFERENCE
import com.barclays.absa.banking.freeCover.ui.FreeCoverData
import com.barclays.absa.banking.presentation.shared.IntentFactory
import styleguide.cards.Offer
import styleguide.utils.extensions.toTitleCase

class ExploreHubFreeCoverOffer(var exploreHubInterface: NewExploreHubInterface, var freeCoverData: FreeCoverData?, var onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer() {
    constructor(exploreHubInterface: NewExploreHubInterface, freeCoverData: FreeCoverData?) : this(exploreHubInterface, freeCoverData, null)

    private var context = exploreHubInterface.context()
    override var offerTile: Offer = Offer(context.getString(R.string.free_cover_title).toTitleCase(), context.getString(R.string.free_cover_description), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_free_cover_small
    override var cardDescription: String = ""
    override var buttonText: String = context.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.freeCoverInsurance
    override var buildConfigToggle: Boolean = BuildConfig.TOGGLE_DEF_FREE_COVER_ENABLED
    override var additionalToggleChecks: Boolean = freeCoverData?.disableFreeCover.equals("false", true)
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            context.startActivity(IntentFactory.capabilityUnavailable(context as Activity, R.string.feature_unavailable, context.getString(R.string.feature_unavailable_message, context.getString(R.string.free_cover_title))))
        } else {
            exploreHubInterface.performCasaAndFicaCheck(this, CheckType.ALL)
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {
        exploreHubInterface.dismissProgressDialog()

        when {
            !casaAndFicaCheckStatus.casaApproved -> context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_casa_failure_message))
            !casaAndFicaCheckStatus.ficaApproved -> context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_fica_failure_message))
            else -> freeCoverData?.let { freeCoverData -> launchFreeCoverHub(freeCoverData, casaAndFicaCheckStatus.casaReference) }
        }
    }

    private fun launchFreeCoverHub(freeCoverData: FreeCoverData, casaReference: String) {
        Intent(context, FreeCoverActivity::class.java).apply {
            putExtra(FREE_COVER_CASA_REFERENCE, casaReference)
            putExtra(FreeCoverActivity.FREE_COVER_DATA, freeCoverData)
            context.startActivity(this)
        }

    }
}