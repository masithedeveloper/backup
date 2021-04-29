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
 *
 */
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto.CustomerHistoryVoucher
import com.barclays.absa.utils.DateUtils.*
import com.barclays.absa.utils.ImageUtils
import kotlinx.android.synthetic.main.behavioural_rewards_voucher_item.view.*

class BehaviouralRewardsVoucherAdapter(val vouchers: ArrayList<CustomerHistoryVoucher>, val rewardsVoucherClick: RewardsVoucherClick) : RecyclerView.Adapter<BehaviouralRewardsVoucherAdapter.VoucherViewHolder>() {

    companion object {
        const val VOUCHER_EXPIRED = "EXPIRED"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder = VoucherViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.behavioural_rewards_voucher_item, parent, false))

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = vouchers[position]
        holder.onBind(voucher)
    }

    override fun getItemCount(): Int = vouchers.size

    inner class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var voucherTitleTextView: TextView = itemView.voucherTitleTextView
        var voucherStatusTextView: TextView = itemView.voucherStatusTextView
        var voucherButton: Button = itemView.voucherButton
        var logoImageView: ImageView = itemView.logoImageView
        var parentConstraintLayout: ConstraintLayout = itemView.parentConstraintLayout

        fun onBind(voucher: CustomerHistoryVoucher) {
            with(voucher) {
                voucherTitleTextView.text = offerDescription

                if (VOUCHER_EXPIRED.equals(voucher.offerStatus, ignoreCase = true)) {
                    parentConstraintLayout.background = ContextCompat.getDrawable(itemView.context, R.drawable.ic_behavioural_card_expired)
                    voucherStatusTextView.text = itemView.context.getString(R.string.behavioural_rewards_voucher_expired_on, formatDate(offerExpiryDateTime, DASHED_DATETIME_PATTERN, SLASHED_DATE_PATTERN))
                    voucherButton.visibility = View.GONE
                } else {
                    parentConstraintLayout.background = ContextCompat.getDrawable(itemView.context, R.drawable.ic_behavioural_card)
                    voucherStatusTextView.text = itemView.context.getString(R.string.behavioural_rewards_voucher_viewed_on, format(redemptionDate, SLASHED_DATE_PATTERN))
                    voucherButton.visibility = View.VISIBLE
                }

                voucherButton.setOnClickListener { rewardsVoucherClick.onRewardsVoucherClicked(this) }

                if (voucherImage.isNotEmpty()) {
                    ImageUtils.convertImageToBitmapFromBase64(voucherImage)?.let { bitmapPartnerImage ->
                        logoImageView.setImageBitmap(bitmapPartnerImage)
                    }
                }
            }
        }
    }
}

interface RewardsVoucherClick {
    fun onRewardsVoucherClicked(voucher: CustomerHistoryVoucher)
}