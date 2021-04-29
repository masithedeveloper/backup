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
import com.barclays.absa.banking.flexiFuneral.services.dto.FlexiFuneralData
import com.barclays.absa.banking.flexiFuneral.ui.FlexiFuneralActivity
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import styleguide.cards.Offer
import styleguide.utils.extensions.toTitleCase

class ExploreHubFlexiFuneralOffer(var exploreHubInterface: NewExploreHubInterface, var flexiFuneralData: FlexiFuneralData?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer() {
    constructor(exploreHubInterface: NewExploreHubInterface, flexiFuneralData: FlexiFuneralData?) : this(exploreHubInterface, flexiFuneralData, null)

    private var context = exploreHubInterface.context()

    override var offerTile: Offer = Offer(context.getString(R.string.flexi_funeral_cover_title).toTitleCase(), context.getString(R.string.flexi_funeral_peace_of_mind_for_you_and_your_family), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_flexi_funeral_small
    override var cardDescription: String = ""
    override var buttonText: String = context.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.wimiFlexiFuneralCover
    override var buildConfigToggle: Boolean = BuildConfig.TOGGLE_DEF_FLEXI_FUNERAL_ENABLED
    override var additionalToggleChecks: Boolean = flexiFuneralData?.disableFlexi.equals("false", ignoreCase = true) && !flexiFuneralData?.lowestPremium.isNullOrEmpty()
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }


    private fun onOfferApplyButtonClickListener() {
        trackAction("WIMI_FlexiFuneral", "FlexiFuneral_ExploreHub_Apply")
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            context.startActivity(IntentFactory.capabilityUnavailable(context as Activity, R.string.feature_unavailable, context.getString(R.string.feature_unavailable_message, context.getString(R.string.feature_switching_apply_flexi_funeral))))
        } else {
            exploreHubInterface.performCasaAndFicaCheck(this, CheckType.ALL)
        }
    }

    private fun launchFlexiFuneralHub(flexiFuneralData: FlexiFuneralData, casaReference: String) {
        Intent(context, FlexiFuneralActivity::class.java).apply {
            flexiFuneralData.casaReference = casaReference
            putExtra(FlexiFuneralActivity.FLEXI_FUNERAL_DATA, flexiFuneralData)
            context.startActivity(this)
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {
        exploreHubInterface.dismissProgressDialog()

        if (!casaAndFicaCheckStatus.casaApproved) {
            trackAction("WIMI_FlexiFuneral", "FlexiFuneral_CASAError_ScreenDisplayed")
            context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_casa_failure_message))
        } else if (!casaAndFicaCheckStatus.ficaApproved) {
            trackAction("WIMI_FlexiFuneral", "FlexiFuneral_FICAError_ScreenDisplayed")
            context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_fica_failure_message))
        } else {
            flexiFuneralData?.let { flexiFuneralData -> launchFlexiFuneralHub(flexiFuneralData, casaAndFicaCheckStatus.casaReference) }
        }
    }
}