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
import android.os.Parcelable
import com.barclays.absa.banking.R
import com.barclays.absa.banking.cluster.ui.NewExploreHubInterface
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.explore.ui.CheckType
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.ReferenceCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.funeralCover.ui.FuneralCoverApplyNowActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.unitTrusts.services.dto.FuneralData
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.TextFormatUtils
import styleguide.cards.Offer
import styleguide.utils.extensions.toSentenceCase

class ExploreHubOldFuneralCoverOffer(var exploreHubInterface: NewExploreHubInterface, private val funeralData: FuneralData?, private val offerResponseObject: OffersResponseObject?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer() {
    constructor(exploreHubInterface: NewExploreHubInterface, funeralData: FuneralData?, offerResponseObject: OffersResponseObject?) : this(exploreHubInterface, funeralData, offerResponseObject, null)

    private var context = exploreHubInterface.context()
    private var formattedPremiumAmount: String = if (!funeralData?.policyFee.isNullOrEmpty()) TextFormatUtils.formatBasicAmount(funeralData?.lowestPremium) else ""

    override var offerTile: Offer = Offer(context.getString(R.string.funeral_cover_title).toSentenceCase(), context.getString(R.string.funeral_cover_buy_hub_message), String.format(context.getString(R.string.funeral_cover_from), formattedPremiumAmount), context.getString(R.string.funeral_per_month), false)
    override var offerBackgroundImage: Int = R.drawable.ic_old_funeral_cover_apply_fragment
    override var cardDescription: String = ""
    override var buttonText: String = context.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.oldFuneralCoverApply
    override var buildConfigToggle: Boolean = true
    override var additionalToggleChecks: Boolean = !funeralData?.lowestPremium.isNullOrEmpty() && !funeralData?.policyFee.isNullOrEmpty()
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }
    private val appCacheService: IAppCacheService = getServiceInterface()

    private fun onOfferApplyButtonClickListener() {
        appCacheService.setPolicyFee(funeralData?.policyFee ?: "")

        trackAction("Funeral cover tile clicked")
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            context.startActivity(IntentFactory.capabilityUnavailable(context as Activity, R.string.feature_unavailable, context.getString(R.string.feature_unavailable_message, context.getString(R.string.feature_switching_funeral_cover))))
        } else {
            AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.BUY_HUB_CONST, "Buy hub funeral cover tile clicked", BMBConstants.TRUE_CONST)
            AnalyticsUtils.getInstance().trackCustomScreenView("WIMI_ Life_FC_Apply_BuyHubClick", BMBConstants.BUY_HUB_CONST, BMBConstants.TRUE_CONST)
            exploreHubInterface.performCasaAndFicaCheck(this, CheckType.ALL)
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {
        exploreHubInterface.dismissProgressDialog()

        if (!casaAndFicaCheckStatus.casaApproved) {
            context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_casa_failure_message))
        } else if (!casaAndFicaCheckStatus.ficaApproved) {
            context.startActivity(IntentFactory.getUnableToContinueScreen(context as Activity, R.string.unable_to_continue, R.string.risk_based_approach_fica_failure_message))
        } else {
            Intent(context, FuneralCoverApplyNowActivity::class.java).apply {
                offerTile.amount = offerTile.amount.replace(String.format(context.getString(R.string.funeral_cover_from), ""), "")
                putExtra(FuneralCoverApplyNowActivity.FUNERAL_OFFER, offerTile)
                putExtra(BMBConstants.FUNERAL_QUOTE_KEY, offerResponseObject as Parcelable?)
                context.startActivity(this)
                ReferenceCache.casaReference = casaAndFicaCheckStatus.casaReference
            }
        }
    }
}