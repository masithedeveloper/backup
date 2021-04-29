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

package com.barclays.absa.banking.explore.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.LottieAnimationView
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.cluster.ui.NewExploreHubInterface
import com.barclays.absa.banking.directMarketing.DirectMarketingActivity
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicators
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.explore.ui.offers.business.ExploreHubBusinessFixedDepositOffer
import com.barclays.absa.banking.explore.ui.offers.business.ExploreHubBusinessVCLOffer
import com.barclays.absa.banking.explore.ui.offers.retail.*
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.unitTrusts.services.dto.FuneralData
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustData
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.BannerManager
import kotlinx.android.synthetic.main.explore_hub_fragment.*
import styleguide.cards.Alert
import styleguide.cards.Offer
import styleguide.utils.NotifyingLinearLayoutManager

class NewExploreHubFragment : BaseFragment(R.layout.explore_hub_fragment), NewExploreHubInterface {
    companion object {
        const val NEW_OVERDRAFT_LIMIT = "NEW_OVERDRAFT_LIMIT"

        const val LAW_FOR_YOU_CASA_REFERENCE = "LAW_FOR_YOU_CASA_REFERENCE"
        const val LAW_FOR_YOU_OFFERS_RESPONSE = "LAW_FOR_YOU_OFFERS_RESPONSE"

        private const val DIRECT_MARKETING_INDICATORS = "DIRECT_MARKETING_INDICATORS"
        private const val NUMBER_OF_TIMES_LARGE_BANNER_SHOULD_BE_DISPLAYED = 3

        @JvmStatic
        fun newInstance() = NewExploreHubFragment()
    }

    private lateinit var exploreHubViewModel: ExploreHubViewModel

    private lateinit var offerArrayList: ArrayList<ExploreHubBaseOffer>

    private var offersResponseObject: OffersResponseObject? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        exploreHubViewModel = (context as ExploreHubHostInterface).exploreHubViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackAction("Explore", "Explore hub")
        AnalyticsUtils.getInstance().trackCustomScreen("ExploreHub_OffersScreen", "ExploreHub_OffersScreen", BMBConstants.TRUE_CONST)
        resultImageView.visibility = View.GONE

