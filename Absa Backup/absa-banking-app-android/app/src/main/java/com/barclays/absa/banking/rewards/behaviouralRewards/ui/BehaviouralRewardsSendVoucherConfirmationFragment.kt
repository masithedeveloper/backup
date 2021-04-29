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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.behaviouralRewards.shareVoucher.BehaviouralRewardsShareVoucherViewModel
import com.barclays.absa.banking.express.behaviouralRewards.shareVoucher.dto.BehaviouralRewardsShareVoucher
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.behavioural_rewards_send_voucher_confirmation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class BehaviouralRewardsSendVoucherConfirmationFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_send_voucher_confirmation_fragment) {
    private lateinit var behaviouralRewardsShareVoucherViewModel: BehaviouralRewardsShareVoucherViewModel
    private lateinit var behaviouralRewardsShareVoucher: BehaviouralRewardsShareVoucher

    override fun onAttach(context: Context) {
        super.onAttach(context)
        behaviouralRewardsShareVoucherViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            behaviouralRewardsShareVoucher = BehaviouralRewardsSendVoucherConfirmationFragmentArgs.fromBundle(it).shareVoucher
        }
        setToolBar(getString(R.string.behavioural_rewards_confirm_title))
        initViews()
        setUpOnClickListeners()

        trackAnalytics("ShareVoucherConfirmation_ScreenDisplayed")
    }

    fun initViews() {
        contactNumberSecondaryContentAndLabelView.setContentText(behaviouralRewardsShareVoucher.cellNumber)
        voucherSecondaryContentAndLabelView.setContentText(behaviouralRewardsShareVoucher.offerDescription)
    }

    private fun setUpOnClickListeners() {
        sendVoucherButton.setOnClickListener {
            behaviouralRewardsShareVoucherViewModel.shareVoucher(behaviouralRewardsShareVoucher)
            behaviouralRewardsShareVoucherViewModel.shareVoucherLiveData.observe(viewLifecycleOwner, Observer {
                dismissProgressDialog()
                launchSuccessScreen()
            })

            trackAnalytics("ShareVoucherConfirmation_SendVoucherButtonClicked")
        }
    }

    private fun launchSuccessScreen() {
        hideToolBar()
        behaviouralRewardsActivity.resetAppBarState()
        val description = getString(R.string.behavioural_rewards_success_voucher_details, behaviouralRewardsShareVoucher.cellNumber)
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.behavioural_rewards_success_title))
                .setDescription(description, arrayOf(description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            trackAnalytics("ShareVoucherResult_DoneButtonClicked")
            findNavController().navigate(R.id.action_genericResultScreenFragment_to_behaviouralRewardsHubFragment)
        }

        navigate(BehaviouralRewardsSendVoucherConfirmationFragmentDirections.actionBehaviouralRewardsSendVoucherConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }
}