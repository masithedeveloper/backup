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
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.behaviouralRewards.acceptAndDisplayVoucherDetails.BehaviouralRewardsAcceptAndDisplayVoucherDetailsViewModel
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto.CustomerHistoryVoucher
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.utils.DateUtils.*
import com.barclays.absa.utils.ImageUtils.convertImageToBitmapFromBase64
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.behavioural_rewards_voucher_details_fragment.*

class BehaviouralRewardsVoucherDetailsFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_voucher_details_fragment), OnBackPressedInterface {

    private lateinit var viewModel: BehaviouralRewardsAcceptAndDisplayVoucherDetailsViewModel
    private lateinit var customerHistoryVoucher: CustomerHistoryVoucher

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            customerHistoryVoucher = BehaviouralRewardsVoucherDetailsFragmentArgs.fromBundle(it).customerHistoryVoucher
        }
        showToolBar()

        if (customerHistoryVoucher.isFromClaimScreen) {
            setToolBarNoBack(R.string.behavioural_rewards_voucher_details_title)
        } else {
            setToolBar(getString(R.string.behavioural_rewards_voucher_details_title))
        }

        setupVoucherDetails()
        setUpOnClickListeners()

        trackAnalytics("VoucherDetails_ScreenDisplayed")
    }

    fun setUpOnClickListeners() {
        sendToAFriendOptionActionButtonView.setOnClickListener {
            trackAnalytics("VoucherDetails_ShareVoucherClicked")
            navigate(BehaviouralRewardsVoucherDetailsFragmentDirections.actionBehaviouralRewardsVoucherDetailsFragmentToBehaviouralRewardsSendVoucherFragment(customerHistoryVoucher))
        }

        voucherDetailsDoneButton.setOnClickListener {
            trackAnalytics("VoucherDetails_DoneButtonClicked")
            navigate(BehaviouralRewardsVoucherDetailsFragmentDirections.actionBehaviouralRewardsVoucherDetailsFragmentToBehaviouralRewardsHubFragment())
        }

        termsAndConditionsOptionActionButtonView.setOnClickListener {
            trackAnalytics("VoucherDetails_PartnerTermsAndConditionsClicked")
            navigate(BehaviouralRewardsVoucherDetailsFragmentDirections.actionBehaviouralRewardsVoucherDetailsFragmentToBehaviouralRewardsVoucherTermsAndConditionsFragment(customerHistoryVoucher))
        }
    }

    private fun setupVoucherDetails() {
        customerHistoryVoucher.apply {
            voucherNameTextView.text = offerDescription
            voucherExpiryDateTextView.text = getString(R.string.behavioural_rewards_voucher_expiry_date, formatDate(offerExpiryDateTime, DASHED_DATETIME_PATTERN, SLASHED_DATE_PATTERN))
            voucherCodeTextView.text = rewardPinVoucher

            val voucherPartnerMetaData = fetchVoucherPartnerMetaDataViewModel.voucherPartnersMetaDataResponseLiveData.value?.voucherPartnerMetaData
            voucherPartnerMetaData?.find { it.partnerId == partnerId }?.let {
                termsAndConditions = it.partnerTermsAndConditions
                convertImageToBitmapFromBase64(it.partnerImage)?.let { bitmapPartnerImage ->
                    voucherImageView.setImageBitmap(bitmapPartnerImage)
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        navigate(BehaviouralRewardsVoucherDetailsFragmentDirections.actionBehaviouralRewardsVoucherDetailsFragmentToBehaviouralRewardsHubFragment())
        return true
    }
}