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

import android.app.Activity
import android.content.Intent
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.cluster.ui.NewExploreHubInterface
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.explore.ui.offers.OfferListener
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.OperatorPermissionUtils
import styleguide.cards.Offer

class ExploreHubBusinessFixedDepositOffer(exploreHubInterface: NewExploreHubInterface, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer(), OfferListener {
    constructor(exploreHubFragment: NewExploreHubInterface) : this(exploreHubFragment, null)

    private val context = exploreHubInterface.context()
    override var offerTile: Offer = Offer(context.getString(R.string.fixed_deposit_account), context.getString(R.string.fixed_deposit_banner_title), "", "", false)
    override var offerBackgroundImage: Int = R.drawable.ic_fixed_deposit_small
    override var cardDescription: String = ""
    override var buttonText: String = context.getString(R.string.apply)
    override var featureToggle: Int = featureSwitchingToggles.fixedDepositApplicationSoleProprietor
    override var buildConfigToggle: Boolean = true
    override var additionalToggleChecks: Boolean = OperatorPermissionUtils.isMainUser() && CustomerProfileObject.instance.numberOfAuthorisations.toInt() < 2
    override var onClickListener: () -> Unit = { onOfferApplyButtonClickListener() }

    private fun onOfferApplyButtonClickListener() {
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            context.startActivity(IntentFactory.capabilityUnavailable(context as Activity, R.string.feature_unavailable, context.getString(R.string.feature_unavailable_message, context.getString(R.string.feature_switching_apply_fixed_deposit))))
        } else {
            context.startActivity(Intent(context, FixedDepositActivity::class.java))
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {}
}