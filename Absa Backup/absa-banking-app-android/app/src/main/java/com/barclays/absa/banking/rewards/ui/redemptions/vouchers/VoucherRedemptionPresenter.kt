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

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedemptionRetailVouchers
import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.framework.BaseView
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService
import com.barclays.absa.banking.payments.services.RewardsRedemptionInteractor
import com.barclays.absa.banking.payments.services.RewardsRedemptionService
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import styleguide.forms.SelectorInterface
import styleguide.forms.SelectorList
import java.lang.ref.WeakReference

class VoucherRedemptionPresenter(val redeemVoucherWeakReference: WeakReference<BaseView>) : AbstractPresenter(redeemVoucherWeakReference) {
    private val rewardsRedemptionService: RewardsRedemptionService = RewardsRedemptionInteractor()
    private val voucherRedemptionExtendedResponseListener = VoucherRedemptionExtendedResponseListener(this)
    private lateinit var voucherRedemptionConfirmationExtendedResponseListener: VoucherRedemptionConfirmationExtendedResponseListener
    private lateinit var movieVouchers: ArrayList<RewardsRedemptionRetailVouchers>
    private var isFirstCall = true
    private val rewardsCacheService: IRewardsCacheService = getServiceInterface()

    fun fetchRewardsVouchers() {
        showProgressIndicator()
        movieVouchers = ArrayList()
        val unClassifiedMovieVouchers = rewardsCacheService.getRewardsRedemption()
        if (unClassifiedMovieVouchers?.voucherList != null && !unClassifiedMovieVouchers.voucherList?.isEmpty()!!) {
            val voucherType = "Movie"
            for (unclassifiedVoucher in unClassifiedMovieVouchers.voucherList!!) {
                if (voucherType == unclassifiedVoucher.voucherType) {
                    movieVouchers.add(unclassifiedVoucher)
                }
            }
            dismissProgressIndicator()
        } else {
            dismissProgressIndicator()
            showGenericErrorMessage(this.javaClass.simpleName)
        }
    }

    fun filterVouchers() {
        val voucherItems = SelectorList<VoucherItem>()
        for (voucher in movieVouchers) {
            voucherItems.add(VoucherItem(voucher))
        }
        (viewWeakReference.get() as VoucherRedemptionView).onVoucherItemsResult(voucherItems)
    }

    fun attachSureCheckDelegate(sureCheckDelegate: SureCheckDelegate) {
        voucherRedemptionConfirmationExtendedResponseListener = VoucherRedemptionConfirmationExtendedResponseListener(this, sureCheckDelegate)
    }

    fun onVoucherSelected(position: Int, cellNumber: String?) {
        if (movieVouchers.isNotEmpty()) {
            val voucher = movieVouchers[position]
            val voucherRedemptionInfo = VoucherRedemptionInfo(voucher.vendorId, voucher.vendorName, voucher.fixedAmount, cellNumber)
            rewardsCacheService.setRedeemVoucherInfo(voucherRedemptionInfo)
        }
    }

    fun compareAmounts(rewardsAmount: Amount): Boolean {
        val voucherRedemptionInfo = rewardsCacheService.getRedeemVoucherInfo()
        return voucherRedemptionInfo?.fixedAmount?.amountDouble!!.compareTo(rewardsAmount.amountDouble) > 0
    }

    fun onNextClicked(cellNumber: String) {
        val voucherRedemptionInfo = rewardsCacheService.getRedeemVoucherInfo() ?: return
        voucherRedemptionInfo.cellNumber = cellNumber.replace("\\s".toRegex(), "")
        rewardsCacheService.deleteRedeemVoucherInfo()
        rewardsCacheService.setRedeemVoucherInfo(voucherRedemptionInfo)
    }

    fun getRedeemVoucherInfo(): VoucherRedemptionInfo = rewardsCacheService.getRedeemVoucherInfo() ?: VoucherRedemptionInfo()

    fun onRedeemClicked() {
        performVoucherRedemption(true)
    }

    fun performVoucherRedemption(isFirstCall: Boolean) {
        this.isFirstCall = isFirstCall
        val redeemVoucherInfo = rewardsCacheService.getRedeemVoucherInfo()
        rewardsRedemptionService.redeemVoucher(redeemVoucherInfo, voucherRedemptionExtendedResponseListener)
    }

    fun performVoucherConfirmation(transactionReferenceId: String?) {
        showProgressIndicator()
        rewardsRedemptionService.redeemRewardsResult(transactionReferenceId, isFirstCall, voucherRedemptionConfirmationExtendedResponseListener)
    }

    class VoucherItem(val voucher: RewardsRedemptionRetailVouchers) : SelectorInterface {

        override val displayValue: String?
            get() = voucher.vendorName ?: ""

        override val displayValueLine2: String?
            get() = voucher.fixedAmount.toString()
    }
}