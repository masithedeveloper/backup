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
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.databinding.VoucherRedemptionFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import styleguide.forms.SelectorList
import styleguide.utils.extensions.toFormattedCellphoneNumber
import java.lang.ref.WeakReference

class VoucherRedemptionFragment : AbsaBaseFragment<VoucherRedemptionFragmentBinding>(), VoucherRedemptionView {

    private lateinit var voucherRedemptionPresenter: VoucherRedemptionPresenter
    private lateinit var onProceedCallback: OnProceedCallback
    private var rewardsBalanceDescription: String? = ""
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    override fun getLayoutResourceId(): Int = R.layout.voucher_redemption_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        voucherRedemptionPresenter = VoucherRedemptionPresenter(WeakReference(this))
        onProceedCallback = context as OnProceedCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(context?.getString(R.string.redeem_voucher)) { activity?.finish() }

        voucherRedemptionPresenter.fetchRewardsVouchers()

        val rewardsAccount = rewardsCacheService.getRewardsAccount()
        val customerProfile = CustomerProfileObject.instance
        if (rewardsAccount != null) {
            rewardsBalanceDescription = rewardsAccount.currentBalance.toString() + " " + getString(R.string.available)
        } else {
            showGenericErrorMessage()
        }

        voucherRedemptionPresenter.filterVouchers()

        binding.voucherNormalInputView.setDescription(rewardsBalanceDescription)
        binding.voucherNormalInputView.setItemSelectionInterface {
            binding.voucherNormalInputView.setDescription(rewardsBalanceDescription)
            voucherRedemptionPresenter.onVoucherSelected(it, customerProfile.cellNumber)

            if (voucherRedemptionPresenter.compareAmounts(rewardsAccount?.currentBalance ?: Amount())) {
                binding.voucherNormalInputView.setError(getString(R.string.voucher_insufficient_funds))
            }

            val voucher = voucherRedemptionPresenter.getRedeemVoucherInfo().vendorName
            if (voucher != null && voucher.contains("ShowMax")) {
                AnalyticsUtils.getInstance().trackCustomScreenView("ShowMax", "Redeem Rewards", "Rewards Voucher")
            } else {
                AnalyticsUtils.getInstance().trackCustomScreenView("Nu Metro", "Redeem Rewards", "Rewards Voucher")
            }
        }

        binding.cellNumberNormalInputView.selectedValue = customerProfile.cellNumber.toFormattedCellphoneNumber()
        binding.cellNumberNormalInputView.setCustomOnClickListener {
            val currentActivity = activity as VoucherRedemptionActivity
            currentActivity.pickContact()
        }

        binding.nextButton.setOnClickListener {
            if (binding.voucherNormalInputView.selectedValue.isEmpty()) {
                binding.voucherNormalInputView.setError(getString(R.string.voucher_selection_error))
            } else if (binding.cellNumberNormalInputView.selectedValue.isEmpty()) {
                binding.cellNumberNormalInputView.setError(getString(R.string.voucher_cellphone_error))
            } else {
                if (binding.cellNumberNormalInputView.selectedValue.length < CELL_NUMBER_MINIMUM_LENGTH) {
                    binding.cellNumberNormalInputView.setError(getString(R.string.voucher_cellphone_error))
                } else {
                    voucherRedemptionPresenter.onNextClicked(binding.cellNumberNormalInputView.selectedValue)
                }
                onProceedCallback.onNext()
            }
        }

        AnalyticsUtils.getInstance().trackCustomScreenView("Redeem a Voucher Screen", "Redeem Rewards", "Rewards Voucher")
    }

    override fun onVoucherItemsResult(retailerVouchers: SelectorList<VoucherRedemptionPresenter.VoucherItem>) {
        binding.voucherNormalInputView.setList(retailerVouchers, getString(R.string.voucher_hint_text))
    }

    override fun onCellNumberResult(cellNumber: String) {
        if (binding.cellNumberNormalInputView.errorTextView?.visibility == View.VISIBLE) {
            binding.cellNumberNormalInputView.clearError()
        }
        binding.cellNumberNormalInputView.selectedValue = cellNumber
    }


    companion object {
        private const val CELL_NUMBER_MINIMUM_LENGTH = 10
        fun newInstance(): VoucherRedemptionFragment = VoucherRedemptionFragment()
    }

    interface OnProceedCallback {
        fun onNext()
    }
}