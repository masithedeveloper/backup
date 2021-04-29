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

package com.barclays.absa.banking.depositorPlus.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.depositorPlus.ui.DepositorPlusActivity.Companion.DEPOSITOR_PLUS
import com.barclays.absa.banking.express.invest.getProductInterestRate.ProductInterestRateViewModel
import com.barclays.absa.banking.express.invest.getProductInterestRate.dto.ProductInterestRateRequest
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestFundYourAccountFragment
import com.barclays.absa.utils.viewModel

class DepositorPlusFundYourAccountFragment : SaveAndInvestFundYourAccountFragment() {

    companion object {
        private const val MINIMUM_RECURRING_AMOUNT = 3.00
    }

    private lateinit var productInterestRateViewModel: ProductInterestRateViewModel

    override var minimumRecurringAmount: Double = MINIMUM_RECURRING_AMOUNT
    override var defaultAccountName: String = DEPOSITOR_PLUS

    override fun onAttach(context: Context) {
        super.onAttach(context)
        productInterestRateViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackAnalyticsAction("FundYourAccountScreen_ScreenDisplayed")

        setToolBar(R.string.depositor_plus_setup_your_account)
        saveAndInvestActivity.showProgressIndicatorAndToolbar()
        saveAndInvestActivity.setProgressStep(2)
    }

    override fun calculateInterestRate(initialDeposit: String): String {
        val amount = initialDeposit.toDouble()
        val interestRate = interestRates.find { interestRateDetails -> amount >= interestRateDetails.fromBalance && amount <= interestRateDetails.toBalance }?.interestRate ?: 0.00
        saveAndInvestViewModel.interestRate = interestRate.toString()
        return "$interestRate %"
    }

    override fun navigateOnTransferLimitSuccess() {
        navigate(DepositorPlusFundYourAccountFragmentDirections.actionDepositorPlusFundYourAccountFragmentToDepositorPlusInterestPaymentDetailsFragment())
    }

    override fun fetchInterestRates() {
        with(productInterestRateViewModel) {
            fetchProductInterestRates(ProductInterestRateRequest().apply {
                productCode = saveAndInvestActivity.productType.productCode
                creditRatePlan = saveAndInvestActivity.productType.creditRatePlanCode
            })
            productInterestRatesLiveData.observe(viewLifecycleOwner, {
                interestRates = it
                setAmountInputViewsMaximumValidation()
                dismissProgressDialog()
                productInterestRatesLiveData.removeObservers(viewLifecycleOwner)
            })
        }
    }
}