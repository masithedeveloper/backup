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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.rewards.behaviouralRewards.models.BehaviouralRewardsCarouselModel
import com.barclays.absa.utils.ListenerUtil
import kotlinx.android.synthetic.main.behavioural_rewards_cash_back_item.*


class BehaviouralRewardsCashBackItemFragment : Fragment(R.layout.behavioural_rewards_cash_back_item) {

    companion object {
        private const val CASH_BACK_ITEM = "cash_back_item"

        @JvmStatic
        fun newInstance(behaviouralRewardsCarouselModel: BehaviouralRewardsCarouselModel): BehaviouralRewardsCashBackItemFragment {
            return BehaviouralRewardsCashBackItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(CASH_BACK_ITEM, behaviouralRewardsCarouselModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<BehaviouralRewardsCarouselModel>(CASH_BACK_ITEM)?.let { behaviouralRewardsCarouselModel ->
            cashBackAmountTextView.text = behaviouralRewardsCarouselModel.title
            cashBackAmountDescriptionTextView.text = behaviouralRewardsCarouselModel.description

            val onClickInterface = ListenerUtil.getListener(this, OnClickInterface::class.java)

            if (behaviouralRewardsCarouselModel.actions.size == 1) {
                cashBackButton.visibility = View.VISIBLE
                cashBackButton.text = behaviouralRewardsCarouselModel.actions[0]
                cashBackButton.setOnClickListener { onClickInterface?.primaryOnClick() }
            } else if (behaviouralRewardsCarouselModel.actions.size == 2) {
                cashBackButton.visibility = View.GONE
                cashBackButtonsLinearLayout.visibility = View.VISIBLE
                leftFloatingActionButton.setTitleText(behaviouralRewardsCarouselModel.actions[0])
                rightFloatingActionButton.setTitleText(behaviouralRewardsCarouselModel.actions[1])
                leftFloatingActionButton.setImageResource(behaviouralRewardsCarouselModel.imageResource[0])
                rightFloatingActionButton.setImageResource(behaviouralRewardsCarouselModel.imageResource[1])
                leftFloatingActionButton.setOnClickListener { onClickInterface?.primaryOnClick() }
                rightFloatingActionButton.setOnClickListener { onClickInterface?.secondaryOnClick() }
            }
        }
    }
}

interface OnClickInterface {
    fun primaryOnClick()
    fun secondaryOnClick()
}