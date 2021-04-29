/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */
package com.barclays.absa.banking.card.ui.creditCard.hub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.creditCardInsurance.LifeInsurance
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.funeralCover.ui.FuneralCoverCardLifePlanActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.PdfUtil.showPDFInApp
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.credit_card_insurance_fragment.*

class CreditCardInsuranceFragment : ItemPagerFragment() {
    private lateinit var creditCardHubActivity: CreditCardHubActivity
    private var creditCardNumber: String? = null
    private var hasInsurancePolicy = false
    private val appCacheService: IAppCacheService = getServiceInterface()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        creditCardHubActivity = context as CreditCardHubActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.credit_card_insurance_fragment, container, false).rootView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFeatureSwitchingVisibilityToggles()
        findOutMoreButton.setOnClickListener { openFindOutMorePdf() }
        getCoverButton.setOnClickListener { applyForCover() }
        creditCardNumber = arguments?.getString(CREDIT_CARD_NUMBER)

        (arguments?.getSerializable(CREDIT_CARD_INSURANCE) as? LifeInsurance)?.let {
            if ("true".equals(it.policyExist, ignoreCase = true)) {
                showCreditCardInsurancePolicyDetails(it)
                hasInsurancePolicy = true
            } else if ("false".equals(it.policyExist, ignoreCase = true)) {
                getCoverButton.visibility = View.VISIBLE
                val creditProtection = appCacheService.getCreditProtection()
                monthlyCostTitleAndDescriptionView.title = TextFormatUtils.formatBasicAmountAsRand(creditProtection?.monthlyPremium)
                AnalyticsUtil.trackAction("Life_CCProt_ApplyOption", "Credit Card Protection${BMBConstants.TRUE_CONST}")
            }
        }
        AnalyticsUtil.trackAction("Insurance tab", "Credit card hub")
    }

    override fun onResume() {
        super.onResume()
        if (creditCardHubActivity.shouldShowInsuranceQuoteFailureMessage) {
            showFailedToLoadQuoteMessage()
        }
        CreditCardHubActivity.hideKeyboard(creditCardHubActivity)
    }

    private fun showCreditCardInsurancePolicyDetails(lifeInsurance: LifeInsurance) {
        policyStartDateView.setLineItemViewContent(DateUtils.getDateWithMonthNameFromStringWithoutHyphen(lifeInsurance.commencementDate.toString()))
        cardLifeDetailsContainerConstraintLayout.visibility = View.VISIBLE
        getCoverButton.visibility = View.GONE
        topContainerConstraintLayout.visibility = View.GONE
        cardLifePolicyNumber.description = lifeInsurance.policyNumber
    }

    private fun openFindOutMorePdf() {
        var funeralCoverLink = "https://ib.absa.co.za/absa-online/assets/Assets/Richmedia/AFS/PDF/LearnMoreCreditProtection_${(if (BMBApplication.AFRIKAANS_CODE.equals(BMBApplication.getApplicationLocale().toString(), ignoreCase = true)) "AFR" else "ENG")}.pdf"
        funeralCoverLink = String.format(funeralCoverLink, if (creditCardHubActivity.intent.hasExtra(AppConstants.CLIENT_TYPE)) creditCardHubActivity.intent.extras?.getString(AppConstants.CLIENT_TYPE) else INSURANCE)
        showPDFInApp(creditCardHubActivity, funeralCoverLink)
        if (hasInsurancePolicy) {
            AnalyticsUtils.getInstance().trackCustomScreenView("Life_CCProt_FindOutMoreHaveHaveInsurance", "Credit Card Protection", BMBConstants.TRUE_CONST)
        } else {
            AnalyticsUtils.getInstance().trackCustomScreenView("Life_CCProt_ApplyFindOutMore", "Credit Card Protection", BMBConstants.TRUE_CONST)
        }
    }

    override fun getTabDescription(): String = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    private fun setupFeatureSwitchingVisibilityToggles() {
        if (featureSwitchingToggles.creditCardLifeInsurance == FeatureSwitchingStates.GONE.key) {
            getCoverButton.visibility = View.GONE
        }
    }

    private fun applyForCover() {
        if (featureSwitchingToggles.creditCardLifeInsurance == FeatureSwitchingStates.DISABLED.key) {
            startActivity(IntentFactory.capabilityUnavailable(creditCardHubActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_apply_for_credit_protection))))
        } else {
            creditCardNumber?.let { cardNumber ->
                Intent(creditCardHubActivity, FuneralCoverCardLifePlanActivity::class.java).apply {
                    putExtra(CreditCardHubActivity.CREDIT_CARD, cardNumber)
                    startActivity(this)
                }
            }
        }
    }

    private fun showFailedToLoadQuoteMessage() {
        insuranceTextView.title = getString(R.string.credit_card_hub_credit_protection)
        insuranceTextView.description = getString(R.string.credit_card_hub_insurance_description)
        getCoverButton.visibility = View.GONE
        monthlyCostTitleAndDescriptionView.visibility = View.GONE
        descriptionTextView.setText(R.string.quote_credit_protection)
    }

    companion object {
        const val CREDIT_CARD_INSURANCE = "CREDIT_CARD_INSURANCE"
        const val CREDIT_CARD_NUMBER = "CREDIT_CARD_NUMBER"
        const val INSURANCE = "I"

        @JvmStatic
        fun newInstance(description: String?, cardInsurance: LifeInsurance?, creditCardNumber: String): CreditCardInsuranceFragment {
            return CreditCardInsuranceFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CREDIT_CARD_INSURANCE, cardInsurance)
                    putString(CREDIT_CARD_NUMBER, creditCardNumber)
                    putString(TAB_DESCRIPTION_KEY, description)
                }
            }
        }
    }
}