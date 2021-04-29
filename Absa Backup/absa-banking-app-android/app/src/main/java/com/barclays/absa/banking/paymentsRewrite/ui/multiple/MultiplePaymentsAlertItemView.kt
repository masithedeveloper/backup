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
 */

package com.barclays.absa.banking.paymentsRewrite.ui.multiple

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MultiplePaymentsAlertItemBinding

class MultiplePaymentsAlertItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var binding = MultiplePaymentsAlertItemBinding.inflate(LayoutInflater.from(context), this)

    init {
        background = ContextCompat.getDrawable(context, R.drawable.validation_alert_border)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            val sideMargins = resources.getDimensionPixelSize(R.dimen.medium_space)
            val topMargin = resources.getDimensionPixelSize(R.dimen.normal_space)
            setMargins(sideMargins, topMargin, sideMargins, 0)
        }

        with(binding.beneficiaryTitleAndDescriptionView) {
            setTitleTextColor(ContextCompat.getColor(context, R.color.light_red))
            setDescriptionTextColor(ContextCompat.getColor(context, R.color.pink))
        }

        with(binding.amountTitleAndDescriptionView) {
            setTitleTextColor(ContextCompat.getColor(context, R.color.light_red))
            setDescriptionTextColor(ContextCompat.getColor(context, R.color.pink))
        }
    }

    fun setBeneficiaryTitle(beneficiaryName: String) {
        binding.beneficiaryTitleAndDescriptionView.title = beneficiaryName
    }

    fun setAmountTitle(amount: String) {
        binding.amountTitleAndDescriptionView.title = amount
    }

    fun setAmountDescription(description: String) {
        binding.amountTitleAndDescriptionView.description = description
    }

    fun hideArrow() {
        binding.nextArrowImageView.visibility = View.GONE
    }

    fun showArrow() {
        binding.nextArrowImageView.visibility = View.VISIBLE
    }

    fun resetBorder() {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        background = null
    }

    fun setArrowIcon(@DrawableRes icon: Int) {
        binding.nextArrowImageView.setImageDrawable(ContextCompat.getDrawable(context, icon))
    }

    fun disableView() {
        setTitleTextColor(R.color.grey)
        setDescriptionTextColor(R.color.grey)
        setArrowIcon(R.drawable.ic_arrow_next_grey)
        resetBorder()
    }

    fun setTitleTextColor(@ColorRes color: Int) {
        binding.beneficiaryTitleAndDescriptionView.setTitleTextColor(ContextCompat.getColor(context, color))
        binding.amountTitleAndDescriptionView.setTitleTextColor(ContextCompat.getColor(context, color))
    }

    fun setDescriptionTextColor(@ColorRes color: Int) {
        binding.beneficiaryTitleAndDescriptionView.setDescriptionTextColor(ContextCompat.getColor(context, color))
        binding.amountTitleAndDescriptionView.setDescriptionTextColor(ContextCompat.getColor(context, color))
    }
}