        if (!::exploreHubViewModel.isInitialized) {
            exploreHubViewModel = (requireContext() as ExploreHubHostInterface).exploreHubViewModel()
        }
        exploreHubViewModel.apply {
            if (appCacheService.getExploreHubOffers() == null || appCacheService.shouldUpdateExploreHub()) {
                exploreOffers = MutableLiveData()
                fetchOffers()
                attachObserver()
            } else {
                buildOffers(appCacheService.getExploreHubOffers()!!)
            }
        }
    }

    private fun attachObserver() {
        exploreHubViewModel.exploreOffers.observe(viewLifecycleOwner, {
            appCacheService.setExploreHubOffers(it)
            appCacheService.setShouldUpdateExploreHub(false)
            buildOffers(it)
        })
    }

    private fun buildOffers(it: OffersResponseObject) {
        appCacheService.setExploreHubOffers(it)
        val isBusinessAccount: Boolean = baseActivity.isBusinessAccount

        if (!isBusinessAccount) {
            offerArrayList = ArrayList()
            with(offerArrayList) {
                add(ExploreHubFuturePlanOffer(this@NewExploreHubFragment))
                add(ExploreHubDepositorPlusOffer(this@NewExploreHubFragment))
                add(ExploreHubFlexiFuneralOffer(this@NewExploreHubFragment, it.applyFlexiFuneralData))
                add(ExploreHubLawForYouOffer(this@NewExploreHubFragment, it.applyLawForYou))
                add(ExploreHubPersonaLoanOffer(this@NewExploreHubFragment, it.personalLoanData))
                add(ExploreHubUltimateProtectorOffer(this@NewExploreHubFragment, it.applyUltimateProtectorData))
                add(ExploreHubUnitTrustOffer(this@NewExploreHubFragment, it.applyUnitTrustData, it.hasUnitTrustAccount, populateUnitTrustData(it)))
                add(ExploreHubFixedDepositOffer(this@NewExploreHubFragment))
                add(ExploreHubRetailVCLOffer(this@NewExploreHubFragment, it.buildVCLModel(), it.displayCreditCardGadget))

                if (it.applyFuneralData != null) {
                    add(ExploreHubOldFuneralCoverOffer(this@NewExploreHubFragment, it.applyFuneralData, it))
                } else {
                    add(ExploreHubOldFuneralCoverOffer(this@NewExploreHubFragment, populateFuneralData(it), it))
                }
                add(ExploreHubOverdraftOffer(this@NewExploreHubFragment, it.overdraftGadgets))
                add(ExploreHubCreditCardHotLeadsOffer(this@NewExploreHubFragment, it.showApplyCreditCard))
                add(ExploreHubSolePropRegistrationOffer(this@NewExploreHubFragment, it.businessAccountData))
                add(ExploreHubFreeCoverOffer(this@NewExploreHubFragment, it.freeCoverData))
            }
        } else {
            offerArrayList = ArrayList()
            with(offerArrayList) {
                add(ExploreHubBusinessFixedDepositOffer(this@NewExploreHubFragment))
                add(ExploreHubBusinessVCLOffer(this@NewExploreHubFragment, it.applyBusinessBankOverdraft))
            }
        }

        if (offerArrayList.isNotEmpty()) {
            exploreHubRecyclerView.removeAllViews()
            exploreHeadingView.visibility = View.VISIBLE

            val exploreHubLayoutManager = NotifyingLinearLayoutManager(requireContext())
            exploreHubRecyclerView.layoutManager = exploreHubLayoutManager
            exploreHubRecyclerView.adapter = ExploreHubRecyclerViewAdapter(offerArrayList.filter { exploreHubBaseOffer -> exploreHubBaseOffer.isEnabled() })

            exploreHubLayoutManager.layoutCompleteCallback = object : NotifyingLinearLayoutManager.OnLayoutCompleteCallback {
                override fun onLayoutComplete() {
                    dismissProgressDialog()
                }
            }
        } else {
            dismissProgressDialog()
            noOffersAvailable()
        }

        //For the time being this code is being kept, but long story short, it should be removed from Explore given that Manage Profile should be used instead
        if (!BuildConfig.TOGGLE_DEF_MANAGE_PROFILE_ENABLED && "N".equals(it.marketingIndicators?.nonCreditIndicator, ignoreCase = true)) {
            stayInTouchHeadingView.visibility = View.VISIBLE
            val numberOfTimesLargeBannerDisplayed = BannerManager.getLargeBannerShownCount()
            if (numberOfTimesLargeBannerDisplayed > NUMBER_OF_TIMES_LARGE_BANNER_SHOULD_BE_DISPLAYED) {
                createDirectMarketingSmallOfferBanner()
            } else {
                BannerManager.incrementLargeBannerShownCount()
                createDirectMarketingLargeOfferBanner()
            }
        }

        createFraudSmallOfferBanner()
    }

    private fun createDirectMarketingSmallOfferBanner() {
        with(directMarketingAlertView) {
            setAlert(Alert(getString(R.string.marketing_consent), ""))
            hideTextView()
            visibility = View.VISIBLE
            setOnClickListener {
                preventDoubleClick(this)
                navigateToDirectMarketingScreen(offersResponseObject?.marketingIndicators)
            }
        }
    }

    private fun createDirectMarketingLargeOfferBanner() {
        val marketingConsent = Offer("", getString(R.string.hear_about_more_exciting_offers), "", "", false)
        with(directMarketingOfferView) {
            offer = marketingConsent
            setCardViewColor(ContextCompat.getColor(requireContext(), R.color.light_grey))

            visibility = View.VISIBLE
            hideLabel()
            setOnClickListener {
                trackAction("Direct Marketing Banner Clicked")
                preventDoubleClick(this)
                navigateToDirectMarketingScreen(offersResponseObject?.marketingIndicators)
            }
        }
    }

    private fun createFraudSmallOfferBanner() {
        fraudNotificationHeadingView.visibility = View.VISIBLE
        with(fraudNotificationAlertView) {
            setAlert(Alert(getString(R.string.fraud_notification_never_share), ""))
            setImage(R.drawable.ic_heart_dark)
            visibility = View.VISIBLE
            setOnClickListener {
                preventDoubleClick(this)
                startActivity(Intent(context, FraudNotificationAlertActivity::class.java))
            }
        }
    }

    private fun navigateToDirectMarketingScreen(successResponse: MarketingIndicators?) {
        Intent(activity, DirectMarketingActivity::class.java).apply {
            putExtra(DIRECT_MARKETING_INDICATORS, successResponse)
            startActivity(this)
        }
    }

    private fun populateFuneralData(successResponse: OffersResponseObject): FuneralData {
        return FuneralData().apply {
            if (successResponse.insurancePolicyHeaders?.isNullOrEmpty() == true) {
                lowestPremium = successResponse.insurancePolicyHeaders?.first()?.premiumAmount
                policyFee = successResponse.policyFee
            }
        }
    }

    private fun populateUnitTrustData(successResponse: OffersResponseObject): UnitTrustData {
        return UnitTrustData().apply {
            if (successResponse.allFundsMinDebitOrderAmt != null && successResponse.allFundsMinLumpSumAmt != null) {
                allFundsMinDebitOrderAmt = successResponse.allFundsMinDebitOrderAmt.toString()
                allFundsMinLumpSumAmt = successResponse.allFundsMinLumpSumAmt.toString()
                hasUnitTrustAccount = successResponse.hasUnitTrustAccount
                validateClientStatus = successResponse.validateClientStatus
            }
        }
    }

    private fun noOffersAvailable() {
        if (resultImageView is LottieAnimationView) {
            val lottieAnimationView = resultImageView as LottieAnimationView
            lottieAnimationView.setAnimation("blank_state.json")
        } else {
            resultImageView.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_blank_state)
        }
        resultImageView.visibility = View.VISIBLE
        titleCenteredTitleView.visibility = View.VISIBLE
        contentDescriptionView.visibility = View.VISIBLE
    }

    override fun onDetach() {
        if (::exploreHubViewModel.isInitialized) {
            exploreHubViewModel.clearExploreHubData()
        }

        super.onDetach()
    }

    override fun context(): Context {
        return requireContext()
    }

    override fun performCasaAndFicaCheck(exploreHubOffer: ExploreHubBaseOffer, checkType: CheckType) {
        if (exploreHubViewModel.casaAndFicaCheckStatus.value == null) {
            exploreHubViewModel.fetchCasaAndFicaStatus(checkType)

            exploreHubViewModel.casaAndFicaCheckStatus.removeObservers(viewLifecycleOwner)
        }

        exploreHubViewModel.casaAndFicaCheckStatus.observe(viewLifecycleOwner, {
            exploreHubOffer.onCasaAndFicaCallResponse(it)
        })
    }
}