/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.express.behaviouralRewards.acceptAndDisplayVoucherDetails.BehaviouralRewardsAcceptAndDisplayVoucherDetailsViewModel
import com.barclays.absa.banking.express.behaviouralRewards.acceptAndDisplayVoucherDetails.dto.BehaviouralRewardsAcceptAndDisplayVoucherDetailsRequest
import com.barclays.absa.banking.express.behaviouralRewards.fetchAvailableVouchers.dto.AvailableVouchersResponse
import com.barclays.absa.banking.express.behaviouralRewards.shared.Challenge
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto.CustomerHistoryVoucher
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto.Voucher
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto.VoucherDetails
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.behavioural_rewards_claim_reward_fragment.*

class BehaviouralRewardsClaimRewardFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_claim_reward_fragment), OnBackPressedInterface {

    private val voucherList = mutableListOf<Voucher>()
    private var challenge: Challenge = Challenge()
    private lateinit var voucherPagerAdapter: VoucherPagerAdapter
    private lateinit var acceptAndDisplayVoucherDetailsViewModel: BehaviouralRewardsAcceptAndDisplayVoucherDetailsViewModel
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    companion object {
        const val CHALLENGE_BUNDLE = "challengeBundle"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        acceptAndDisplayVoucherDetailsViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.behavioural_rewards_claim_your_voucher))
        behaviouralRewardsActivity.resetAppBarState()

        arguments?.let {
            challenge = it.getParcelable(CHALLENGE_BUNDLE) ?: BehaviouralRewardsChallengeTermsAndConditionsFragmentArgs.fromBundle(it).behaviouralRewardsChallenge
        }
        initViews()
        setUpOnClickListeners()

        trackAnalytics("ClaimYourVoucher_ScreenDisplayed")
    }

    fun initViews() {
        val availableVouchersResponse = fetchAvailableVouchersViewModel.availableVouchersResponseLiveData.value
        if (availableVouchersResponse != null) {
            setUpVouchers(availableVouchersResponse)
        } else {
            fetchAvailableVouchersViewModel.fetchAvailableVouchers()

            fetchAvailableVouchersViewModel.availableVouchersResponseLiveData.observe(viewLifecycleOwner, { availableVoucherResponse ->
                setUpVouchers(availableVoucherResponse)
                dismissProgressDialog()
            })
        }
    }

    private fun setUpVouchers(availableVoucherResponse: AvailableVouchersResponse) {
        fetchVoucherPartnerMetaDataViewModel.voucherPartnersMetaDataResponseLiveData.value?.apply {
            availableVoucherResponse.voucherPartners.forEach { voucherPartner ->
                voucherPartner.partnerOffers.forEach { partnerOffer ->
                    val voucherMetaData = voucherPartnerMetaData.find { it.partnerId == voucherPartner.partnerId }

                    voucherList.add(Voucher().apply {
                        title = voucherMetaData?.partnerName ?: ""
                        imageData = voucherMetaData?.partnerImage ?: ""
                        message = partnerOffer.offerDescription
                        voucherDetails = VoucherDetails().apply {
                            offerId = partnerOffer.offerId
                            offerTier = partnerOffer.offerTier
                            partnerId = voucherPartner.partnerId
                            challengeId = challenge.challengeId
                        }
                    })
                }
            }

            if (voucherList.isNotEmpty()) {
                setupViewPager()
            } else {
                MonitoringInteractor().logTechnicalEvent(this.javaClass.simpleName, "", "No available vouchers from partners")
                showMessage(getString(R.string.sorry_title), getString(R.string.something_went_wrong)) { _, _ ->
                    navigate(BehaviouralRewardsClaimRewardFragmentDirections.actionBehaviouralRewardsClaimRewardFragmentToBehaviouralRewardsHubFragment())
                }
            }
        }
    }

    private fun setupViewPager() {
        voucherPagerAdapter = VoucherPagerAdapter(voucherList, this@BehaviouralRewardsClaimRewardFragment)
        with(voucherViewPager) {
            adapter = voucherPagerAdapter

            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3

            val marginStartEnd = resources.getDimensionPixelSize(R.dimen.view_pager_padding)
            setPadding(marginStartEnd, 0, marginStartEnd, 0)
            setPageTransformer(MarginPageTransformer(resources.getDimensionPixelSize(R.dimen.extra_tiny_space)))

            viewPagerIndicator.setupWithViewPager(this)

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    voucherSelected(position)
                }
            })
        }
    }

    fun setUpOnClickListeners() {
        acceptVoucherButton.setOnClickListener {
            preventDoubleClick(it)
            trackAnalytics("ClaimYourVoucher_AcceptVoucherClicked")
            selectAndDisplayVoucher()
        }
    }

    private fun selectAndDisplayVoucher() {
        val selectedVoucher = voucherList.first { voucher -> voucher.isSelected }.voucherDetails
        acceptAndDisplayVoucherDetailsViewModel.acceptVoucherDetails(BehaviouralRewardsAcceptAndDisplayVoucherDetailsRequest().apply {
            offerId = selectedVoucher.offerId
            offerTier = selectedVoucher.offerTier
            partnerId = selectedVoucher.partnerId
            challengeId = selectedVoucher.challengeId
        })

        acceptAndDisplayVoucherDetailsViewModel.acceptVoucherDetailsResponseLiveData.observe(viewLifecycleOwner, { acceptAndDisplayVoucherDetails ->
            dismissProgressDialog()
            val customerVoucher = CustomerHistoryVoucher().apply {
                partnerId = acceptAndDisplayVoucherDetails.partnerId
                offerDescription = acceptAndDisplayVoucherDetails.offerDescription
                offerExpiryDateTime = acceptAndDisplayVoucherDetails.voucherExpiryDateTime
                rewardPinVoucher = acceptAndDisplayVoucherDetails.voucherPin
                voucherImage = acceptAndDisplayVoucherDetails.partnerImage
                isFromClaimScreen = true
            }
            rewardsCacheService.clearBehaviouralRewardsChallenges()
            navigate(BehaviouralRewardsClaimRewardFragmentDirections.actionBehaviouralRewardsClaimRewardFragmentToBehaviouralRewardsVoucherDetailsFragment(customerVoucher))
        })
    }

    fun voucherSelected(index: Int) {
        voucherList.forEach { it.isSelected = false }

        voucherList[index].isSelected = true
        voucherPagerAdapter.selectVoucher(index)
    }

    inner class VoucherPagerAdapter(private val voucherList: List<Voucher>, fragment: BehaviouralRewardsClaimRewardFragment) : FragmentStateAdapter(fragment) {
        private var selectedIndex = -1

        override fun createFragment(position: Int) = BehaviouralRewardsVoucherItemFragment.newInstance(voucherList[position], position)

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {

            if (childFragmentManager.fragments.size > position && selectedIndex != position) {
                val behaviouralRewardsVoucherItemFragment = childFragmentManager.fragments[position] as? BehaviouralRewardsVoucherItemFragment
                if (behaviouralRewardsVoucherItemFragment != null) {
                    behaviouralRewardsVoucherItemFragment.resetFragment()
                } else {
                    super.onBindViewHolder(holder, position, payloads)
                }
            }

            super.onBindViewHolder(holder, position, payloads)
        }

        override fun getItemCount() = voucherList.size

        fun selectVoucher(index: Int) {
            selectedIndex = index
            (childFragmentManager.fragments[index] as? BehaviouralRewardsVoucherItemFragment)?.selectVoucher()
            notifyDataSetChanged()
        }
    }

    override fun onBackPressed(): Boolean {
        navigate(BehaviouralRewardsClaimRewardFragmentDirections.actionBehaviouralRewardsClaimRewardFragmentToBehaviouralRewardsHubFragment())
        return true
    }
}