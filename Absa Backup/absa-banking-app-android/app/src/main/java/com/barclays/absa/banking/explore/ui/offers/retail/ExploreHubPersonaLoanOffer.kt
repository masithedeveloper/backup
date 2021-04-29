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
import android.os.Parcelable
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.explore.CasaAndFicaCheckStatus
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.explore.ui.offers.OfferListener
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.personalLoan.services.CreditLimitsResponse
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanApplyActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.TextFormatUtils
import styleguide.cards.Offer
import styleguide.utils.extensions.toDoubleString
import styleguide.utils.extensions.toSentenceCase
import java.util.*

class ExploreHubPersonaLoanOffer(var exploreHubFragment: NewExploreHubFragment, var personalLoanData: CreditLimitsResponse?, val onOfferClicked: (() -> Unit)? = null) : ExploreHubBaseOffer(), OfferListener {
    constructor(exploreHubFragment: NewExploreHubFragment, personalLoanData: CreditLimitsResponse?) : this(exploreHubFragment, personalLoanData, null)

    override var offerTile: Offer = if (!personalLoanData?.creditLimits.isNullOrEmpty()) {
        val maximumCreditLimitIndex = personalLoanData?.creditLimits?.size?.minus(1)
        Offer(exploreHubFragment.getString(R.string.personal_loan_title), exploreHubFragment.getString(R.string.personal_loan_preapproved_amount_description, TextFormatUtils.formatBasicAmountAsRand(maximumCreditLimitIndex?.let { personalLoanData?.creditLimits?.get(it)?.amount.toDoubleString() })), "", "", false)
    } else {
        Offer(exploreHubFragment.getString(R.string.personal_loan_title).toSentenceCase(), exploreHubFragment.getString(R.string.personal_loan_non_preapproved_description), "", "", false)
    }

    override var offerBackgroundImage: Int = R.drawable.ic_personal_loan_small
    override var buttonText: String = exploreHubFragment.getString(R.string.apply)
    override var cardDescription: String = ""
    override var featureToggle: Int = featureSwitchingToggles.personalLoan
    override var buildConfigToggle: Boolean = BuildConfig.TOGGLE_DEF_PERSONAL_LOANS_ENABLED
    override var additionalToggleChecks: Boolean = true
    override var onClickListener: () -> Unit = { applyForPersonalLoan() }

    private fun applyForPersonalLoan() {
        AnalyticsUtil.trackAction("Personal Loans", "PersonalLoans_ExploreScreen_ApplyButtonClicked")
        if (featureToggle == FeatureSwitchingStates.DISABLED.key) {
            exploreHubFragment.startActivity(IntentFactory.capabilityUnavailable(exploreHubFragment.activity, R.string.feature_unavailable, exploreHubFragment.getString(R.string.feature_unavailable_message, exploreHubFragment.getString(R.string.feature_switching_apply_for_personal_loan_vcl))))
        } else {
            Intent(exploreHubFragment.activity, PersonalLoanApplyActivity::class.java).apply {
                putParcelableArrayListExtra(PersonalLoanApplyActivity.MAXIMUM_CREDIT_LIMIT_LIST, personalLoanData?.creditLimits as ArrayList<out Parcelable?>)
                exploreHubFragment.startActivity(this)
            }
        }
    }

    override fun onCasaAndFicaCallResponse(casaAndFicaCheckStatus: CasaAndFicaCheckStatus) {}
}