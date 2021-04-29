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

package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto.Voucher
import com.barclays.absa.utils.ImageUtils.convertImageToBitmapFromBase64
import com.barclays.absa.utils.ImageUtils.scaleBitmap
import kotlinx.android.synthetic.main.behavioural_rewards_claim_reward_item.*

class BehaviouralRewardsVoucherItemFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_claim_reward_item) {

    private val animationDuration: Long = 200

    private var isSelected = false
    private var index = -1

    companion object {
        private const val ARG_VOUCHER = "voucher"
        private const val INDEX = "index"

        @JvmStatic
        fun newInstance(voucher: Voucher, position: Int): BehaviouralRewardsVoucherItemFragment {
            return BehaviouralRewardsVoucherItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_VOUCHER, voucher)
                    putInt(INDEX, position)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showToolBar()

        arguments?.let { bundle ->
            index = bundle.getInt(INDEX)
            bundle.getParcelable<Voucher>(ARG_VOUCHER)?.let { voucher ->
                isSelected = voucher.isSelected
                voucherTitleTextView.text = voucher.title
                voucherMessageTextView.text = voucher.message
                if (voucher.imageData.isNotEmpty()) {
                    convertImageToBitmapFromBase64(voucher.imageData)?.let {
                        val scaleBitmap = scaleBitmap(it, resources.getDimensionPixelOffset(R.dimen._95sdp))
                        voucherImageView.setImageBitmap(scaleBitmap)
                    }
                }
            }
        }
    }

    fun selectVoucher() {
        if (!isSelected) {
            isSelected = true
            contentConstraintLayout.elevation = resources.getDimension(R.dimen.extra_tiny_space)
            contentConstraintLayout.animate().scaleX(1.05f).scaleY(1.05f).setDuration(animationDuration).start()
            (selectedImageView.drawable as TransitionDrawable).startTransition(700)
        } else {
            unselectVoucher()
        }
    }

    private fun unselectVoucher() {
        isSelected = false
        contentConstraintLayout.elevation = 0f
        contentConstraintLayout.animate().scaleX(1f).scaleY(1f).setDuration(animationDuration).start()
        (selectedImageView.drawable as TransitionDrawable).resetTransition()
    }

    fun resetFragment() {
        if (isSelected) {
            unselectVoucher()
        }
    }
}