/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.cluster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject
import com.barclays.absa.banking.explore.ui.ExploreHubRecyclerViewAdapter
import com.barclays.absa.banking.explore.ui.offers.ExploreHubBaseOffer
import com.barclays.absa.banking.explore.ui.offers.retail.*
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity
import com.barclays.absa.banking.home.ui.HomeContainerViewModel
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.unitTrusts.services.dto.FuneralData
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.explore_hub_fragment.*

class InsuranceClusterActivity : BaseClusterActivity(), ClusterView {
    private val homeCacheService: IHomeCacheService = getServiceInterface()
    private lateinit var homeContainerViewModel: HomeContainerViewModel
    private var offerArrayList = ArrayList<ExploreHubBaseOffer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClusterViews()
        initAdapter()
        homeContainerViewModel = viewModel()
    }

    private fun initClusterViews() {
        setClusterImageView(R.drawable.ic_insurance_small)
        setClusterTypeTextView(getString(R.string.cluster_insurance_heading))
        val insurancePolicies = homeCacheService.getInsurancePolicies()
        if (insurancePolicies.isNotEmpty()) {
            setNumberOfPolicies(insurancePolicies.size)
        }
    }

    private fun initAdapter() {
        val insurancePolicyList = ClusterHelper.getSortedPolicyListWithHeader(homeCacheService.getInsurancePolicies(), this)
        binding.accountsRecyclerView.adapter = ClusterAdapter(this, insurancePolicyList)
    }

    private fun setNumberOfPolicies(numberOfPolicies: Int) {
        when (numberOfPolicies) {
            1 -> binding.numberActivePoliciesTextView.text = getString(R.string.insurance_cluster_only_one_policy, numberOfPolicies)
            else -> binding.numberActivePoliciesTextView.text = getString(R.string.insurance_cluster_multiple_policies, numberOfPolicies)
        }
    }

    override fun buildOffers(offersResponseObject: OffersResponseObject) {
        if (!isBusinessAccount) {
            offerArrayList.apply {
                add(ExploreHubFlexiFuneralOffer(this@InsuranceClusterActivity, offersResponseObject.applyFlexiFuneralData))
                add(ExploreHubLawForYouOffer(this@InsuranceClusterActivity, offersResponseObject.applyLawForYou))
                add(ExploreHubUltimateProtectorOffer(this@InsuranceClusterActivity, offersResponseObject.applyUltimateProtectorData))

                if (offersResponseObject.applyFuneralData != null) {
                    add(ExploreHubOldFuneralCoverOffer(this@InsuranceClusterActivity, offersResponseObject.applyFuneralData, offersResponseObject))
                } else {
                    add(ExploreHubOldFuneralCoverOffer(this@InsuranceClusterActivity, populateFuneralData(offersResponseObject), offersResponseObject))
                }

                add(ExploreHubFreeCoverOffer(this@InsuranceClusterActivity, offersResponseObject.freeCoverData))
            }
        }

        if (!offerArrayList.filter { exploreHubBaseOffer -> exploreHubBaseOffer.isEnabled() }.isNullOrEmpty()) {
            binding.apply {
                resultImageView.visibility = View.GONE
                titleCenteredTitleView.visibility = View.GONE
                contentDescriptionView.visibility = View.GONE
                getMoreOffersRecyclerView.visibility = View.VISIBLE
                getMoreOffersRecyclerView.adapter = ExploreHubRecyclerViewAdapter(offerArrayList.filter { exploreHubBaseOffer -> exploreHubBaseOffer.isEnabled() })
            }
        } else {
            noOffersAvailable()
        }
    }

    override fun onCardClicked(entry: Entry) {
        val policy = entry as Policy
        homeContainerViewModel.policyDetailLiveData = MutableLiveData()
        homeContainerViewModel.fetchPolicyInformation(policy)
        attachObserver()
    }

    private fun attachObserver() {
        homeContainerViewModel.policyDetailLiveData.observe(this, {
            dismissProgressDialog()
            startActivity(Intent(this, InsurancePolicyClaimsBaseActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        })

        homeContainerViewModel.failureLiveData.observe(this, {
            dismissProgressDialog()
            showGenericErrorMessage()
        })
    }

    private fun populateFuneralData(successResponse: OffersResponseObject): FuneralData {
        return FuneralData().apply {
            if (successResponse.insurancePolicyHeaders.isNullOrEmpty()) {
                lowestPremium = successResponse.insurancePolicyHeaders?.first()?.premiumAmount
                policyFee = successResponse.policyFee
            }
        }
    }
}