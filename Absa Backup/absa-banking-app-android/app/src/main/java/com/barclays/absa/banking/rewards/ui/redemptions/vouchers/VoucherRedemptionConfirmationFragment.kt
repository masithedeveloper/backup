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
package com.barclays.absa.banking.rewards.ui.redemptions.vouchers

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.VoucherRedemptionConfirmationFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import styleguide.utils.extensions.toFormattedCellphoneNumber
import java.lang.ref.WeakReference

class VoucherRedemptionConfirmationFragment : AbsaBaseFragment<VoucherRedemptionConfirmationFragmentBinding>() {

    private lateinit var voucherRedemptionPresenter: VoucherRedemptionPresenter
    private lateinit var voucherRedemptionConfirmation: VoucherRedemptionConfirmationView

    private val handler = Handler(Looper.getMainLooper())
    private val sureCheckDelegate = object : SureCheckDelegate(context) {
        override fun onSureCheckProcessed() {
            handler.postDelayed({
                voucherRedemptionPresenter.performVoucherRedemption(false)
            }, 250)
        }

        override fun onSureCheckRejected() {
            voucherRedemptionConfirmation.showFailureScreen()
        }

        override fun onSureCheckCancelled() {
            voucherRedemptionConfirmation.showFailureScreen()
        }

        override fun onSureCheckFailed() {
            voucherRedemptionConfirmation.showFailureScreen()
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.voucher_redemption_confirmation_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        voucherRedemptionConfirmation = context as VoucherRedemptionConfirmationView
        voucherRedemptionPresenter = VoucherRedemptionPresenter(WeakReference(voucherRedemptionConfirmation))
        voucherRedemptionPresenter.attachSureCheckDelegate(sureCheckDelegate)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(activity?.supportFragmentManager, context?.getString(R.string.confirm_voucher))
        val voucher = voucherRedemptionPresenter.getRedeemVoucherInfo()
        binding.subscriptionPeriodSecondaryContentAndLabelView.setContentText(voucher.vendorName)
        binding.subscriptionAmountSecondaryContentAndLabelView.setContentText(voucher.fixedAmount.toString())
        binding.cellNumberSecondaryContentAndLabelView.setContentText(voucher.cellNumber.toFormattedCellphoneNumber())
        binding.redeemButton.setOnClickListener {
            voucherRedemptionPresenter.onRedeemClicked()
        }
    }

    companion object {
        fun newInstance(): VoucherRedemptionConfirmationFragment = VoucherRedemptionConfirmationFragment()
    }
}